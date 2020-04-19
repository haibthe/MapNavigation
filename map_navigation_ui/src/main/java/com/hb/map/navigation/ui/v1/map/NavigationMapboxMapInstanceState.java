package com.hb.map.navigation.ui.v1.map;

import android.os.Parcel;
import android.os.Parcelable;

public class NavigationMapboxMapInstanceState implements Parcelable {

    public static final Creator<NavigationMapboxMapInstanceState> CREATOR =
            new Creator<NavigationMapboxMapInstanceState>() {
                @Override
                public NavigationMapboxMapInstanceState createFromParcel(Parcel in) {
                    return new NavigationMapboxMapInstanceState(in);
                }

                @Override
                public NavigationMapboxMapInstanceState[] newArray(int size) {
                    return new NavigationMapboxMapInstanceState[size];
                }
            };
    private final NavigationMapSettings settings;

    NavigationMapboxMapInstanceState(NavigationMapSettings settings) {
        this.settings = settings;
    }

    private NavigationMapboxMapInstanceState(Parcel in) {
        settings = in.readParcelable(NavigationMapSettings.class.getClassLoader());
    }

    NavigationMapSettings retrieveSettings() {
        return settings;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(settings, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
