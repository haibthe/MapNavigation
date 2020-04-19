package com.hb.map.navigation.ui.v1;

import androidx.annotation.Nullable;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.OnLifecycleEvent;

class NavigationViewSubscriber implements LifecycleObserver {

    private final LifecycleOwner lifecycleOwner;
    private final NavigationViewModel navigationViewModel;
    private final NavigationPresenter navigationPresenter;

    NavigationViewSubscriber(final LifecycleOwner owner, final NavigationViewModel navigationViewModel,
                             final NavigationPresenter navigationPresenter) {
        lifecycleOwner = owner;
        lifecycleOwner.getLifecycle().addObserver(this);
        this.navigationViewModel = navigationViewModel;
        this.navigationPresenter = navigationPresenter;
    }

    void subscribe() {
        navigationViewModel.retrieveRoute().observe(lifecycleOwner, directionsRoute -> {
            if (directionsRoute != null) {
                navigationPresenter.onRouteUpdate(directionsRoute);
            }
        });

        navigationViewModel.retrieveDestination().observe(lifecycleOwner, point -> {
            if (point != null) {
                navigationPresenter.onDestinationUpdate(point);
            }
        });

        navigationViewModel.retrieveNavigationLocation().observe(lifecycleOwner, location -> {
            if (location != null) {
                navigationPresenter.onNavigationLocationUpdate(location);
            }
        });

        navigationViewModel.retrieveShouldRecordScreenshot().observe(lifecycleOwner, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean shouldRecordScreenshot) {
                if (shouldRecordScreenshot != null && shouldRecordScreenshot) {
                    navigationPresenter.onShouldRecordScreenshot();
                }
            }
        });
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void unsubscribe() {
        navigationViewModel.retrieveRoute().removeObservers(lifecycleOwner);
        navigationViewModel.retrieveDestination().removeObservers(lifecycleOwner);
        navigationViewModel.retrieveNavigationLocation().removeObservers(lifecycleOwner);
        navigationViewModel.retrieveShouldRecordScreenshot().removeObservers(lifecycleOwner);
    }
}
