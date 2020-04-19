package com.hb.map.navigation.ui.v1;

import android.location.Location;

import com.hb.map.navigation.v1.routeprogress.ProgressChangeListener;
import com.hb.map.navigation.v1.routeprogress.RouteProgress;

class NavigationViewModelProgressChangeListener implements ProgressChangeListener {

    private final NavigationViewModel viewModel;

    NavigationViewModelProgressChangeListener(NavigationViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void onProgressChange(Location location, RouteProgress routeProgress) {
        viewModel.updateRouteProgress(routeProgress);
        viewModel.updateLocation(location);
    }
}