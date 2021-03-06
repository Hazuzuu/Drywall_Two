package sapphyx.gsd.com.drywall.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.leinardi.android.speeddial.SpeedDialActionItem;
import com.leinardi.android.speeddial.SpeedDialOverlayLayout;
import com.leinardi.android.speeddial.SpeedDialView;
import com.nj.imagepicker.ImagePicker;
import com.nj.imagepicker.listener.ImageResultListener;
import com.nj.imagepicker.result.ImageResult;
import com.nj.imagepicker.utils.DialogConfiguration;

import java.io.IOException;

import dmax.dialog.SpotsDialog;
import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.activity.About;
import sapphyx.gsd.com.drywall.activity.Settings;
import sapphyx.gsd.com.drywall.util.AnimationHelper;
import sapphyx.gsd.com.drywall.util.SettingsProvider;
import sapphyx.gsd.com.drywall.views.NumberTextview;

import static sapphyx.gsd.com.drywall.activity.MainActivityBase.ARGS_INSTANCE;

/**
 * Created by ry on 2/11/18.
 * This is where the magic happens
 */

public class Device extends Fragment {

    Drawable wallpaperDrawable;
    ImageView wallpaper;
    LinearLayout infoWalls, infoCategories, infoFeatured;
    FrameLayout source, rate, apps, privacy;
    NumberTextview wallCount, categoryCount, featuredCount, providerCount;

    int collectionsTotal, categoryOne, categoryTwo, categoryThree, categoryFour, categoryFive, categorySix, categorySeven, categoryEight, categoryNine;

    private String URL_SOURCE = "https://github.com/rgocal/Drywall_Two";
    private String URL_RATE = "https://play.google.com/store/apps/details?id=sapphyx.gsd.com.drywall";
    private String URL_APPS = "https://play.google.com/store/apps/dev?id=8934848548747347540";
    private String URL_PRIVACY = "http://www.gocalsd.weebly.com/privacy";

