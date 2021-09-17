package com.luteapp.todoagenda.widget;

import com.luteapp.todoagenda.R;
import com.luteapp.todoagenda.prefs.InstanceSettings;
import com.luteapp.todoagenda.prefs.OrderedEventSource;
import com.luteapp.todoagenda.util.PermissionsUtil;
import org.joda.time.DateTime;

import java.util.List;

import static com.luteapp.todoagenda.widget.LastEntry.LastEntryType.EMPTY;
import static com.luteapp.todoagenda.widget.LastEntry.LastEntryType.NO_PERMISSIONS;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.LIST_FOOTER;

/** @author yvolk@yurivolkov.com */
public class LastEntry extends WidgetEntry<LastEntry> {

    public static LastEntry forEmptyList(InstanceSettings settings) {
        LastEntry.LastEntryType entryType = PermissionsUtil.arePermissionsGranted(settings.getContext())
                ? EMPTY
                : NO_PERMISSIONS;
        return new LastEntry(settings, entryType, settings.clock().now());
    }

    public static void addLast(InstanceSettings settings, List<WidgetEntry> widgetEntries) {
        LastEntry entry = widgetEntries.isEmpty()
            ? LastEntry.forEmptyList(settings)
            : new LastEntry(settings, LastEntryType.LAST, widgetEntries.get(widgetEntries.size() - 1).entryDate);
        widgetEntries.add(entry);
    }

    @Override
    public OrderedEventSource getSource() {
        return OrderedEventSource.LAST_ENTRY;
    }

    public enum LastEntryType {
        NOT_LOADED(R.layout.item_not_loaded),
        NO_PERMISSIONS(R.layout.item_no_permissions),
        EMPTY(R.layout.item_empty_list),
        LAST(R.layout.item_last);

        final int layoutId;

        LastEntryType(int layoutId) {
            this.layoutId = layoutId;
        }
    }

    public final LastEntryType type;

    public LastEntry(InstanceSettings settings, LastEntryType type, DateTime date) {
        super(settings, LIST_FOOTER, date, true, null);
        this.type = type;
    }
}