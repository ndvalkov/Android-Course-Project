package com.academy.ndvalkov.mediamonitoringapp.common.events;


/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */
public class FilterEvent {
    public final boolean openFilter;

    public FilterEvent(boolean openFilter) {
        this.openFilter = openFilter;
    }
}
