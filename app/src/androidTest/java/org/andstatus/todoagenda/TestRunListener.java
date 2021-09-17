package com.luteapp.todoagenda;

import android.util.Log;

import com.luteapp.todoagenda.EnvironmentChangedReceiver;
import com.luteapp.todoagenda.prefs.AllSettings;
import com.luteapp.todoagenda.provider.EventProviderType;
import com.luteapp.todoagenda.provider.FakeCalendarContentProvider;
import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;

/**
 * @author yvolk@yurivolkov.com
 */
public class TestRunListener extends RunListener {
    private static final String TAG = "testSuite";

    public TestRunListener() {
        Log.i(TAG,  "TestRunListener created");
        EnvironmentChangedReceiver.sleep(5000);
    }

    @Override
    public void testSuiteFinished(Description description) throws Exception {
        super.testSuiteFinished(description);
        Log.i(TAG, "Test Suite finished: " + description);
        if (description.toString().equals("null")) restoreApp();
    }

    private void restoreApp() {
        Log.i(TAG, "On restore app");
        EnvironmentChangedReceiver.sleep(2000);

        FakeCalendarContentProvider.tearDown();

        AllSettings.forget();
        EventProviderType.forget();
        EnvironmentChangedReceiver.forget();

        Log.i(TAG, "App restored");
    }
}
