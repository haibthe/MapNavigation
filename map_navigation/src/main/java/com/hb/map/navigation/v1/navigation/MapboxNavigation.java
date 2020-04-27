package com.hb.map.navigation.v1.navigation;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hb.map.navigation.v1.location.RawLocationListener;
import com.hb.map.navigation.v1.milestone.BannerInstructionMilestone;
import com.hb.map.navigation.v1.milestone.Milestone;
import com.hb.map.navigation.v1.milestone.MilestoneEventListener;
import com.hb.map.navigation.v1.milestone.VoiceInstructionMilestone;
import com.hb.map.navigation.v1.navigation.camera.Camera;
import com.hb.map.navigation.v1.offroute.OffRoute;
import com.hb.map.navigation.v1.offroute.OffRouteListener;
import com.hb.map.navigation.v1.route.FasterRoute;
import com.hb.map.navigation.v1.route.FasterRouteListener;
import com.hb.map.navigation.v1.routeprogress.ProgressChangeListener;
import com.hb.map.navigation.v1.snap.Snap;
import com.hb.map.navigation.v1.utils.ValidationUtils;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.navigator.Navigator;
import com.mapbox.navigator.NavigatorConfig;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import timber.log.Timber;

import static com.hb.map.navigation.v1.navigation.NavigationConstants.BANNER_INSTRUCTION_MILESTONE_ID;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.NON_NULL_APPLICATION_CONTEXT_REQUIRED;
import static com.hb.map.navigation.v1.navigation.NavigationConstants.VOICE_INSTRUCTION_MILESTONE_ID;

/**
 * A MapboxNavigation class for interacting with and customizing a navigation session.
 * <p>
 * Instance of this class are used to setup, customize, start, and end a navigation session.
 *
 * @see <a href="https://www.mapbox.com/android-docs/navigation/">Navigation documentation</a>
 * @since 0.1.0
 */
public class MapboxNavigation implements ServiceConnection {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500;

    static {
        NavigationLibraryLoader.load();
    }

    private final String accessToken;
    private NavigationEventDispatcher navigationEventDispatcher;
    private NavigationEngineFactory navigationEngineFactory;
    private NavigationService navigationService;
    private MapboxNavigator mapboxNavigator;
    private DirectionsRoute directionsRoute;
    private MapboxNavigationOptions options;
    private LocationEngine locationEngine;
    private LocationEngineRequest locationEngineRequest;
    private Set<Milestone> milestones;
    private Context applicationContext;
    private boolean isBound;
    private RouteRefresher routeRefresher;

    /**
     * Constructs a new instance of this class using the default options. This should be used over
     * {@link #MapboxNavigation(Context, String, MapboxNavigationOptions)} if all the default options
     * fit your needs.
     * <p>
     * Initialization will also add the default milestones and create a new location engine
     * which will be used during navigation unless a different engine gets passed in through
     * {@link #setLocationEngine(LocationEngine)}.
     * </p>
     *
     * @param context     required in order to create and bind the navigation service
     * @param accessToken a valid Mapbox access token
     * @since 0.5.0
     */
    public MapboxNavigation(@NonNull Context context, @NonNull String accessToken) {
        this(context, accessToken, MapboxNavigationOptions.builder().build());
    }

    /**
     * Constructs a new instance of this class using a custom built options class. Building a custom
     * {@link MapboxNavigationOptions} object and passing it in allows you to further customize the
     * user experience. While many of the default values have been tested thoroughly, you might find
     * that your app requires special tweaking. Once this class is initialized, the options specified
     * through the options class cannot be modified.
     * <p>
     * Initialization will also add the default milestones and create a new location engine
     * which will be used during navigation unless a different engine gets passed in through
     * {@link #setLocationEngine(LocationEngine)}.
     * </p>
     *
     * @param context     required in order to create and bind the navigation service
     * @param options     a custom built {@code MapboxNavigationOptions} class
     * @param accessToken a valid Mapbox access token
     * @see MapboxNavigationOptions
     * @since 0.5.0
     */
    public MapboxNavigation(@NonNull Context context, @NonNull String accessToken,
                            @NonNull MapboxNavigationOptions options) {
        initializeContext(context);
        this.accessToken = accessToken;
        this.options = options;
        initialize();
    }

