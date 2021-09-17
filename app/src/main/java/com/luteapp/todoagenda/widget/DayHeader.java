package com.luteapp.todoagenda.widget;

import com.luteapp.todoagenda.prefs.InstanceSettings;
import com.luteapp.todoagenda.prefs.OrderedEventSource;
import org.joda.time.DateTime;

public class DayHeader extends WidgetEntry<DayHeader> {

    public DayHeader(InstanceSettings settings, WidgetEntryPosition entryPosition, DateTime date) {
        super(settings, entryPosition, date, true, null);
    }

    @Override
    public OrderedEventSource getSource() {
        return OrderedEventSource.DAY_HEADER;
    }
}
