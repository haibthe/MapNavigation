package com.hb.map.navigation.v1.route;

import android.location.Location;

import com.hb.map.navigation.v1.routeprogress.RouteProgress;
import com.mapbox.api.directions.v5.models.DirectionsResponse;

/**
 * This class can be subclassed to provide custom logic for checking / determining
 * new / faster routes while navigating.
 * <p>
 * To provide your implementation,
 * use {@link com.hb.map.navigation.v1.navigation.MapboxNavigation#setFasterRouteEngine(FasterRoute)}.
 * <p>
 * {@link FasterRoute#shouldCheckFasterRoute(Location, RouteProgress)} determines how quickly a
 * new route will be fetched by {@link RouteFetcher}.
 * <p>
 * {@link FasterRoute#isFasterRoute(DirectionsResponse, RouteProgress)} determines if the new route
 * retrieved by {@link RouteFetcher} is actually faster than the current route.
 *
 * @since 0.9.0
 */
public abstract class FasterRoute {

    /**
     * This method determine if a new {@link DirectionsResponse} should
     * be retrieved by {@link RouteFetcher}.
     * <p>
     * It will also be called every time
     * the <tt>NavigationEngine</tt> gets a valid {@link Location} update.
     * <p>
     * The most recent snapped location and route progress are provided.  Both can be used to
     * determine if a new route should be fetched or not.
     *
     * @param location      current snapped location
     * @param routeProgress current route progress
     * @return true if should check, false if not
     * @since 0.9.0
     */
    public abstract boolean shouldCheckFasterRoute(Location location, RouteProgress routeProgress);

    /**
     * This method will be used to determine if the route retrieved is
     * faster than the one that's currently being navigated.
     *
     * @param response      provided by {@link RouteFetcher}
     * @param routeProgress current route progress
     * @return true if the new route is considered faster, false if not
     */
    public abstract boolean isFasterRoute(DirectionsResponse response, RouteProgress routeProgress);
}
