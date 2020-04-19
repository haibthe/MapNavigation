# Consumer proguard rules for libandroid-navigation-ui

# --- Picasso ---
-dontwarn com.squareup.okhttp.**

# --- com.mapbox.api.directions.v5.MapboxDirections ---
-dontwarn com.sun.xml.internal.ws.spi.db.BindingContextFactory

# --- com.amazonaws.util.json.JacksonFactory ---
-dontwarn com.fasterxml.jackson.core.**

# --- Mapbox ---
-dontwarn com.hb.map.navigation.ui.v1.**

# Temporarily adding this explicitly here as we were seeing Maps events being obfuscated
# --- Maps SDK Telemetry ---
-keep class com.mapbox.mapboxsdk.module.telemetry.**
