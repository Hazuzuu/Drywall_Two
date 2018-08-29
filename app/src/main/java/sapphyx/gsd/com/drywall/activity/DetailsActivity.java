package sapphyx.gsd.com.drywall.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import dmax.dialog.SpotsDialog;
import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.util.AnimationHelper;
import sapphyx.gsd.com.drywall.util.RoundDialogHelper;

public class DetailsActivity extends Activity {

    public String wall, wallTitle, wallInfo;
    private String saveWallLocation, picName, downloadMessage, applyMessage;

    private TextView title;
    private PhotoView image;
    public Rect rect;
    private ImageButton set, crop, exit, help, download, info;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try{
                    Bitmap bm = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);

                    WallpaperManager wm = WallpaperManager.getInstance(DetailsActivity.this);
                    wm.setBitmap(bm);

                    Toast.makeText(getApplicationContext(), "Cropped and Set!", Toast.LENGTH_SHORT).show();
                    finish();

                } catch (IOException e){
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        overridePendingTransition(R.anim.slide_up, R.anim.slide_out);

        setContentView(R.layout.activity_details);

        image = findViewById(R.id.bigwall);
        set = findViewById(R.id.set_button);
        title = findViewById(R.id.wallTitle);
        exit = findViewById(R.id.wallExit);
        help = findViewById(R.id.help_button);
        crop = findViewById(R.id.crop_button);
        download = findViewById(R.id.download_button);
        info = findViewById(R.id.info_button);

        wallTitle = getIntent().getStringExtra("wallTitle");
        wall = getIntent().getStringExtra("wall");
        wallInfo = getIntent().getStringExtra("wallInfo");

        downloadMessage = getString(R.string.download_message);
        applyMessage = getString(R.string.apply_message);

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Dialog dialog = new Dialog(DetailsActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.round_dialog);

                TextView text = dialog.findViewById(R.id.text_dialog);
                text.setText(downloadMessage);

                Button dialogButton = dialog.findViewById(R.id.btn_dialog);
                dialogButton.setText("Download");
                dialogButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadFile();
                        dialog.dismiss();
                    }
                });

                AnimationHelper.animateGroup(
                        dialog.findViewById(R.id.text_dialog),
                        dialog.findViewById(R.id.btn_dialog)
                );

                dialog.show();
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(DetailsActivity.this, FeatureInfoActivity.class)
                        .putExtra("wallDescription", wallInfo)
                        .putExtra("wallTitle", wallTitle);
                startActivity(intent);
            }
        });

        crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailsActivity.this, R.string.crop_wait, Toast.LENGTH_LONG).show();
                Picasso.with(DetailsActivity.this)
                        .load(wall)
                        .networkPolicy(NetworkPolicy.OFFLINE)
                        .into(wallCropTarget);
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoundDialogHelper alert = new RoundDialogHelper();
                alert.showMessageDialog(DetailsActivity.this, getResources().getString(R.string.crop_not_available));
            }
        });



        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(DetailsActivity.this);
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
                        final AlertDialog dialogApply = new SpotsDialog(DetailsActivity.this);
                        dialogApply.show();
                        dialogApply.setMessage("Applying...");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Picasso.with(DetailsActivity.this)
                                        .load(wall)
                                        .into(homescreenTarget);
                                dialogApply.dismiss();
                                finish();
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
                        final AlertDialog dialogApply = new SpotsDialog(DetailsActivity.this);
                        dialogApply.show();
                        dialogApply.setMessage("Applying...");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Picasso.with(DetailsActivity.this)
                                        .load(wall)
                                        .into(lockscreenTarget);
                                dialogApply.dismiss();
                                finish();
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
        });

        title.setText(wallTitle);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overridePendingTransition(R.anim.slide_out, R.anim.slide_up);
                finish();
            }
        });

        Picasso.with(DetailsActivity.this)
                .load(wall)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        //Try again online if cache failed
                        Picasso.with(DetailsActivity.this)
                                .load(wall)
                                .error(R.drawable.ic_alert)
                                .into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Log.v("Picasso","Could not fetch image");
                                    }
                                });
                    }
                });

        saveWallLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + getResources().getString(R.string.walls_save_location);
        picName = getResources().getString(R.string.walls_prefix_name);

    }

    private void downloadFile() {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/Drywall");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(wall);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Drywall")
                .setDescription("Requesting wallpaper and downloading...")
                .setDestinationInExternalPublicDir("/Drywall", title + ".png");

        mgr.enqueue(request);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        overridePendingTransition(R.anim.slide_out, R.anim.slide_up);

    }

    //Set Homescreen Wallpaper
    private final Target homescreenTarget = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        WallpaperManager wm = WallpaperManager.getInstance(DetailsActivity.this);
                        wm.setBitmap(bitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    //Set Lockscreen Wallpaper
    private final Target lockscreenTarget = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void run() {
                    try {
                        WallpaperManager wm = WallpaperManager.getInstance(DetailsActivity.this);
                        wm.setBitmap(bitmap, rect, true, WallpaperManager.FLAG_LOCK);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }

    };

        public final Target wallCropTarget = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ImageView wall = findViewById(R.id.bigwall);
                        Uri wallUri = getLocalBitmapUri(wall);
                        if (wallUri != null) {
                            CropImage.activity(wallUri)
                                    .setGuidelines(CropImageView.Guidelines.ON)
                                    .start(DetailsActivity.this);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private String convertWallName(String link) {
        return (link
                .replaceAll("png", "")                   // Deletes png extension
                .replaceAll("jpg", "")                   // Deletes jpg extension
                .replaceAll("jpeg", "")                  // Deletes jpeg extension
                .replaceAll("bmp", "")                   // Deletes bmp extension
                .replaceAll("[^a-zA-Z0-9\\p{Z}]", "")    // Remove all special characters and symbols
                .replaceFirst("^[0-9]+(?!$)", "")        // Remove all leading numbers unless they're all numbers
                .replaceAll("\\p{Z}", "_"))              // Replace all kinds of spaces with underscores
        ;
    }

    void showNoPicDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(DetailsActivity.this).create();
        alertDialog.setTitle(R.string.error);
        alertDialog.setMessage(getString(R.string.wall_error));

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public Uri getLocalBitmapUri(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp;
        if (drawable instanceof BitmapDrawable)
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        else
            return null;
        Uri bmpUri = null;
        try {
            File file = new File(saveWallLocation, picName + convertWallName(wall) + ".png");
            file.getParentFile().mkdirs();
            file.delete();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
}