    /**
     * Constructs a new instance of this class using a custom built options class. Building a custom
     * {@link MapboxNavigationOptions} object and passing it in allows you to further customize the
     * user experience. Once this class is initialized, the options specified
     * through the options class cannot be modified.
     *
     * @param context        required in order to create and bind the navigation service
     * @param accessToken    a valid Mapbox access token
     * @param options        a custom built {@code MapboxNavigationOptions} class
     * @param locationEngine a LocationEngine to provide Location updates
     * @see MapboxNavigationOptions
     * @since 0.19.0
     */
    public MapboxNavigation(@NonNull Context context,
                            @NonNull String accessToken,
                            @NonNull MapboxNavigationOptions options,
                            @NonNull LocationEngine locationEngine) {
        initializeContext(context);
        this.accessToken = accessToken;
        this.options = options;
        this.locationEngine = locationEngine;
        initialize();
    }

    // Package private (no modifier) for testing purposes
    MapboxNavigation(@NonNull Context context,
                     @NonNull String accessToken,
                     LocationEngine locationEngine,
                     MapboxNavigator mapboxNavigator) {
        initializeContext(context);
        this.accessToken = accessToken;
        this.options = MapboxNavigationOptions.builder().build();
        this.locationEngine = locationEngine;
        this.mapboxNavigator = mapboxNavigator;
        initializeForTest();
    }

    // Lifecycle

    /**
     * Critical to place inside your navigation activity so that when your application gets destroyed
     * the navigation service unbinds and gets destroyed, preventing any memory leaks. Calling this
     * also removes all listeners that have been attached.
     */
    public void onDestroy() {
        stopNavigation();
        removeOffRouteListener(null);
        removeProgressChangeListener(null);
        removeMilestoneEventListener(null);
        removeNavigationEventListener(null);
        removeFasterRouteListener(null);
        removeRawLocationListener(null);
    }

    // Public APIs

    /**
     * Navigation {@link Milestone}s provide a powerful way to give your user instructions at custom
     * defined locations along their route. Default milestones are automatically added unless
     * {@link MapboxNavigationOptions#defaultMilestonesEnabled()} is set to false but they can also
     * be individually removed using the {@link #removeMilestone(Milestone)} API. Once a custom
     * milestone is built, it will need to be passed into the navigation SDK through this method.
     * <p>
     * Milestones can only be added once and must be removed and added back if any changes are
     * desired.
     * </p>
     *
     * @param milestone a custom built milestone
     * @since 0.4.0
     */
    public void addMilestone(@NonNull Milestone milestone) {
        boolean milestoneAdded = milestones.add(milestone);
        if (!milestoneAdded) {
            Timber.w("Milestone has already been added to the stack.");
        }
    }

    /**
     * Adds the given list of {@link Milestone} to be triggered during navigation.
     * <p>
     * Milestones can only be added once and must be removed and added back if any changes are
     * desired.
     * </p>
     *
     * @param milestones a list of custom built milestone
     * @since 0.14.0
     */
    public void addMilestones(@NonNull List<Milestone> milestones) {
        boolean milestonesAdded = this.milestones.addAll(milestones);
        if (!milestonesAdded) {
            Timber.w("These milestones have already been added to the stack.");
        }
    }

