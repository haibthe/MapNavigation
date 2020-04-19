package com.hb.map.navigation.v1.milestone;


import com.hb.map.navigation.v1.routeprogress.RouteProgress;

public interface MilestoneEventListener {

    void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone milestone);

}
