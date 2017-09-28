package com.academy.ndvalkov.mediamonitoringapp.common.events.main;

/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */

public class FilterActionHideEvent {
    public final boolean actionHide;

    public FilterActionHideEvent(boolean actionHide) {
        this.actionHide = actionHide;
    }
}