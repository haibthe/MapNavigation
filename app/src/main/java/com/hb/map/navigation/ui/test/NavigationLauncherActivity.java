package com.hb.map.navigation.ui.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.hb.map.navigation.app.R;
import com.hb.map.navigation.app.databinding.ActivityNavigationLauncherBinding;
import com.hb.map.navigation.ui.v1.NavigationLauncher;
import com.hb.map.navigation.ui.v1.NavigationLauncherOptions;
import com.hb.map.navigation.ui.v1.camera.CameraUpdateMode;
import com.hb.map.navigation.ui.v1.camera.NavigationCameraUpdate;
import com.hb.map.navigation.ui.v1.map.NavigationMapboxMap;
import com.hb.map.navigation.ui.v1.route.OnRouteSelectionChangeListener;
import com.hb.map.navigation.utils.AppUtils;
import com.hb.map.navigation.v1.navigation.NavigationRoute;
import com.hb.map.navigation.v1.utils.LocaleUtils;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.core.constants.Constants;
import com.mapbox.core.utils.TextUtils;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdate;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.exceptions.InvalidLatLngBoundsException;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Response;
import timber.log.Timber;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class NavigationLauncherActivity extends AppCompatActivity implements OnMapReadyCallback,
        MapboxMap.OnMapLongClickListener, OnRouteSelectionChangeListener {

    private static final int CAMERA_ANIMATION_DURATION = 1000;
    private static final int DEFAULT_CAMERA_ZOOM = 16;
    private static final int CHANGE_SETTING_REQUEST_CODE = 1;
    private static final int INITIAL_ZOOM = 16;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 500;

    private final NavigationLauncherLocationCallback callback = new NavigationLauncherLocationCallback(this);
    private final LocaleUtils localeUtils = new LocaleUtils();
    private final List<Point> wayPoints = new ArrayList<>();

    private LocationEngine locationEngine;
    private NavigationMapboxMap map;
    private DirectionsRoute route;
    private Point currentLocation;
    private boolean locationFound;

    private ActivityNavigationLauncherBinding mBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentLocation = Point.fromLngLat(106.68986, 10.805176);

        mBinding = ActivityNavigationLauncherBinding.inflate(getLayoutInflater());

        setContentView(mBinding.getRoot());
        mBinding.mapView.onCreate(savedInstanceState);
        mBinding.mapView.getMapAsync(this);

        mBinding.launchRouteBtn.setOnClickListener(this::onRouteLaunchClick);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_view_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                showSettings();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_SETTING_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean shouldRefetch = data.getBooleanExtra(NavigationSettingsActivity.UNIT_TYPE_CHANGED, false)
                    || data.getBooleanExtra(NavigationSettingsActivity.LANGUAGE_CHANGED, false);
            if (!wayPoints.isEmpty() && shouldRefetch) {
                fetchRoute();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mBinding.mapView.onStart();
    }

    @SuppressWarnings({"MissingPermission"})
    @Override
    public void onResume() {
        super.onResume();
        mBinding.mapView.onResume();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates(buildEngineRequest(), callback, null);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.mapView.onPause();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates(callback);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mBinding.mapView.onLowMemory();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mBinding.mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBinding.mapView.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mBinding.mapView.onSaveInstanceState(outState);
    }

    //    @OnClick(R.id.launch_route_btn)
    public void onRouteLaunchClick(View v) {
        launchNavigationWithRoute();
//    Timber.d("Route: " + route.toJson());
        String veryLongString = route.toJson();
        int maxLogSize = 2046;
        for (int i = 0; i <= veryLongString.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i + 1) * maxLogSize;
            end = end > veryLongString.length() ? veryLongString.length() : end;
            Timber.d(veryLongString.substring(start, end));
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onMapReady(@NotNull MapboxMap mapboxMap) {
        mapboxMap.setStyle(Style.MAPBOX_STREETS, style -> {
            mapboxMap.addOnMapLongClickListener(this);
            map = new NavigationMapboxMap(mBinding.mapView, mapboxMap);
            map.setOnRouteSelectionChangeListener(this);
            map.updateLocationLayerRenderMode(RenderMode.COMPASS);
            initializeLocationEngine();


            new AsyncTask<Void, Void, DirectionsRoute>() {

                //                String fileTest = "directions_route_convert.json";
                String fileTest = "directions_test.json";
//                String fileTest = "directions-route.json";

                @Override
                protected DirectionsRoute doInBackground(Void... voids) {
                    try {
                        String text = AppUtils.loadStringFromAssets(getBaseContext(), fileTest);
//                        DirectionsResponse response = DirectionsResponse.fromJson(text);
//                        return response.routes().get(0);
                        DirectionsRoute route = DirectionsRoute.fromJson(text);
                        return route;

                    } catch (Exception e) {
                        Timber.e(e);
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(DirectionsRoute result) {
                    super.onPostExecute(result);
                    Timber.d("Result: " + result.toJson());
                    if (result != null && result.distance() > 25d) {
                        route = result;
                        mBinding.launchRouteBtn.setEnabled(true);
                        map.drawRoute(route);
                        boundCameraToRoute();
                    } else {
                        Snackbar.make(mBinding.mapView, R.string.error_select_longer_route, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        });
    }

    @Override
    public boolean onMapLongClick(@NonNull LatLng point) {
        if (wayPoints.size() == 2) {
            Snackbar.make(mBinding.mapView, "Max way points exceeded. Clearing route...", Snackbar.LENGTH_SHORT).show();
            wayPoints.clear();
            map.clearMarkers();
            map.removeRoute();
            return false;
        }
        wayPoints.add(Point.fromLngLat(point.getLongitude(), point.getLatitude()));
        mBinding.launchRouteBtn.setEnabled(false);
        mBinding.loading.setVisibility(View.VISIBLE);
        setCurrentMarkerPosition(point);
        if (locationFound) {
            fetchRoute();
        }
        return false;
    }

    @Override
    public void onNewPrimaryRouteSelected(DirectionsRoute directionsRoute) {
        route = directionsRoute;
    }

    void updateCurrentLocation(Point currentLocation) {
        this.currentLocation = currentLocation;
    }

    void onLocationFound(Location location) {
        map.updateLocation(location);
        if (!locationFound) {
            animateCamera(new LatLng(location.getLatitude(), location.getLongitude()));
            Snackbar.make(mBinding.mapView, R.string.explanation_long_press_waypoint, Snackbar.LENGTH_LONG).show();
            locationFound = true;
            hideLoading();
        }
    }

    private void showSettings() {
        startActivityForResult(new Intent(this, NavigationSettingsActivity.class), CHANGE_SETTING_REQUEST_CODE);
    }

    @SuppressWarnings({"MissingPermission"})
    private void initializeLocationEngine() {
        locationEngine = LocationEngineProvider.getBestLocationEngine(getApplicationContext());
        LocationEngineRequest request = buildEngineRequest();
        locationEngine.requestLocationUpdates(request, callback, null);
        locationEngine.getLastLocation(callback);
    }

    private void fetchRoute() {
        NavigationRoute.Builder builder = NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(currentLocation)
                .profile(getRouteProfileFromSharedPreferences())
                .alternatives(true);

        for (Point wayPoint : wayPoints) {
            builder.addWaypoint(wayPoint);
        }

        setFieldsFromSharedPreferences(builder);
        builder.build().getRoute(new SimplifiedCallback() {
            @Override
            public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                if (validRouteResponse(response)) {
                    hideLoading();
                    route = response.body().routes().get(0);
                    if (route.distance() > 25d) {
                        mBinding.launchRouteBtn.setEnabled(true);
                        map.drawRoutes(response.body().routes());
                        boundCameraToRoute();
                    } else {
                        Snackbar.make(mBinding.mapView, R.string.error_select_longer_route, Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        mBinding.loading.setVisibility(View.VISIBLE);
    }

    private void setFieldsFromSharedPreferences(NavigationRoute.Builder builder) {
        builder
                .language(getLanguageFromSharedPreferences())
                .voiceUnits(getUnitTypeFromSharedPreferences());
    }

    private String getUnitTypeFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultUnitType = getString(R.string.default_unit_type);
        String unitType = sharedPreferences.getString(getString(R.string.unit_type_key), defaultUnitType);
        if (unitType.equals(defaultUnitType)) {
            unitType = localeUtils.getUnitTypeForDeviceLocale(this);
        }

        return unitType;
    }

    private Locale getLanguageFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String defaultLanguage = getString(R.string.default_locale);
        String language = sharedPreferences.getString(getString(R.string.language_key), defaultLanguage);
        if (language.equals(defaultLanguage)) {
            return localeUtils.inferDeviceLocale(this);
        } else {
            return new Locale(language);
        }
    }

    private boolean getShouldSimulateRouteFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getBoolean(getString(R.string.simulate_route_key), false);
    }

    private String getRouteProfileFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(
                getString(R.string.route_profile_key), DirectionsCriteria.PROFILE_DRIVING_TRAFFIC
        );
    }

    private String obtainOfflinePath() {
        File offline = getExternalStoragePublicDirectory("Offline");
        return offline.getAbsolutePath();
    }

    private String retrieveOfflineVersionFromPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString(getString(R.string.offline_version_key), "");
    }

    private void launchNavigationWithRoute() {
        if (route == null) {
            Snackbar.make(mBinding.mapView, R.string.error_route_not_available, Snackbar.LENGTH_SHORT).show();
            return;
        }

        NavigationLauncherOptions.Builder optionsBuilder = NavigationLauncherOptions.builder()
                .shouldSimulateRoute(getShouldSimulateRouteFromSharedPreferences());
        CameraPosition initialPosition = new CameraPosition.Builder()
                .target(new LatLng(currentLocation.latitude(), currentLocation.longitude()))
                .zoom(INITIAL_ZOOM)
                .build();
        optionsBuilder.initialMapCameraPosition(initialPosition);
        optionsBuilder.directionsRoute(route);
        optionsBuilder.shouldSimulateRoute(true);
        String offlinePath = obtainOfflinePath();
        if (!TextUtils.isEmpty(offlinePath)) {
            optionsBuilder.offlineRoutingTilesPath(offlinePath);
        }
        String offlineVersion = retrieveOfflineVersionFromPreferences();
        if (!offlineVersion.isEmpty()) {
            optionsBuilder.offlineRoutingTilesVersion(offlineVersion);
        }
        // TODO Testing dynamic offline
        /**
         * File downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
         * String databaseFilePath = downloadDirectory + "/" + "kingfarm.db";
         * String offlineStyleUrl = "mapbox://styles/mapbox/navigation-guidance-day-v4";
         * optionsBuilder.offlineMapOptions(new MapOfflineOptions(databaseFilePath, offlineStyleUrl));
         */
        NavigationLauncher.startNavigation(this, optionsBuilder.build());
    }

    private boolean validRouteResponse(Response<DirectionsResponse> response) {
        return response.body() != null && !response.body().routes().isEmpty();
    }

    private void hideLoading() {
        if (mBinding.loading.getVisibility() == View.VISIBLE) {
            mBinding.loading.setVisibility(View.INVISIBLE);
        }
    }

    public void boundCameraToRoute() {
        if (route != null) {
            List<Point> routeCoords = LineString.fromPolyline(route.geometry(),
                    Constants.PRECISION_6).coordinates();
            List<LatLng> bboxPoints = new ArrayList<>();
            for (Point point : routeCoords) {
                bboxPoints.add(new LatLng(point.latitude(), point.longitude()));
            }
            if (bboxPoints.size() > 1) {
                try {
                    LatLngBounds bounds = new LatLngBounds.Builder().includes(bboxPoints).build();
                    // left, top, right, bottom
                    int topPadding = mBinding.launchBtnFrame.getHeight() * 2;
                    animateCameraBbox(bounds, CAMERA_ANIMATION_DURATION, new int[]{50, topPadding, 50, 100});
                } catch (InvalidLatLngBoundsException exception) {
                    Toast.makeText(this, R.string.error_valid_route_not_found, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void animateCameraBbox(LatLngBounds bounds, int animationTime, int[] padding) {
        CameraPosition position = map.retrieveMap().getCameraForLatLngBounds(bounds, padding);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(position);
        NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
        navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
        map.retrieveCamera().update(navigationCameraUpdate, animationTime);
    }

    private void animateCamera(LatLng point) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(point, DEFAULT_CAMERA_ZOOM);
        NavigationCameraUpdate navigationCameraUpdate = new NavigationCameraUpdate(cameraUpdate);
        navigationCameraUpdate.setMode(CameraUpdateMode.OVERRIDE);
        map.retrieveCamera().update(navigationCameraUpdate, CAMERA_ANIMATION_DURATION);
    }

    private void setCurrentMarkerPosition(LatLng position) {
        if (position != null) {
            map.addDestinationMarker(Point.fromLngLat(position.getLongitude(), position.getLatitude()));
        }
    }

    @NonNull
    private LocationEngineRequest buildEngineRequest() {
        return new LocationEngineRequest.Builder(UPDATE_INTERVAL_IN_MILLISECONDS)
                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS)
                .build();
    }

    private static class NavigationLauncherLocationCallback implements LocationEngineCallback<LocationEngineResult> {

        private final WeakReference<NavigationLauncherActivity> activityWeakReference;

        NavigationLauncherLocationCallback(NavigationLauncherActivity activity) {
            this.activityWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(LocationEngineResult result) {
            NavigationLauncherActivity activity = activityWeakReference.get();
            if (activity != null) {
                Location location = result.getLastLocation();
                if (location == null) {
                    return;
                }
                activity.updateCurrentLocation(Point.fromLngLat(location.getLongitude(), location.getLatitude()));
                activity.onLocationFound(location);
            }
        }

        @Override
        public void onFailure(@NonNull Exception exception) {
            Timber.e(exception);
        }
    }
}
