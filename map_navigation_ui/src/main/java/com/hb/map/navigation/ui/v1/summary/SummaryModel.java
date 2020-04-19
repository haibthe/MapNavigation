package com.hb.map.navigation.ui.v1.summary;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;

import com.hb.map.navigation.v1.navigation.NavigationTimeFormat;
import com.hb.map.navigation.v1.routeprogress.RouteProgress;
import com.hb.map.navigation.v1.utils.DistanceFormatter;

import java.util.Calendar;

import static com.hb.map.navigation.v1.utils.time.TimeFormatter.formatTime;
import static com.hb.map.navigation.v1.utils.time.TimeFormatter.formatTimeRemaining;

public class SummaryModel {

    private final String distanceRemaining;
    private final SpannableStringBuilder timeRemaining;
    private final String arrivalTime;

    public SummaryModel(Context context, DistanceFormatter distanceFormatter, RouteProgress progress,
                        @NavigationTimeFormat.Type int timeFormatType) {
        distanceRemaining = distanceFormatter.formatDistance(progress.distanceRemaining()).toString();
        double legDurationRemaining = progress.currentLegProgress().durationRemaining();
        timeRemaining = formatTimeRemaining(context, legDurationRemaining);
        Calendar time = Calendar.getInstance();
        boolean isTwentyFourHourFormat = DateFormat.is24HourFormat(context);
        arrivalTime = formatTime(time, legDurationRemaining, timeFormatType, isTwentyFourHourFormat);
    }

    String getDistanceRemaining() {
        return distanceRemaining;
    }

    SpannableStringBuilder getTimeRemaining() {
        return timeRemaining;
    }

    String getArrivalTime() {
        return arrivalTime;
    }
}
