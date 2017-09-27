package com.academy.ndvalkov.mediamonitoringapp.common.events;
/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */

public class UpdateSummaryEvent {
    public final boolean update;

    public UpdateSummaryEvent(boolean update) {
        this.update = update;
    }
}