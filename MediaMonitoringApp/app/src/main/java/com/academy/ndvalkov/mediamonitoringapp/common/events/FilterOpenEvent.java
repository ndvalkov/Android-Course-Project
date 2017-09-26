package com.academy.ndvalkov.mediamonitoringapp.common.events;

/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */
public class FilterOpenEvent {
    public final boolean openFilter;

    public FilterOpenEvent(boolean openFilter) {
        this.openFilter = openFilter;
    }
}