    /**
     * Remove a specific milestone by passing in the instance of it. Removal of all the milestones can
     * be achieved by passing in null rather than a specific milestone.
     *
     * @param milestone a milestone you'd like to have removed or null if you'd like to remove all
     *                  milestones
     * @since 0.4.0
     */
    @SuppressWarnings("WeakerAccess") // Public exposed for usage outside SDK
    public void removeMilestone(@Nullable Milestone milestone) {
        if (milestone == null) {
            milestones.clear();
            return;
        } else if (!milestones.contains(milestone)) {
            Timber.w("Milestone attempting to remove does not exist in stack.");
            return;
        }
        milestones.remove(milestone);
    }

    public void removeMilestone(int milestoneIdentifier) {
        for (Milestone milestone : milestones) {
            if (milestoneIdentifier == milestone.getIdentifier()) {
                removeMilestone(milestone);
                return;
            }
        }
        Timber.w("No milestone found with the specified identifier.");
    }


    @NonNull
    public LocationEngine getLocationEngine() {
        return locationEngine;
    }

    public void setLocationEngine(@NonNull LocationEngine locationEngine) {
        this.locationEngine = locationEngine;
        if (isServiceAvailable()) {
            navigationService.updateLocationEngine(locationEngine);
        }
    }

    public void setLocationEngineRequest(@NonNull LocationEngineRequest locationEngineRequest) {
        this.locationEngineRequest = locationEngineRequest;

        if (isServiceAvailable()) {
            navigationService.updateLocationEngineRequest(locationEngineRequest);
        }
    }


    public void startNavigation(@NonNull DirectionsRoute directionsRoute) {
        startNavigationWith(directionsRoute, DirectionsRouteType.NEW_ROUTE);
    }


    public void startNavigation(@NonNull DirectionsRoute directionsRoute, @NonNull DirectionsRouteType routeType) {
        startNavigationWith(directionsRoute, routeType);
    }

    public void stopNavigation() {
        Timber.d("MapboxNavigation stopNavigation called");
        if (isServiceAvailable()) {
            applicationContext.unbindService(this);
            isBound = false;
            navigationService.endNavigation();
            navigationService.stopSelf();
            navigationEventDispatcher.onNavigationEvent(false);
        }
    }

    // Listeners


    public void addMilestoneEventListener(@NonNull MilestoneEventListener milestoneEventListener) {
        navigationEventDispatcher.addMilestoneEventListener(milestoneEventListener);
    }

    public void removeMilestoneEventListener(@Nullable MilestoneEventListener milestoneEventListener) {
        navigationEventDispatcher.removeMilestoneEventListener(milestoneEventListener);
    }


    public void addProgressChangeListener(@NonNull ProgressChangeListener progressChangeListener) {
        navigationEventDispatcher.addProgressChangeListener(progressChangeListener);
    }


    public void removeProgressChangeListener(@Nullable ProgressChangeListener progressChangeListener) {
        navigationEventDispatcher.removeProgressChangeListener(progressChangeListener);
    }


    public void addOffRouteListener(@NonNull OffRouteListener offRouteListener) {
        navigationEventDispatcher.addOffRouteListener(offRouteListener);
    }

    public void removeOffRouteListener(@Nullable OffRouteListener offRouteListener) {
        navigationEventDispatcher.removeOffRouteListener(offRouteListener);
    }


    public void addNavigationEventListener(@NonNull NavigationEventListener navigationEventListener) {
        navigationEventDispatcher.addNavigationEventListener(navigationEventListener);
    }


    public void removeNavigationEventListener(@Nullable NavigationEventListener navigationEventListener) {
        navigationEventDispatcher.removeNavigationEventListener(navigationEventListener);
    }

    public void addFasterRouteListener(@NonNull FasterRouteListener fasterRouteListener) {
        navigationEventDispatcher.addFasterRouteListener(fasterRouteListener);
    }

    public void removeFasterRouteListener(@Nullable FasterRouteListener fasterRouteListener) {
        navigationEventDispatcher.removeFasterRouteListener(fasterRouteListener);
    }

    public void addRawLocationListener(@NonNull RawLocationListener rawLocationListener) {
        navigationEventDispatcher.addRawLocationListener(rawLocationListener);
    }


