package sapphyx.gsd.com.drywall.fragments;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.widget.Toast;

import java.util.List;

import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.util.DryPreferenceFragment;
import sapphyx.gsd.com.drywall.util.SettingsProvider;

public class WallpaperActions extends DryPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.papers);
        populateWallpaperTypes();
    }

    private void populateWallpaperTypes() {
        final Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> rList = pm.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);

        String packageToHide = "sapphyx.gsd.com.drywall";

        final PreferenceScreen parent = getPreferenceScreen();
        parent.setOrderingAsAdded(false);
        for (ResolveInfo info : rList) {
                Preference pref = new Preference(getActivity());
                pref.setLayoutResource(R.layout.preference_wallpaper_type);

                Intent prefIntent = new Intent(intent);
                prefIntent.setComponent(new ComponentName(
                        info.activityInfo.packageName, info.activityInfo.name));
                if(!packageToHide.equals(info.activityInfo.packageName)) {

                    pref.setIntent(prefIntent);

                    CharSequence label = info.loadLabel(pm);
                    if (label == null) label = info.activityInfo.packageName;
                    pref.setTitle(label);
                    pref.setIcon(info.loadIcon(pm));

                    parent.addPreference(pref);

                    SettingsProvider.get(getActivity())
                            .edit()
                            .putInt("wallpaperProviders", rList.size())
                            .apply();
                }
        }
    }

    protected PackageManager getPackageManager() {
        return getActivity().getPackageManager();
    }
}