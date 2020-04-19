package com.hb.map.navigation.ui.v1;

import android.view.View;

class RouteOverviewBtnClickListener implements View.OnClickListener {

    private NavigationPresenter presenter;

    RouteOverviewBtnClickListener(NavigationPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void onClick(View view) {
        presenter.onRouteOverviewClick();
    }
}
