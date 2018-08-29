package sapphyx.gsd.com.drywall.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shashank.sony.fancyaboutpagelib.FancyAboutPage;

import sapphyx.gsd.com.drywall.R;

/**
 * Created by ry on 2/20/18.
 */

public class AboutDev extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dev_main);

        Toolbar toolbar = findViewById(R.id.toolbar);

        AppCompatActivity activity = this;
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("About Drywall");
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorPrimaryDark));

        FancyAboutPage fancyAboutPage=findViewById(R.id.fancyaboutpage);
        fancyAboutPage.setCover(R.drawable.gsd_logo);
        fancyAboutPage.setName("Ryan Gocal");
        fancyAboutPage.setDescription("A free lance android developer that seeks innovation, and originality");
        fancyAboutPage.setAppIcon(R.mipmap.ic_launcher);
        fancyAboutPage.setAppName("Drywall");
        fancyAboutPage.setVersionNameAsAppSubTitle("2.01.10");
        fancyAboutPage.setAppDescription("Drywall is a powerfull wallpaper provider powered by rss and minimalistic material design.\n\n" +
                "Built with minimal use of Android Libraries, a small utility belt of settings and tools and some pretty awesome walls for your launcher homescreen.\n\n" +
                "Wallpapers used from UnSplash are credited by their orignal Authors and Direct links.\n\n" +
                "This application aims to be quit different from the other wallpaper apps you have probably tried. Enjoy!");
        fancyAboutPage.addEmailLink("rgocal09@gmail.com");
        fancyAboutPage.addFacebookLink("https://www.facebook.com/Gocalsd/");
        fancyAboutPage.addTwitterLink("https://twitter.com/Rgocal");
        fancyAboutPage.addGitHubLink("https://github.com/rgocal");
    }
}