package com.hb.map.navigation.v1.navigation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hb.map.navigation.converter.IConverter;
import com.hb.map.navigation.vbd.entities.VbdRouteResponse;
import com.hb.map.navigation.vbd.service.VBDConverter;
import com.hb.map.navigation.vbd.service.VBDDirections;
import com.hb.map.navigation.vbd.service.BaseVBDService;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import okhttp3.EventListener;
import okhttp3.Interceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public final class NavigationRoute {

    public interface ICallback {
        void onResponse(DirectionsResponse response);

        void onFailure(Throwable t);
    }

    private final VBDDirections vbdDirections;

    private IConverter<VbdRouteResponse> mConverter;

    NavigationRoute(VBDDirections vbdDirections) {
        this.vbdDirections = vbdDirections;
        mConverter = new VBDConverter(vbdDirections.baseUrl(), "");
    }

    public void getRoute(final NavigationRoute.ICallback callback) {
        Callback<VbdRouteResponse> vbdCallBack = new Callback<VbdRouteResponse>() {
            @Override
            public void onResponse(Call<VbdRouteResponse> call, Response<VbdRouteResponse> response) {
                if (callback != null) {
                    DirectionsRoute route = mConverter.convert(response.body());
                    if (route != null) {
                        ArrayList<DirectionsRoute> routes = new ArrayList<>();
                        routes.add(route);
                        DirectionsResponse newResponse = DirectionsResponse.builder()
                                .code("200")
                                .uuid(UUID.randomUUID().toString())
                                .routes(routes)
                                .build();

                        callback.onResponse(newResponse);
                    }
                }
            }

            @Override
            public void onFailure(Call<VbdRouteResponse> call, Throwable t) {
                callback.onFailure(t);
            }
        };
        vbdDirections.enqueueCall(vbdCallBack);
    }


    public Call<VbdRouteResponse> getCall() {
        return vbdDirections.cloneCall();
    }


    public void cancelCall() {
        if (!getCall().isExecuted()) {
            getCall().cancel();
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private final VBDDirections.Builder directionsBuilder;
        private NavigationRouteWaypoint origin;
        private NavigationRouteWaypoint destination;
        private List<NavigationRouteWaypoint> waypoints = new ArrayList<>();


        private Builder() {
            this(VBDDirections.builder());
        }

        Builder(VBDDirections.Builder directionsBuilder) {
            this.directionsBuilder = directionsBuilder;
        }

        public Builder origin(@NonNull Point origin) {
            this.origin = new NavigationRouteWaypoint(origin, null, null);
            return this;
        }


        public Builder origin(@NonNull Point origin, @Nullable Double angle,
                              @Nullable Double tolerance) {
            this.origin = new NavigationRouteWaypoint(origin, angle, tolerance);
            return this;
        }


        public Builder destination(@NonNull Point destination) {
            this.destination = new NavigationRouteWaypoint(destination, null, null);
            return this;
        }


        public Builder destination(@NonNull Point destination, @Nullable Double angle,
                                   @Nullable Double tolerance) {
            this.destination = new NavigationRouteWaypoint(destination, angle, tolerance);
            return this;
        }


        public Builder addWaypoint(@NonNull Point waypoint) {
            this.waypoints.add(new NavigationRouteWaypoint(waypoint, null, null));
            return this;
        }


        public Builder addWaypoint(@NonNull Point waypoint, @Nullable Double angle,
                                   @Nullable Double tolerance) {
            this.waypoints.add(new NavigationRouteWaypoint(waypoint, angle, tolerance));
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            directionsBuilder.baseUrl(baseUrl);
            return this;
        }


        public Builder interceptor(Interceptor interceptor) {
            directionsBuilder.interceptor(interceptor);
            return this;
        }

        public Builder eventListener(EventListener eventListener) {
            directionsBuilder.eventListener(eventListener);
            return this;
        }

        public Builder setInterceptor(Interceptor interceptor) {
            directionsBuilder.interceptor(interceptor);
            return  this;
        }

        public NavigationRoute build() {
            assembleWaypoints();
            directionsBuilder.steps(1);
            return new NavigationRoute(directionsBuilder.build());
        }


        private void assembleWaypoints() {
            if (origin != null) {
                directionsBuilder.origin(origin.getWaypoint());
            }

            for (NavigationRouteWaypoint waypoint : waypoints) {
                directionsBuilder.addWaypoint(waypoint.getWaypoint());
            }

            if (destination != null) {
                directionsBuilder.destination(destination.getWaypoint());
            }
        }
    }
}