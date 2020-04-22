package com.hb.map.navigation.ui.v1;


import android.view.View;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.hb.map.navigation.ui.v1.listeners.BannerInstructionsListener;
import com.hb.map.navigation.ui.v1.listeners.InstructionListListener;
import com.hb.map.navigation.ui.v1.listeners.NavigationListener;
import com.hb.map.navigation.ui.v1.listeners.RouteListener;
import com.hb.map.navigation.ui.v1.listeners.SpeechAnnouncementListener;
import com.hb.map.navigation.ui.v1.voice.SpeechAnnouncement;
import com.hb.map.navigation.v1.milestone.MilestoneEventListener;
import com.hb.map.navigation.v1.navigation.MapboxNavigation;
import com.hb.map.navigation.v1.routeprogress.ProgressChangeListener;
import com.mapbox.api.directions.v5.models.BannerInstructions;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;

class NavigationViewEventDispatcher {

    private ProgressChangeListener progressChangeListener;
    private MilestoneEventListener milestoneEventListener;
    private NavigationListener navigationListener;
    private RouteListener routeListener;
    private BottomSheetBehavior.BottomSheetCallback bottomSheetCallback;
    private InstructionListListener instructionListListener;
    private SpeechAnnouncementListener speechAnnouncementListener;
    private BannerInstructionsListener bannerInstructionsListener;

    /**
     * Initializes the listeners in the dispatcher, as well as the listeners in the {@link MapboxNavigation}
     *
     * @param navigationViewOptions that contains all listeners to attach
     */
    void initializeListeners(NavigationViewOptions navigationViewOptions, NavigationViewModel navigationViewModel) {
        assignNavigationListener(navigationViewOptions.navigationListener(), navigationViewModel);
        assignRouteListener(navigationViewOptions.routeListener());
        assignBottomSheetCallback(navigationViewOptions.bottomSheetCallback());
        MapboxNavigation navigation = navigationViewModel.retrieveNavigation();
        assignProgressChangeListener(navigationViewOptions, navigation);
        assignMilestoneEventListener(navigationViewOptions, navigation);
        assignInstructionListListener(navigationViewOptions.instructionListListener());
        assignSpeechAnnouncementListener(navigationViewOptions.speechAnnouncementListener());
        assignBannerInstructionsListener(navigationViewOptions.bannerInstructionsListener());
    }

    void onDestroy(@Nullable MapboxNavigation navigation) {
        if (navigation != null) {
            removeProgressChangeListener(navigation);
            removeMilestoneEventListener(navigation);
        }
    }

    void assignNavigationListener(@Nullable NavigationListener navigationListener,
                                  NavigationViewModel navigationViewModel) {
        this.navigationListener = navigationListener;
        if (navigationListener != null && navigationViewModel.isRunning()) {
            navigationListener.onNavigationRunning();
        }
    }

    void assignRouteListener(@Nullable RouteListener routeListener) {
        this.routeListener = routeListener;
    }

    void assignBottomSheetCallback(@Nullable BottomSheetBehavior.BottomSheetCallback bottomSheetCallback) {
        this.bottomSheetCallback = bottomSheetCallback;
    }

    void assignInstructionListListener(@Nullable InstructionListListener instructionListListener) {
        this.instructionListListener = instructionListListener;
    }

    void assignSpeechAnnouncementListener(@Nullable SpeechAnnouncementListener speechAnnouncementListener) {
        this.speechAnnouncementListener = speechAnnouncementListener;
    }

    void assignBannerInstructionsListener(@Nullable BannerInstructionsListener bannerInstructionsListener) {
        this.bannerInstructionsListener = bannerInstructionsListener;
    }

    void onNavigationFinished() {
        if (navigationListener != null) {
            navigationListener.onNavigationFinished();
        }
    }

    void onCancelNavigation() {
        if (navigationListener != null) {
            navigationListener.onCancelNavigation();
        }
    }

    void onNavigationRunning() {
        if (navigationListener != null) {
            navigationListener.onNavigationRunning();
        }
    }

    boolean allowRerouteFrom(Point point) {
        return routeListener == null || routeListener.allowRerouteFrom(point);
    }

    void onOffRoute(Point point) {
        if (routeListener != null) {
            routeListener.onOffRoute(point);
        }
    }

    void onRerouteAlong(DirectionsRoute directionsRoute) {
        if (routeListener != null) {
            routeListener.onRerouteAlong(directionsRoute);
        }
    }

    void onFailedReroute(String errorMessage) {
        if (routeListener != null) {
            routeListener.onFailedReroute(errorMessage);
        }
    }

    void onArrival() {
        if (routeListener != null) {
            routeListener.onArrival();
        }
    }

    void onBottomSheetStateChanged(View bottomSheet, int newState) {
        if (bottomSheetCallback != null) {
            bottomSheetCallback.onStateChanged(bottomSheet, newState);
        }
    }

    void onInstructionListVisibilityChanged(boolean shown) {
        if (instructionListListener != null) {
            instructionListListener.onInstructionListVisibilityChanged(shown);
        }
    }

    SpeechAnnouncement onAnnouncement(SpeechAnnouncement announcement) {
        if (speechAnnouncementListener != null) {
            return speechAnnouncementListener.willVoice(announcement);
        }
        return announcement;
    }

    BannerInstructions onBannerDisplay(BannerInstructions instructions) {
        if (bannerInstructionsListener != null) {
            return bannerInstructionsListener.willDisplay(instructions);
        }
        return instructions;
    }

    private void assignProgressChangeListener(NavigationViewOptions navigationViewOptions, MapboxNavigation navigation) {
        this.progressChangeListener = navigationViewOptions.progressChangeListener();
        if (progressChangeListener != null) {
            navigation.addProgressChangeListener(progressChangeListener);
        }
    }

    private void assignMilestoneEventListener(NavigationViewOptions navigationViewOptions, MapboxNavigation navigation) {
        this.milestoneEventListener = navigationViewOptions.milestoneEventListener();
        if (milestoneEventListener != null) {
            navigation.addMilestoneEventListener(milestoneEventListener);
        }
    }

    private void removeMilestoneEventListener(MapboxNavigation navigation) {
        if (milestoneEventListener != null) {
            navigation.removeMilestoneEventListener(milestoneEventListener);
        }
    }

    private void removeProgressChangeListener(MapboxNavigation navigation) {
        if (progressChangeListener != null) {
            navigation.removeProgressChangeListener(progressChangeListener);
        }
    }
}
