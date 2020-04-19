package com.hb.map.navigation.ui.v1.camera;

import com.mapbox.mapboxsdk.location.OnCameraTrackingChangedListener;

class NavigationCameraTrackingChangedListener implements OnCameraTrackingChangedListener {

    private final NavigationCamera camera;

    NavigationCameraTrackingChangedListener(NavigationCamera camera) {
        this.camera = camera;
    }

    @Override
    public void onCameraTrackingDismissed() {
        camera.updateCameraTrackingMode(NavigationCamera.NAVIGATION_TRACKING_MODE_NONE);
    }

    @Override
    public void onCameraTrackingChanged(int currentMode) {
        Integer trackingMode = camera.findTrackingModeFor(currentMode);
        if (trackingMode != null) {
            camera.updateCameraTrackingMode(trackingMode);
        }
    }
}