package com.hb.map.navigation.ui.v1;

import android.view.View;

public interface NavigationButton {

    /**
     * Adds an onClickListener to the button
     *
     * @param onClickListener to add
     */
    void addOnClickListener(View.OnClickListener onClickListener);

    /**
     * Removes an onClickListener from the button
     *
     * @param onClickListener to remove
     */
    void removeOnClickListener(View.OnClickListener onClickListener);

    /**
     * Hides the button
     */
    void hide();

    /**
     * Shows the button
     */
    void show();
}
