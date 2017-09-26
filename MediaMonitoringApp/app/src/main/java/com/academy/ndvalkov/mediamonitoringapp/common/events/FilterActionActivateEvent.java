package com.academy.ndvalkov.mediamonitoringapp.common.events;

/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */

public class FilterActionActivateEvent {
    public final boolean actionActivate;

    public FilterActionActivateEvent(boolean actionActivate) {
        this.actionActivate = actionActivate;
    }
}
