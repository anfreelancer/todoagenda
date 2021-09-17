package com.luteapp.todoagenda.widget;

import android.content.Intent;

import com.luteapp.todoagenda.RemoteViewsFactory;
import com.luteapp.todoagenda.prefs.InstanceSettings;
import com.luteapp.todoagenda.prefs.OrderedEventSource;
import com.luteapp.todoagenda.prefs.dateformat.DateFormatType;
import com.luteapp.todoagenda.util.DateUtil;
import com.luteapp.todoagenda.util.MyClock;
import org.joda.time.DateTime;

import java.util.concurrent.atomic.AtomicLong;

import static com.luteapp.todoagenda.util.DateUtil.isSameDate;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.DAY_HEADER;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.END_OF_LIST;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.ENTRY_DATE;
import static com.luteapp.todoagenda.widget.WidgetEntryPosition.PAST_AND_DUE;

public abstract class WidgetEntry<T extends WidgetEntry<T>> implements Comparable<WidgetEntry<T>> {
    public static final String EXTRA_WIDGET_ENTRY_ID = RemoteViewsFactory.PACKAGE + ".extra.WIDGET_ENTRY_ID";
    private final static AtomicLong idGenerator = new AtomicLong(0);
    protected final InstanceSettings settings;
    public final long entryId = idGenerator.incrementAndGet();
    public final WidgetEntryPosition entryPosition;
    public final DateTime entryDate;
    public final DateTime entryDay;
    public final boolean allDay;
    public final DateTime endDate;
    public final TimeSection timeSection;

    protected WidgetEntry(InstanceSettings settings, WidgetEntryPosition entryPosition, DateTime entryDate, boolean allDay, DateTime endDate) {
        this.settings = settings;
        this.entryPosition = entryPosition;
        this.entryDate = fixEntryDate(entryPosition, entryDate);
        entryDay = calcEntryDay(settings, entryPosition, this.entryDate);
        this.allDay = allDay;
        this.endDate = endDate;
        timeSection = calcTimeSection(settings, entryPosition, entryDay, endDate);
    }

    private static DateTime fixEntryDate(WidgetEntryPosition entryPosition, DateTime entryDate) {
        switch (entryPosition) {
            case ENTRY_DATE:
                throwIfNull(entryPosition, entryDate);
                return entryDate;
            case PAST_AND_DUE_HEADER:
            case PAST_AND_DUE:
            case START_OF_TODAY:
            case HIDDEN:
                return entryDate == null
                        ? MyClock.DATETIME_MIN
                        : entryDate;
            case DAY_HEADER:
            case START_OF_DAY:
                throwIfNull(entryPosition, entryDate);
                return entryDate.withTimeAtStartOfDay();
            case END_OF_DAY:
                throwIfNull(entryPosition, entryDate);
                return entryDate.withTimeAtStartOfDay().plusDays(1).minusMillis(1);
            case END_OF_TODAY:
            case END_OF_LIST_HEADER:
            case END_OF_LIST:
            case LIST_FOOTER:
                return entryDate == null
                        ? MyClock.DATETIME_MAX
                        : entryDate;
            default:
                throw new IllegalArgumentException("Invalid position " + entryPosition + "; entryDate: " + entryDate);
        }
    }

    private static DateTime calcEntryDay(InstanceSettings settings, WidgetEntryPosition entryPosition, DateTime entryDate) {
        switch (entryPosition) {
            case START_OF_TODAY:
            case END_OF_TODAY:
                return settings.clock().now().withTimeAtStartOfDay();
            default:
                return entryDate.withTimeAtStartOfDay();
        }
    }

    private static TimeSection calcTimeSection(InstanceSettings settings, WidgetEntryPosition entryPosition,
                                               DateTime entryDay, DateTime endDate) {
        switch (entryPosition) {
            case PAST_AND_DUE_HEADER:
                return TimeSection.PAST;
            case START_OF_TODAY:
                return TimeSection.TODAY;
            case END_OF_TODAY:
            case END_OF_LIST_HEADER:
            case END_OF_LIST:
            case LIST_FOOTER:
                return TimeSection.FUTURE;
            default:
                break;
        }
        if (settings.clock().isToday(entryDay)) {
            if (entryPosition == DAY_HEADER) return TimeSection.TODAY;

            if (settings.clock().isToday(endDate)) {
                return settings.clock().isBeforeNow(endDate)
                        ? TimeSection.PAST
                        : TimeSection.TODAY;
            }
            return TimeSection.TODAY;
        }
        return settings.clock().isBeforeToday(entryDay)
                ? TimeSection.PAST
                : (settings.clock().isToday(endDate) ? TimeSection.TODAY : TimeSection.FUTURE);
    }

