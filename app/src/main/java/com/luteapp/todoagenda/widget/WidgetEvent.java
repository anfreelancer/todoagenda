package com.luteapp.todoagenda.widget;

import com.luteapp.todoagenda.prefs.OrderedEventSource;

public interface WidgetEvent {
    OrderedEventSource getEventSource();
    long getEventId();
}
