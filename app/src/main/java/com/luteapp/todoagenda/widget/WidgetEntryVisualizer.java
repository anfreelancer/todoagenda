package com.luteapp.todoagenda.widget;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.luteapp.todoagenda.provider.EventProvider;

import com.luteapp.todoagenda.R;
import com.luteapp.todoagenda.prefs.InstanceSettings;
import com.luteapp.todoagenda.prefs.colors.TextColorPref;
import com.luteapp.todoagenda.prefs.dateformat.DateFormatType;
import com.luteapp.todoagenda.util.MyStringBuilder;
import com.luteapp.todoagenda.util.RemoteViewsUtil;

import java.util.List;

import static com.luteapp.todoagenda.util.RemoteViewsUtil.setBackgroundColor;
import static com.luteapp.todoagenda.util.RemoteViewsUtil.setMultiline;
import static com.luteapp.todoagenda.util.RemoteViewsUtil.setTextColor;
import static com.luteapp.todoagenda.util.RemoteViewsUtil.setTextSize;
import static com.luteapp.todoagenda.util.RemoteViewsUtil.setViewWidth;

public abstract class WidgetEntryVisualizer<T extends WidgetEntry<T>> {
    protected final EventProvider eventProvider;

    public WidgetEntryVisualizer(EventProvider eventProvider) {
        this.eventProvider = eventProvider;
    }

    @NonNull
    public RemoteViews getRemoteViews(WidgetEntry entry, int position) {
        RemoteViews rv = new RemoteViews(getContext().getPackageName(), getSettings().getEventEntryLayout().layoutId);
        setTitle(entry, rv);
        setDetails(entry, rv);
        setDate(entry, rv);
        setTime(entry, rv);

        setIndicators(entry, rv);
        if (getSettings().isCompactLayout()) {
            RemoteViewsUtil.setPadding(getSettings(), rv, R.id.event_entry, R.dimen.zero, R.dimen.zero, R.dimen.zero, R.dimen.zero);
        } else {
            RemoteViewsUtil.setPadding(getSettings(), rv, R.id.event_entry, R.dimen.calender_padding, R.dimen.zero, R.dimen.calender_padding, R.dimen.entry_bottom_padding);
        }
        setBackgroundColor(rv, R.id.event_entry, getSettings().colors().getEntryBackgroundColor(entry));
        return rv;
    }

    protected void setIndicators(WidgetEntry entry, RemoteViews rv) {
        for (AlarmIndicatorScaled indicator : AlarmIndicatorScaled.values()) {
            rv.setViewVisibility(indicator.indicatorResId, View.GONE);
        }
        for (RecurringIndicatorScaled indicator : RecurringIndicatorScaled.values()) {
            rv.setViewVisibility(indicator.indicatorResId, View.GONE);
        }
    }

    @NonNull
    protected InstanceSettings getSettings() {
        return eventProvider.getSettings();
    }

    public Context getContext() {
        return eventProvider.context;
    }

    public abstract List<T> queryEventEntries();

    protected void setTitle(WidgetEntry entry, RemoteViews rv) {
        int viewId = R.id.event_entry_title;
        rv.setTextViewText(viewId, getTitleString(entry));
        setTextSize(getSettings(), rv, viewId, R.dimen.event_entry_title);
        setTextColor(getSettings(), TextColorPref.forTitle(entry), rv, viewId, R.attr.eventEntryTitle);
        setMultiline(rv, viewId, getSettings().isMultilineTitle());
    }

    protected CharSequence getTitleString(WidgetEntry event) {
        return getSettings().getEventEntryLayout() == EventEntryLayout.DEFAULT
            ? event.getTitle()
            : MyStringBuilder.of(event.getTitle())
                .withSeparator(event.getLocationString(), EventEntryLayout.SPACE_PIPE_SPACE);
    }

    protected void setDetails(WidgetEntry entry, RemoteViews rv) {
        if (getSettings().getEventEntryLayout() == EventEntryLayout.ONE_LINE) return;

        MyStringBuilder eventDetails = MyStringBuilder
                .of(entry.formatEntryDate())
                .withSpace(entry.getEventTimeString())
                .withSeparator(entry.getLocationString(), EventEntryLayout.SPACE_PIPE_SPACE);
        int viewId = R.id.event_entry_details;
        if (eventDetails.isEmpty()) {
            rv.setViewVisibility(viewId, View.GONE);
        } else {
            rv.setViewVisibility(viewId, View.VISIBLE);
            rv.setTextViewText(viewId, eventDetails);
            setTextSize(getSettings(), rv, viewId, R.dimen.event_entry_details);
            setTextColor(getSettings(), TextColorPref.forDetails(entry), rv, viewId, R.attr.dayHeaderTitle);
            setMultiline(rv, viewId, getSettings().isMultilineDetails());
        }
    }

    protected void setDate(WidgetEntry entry, RemoteViews rv) {
        if (getSettings().getEventEntryLayout() == EventEntryLayout.DEFAULT) return;

        if (getSettings().getEntryDateFormat().type == DateFormatType.HIDDEN) {
            rv.setViewVisibility(R.id.event_entry_days, View.GONE);
            rv.setViewVisibility(R.id.event_entry_days_right, View.GONE);
        } else {
            int days = getSettings().clock().getNumberOfDaysTo(entry.entryDate);
            boolean daysAsText = getSettings().getEntryDateFormat().type != DateFormatType.NUMBER_OF_DAYS ||
                    (days > -2 && days < 2);

            int viewToShow = daysAsText ? R.id.event_entry_days : R.id.event_entry_days_right;
            int viewToHide = daysAsText ? R.id.event_entry_days_right : R.id.event_entry_days;
            rv.setViewVisibility(viewToHide, View.GONE);
            rv.setViewVisibility(viewToShow, View.VISIBLE);

            rv.setTextViewText(viewToShow, entry.formatEntryDate());
            setViewWidth(getSettings(), rv, viewToShow, daysAsText
                    ? R.dimen.days_to_event_width
                    : R.dimen.days_to_event_right_width);
            setTextSize(getSettings(), rv, viewToShow, R.dimen.event_entry_details);
            setTextColor(getSettings(), TextColorPref.forDetails(entry), rv, viewToShow, R.attr.dayHeaderTitle);
        }
    }

    protected void setTime(WidgetEntry entry, RemoteViews rv) {
        if (getSettings().getEventEntryLayout() == EventEntryLayout.DEFAULT) return;

        int viewId = R.id.event_entry_time;
        RemoteViewsUtil.setMultiline(rv, viewId, getSettings().getShowEndTime());
        rv.setTextViewText(viewId, entry.getEventTimeString().replace(CalendarEntry
                .SPACE_DASH_SPACE, "\n"));
        setViewWidth(getSettings(), rv, viewId, R.dimen.event_time_width);
        setTextSize(getSettings(), rv, viewId, R.dimen.event_entry_details);
        setTextColor(getSettings(), TextColorPref.forDetails(entry), rv, viewId, R.attr.dayHeaderTitle);
    }

    public boolean isFor(WidgetEntry entry) {
        return entry.getSource().source.providerType == eventProvider.type;
    }

    public Intent newViewEntryIntent(WidgetEntry entry) {
        return null;
    }
}