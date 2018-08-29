package sapphyx.gsd.com.drywall.fragments;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.activity.About;
import sapphyx.gsd.com.drywall.activity.Settings;
import sapphyx.gsd.com.drywall.util.AnimationHelper;

import static sapphyx.gsd.com.drywall.activity.MainActivityBase.ARGS_INSTANCE;

/**
 * Created by ry on 2/11/18.
 */

public class Device extends Fragment {

    public Context context;
    private Drawable wallpaperDrawable, lockscreenDrawable;
    private ImageView wallpaper, lockscreen;
    private CardView lockscreenCard;

    public Device() {
    }

    public static Device newInstance(int instance) {
        Bundle args = new Bundle();
        args.putInt(ARGS_INSTANCE, instance);
        Device fragment = new Device();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.wallpaper_background, container, false);
        setHasOptionsMenu(true);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());

        wallpaper = v.findViewById(R.id.wallpaperView);
        lockscreen = v.findViewById(R.id.lockscreenView);
        lockscreenCard = v.findViewById(R.id.lockscreen_Card);

        AnimationHelper.quickViewReveal(v.findViewById(R.id.wallpaperView), 300);

        AnimationHelper.quickViewReveal(v.findViewById(R.id.app_container), 500);
        AnimationHelper.quickViewReveal(v.findViewById(R.id.more_container), 800);

        wallpaperDrawable = wallpaperManager.getDrawable();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            lockscreenDrawable = wallpaperManager.getBuiltInDrawable(WallpaperManager.FLAG_LOCK);
            lockscreen.setImageDrawable(lockscreenDrawable);
            //Lets hide this for now...untill its working
            lockscreenCard.setVisibility(View.GONE);
        }else{
            lockscreenCard.setVisibility(View.GONE);
        }

        wallpaper.setImageDrawable(wallpaperDrawable);


        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.device_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                wallpaper.setImageDrawable(wallpaperDrawable);
                Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
                break;

            case R.id.actions:
                Intent intent = new Intent(getContext(), WallpaperActions.class);
                startActivity(intent);
                break;

            case R.id.about:
                Intent about_intent = new Intent(getContext(), About.class);
                startActivity(about_intent);
                break;

            case R.id.settings:
                Intent settings_intent = new Intent(getContext(), Settings.class);
                startActivity(settings_intent);
                break;
        }
        return true;

    }
}