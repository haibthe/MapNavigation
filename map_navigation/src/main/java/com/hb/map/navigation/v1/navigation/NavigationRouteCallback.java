//package com.hb.map.navigation.v1.navigation;
//
//
//import androidx.annotation.NonNull;
//
//import com.mapbox.api.directions.v5.models.DirectionsResponse;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//class NavigationRouteCallback implements Callback<DirectionsResponse> {
//
//    private final Callback<DirectionsResponse> callback;
//
//    NavigationRouteCallback(NavigationRouteEventListener listener, Callback<DirectionsResponse> callback) {
//        this.callback = callback;
//    }
//
//    @Override
//    public void onResponse(@NonNull Call<DirectionsResponse> call, @NonNull Response<DirectionsResponse> response) {
//        callback.onResponse(call, response);
//    }
//
//    @Override
//    public void onFailure(@NonNull Call<DirectionsResponse> call, @NonNull Throwable throwable) {
//        callback.onFailure(call, throwable);
//    }
//
//    private boolean isValid(Response<DirectionsResponse> response) {
//        return response.body() != null && !response.body().routes().isEmpty();
//    }
//
//}