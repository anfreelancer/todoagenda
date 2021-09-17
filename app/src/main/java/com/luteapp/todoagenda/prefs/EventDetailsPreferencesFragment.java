package com.luteapp.todoagenda.prefs;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.luteapp.todoagenda.R;

public class EventDetailsPreferencesFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences_event_details);
    }
}