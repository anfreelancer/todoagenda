package com.luteapp.todoagenda.task;

import com.luteapp.todoagenda.prefs.InstanceSettings;
import com.luteapp.todoagenda.prefs.OrderedEventSource;
import com.luteapp.todoagenda.util.StringUtil;
import com.luteapp.todoagenda.widget.WidgetEvent;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

public class TaskEvent implements WidgetEvent {

    private OrderedEventSource eventSource;
    private long id;
    private String title = "";
    private final InstanceSettings settings;
    private final DateTimeZone zone;
    private DateTime startDate;
    private boolean allDay = false;
    private DateTime dueDate;
    private int color;
    private TaskStatus status = TaskStatus.UNKNOWN;

    public TaskEvent(InstanceSettings settings, DateTimeZone zone) {
        this.settings = settings;
        this.zone = zone;
    }

    @Override
    public OrderedEventSource getEventSource() {
        return eventSource;
    }

    public TaskEvent setEventSource(OrderedEventSource eventSource) {
        this.eventSource = eventSource;
        return this;
    }

    @Override
    public long getEventId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = StringUtil.notNull(title);
    }

    public DateTime getStartDate() {
        return startDate;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public DateTime getDueDate() {
        return dueDate;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setDates(Long startMillis, Long dueMillis) {
        startDate = toStartDate(startMillis, dueMillis);
        dueDate = toDueDate(startMillis, dueMillis);
    }

    private DateTime toStartDate(Long startMillis, Long dueMillis) {
        return startMillis == null ? null : new DateTime(startMillis, zone);
    }

    private DateTime toDueDate(Long startMillis, Long dueMillis) {
        return dueMillis == null ? null : new DateTime(dueMillis, zone);
    }

    public boolean hasStartDate() {
        return startDate != null;
    }

    public boolean hasDueDate() {
        return dueDate != null;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }
}