    public void removeRawLocationListener(@Nullable RawLocationListener rawLocationListener) {
        navigationEventDispatcher.removeRawLocationListener(rawLocationListener);
    }

    // Custom engines


    @NonNull
    public Camera getCameraEngine() {
        return navigationEngineFactory.retrieveCameraEngine();
    }


    public void setCameraEngine(@NonNull Camera cameraEngine) {
        navigationEngineFactory.updateCameraEngine(cameraEngine);
    }


    public Snap getSnapEngine() {
        return navigationEngineFactory.retrieveSnapEngine();
    }

    public void setSnapEngine(@NonNull Snap snapEngine) {
        navigationEngineFactory.updateSnapEngine(snapEngine);
    }


    @NonNull
    public OffRoute getOffRouteEngine() {
        return navigationEngineFactory.retrieveOffRouteEngine();
    }


    public void setOffRouteEngine(@NonNull OffRoute offRouteEngine) {
        navigationEngineFactory.updateOffRouteEngine(offRouteEngine);
    }

    @NonNull
    public FasterRoute getFasterRouteEngine() {
        return navigationEngineFactory.retrieveFasterRouteEngine();
    }


    public void setFasterRouteEngine(@NonNull FasterRoute fasterRouteEngine) {
        navigationEngineFactory.updateFasterRouteEngine(fasterRouteEngine);
    }


    public boolean updateRouteLegIndex(int legIndex) {
        if (checkInvalidLegIndex(legIndex)) {
            return false;
        }
        mapboxNavigator.updateLegIndex(legIndex);
        return true;
    }

    public String retrieveHistory() {
        return mapboxNavigator.retrieveHistory();
    }

    public void toggleHistory(boolean isEnabled) {
        mapboxNavigator.toggleHistory(isEnabled);
    }

    public void addHistoryEvent(String eventType, String eventJsonProperties) {
        mapboxNavigator.addHistoryEvent(eventType, eventJsonProperties);
    }

    public String retrieveSsmlAnnouncementInstruction(int index) {
        return mapboxNavigator.retrieveVoiceInstruction(index).getSsmlAnnouncement();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Timber.d("Connected to service.");
        NavigationService.LocalBinder binder = (NavigationService.LocalBinder) service;
        navigationService = binder.getService();
        navigationService.startNavigation(this);
        isBound = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Timber.d("Disconnected from service.");
        navigationService = null;
        isBound = false;
    }

    String obtainAccessToken() {
        return accessToken;
    }

    DirectionsRoute getRoute() {
        return directionsRoute;
    }

    List<Milestone> getMilestones() {
        return new ArrayList<>(milestones);
    }

    MapboxNavigationOptions options() {
        return options;
    }

    NavigationEventDispatcher getEventDispatcher() {
        return navigationEventDispatcher;
    }

    NavigationEngineFactory retrieveEngineFactory() {
        return navigationEngineFactory;
    }

    MapboxNavigator retrieveMapboxNavigator() {
        return mapboxNavigator;
    }

    @NonNull
    LocationEngineRequest retrieveLocationEngineRequest() {
        return locationEngineRequest;
    }

    @Nullable
    RouteRefresher retrieveRouteRefresher() {
        return routeRefresher;
    }

    private void initializeForTest() {
        // Initialize event dispatcher and add internal listeners
        navigationEventDispatcher = new NavigationEventDispatcher();
        navigationEngineFactory = new NavigationEngineFactory();
        locationEngine = obtainLocationEngine();
        locationEngineRequest = obtainLocationEngineRequest();

        // Create and add default milestones if enabled.
        milestones = new HashSet<>();
        if (options.defaultMilestonesEnabled()) {
            addMilestone(new VoiceInstructionMilestone.Builder().setIdentifier(VOICE_INSTRUCTION_MILESTONE_ID).build());
            addMilestone(new BannerInstructionMilestone.Builder().setIdentifier(BANNER_INSTRUCTION_MILESTONE_ID).build());
        }
    }

