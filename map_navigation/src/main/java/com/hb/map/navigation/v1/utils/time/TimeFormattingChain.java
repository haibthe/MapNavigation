package com.hb.map.navigation.v1.utils.time;


class TimeFormattingChain {

    TimeFormatResolver setup(boolean isDeviceTwentyFourHourFormat) {
        TimeFormatResolver noneSpecified = new NoneSpecifiedTimeFormat(isDeviceTwentyFourHourFormat);
        TimeFormatResolver twentyFourHours = new TwentyFourHoursTimeFormat();
        twentyFourHours.nextChain(noneSpecified);
        TimeFormatResolver rootOfTheChain = new TwelveHoursTimeFormat();
        rootOfTheChain.nextChain(twentyFourHours);

        return rootOfTheChain;
    }
}
