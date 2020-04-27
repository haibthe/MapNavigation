package com.hb.map.navigation.ui.v1.instruction;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.hb.map.navigation.ui.v1.NavigationViewModel;
import com.hb.map.navigation.ui.v1.alert.AlertView;

import timber.log.Timber;

public class NavigationAlertView extends AlertView {

    private static final long THREE_SECOND_DELAY_IN_MILLIS = 3000;
    private NavigationViewModel navigationViewModel;
    private boolean isEnabled = true;

    public NavigationAlertView(Context context) {
        this(context, null);
    }

    public NavigationAlertView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public NavigationAlertView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Sets the NavigationViewModel in the view
     *
     * @param navigationViewModel to set
     */
    public void subscribe(NavigationViewModel navigationViewModel) {
        this.navigationViewModel = navigationViewModel;
    }

    public void updateEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(view -> {
            hide();
        });
    }

    @Nullable
    private FragmentManager obtainSupportFragmentManager() {
        try {
            return ((FragmentActivity) getContext()).getSupportFragmentManager();
        } catch (ClassCastException exception) {
            Timber.e(exception);
            return null;
        }
    }
}
