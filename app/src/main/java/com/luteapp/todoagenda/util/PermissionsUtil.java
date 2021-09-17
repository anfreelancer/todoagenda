package com.luteapp.todoagenda.util;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.luteapp.todoagenda.prefs.AllSettings;
import com.luteapp.todoagenda.provider.EventProviderType;

import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * @author yvolk@yurivolkov.com
 */
public class PermissionsUtil {

    private PermissionsUtil() {
        // Empty
    }

    public static boolean arePermissionsGranted(Context context) {
        AllSettings.ensureLoadedFromFiles(context, false);
        for (String permission: EventProviderType.getNeededPermissions()) {
            if (isPermissionNeeded(context, permission)) {
                return false;
            }
        }
        return true;
    }

    private final static Set<String> grantedPermissions = new ConcurrentSkipListSet<>();
    public static boolean isPermissionNeeded(Context context, String permission) {
        if (isTestMode() || grantedPermissions.contains(permission)) return false;
        boolean granted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
        if (granted) grantedPermissions.add(permission);
        return !granted;
    }

    private static LazyVal<Boolean> isTestMode = LazyVal.of(() -> {
        /*
         * Based on
         * http://stackoverflow.com/questions/21367646/how-to-determine-if-android-application-is-started-with-junit-testing-instrument
         */
        try {
            Class.forName("com.luteapp.todoagenda.provider.FakeCalendarContentProvider");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    });

    public static boolean isTestMode() {
        return isTestMode.get();
    }
}
