package com.academy.ndvalkov.mediamonitoringapp.common.events.main;

/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */

public class NextActionHideEvent {
    public final boolean nextHide;

    public NextActionHideEvent(boolean nextHide) {
        this.nextHide = nextHide;
    }
}
