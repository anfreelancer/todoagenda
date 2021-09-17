package com.luteapp.todoagenda.task;

import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;

import com.luteapp.todoagenda.provider.EventProvider;
import com.luteapp.todoagenda.widget.TaskEntry;
import com.luteapp.todoagenda.widget.WidgetEntry;
import com.luteapp.todoagenda.widget.WidgetEntryVisualizer;

import com.luteapp.todoagenda.R;

import java.util.ArrayList;
import java.util.List;

public class TaskVisualizer extends WidgetEntryVisualizer<TaskEntry> {

    public TaskVisualizer(EventProvider eventProvider) {
        super(eventProvider);
    }

    private AbstractTaskProvider getTaskProvider() {
        return (AbstractTaskProvider) super.eventProvider;
    }

    @Override
    @NonNull
    public RemoteViews getRemoteViews(WidgetEntry eventEntry, int position) {
        RemoteViews rv = super.getRemoteViews(eventEntry, position);

        TaskEntry entry = (TaskEntry) eventEntry;
        setIcon(entry, rv);
        return rv;
    }

    @Override
    public Intent newViewEntryIntent(WidgetEntry eventEntry) {
        TaskEntry entry = (TaskEntry) eventEntry;
        return getTaskProvider().newViewEventIntent(entry.getEvent());
    }

    private void setIcon(TaskEntry entry, RemoteViews rv) {
        if (getSettings().getShowEventIcon()) {
            rv.setViewVisibility(R.id.event_entry_icon, View.VISIBLE);
            rv.setTextColor(R.id.event_entry_icon, entry.getEvent().getColor());
        } else {
            rv.setViewVisibility(R.id.event_entry_icon, View.GONE);
        }
        rv.setViewVisibility(R.id.event_entry_color, View.GONE);
    }

    @Override
    public List<TaskEntry> queryEventEntries() {
        return toTaskEntryList(getTaskProvider().queryEvents());
    }

    private List<TaskEntry> toTaskEntryList(List<TaskEvent> events) {
        List<TaskEntry> entries = new ArrayList<>();
        for (TaskEvent event : events) {
            entries.add(TaskEntry.fromEvent(getSettings(), event));
        }
        return entries;
    }

}
