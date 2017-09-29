package com.academy.ndvalkov.mediamonitoringapp.common.events.monitor;

/**
 * An event object that will be passed as a result to
 * the subscribed methods from other classes and threads.
 */
public class OpenMonitorConfigEvent {
    public final boolean openMonitor;

    public OpenMonitorConfigEvent(boolean openMonitor) {
        this.openMonitor = openMonitor;
    }
}
