package com.luteapp.todoagenda.prefs;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.luteapp.todoagenda.prefs.dateformat.DateFormatType;
import com.luteapp.todoagenda.prefs.dateformat.DateFormatter;
import com.luteapp.todoagenda.MainActivity;
import com.luteapp.todoagenda.R;

import com.luteapp.todoagenda.provider.QueryResultsStorage;
import com.luteapp.todoagenda.util.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.TimeZone;

public class OtherPreferencesFragment extends MyPreferenceFragment
        implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_other);
   }

    @Override
    public void onResume() {
        super.onResume();
        showWidgetInstanceName();

        showSnapshotMode();
        setLockTimeZone();
        showLockTimeZone();

        showRefreshPeriod();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    private void setLockTimeZone() {
        CheckBoxPreference preference = (CheckBoxPreference) findPreference(InstanceSettings.PREF_LOCK_TIME_ZONE);
        if (preference != null) {
            SnapshotMode snapshotMode = ApplicationPreferences.getSnapshotMode(getActivity());
             boolean isChecked = snapshotMode == SnapshotMode.SNAPSHOT_TIME ||
                     ApplicationPreferences.isTimeZoneLocked(getActivity());
            if (preference.isChecked() != isChecked) {
                preference.setChecked(isChecked);
            }
        }
    }

    private void showLockTimeZone() {
        CheckBoxPreference preference = findPreference(InstanceSettings.PREF_LOCK_TIME_ZONE);
        if (preference == null) return;

        SnapshotMode snapshotMode = ApplicationPreferences.getSnapshotMode(getActivity());
        preference.setEnabled(snapshotMode != SnapshotMode.SNAPSHOT_TIME);

        DateTimeZone timeZone = getSettings().clock().getZone();
        preference.setSummary(String.format(
                getText(preference.isChecked() ? R.string.lock_time_zone_on_desc : R.string.lock_time_zone_off_desc).toString(),
                timeZone.getName(DateTime.now(timeZone).getMillis()))
        );
    }

    private void showSnapshotMode() {
        ListPreference preference = findPreference(InstanceSettings.PREF_SNAPSHOT_MODE);
        if (preference == null) return;

        InstanceSettings settings = getSettings();

        CharSequence[] entries = {
            getText(R.string.snapshot_mode_live_data),
            formatSnapshotModeSummary(settings, R.string.snapshot_mode_time),
            formatSnapshotModeSummary(settings, R.string.snapshot_mode_now)
        };
        preference.setEntries(entries);

        SnapshotMode snapshotMode = ApplicationPreferences.getSnapshotMode(getActivity());
        if (snapshotMode.isSnapshotMode()) {
            preference.setSummary(formatSnapshotModeSummary(settings, snapshotMode.valueResId));
        } else {
            preference.setSummary(snapshotMode.valueResId);
        }
    }

    private String formatSnapshotModeSummary(InstanceSettings settings, int valueResId) {
        CharSequence snapshotDateString = settings.hasResults()
                ? new DateFormatter(settings.getContext(), DateFormatType.DEFAULT_WEEKDAY.defaultValue(),
                    settings.clock().now()).formatDate(settings.getResultsStorage().getExecutedAt()) +
                    " " + DateUtil.formatTime(this::getSettings, settings.getResultsStorage().getExecutedAt())
                : "...";
        return String.format(getText(valueResId).toString(), snapshotDateString);
    }

    private void showRefreshPeriod() {
        EditTextPreference preference = (EditTextPreference) findPreference(InstanceSettings.PREF_REFRESH_PERIOD_MINUTES);

        if (preference != null) {
            int value = ApplicationPreferences.getRefreshPeriodMinutes(getActivity());
            preference.setSummary(String.format(getText(R.string.refresh_period_minutes_desc).toString(), value));
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        switch (preference.getKey()) {
            case InstanceSettings.PREF_LOCK_TIME_ZONE:
                if (preference instanceof CheckBoxPreference) {
                    CheckBoxPreference checkPref = (CheckBoxPreference) preference;
                    ApplicationPreferences.setLockedTimeZoneId(getActivity(),
                            checkPref.isChecked() ? TimeZone.getDefault().getID() : "");
                    showLockTimeZone();
                }
                break;
            default:
                break;
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case InstanceSettings.PREF_WIDGET_INSTANCE_NAME:
                getActivity().finish();
                startActivity(MainActivity.intentToConfigure(getActivity(), ApplicationPreferences
                        .getWidgetId(getActivity())));
                break;
            case InstanceSettings.PREF_REFRESH_PERIOD_MINUTES:
                showRefreshPeriod();
                break;
            case InstanceSettings.PREF_SNAPSHOT_MODE:
                SnapshotMode snapshotMode = ApplicationPreferences.getSnapshotMode(getActivity());
                InstanceSettings settings = getSettings();
                if (snapshotMode.isSnapshotMode() && !settings.hasResults()) {
                    settings.setResultsStorage(QueryResultsStorage.getNewResults(getActivity(), settings.widgetId));
                }
                settings.clock().setSnapshotMode(snapshotMode, settings);
                settings.save(key, "newResultsForSnapshotMode");
                showSnapshotMode();
                setLockTimeZone();
                showLockTimeZone();
                break;
            default:
                break;
        }
    }

    private void showWidgetInstanceName() {
        Preference preference = findPreference(InstanceSettings.PREF_WIDGET_INSTANCE_NAME);
        if (preference != null) {
            preference.setSummary(ApplicationPreferences.getWidgetInstanceName(getActivity()) +
                    " (id:" + ApplicationPreferences.getWidgetId(getActivity()) +")");
        }
    }
}