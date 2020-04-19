package com.hb.map.navigation.ui.v1.listeners;

import com.hb.map.navigation.ui.v1.feedback.FeedbackItem;

/**
 * A listener that can be implemented and
 * added to {@link com.hb.map.navigation.ui.v1.NavigationViewOptions} to
 * hook into feedback related events occurring in {@link com.hb.map.navigation.ui.v1.NavigationView}.
 */
public interface FeedbackListener {

    /**
     * Will be triggered when the feedback bottomsheet
     * is opened by a user while navigating.
     *
     * @since 0.8.0
     */
    void onFeedbackOpened();

    /**
     * Will be triggered when the feedback bottomsheet
     * is opened by a user while navigating but then dismissed
     * without clicking on a specific {@link FeedbackItem} in the list.
     *
     * @since 0.8.0
     */
    void onFeedbackCancelled();

    /**
     * Will be triggered when the feedback bottomsheet
     * is opened by a user while navigating and then the user
     * clicks on a specific {@link FeedbackItem} in the list.
     *
     * @param feedbackItem that was clicked
     * @since 0.8.0
     */
    void onFeedbackSent(FeedbackItem feedbackItem);
}
