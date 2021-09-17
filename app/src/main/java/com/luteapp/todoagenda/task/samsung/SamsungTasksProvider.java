package com.luteapp.todoagenda.task.samsung;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;

import com.luteapp.todoagenda.prefs.EventSource;
import com.luteapp.todoagenda.prefs.FilterMode;
import com.luteapp.todoagenda.prefs.OrderedEventSource;
import com.luteapp.todoagenda.provider.EventProviderType;
import com.luteapp.todoagenda.util.IntentUtil;

import com.luteapp.todoagenda.R;

import com.luteapp.todoagenda.task.AbstractTaskProvider;
import com.luteapp.todoagenda.task.TaskEvent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.vavr.control.Try;

public class SamsungTasksProvider extends AbstractTaskProvider {
    private static final String TAG = SamsungTasksProvider.class.getSimpleName();

    // TODO: Check if the below Intent is correct
    private static final Intent ADD_TASK_INTENT = IntentUtil.newViewIntent()
            .setData(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, 0));

    public SamsungTasksProvider(EventProviderType type, Context context, int widgetId) {
        super(type, context, widgetId);
    }

    @Override
    public List<TaskEvent> queryTasks() {
        myContentResolver.onQueryEvents();

        Uri uri = SamsungTasksContract.Tasks.PROVIDER_URI;
        String[] projection = {
                SamsungTasksContract.Tasks.COLUMN_ID,
                SamsungTasksContract.Tasks.COLUMN_TITLE,
                SamsungTasksContract.Tasks.COLUMN_DUE_DATE,
                SamsungTasksContract.Tasks.COLUMN_COLOR,
                SamsungTasksContract.Tasks.COLUMN_LIST_ID,
        };
        String where = getWhereClause();

        return myContentResolver.foldEvents(uri, projection, where, null, null,
                new ArrayList<>(), tasks -> cursor -> {
                    TaskEvent task = newTask(cursor);
                    if (matchedFilter(task)) {
                        tasks.add(task);
                    }
                    return tasks;
                });
    }

    private String getWhereClause() {
        StringBuilder whereBuilder = new StringBuilder();
        whereBuilder.append(SamsungTasksContract.Tasks.COLUMN_DELETED).append(EQUALS).append("0");

        if (getFilterMode() == FilterMode.NORMAL_FILTER) {
            whereBuilder.append(AND).append(SamsungTasksContract.Tasks.COLUMN_COMPLETE).append(EQUALS).append("0");
        }

        Set<String> taskLists = new HashSet<>();
        for (OrderedEventSource orderedSource: getSettings().getActiveEventSources(type)) {
            taskLists.add(Integer.toString(orderedSource.source.getId()));
        }
        if (!taskLists.isEmpty()) {
            whereBuilder.append(AND);
            whereBuilder.append(SamsungTasksContract.Tasks.COLUMN_LIST_ID);
            whereBuilder.append(" IN ( ");
            whereBuilder.append(TextUtils.join(",", taskLists));
            whereBuilder.append(CLOSING_BRACKET);
        }

        return whereBuilder.toString();
    }

    private TaskEvent newTask(Cursor cursor) {
        OrderedEventSource source = getSettings()
                .getActiveEventSource(type,
                        cursor.getInt(cursor.getColumnIndex(SamsungTasksContract.Tasks.COLUMN_LIST_ID)));
        TaskEvent task = new TaskEvent(getSettings(), getSettings().clock().getZone());
        task.setEventSource(source);
        task.setId(cursor.getLong(cursor.getColumnIndex(SamsungTasksContract.Tasks.COLUMN_ID)));
        task.setTitle(cursor.getString(cursor.getColumnIndex(SamsungTasksContract.Tasks.COLUMN_TITLE)));

        Long dueMillis = getPositiveLongOrNull(cursor, SamsungTasksContract.Tasks.COLUMN_DUE_DATE);
        task.setDates(null, dueMillis);

        task.setColor(getColor(cursor, cursor.getColumnIndex(SamsungTasksContract.Tasks.COLUMN_COLOR),
                cursor.getInt(cursor.getColumnIndex(SamsungTasksContract.Tasks.COLUMN_LIST_ID))));

        return task;
    }

    @Override
    public Try<List<EventSource>> fetchAvailableSources() {
        String[] projection = {
                SamsungTasksContract.TaskLists.COLUMN_ID,
                SamsungTasksContract.TaskLists.COLUMN_NAME,
                SamsungTasksContract.TaskLists.COLUMN_COLOR,
        };
        String taskListName = context.getResources().getString(R.string.task_source_samsung);
        return myContentResolver.foldAvailableSources(
                SamsungTasksContract.TaskLists.PROVIDER_URI,
                projection,
                new ArrayList<>(),
                eventSources -> cursor -> {
                    int indId = cursor.getColumnIndex(SamsungTasksContract.TaskLists.COLUMN_ID);
                    int indSummary = cursor.getColumnIndex(SamsungTasksContract.TaskLists.COLUMN_NAME);
                    int indColor = cursor.getColumnIndex(SamsungTasksContract.TaskLists.COLUMN_COLOR);
                    int id = cursor.getInt(indId);
                    EventSource source = new EventSource(type, id, taskListName,
                            cursor.getString(indSummary), getColor(cursor, indColor, id), true);
                    eventSources.add(source);
                    return eventSources;
                });
    }

    @Override
    public Intent newViewEventIntent(TaskEvent event) {
        return IntentUtil.newViewIntent()
            .setData(ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, event.getEventId()))
            .putExtra(SamsungTasksContract.INTENT_EXTRA_TASK, true)
            .putExtra(SamsungTasksContract.INTENT_EXTRA_SELECTED, event.getEventId())
            .putExtra(SamsungTasksContract.INTENT_EXTRA_ACTION_VIEW_FOCUS, 0)
            .putExtra(SamsungTasksContract.INTENT_EXTRA_DETAIL_MODE, true)
            .putExtra(SamsungTasksContract.INTENT_EXTRA_LAUNCH_FROM_WIDGET, true);
    }

    private int getColor(Cursor cursor, int colorIdx, int accountId) {
        if (!cursor.isNull(colorIdx)) {
            return getAsOpaque(cursor.getInt(colorIdx));
        } else {
            int[] fixedColors = context.getResources().getIntArray(R.array.task_list_colors);
            int arrayIdx = accountId % fixedColors.length;
            return fixedColors[arrayIdx];
        }
    }

    @Override
    public Intent getAddEventIntent() {
        return ADD_TASK_INTENT;
    }
}
