package sapphyx.gsd.com.drywall.activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.util.AppModel;
import sapphyx.gsd.com.drywall.util.LooperExecutor;
import sapphyx.gsd.com.drywall.util.SettingsProvider;

/**
 * Created by ry on 2/11/18.
 */

public class Settings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    public static boolean prefChanged;

    @Override
    public void onResume() {
        super.onResume();
        SettingsProvider.get(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!this.isFinishing()) {
            if (Settings.prefChanged) {
                applySettings(this);
            }
        }
        SettingsProvider.get(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    private static final String SCHEME = "package";

    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";

    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";

    private static final int WAIT_BEFORE_RESTART = 250;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.wallpaper_actions);

        SharedPreferences sp = SettingsProvider.get(this);
        sp.registerOnSharedPreferenceChangeListener(this);

        prefChanged = false;

        View touchOutSide = findViewById(R.id.touch_outside);
        touchOutSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.quick_content, new Settings.SettingsFragment())
                .commit();
    }

    @Override
    public void finish() {
        super.finish();
        Log.w("Main Settings", "Applying user preferences");
        if (Settings.prefChanged) {
            applySettings(getBaseContext());
        }
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    public static void applySettings(final Context context) {
        new LooperExecutor(AppModel.getWorkerLooper()).execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(WAIT_BEFORE_RESTART);
                } catch (Exception e) {
                    Log.e("SettingsActivity", "Error waiting", e);
                }

                Intent intent = new Intent(Intent.ACTION_MAIN)
                        .addCategory(Intent.CATEGORY_HOME)
                        .setPackage(context.getPackageName())
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                // Create a pending intent so the application is restarted after Process.killProcess() was called.
                // We use an AlarmManager to call this intent in 50ms
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager.setExact(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 50, pendingIntent);

                // Kill the application
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Settings.prefChanged = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.dry_preferences);

            Preference info = findPreference("info");
            info.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    showInstalledAppDetails(getActivity(), "sapphyx.gsd.com.drywall");
                    return false;
                }
            });

            Preference apps = findPreference("reset");
            apps.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    try {
                        // clearing app data
                        Runtime runtime = Runtime.getRuntime();
                        runtime.exec("pm clear sapphyx.gsd.com.drywall");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return true;
                }
            });
        }
    }

    public static void showInstalledAppDetails(Context context, String packageName) {
        Intent intent = new Intent();
        final int apiLevel = Build.VERSION.SDK_INT;
        if (apiLevel >= 9) { // above 2.3
            intent.setAction(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts(SCHEME, packageName, null);
            intent.setData(uri);
        } else { // below 2.3
            final String appPkgName = (apiLevel == 8 ? APP_PKG_NAME_22
                    : APP_PKG_NAME_21);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setClassName(APP_DETAILS_PACKAGE_NAME,
                    APP_DETAILS_CLASS_NAME);
            intent.putExtra(appPkgName, packageName);
        }
        context.startActivity(intent);
    }
}