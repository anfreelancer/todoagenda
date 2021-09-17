package com.luteapp.todoagenda.widget;

import com.luteapp.todoagenda.prefs.InstanceSettings;
import com.luteapp.todoagenda.prefs.OrderedEventSource;
import com.luteapp.todoagenda.prefs.TaskScheduling;
import com.luteapp.todoagenda.task.TaskEvent;
import com.luteapp.todoagenda.util.DateUtil;
import com.luteapp.todoagenda.util.MyClock;
import org.joda.time.DateTime;

import static com.luteapp.todoagenda.widget.WidgetEntryPosition.END_OF_LIST;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.ENTRY_DATE;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.START_OF_TODAY;

public class TaskEntry extends WidgetEntry<TaskEntry> {
    private TaskEvent event;
    private final DateTime mainDate;

    public static TaskEntry fromEvent(InstanceSettings settings, TaskEvent event) {
        DateTime mainDate = calcMainDate(settings, event);
        WidgetEntryPosition entryPosition = getEntryPosition(settings, mainDate, event);
        return new TaskEntry(settings, entryPosition, mainDate, getEntryDate(settings, entryPosition, event), event);
    }

    private TaskEntry(InstanceSettings settings, WidgetEntryPosition entryPosition, DateTime mainDate,
                      DateTime entryDate, TaskEvent event) {
        super(settings, entryPosition, entryDate, event.isAllDay(), event.getDueDate());
        this.event = event;
        this.mainDate = mainDate;
    }

    /** See https://github.com/plusonelabs/calendar-widget/issues/356#issuecomment-559910887 **/
    private static WidgetEntryPosition getEntryPosition(InstanceSettings settings, DateTime mainDate, TaskEvent event) {
        if (!event.hasStartDate() && !event.hasDueDate()) return settings.getTaskWithoutDates().widgetEntryPosition;

        if (mainDate != null) {
            if (mainDate.isAfter(settings.getEndOfTimeRange())) return END_OF_LIST;
        }

        DateTime otherDate = otherDate(settings, event);

        if (settings.getTaskScheduling() == TaskScheduling.DATE_DUE) {
            if (!event.hasDueDate()) {
                if (settings.clock().isBeforeToday(event.getStartDate())) return START_OF_TODAY;
                if (event.getStartDate().isAfter(settings.getEndOfTimeRange())) return END_OF_LIST;
            }
        } else {
            if (!event.hasStartDate() || settings.clock().isBeforeToday(event.getStartDate())) {
                if (!settings.clock().isBeforeToday(event.getDueDate())) return START_OF_TODAY;
            }
        }
        return WidgetEntry.getEntryPosition(settings, event.isAllDay(), mainDate, otherDate);
    }

    private static DateTime calcMainDate(InstanceSettings settings, TaskEvent event) {
        return settings.getTaskScheduling() == TaskScheduling.DATE_DUE
                ? event.getDueDate()
                : event.getStartDate();
    }

    private static DateTime otherDate(InstanceSettings settings, TaskEvent event) {
        return settings.getTaskScheduling() == TaskScheduling.DATE_DUE
                ? event.getStartDate()
                : event.getDueDate();
    }

    private static DateTime getEntryDate(InstanceSettings settings, WidgetEntryPosition entryPosition, TaskEvent event) {
        switch (entryPosition) {
            case END_OF_TODAY:
            case END_OF_DAY:
            case END_OF_LIST:
            case END_OF_LIST_HEADER:
            case LIST_FOOTER:
            case HIDDEN:
                return getEntryDateOrElse(settings, event, MyClock.DATETIME_MAX);
            default:
                return getEntryDateOrElse(settings, event, MyClock.DATETIME_MIN);
        }
    }

    private static DateTime getEntryDateOrElse(InstanceSettings settings, TaskEvent event, DateTime defaultDate) {
        if (settings.getTaskScheduling() == TaskScheduling.DATE_DUE) {
            return event.hasDueDate()
                    ? event.getDueDate()
                    : (event.hasStartDate() ? event.getStartDate() : defaultDate);
        } else {
            if (event.hasStartDate()) {
                if (settings.clock().isBeforeToday(event.getStartDate())) {
                    return event.hasDueDate() ? event.getDueDate() : defaultDate;
                }
                return event.getStartDate();
            } else {
                return event.hasDueDate() ? event.getDueDate() : defaultDate;
            }
        }
    }

    @Override
    public OrderedEventSource getSource() {
        return event.getEventSource();
    }

    @Override
    public String getTitle() {
        return event.getTitle();
    }

    public TaskEvent getEvent() {
        return event;
    }

    @Override
    public String getEventTimeString() {
        return allDay || entryPosition != ENTRY_DATE
                ? ""
                : DateUtil.formatTime(() -> settings, mainDate);
    }

    @Override
    public String toString() {
        return super.toString() + " TaskEntry [title='" + event.getTitle() + "', startDate=" + event.getStartDate() +
                ", dueDate=" + event.getDueDate() + "]";
    }
}
