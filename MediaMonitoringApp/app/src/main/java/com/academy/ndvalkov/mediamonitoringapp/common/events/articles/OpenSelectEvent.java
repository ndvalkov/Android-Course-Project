package com.academy.ndvalkov.mediamonitoringapp.common.events.articles;

/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */
public class OpenSelectEvent {
    public final boolean select;

    public OpenSelectEvent(boolean select) {
        this.select = select;
    }
}