package com.hb.map.navigation.v1.utils.time;


import java.util.Calendar;

interface TimeFormatResolver {
    void nextChain(TimeFormatResolver chain);

    String obtainTimeFormatted(int type, Calendar time);
}