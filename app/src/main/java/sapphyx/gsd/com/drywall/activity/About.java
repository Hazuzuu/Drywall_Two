package sapphyx.gsd.com.drywall.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import de.psdev.licensesdialog.LicensesDialog;
import sapphyx.gsd.com.drywall.R;

/**
 * Created by ry on 2/11/18.
 */

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.wallpaper_actions);

        View touchOutSide = findViewById(R.id.touch_outside);
        touchOutSide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getFragmentManager().beginTransaction()
                .replace(R.id.quick_content, new About.AboutFragment())
                .commit();
    }

    public static class AboutFragment extends PreferenceFragment {

        private String donateUrl = "https://play.google.com/store/apps/details?id=com.NxIndustries.Sapphire1NE";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.about);

            Preference prefLicense = findPreference("pref_license");
            prefLicense.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new LicensesDialog.Builder(getActivity())
                            .setNotices(R.raw.licenses)
                            .build()
                            .show();
                    return false;
                }
            });

            Preference prefMore = findPreference("pref_more");
            prefMore.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final Intent intent = new Intent(getActivity(), AboutDev.class);
                    startActivity(intent);
                    return false;
                }
            });

            Preference prefDonate = findPreference("pref_donate");
            prefDonate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(donateUrl));
                    startActivity(Intent.createChooser(i, "Open Using"));
                    return false;
                }
            });
        }
    }
}