    /**
     * In-charge of initializing all variables needed to begin a navigation session. Many values can
     * be changed later on using their corresponding setter. An internal progressChangeListeners used
     * to prevent users from removing it.
     */
    private void initialize() {
        // Initialize event dispatcher and add internal listeners
        Navigator navigator = configureNavigator();
        mapboxNavigator = new MapboxNavigator(navigator);
        navigationEventDispatcher = new NavigationEventDispatcher();
        navigationEngineFactory = new NavigationEngineFactory();
        locationEngine = obtainLocationEngine();
        locationEngineRequest = obtainLocationEngineRequest();

        // Create and add default milestones if enabled.
        milestones = new HashSet<>();
        if (options.defaultMilestonesEnabled()) {
            addMilestone(new VoiceInstructionMilestone.Builder().setIdentifier(VOICE_INSTRUCTION_MILESTONE_ID).build());
            addMilestone(new BannerInstructionMilestone.Builder().setIdentifier(BANNER_INSTRUCTION_MILESTONE_ID).build());
        }
    }

    private void initializeContext(Context context) {
        if (context == null || context.getApplicationContext() == null) {
            throw new IllegalArgumentException(NON_NULL_APPLICATION_CONTEXT_REQUIRED);
        }
        applicationContext = context.getApplicationContext();
    }

    @NotNull
    private Navigator configureNavigator() {
        Navigator navigator = new Navigator();
        NavigatorConfig navigatorConfig = navigator.getConfig();
        navigatorConfig.setOffRouteThreshold(options.offRouteThreshold());
        navigatorConfig.setOffRouteThresholdWhenNearIntersection(options.offRouteThresholdWhenNearIntersection());
        navigatorConfig.setIntersectionRadiusForOffRouteDetection(options.intersectionRadiusForOffRouteDetection());
        navigator.setConfig(navigatorConfig);
        return navigator;
    }

    @NonNull
    private LocationEngine obtainLocationEngine() {
        if (locationEngine == null) {
            return LocationEngineProvider.getBestLocationEngine(applicationContext);
        }

        return locationEngine;
    }

    @NonNull
    private LocationEngineRequest obtainLocationEngineRequest() {
        if (locationEngineRequest == null) {
            return new LocationEngineRequest.Builder(UPDATE_INTERVAL_IN_MILLISECONDS)
                    .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                    .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                    .build();
        }

        return locationEngineRequest;
    }

    private void startNavigationWith(@NonNull DirectionsRoute directionsRoute, DirectionsRouteType routeType) {
        ValidationUtils.validDirectionsRoute(directionsRoute, options.defaultMilestonesEnabled());
        this.directionsRoute = directionsRoute;
        routeRefresher = new RouteRefresher(this, new RouteRefresh(accessToken));
        mapboxNavigator.updateRoute(directionsRoute, routeType);
        if (!isBound) {
            startNavigationService();
            navigationEventDispatcher.onNavigationEvent(true);
        }
    }

    private void startNavigationService() {
        Intent intent = getServiceIntent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            applicationContext.startForegroundService(intent);
        } else {
            applicationContext.startService(intent);
        }
        applicationContext.bindService(intent, this, Context.BIND_AUTO_CREATE);
    }

    private Intent getServiceIntent() {
        return new Intent(applicationContext, NavigationService.class);
    }

    private boolean isServiceAvailable() {
        return navigationService != null && isBound;
    }

    private boolean checkInvalidLegIndex(int legIndex) {
        int legSize = directionsRoute.legs().size();
        if (legIndex < 0 || legIndex > legSize - 1) {
            Timber.e("Invalid leg index update: %s Current leg index size: %s", legIndex, legSize);
            return true;
        }
        return false;
    }
}
