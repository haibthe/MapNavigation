package com.hb.map.navigation.ui.v1.camera;

/**
 * Use this listener to observe changes in the {@link NavigationCamera}
 * tracking mode.
 */
public interface OnTrackingModeChangedListener {

    /**
     * Invoked when {@link NavigationCamera} tracking mode changes.
     *
     * @param trackingMode the current tracking mode
     */
    void onTrackingModeChanged(@NavigationCamera.TrackingMode int trackingMode);
}