    public static int height;
    public static int width;
    private String applyMessage, blurMsg;
    private Rect rect;

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

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());

        wallpaper = v.findViewById(R.id.wallpaperView);

        SpeedDialView speedDialView = v.findViewById(R.id.speedDial);
        SpeedDialOverlayLayout overlayLayout = v.findViewById(R.id.overlay);
        speedDialView.setOverlayLayout(overlayLayout);

        speedDialView.inflate(R.menu.action_menu);

        applyMessage = getString(R.string.apply_message);
        blurMsg = getString(R.string.blur_message);


        //info panels
        infoWalls = v.findViewById(R.id.info_walls);
        infoCategories = v.findViewById(R.id.info_categories);
        infoFeatured = v.findViewById(R.id.info_featured);
        wallCount = v.findViewById(R.id.wallCount);
        categoryCount = v.findViewById(R.id.categoryCount);
        featuredCount = v.findViewById(R.id.featuredCount);
        providerCount = v.findViewById(R.id.providersCount);

        AnimationHelper.animateGroup(v.findViewById(R.id.info_walls), v.findViewById(R.id.info_categories), v.findViewById(R.id.info_featured), v.findViewById(R.id.info_providers));

        //archive items
        source = v.findViewById(R.id.app_container);
        rate = v.findViewById(R.id.rate_container);
        apps = v.findViewById(R.id.more_container);
        privacy = v.findViewById(R.id.privacy_container);

        //time to count items...
        categoryOne = SettingsProvider.get(getContext()).getInt("categoryOneCount", 1);
        categoryTwo = SettingsProvider.get(getContext()).getInt("categoryTwoCount", 1);
        categoryThree = SettingsProvider.get(getContext()).getInt("categoryThreeCount", 1);
        categoryFour = SettingsProvider.get(getContext()).getInt("categoryFourCount", 1);
        categoryFive = SettingsProvider.get(getContext()).getInt("categoryFiveCount", 1);
        categorySix = SettingsProvider.get(getContext()).getInt("categorySixCount", 1);
        categorySeven = SettingsProvider.get(getContext()).getInt("categorySevenCount", 1);
        categoryEight = SettingsProvider.get(getContext()).getInt("categoryEightCount", 1);
        categoryNine = SettingsProvider.get(getContext()).getInt("categoryNineCount", 1);

        collectionsTotal = categoryOne + categoryTwo + categoryThree + categoryFour + categoryFive + categorySix + categorySeven + categoryEight + categoryNine;

        wallCount.setEnableAnim(true);
        wallCount.setDuration(1400);
        wallCount.setNumberString(String.valueOf(SettingsProvider.get(getContext()).getInt("wallCount", collectionsTotal)));

        categoryCount.setEnableAnim(true);
        categoryCount.setDuration(1400);
        categoryCount.setNumberString(String.valueOf(SettingsProvider.get(getContext()).getInt("categoryCount", 9)));

        featuredCount.setEnableAnim(true);
        featuredCount.setDuration(1400);
        featuredCount.setNumberString(String.valueOf(SettingsProvider.get(getContext()).getInt("featureCount", 1)));

        providerCount.setEnableAnim(true);
        providerCount.setDuration(1400);
        providerCount.setNumberString(String.valueOf(SettingsProvider.get(getContext()).getInt("wallpaperProviders", 1)));

        source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent
                        (Intent.ACTION_VIEW, Uri.parse(URL_SOURCE));
                startActivity(Intent.createChooser(sendIntent, "Open With"));
            }
        });

        rate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent
                        (Intent.ACTION_VIEW, Uri.parse(URL_RATE));
                startActivity(Intent.createChooser(sendIntent, "Open With"));
            }
        });

        apps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent
                        (Intent.ACTION_VIEW, Uri.parse(URL_APPS));
                startActivity(Intent.createChooser(sendIntent, "Open With"));
            }
        });

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent
                        (Intent.ACTION_VIEW, Uri.parse(URL_PRIVACY));
                startActivity(Intent.createChooser(sendIntent, "Open With"));
            }
        });

        final int xStep = 1;
        final int yStep = 1;

        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

        height = metrics.heightPixels;
        width = metrics.widthPixels;

        speedDialView.setOnActionSelectedListener(new SpeedDialView.OnActionSelectedListener() {
            @Override
            public boolean onActionSelected(SpeedDialActionItem speedDialActionItem) {
                switch (speedDialActionItem.getId()) {
                    case R.id.action_blur:
                        final Dialog dialog = new Dialog(getContext());
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.setCancelable(false);
                        dialog.setContentView(R.layout.round_dialog_buttons);

                        TextView text = dialog.findViewById(R.id.text_dialog);
                        text.setText(blurMsg);

                        Button dialogButtonTwo = dialog.findViewById(R.id.btn_dialog_two);
                        dialogButtonTwo.setText("Cancel");
                        dialogButtonTwo.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });

                        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
                        dialogButton.setText("Blur Wallpaper");
                        dialogButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //Blur the wallpaper
                                try {
                                    Bitmap bm = BlurImage(drawableToBitmap(wallpaperManager.getDrawable()));
                                    wallpaperManager.setBitmap(bm);
                                    Toast.makeText(getActivity(), "Done",
                                            Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Error, Cannot Blur Current Wallpaper",
                                            Toast.LENGTH_LONG).show();
                                }
                                refreshWallpaperPreview();
                                dialog.dismiss();
                            }
                        });

                        AnimationHelper.animateGroup(
                                dialog.findViewById(R.id.text_dialog),
                                dialog.findViewById(R.id.btn_dialog),
                                dialog.findViewById(R.id.btn_dialog_two)
                        );

                        dialog.show();
                        return false; // true to keep the Speed Dial open
                    case R.id.action_single_offset:
                        wallpaperManager.setWallpaperOffsetSteps(xStep, yStep);
                        wallpaperManager.suggestDesiredDimensions(width, height);

                        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
                        Bitmap bitmap = Bitmap.createScaledBitmap(bm, width, height, true);

                        try {
                            wallpaperManager.setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        refreshWallpaperPreview();
                        return false; // true to keep the Speed Dial open
                    case R.id.action_custom_offset:
                        wallpaperManager.setWallpaperOffsetSteps(xStep, yStep);
                        wallpaperManager.suggestDesiredDimensions(SettingsProvider.getInt(getActivity(), "customWidth", width), SettingsProvider.getInt(getActivity(), "customHeight", height));


                        wallpaperDrawable = wallpaperManager.getDrawable();
                        bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();
                        bitmap = Bitmap.createScaledBitmap(bm, SettingsProvider.getInt(getActivity(), "customWidth", width), SettingsProvider.getInt(getActivity(), "customHeight", height), true);

                        try {
                            wallpaperManager.setBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        refreshWallpaperPreview();
                        return false; // true to keep the Speed Dial open
                    case R.id.action_custom_image:
                        ImagePicker.build(new DialogConfiguration()
                                .setTitle("Select Custom Wallpaper")
                                .setOptionOrientation(LinearLayoutCompat.HORIZONTAL), new ImageResultListener() {
                            @Override
                            public void onImageResult(final ImageResult imageResult) {
                                //Take data and put it in wallpaper manager and preview
                                final Dialog dialog = new Dialog(getContext());
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.setContentView(R.layout.round_dialog_buttons);

                                TextView text = dialog.findViewById(R.id.text_dialog);
                                text.setText(applyMessage);

                                Button dialogButton = dialog.findViewById(R.id.btn_dialog);
                                dialogButton.setText("Homescreen");
                                dialogButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final AlertDialog dialogApply = new SpotsDialog(getContext());
                                        dialogApply.show();
                                        dialogApply.setMessage("Applying...");
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            public void run() {
                                                //Apply to Homescreen
                                                try {
                                                    WallpaperManager.getInstance(getActivity()).setBitmap(imageResult.getBitmap());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getContext(), "Err, Something went wrong...", Toast.LENGTH_SHORT).show();
                                                    Log.e("Drywall", "Custom wallpaper couldn't be captured properly");
                                                }
                                                refreshWallpaperPreview();
                                                dialogApply.dismiss();
                                            }
                                        }, 3000);
                                        dialog.dismiss();

                                    }
                                });

                                Button dialogButtonTwo = dialog.findViewById(R.id.btn_dialog_two);
                                dialogButtonTwo.setText("Lockscreen");
                                dialogButtonTwo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        final AlertDialog dialogApply = new SpotsDialog(getContext());
                                        dialogApply.show();
                                        dialogApply.setMessage("Applying...");
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {
                                            @RequiresApi(api = Build.VERSION_CODES.N)
                                            public void run() {
                                                //Apply to Lockscreen
                                                try {
                                                    WallpaperManager.getInstance(getActivity()).setBitmap(imageResult.getBitmap(), rect, true, WallpaperManager.FLAG_LOCK);
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                    Toast.makeText(getContext(), "Err, Something went wrong...", Toast.LENGTH_SHORT).show();
                                                    Log.e("Drywall", "Custom wallpaper couldn't be captured properly");
                                                }
                                                dialogApply.dismiss();
                                            }
                                        }, 3000);
                                        dialog.dismiss();
                                    }
                                });

                                AnimationHelper.animateGroup(
                                        dialog.findViewById(R.id.text_dialog),
                                        dialog.findViewById(R.id.btn_dialog),
                                        dialog.findViewById(R.id.btn_dialog_two)
                                );

                                dialogButtonTwo.setVisibility(View.GONE);
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                    dialogButtonTwo.setVisibility(View.VISIBLE);
                                }

                                dialog.show();
                            }
                        })
                                .show(getChildFragmentManager());
                        return false; // true to keep the Speed Dial open
                    case R.id.action_reset:
                        try {
                            wallpaperManager.clear();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        refreshWallpaperPreview();
                        return false; // true to keep the Speed Dial open
                    default:
                        return false;
                }
            }
        });

        //Add animations here

        wallpaperDrawable = wallpaperManager.getDrawable();

        wallpaper.setImageDrawable(wallpaperDrawable);
        return  v;
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
                refreshWallpaperPreview();
                Toast.makeText(getContext(), "Refreshing", Toast.LENGTH_SHORT).show();
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

    private void refreshWallpaperPreview() {
        AnimationHelper.quickViewReveal(wallpaper, 300);

        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        final Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();

        wallpaper.setImageBitmap(bm);
    }

    public Bitmap BlurImage (Bitmap bitmap) {
        RenderScript rs = RenderScript.create(getContext());

        Bitmap blurredBitmap =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        for (int i = 0; i < 20; i++) {
            Allocation allIn = Allocation.createFromBitmap(rs, bitmap);
            Allocation allOut = Allocation.createFromBitmap(rs, blurredBitmap);

            ScriptIntrinsicBlur blurScript = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            blurScript.setRadius(25.f);

            blurScript.setInput(allIn);
            blurScript.forEach(allOut);

            allOut.copyTo(blurredBitmap);

            bitmap = blurredBitmap;
        }

        rs.destroy();

        return bitmap;
    }

    public Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable == null)
            return null;
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}