package com.luteapp.todoagenda;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.util.Log;

import com.luteapp.todoagenda.prefs.AllSettings;

public class RemoteViewsService extends android.widget.RemoteViewsService {
    private static final String TAG = RemoteViewsService.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate");
        AllSettings.ensureLoadedFromFiles(this, false);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int widgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, 0);
        Log.d(TAG, widgetId + " onGetViewFactory, intent:" + intent);
        com.luteapp.todoagenda.RemoteViewsFactory factory = new com.luteapp.todoagenda.RemoteViewsFactory(this, widgetId, true);
        com.luteapp.todoagenda.RemoteViewsFactory.factories.put(widgetId, factory);
        return factory;
    }
}