    private static void throwIfNull(WidgetEntryPosition entryPosition, DateTime entryDate) {
        if (entryDate == null) {
            throw new IllegalArgumentException("Invalid entry date: " + entryDate + " at position " + entryPosition);
        }
    }

    public boolean isLastEntryOfEvent() {
        return endDate == null ||
                !entryPosition.entryDateIsRequired ||
                endDate.isBefore(MyClock.startOfNextDay(this.entryDate));
    }

    public static WidgetEntryPosition getEntryPosition(InstanceSettings settings, boolean allDay, DateTime mainDate, DateTime otherDate) {
        if (mainDate == null && otherDate == null) return settings.getTaskWithoutDates().widgetEntryPosition;

        DateTime refDate = mainDate == null ? otherDate : mainDate;
        if (settings.getShowPastEventsUnderOneHeader() && settings.clock().isBeforeToday(refDate)) {
            return PAST_AND_DUE;
        }
        if (refDate.isAfter(settings.getEndOfTimeRange())) return END_OF_LIST;
        if (allDay) return settings.getAllDayEventsPlacement().widgetEntryPosition;
        return ENTRY_DATE;
    }

    public String getEventTimeString() {
        return "";
    }

    public abstract OrderedEventSource getSource();

    public String getTitle() {
        return "";
    }

    String getLocationString() {
        return hideLocation() ? "" : getLocation();
    }

    private boolean hideLocation() {
        return getLocation().isEmpty() || !settings.getShowLocation();
    }

    public String getLocation() {
        return "";
    }

    @Override
    public int compareTo(WidgetEntry other) {
        int globalSignum = Integer.signum(entryPosition.globalOrder - other.entryPosition.globalOrder);
        if (globalSignum != 0) return globalSignum;

        if (DateUtil.isSameDay(entryDay, other.entryDay)) {
            int sameDaySignum = Integer.signum(entryPosition.sameDayOrder - other.entryPosition.sameDayOrder);
            if ((sameDaySignum != 0) && DateUtil.isSameDay(entryDay, other.entryDay)) return sameDaySignum;

            if (entryDate.isAfter(other.entryDate)) {
                return 1;
            } else if (entryDate.isBefore(other.entryDate)) {
                return -1;
            }
        } else {
            if (entryDay.isAfter(other.entryDay)) {
                return 1;
            } else if (entryDay.isBefore(other.entryDay)) {
                return -1;
            }
        }

        int sourceSignum = Integer.signum(getSource().order - other.getSource().order);
        return sourceSignum == 0
                ? getTitle().compareTo(other.getTitle())
                : sourceSignum;
    }

    public boolean duplicates(WidgetEntry other) {
        return entryPosition == other.entryPosition &&
            entryDate.equals(other.entryDate) &&
            isSameDate(endDate, other.endDate) &&
            getTitle().equals(other.getTitle()) &&
            getLocation().equals(other.getLocation());
    }

    public CharSequence formatEntryDate() {
        return settings.getEntryDateFormat().type == DateFormatType.HIDDEN || !MyClock.isDateDefined(entryDate)
                ? ""
                : settings.entryDateFormatter().formatDate(entryDate);
    }

    public Intent newOnClickFillInIntent() {
        return new Intent().putExtra(EXTRA_WIDGET_ENTRY_ID, entryId);
    }

    @Override
    public String toString() {
        return entryPosition.value + " [" +
                "entryDate=" +
                (entryDate == MyClock.DATETIME_MIN ? "min" :
                        (entryDate == MyClock.DATETIME_MAX) ? "max" : entryDate) +
                ", endDate=" + endDate +
                (allDay ? ", allDay" : "") +
            "]";
    }

    public WidgetEvent getEvent() {
        return null;
    }

    public boolean notHidden() {
        return entryPosition != WidgetEntryPosition.HIDDEN;
    }
}
