package sapphyx.gsd.com.drywall.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import dmax.dialog.SpotsDialog;
import sapphyx.gsd.com.drywall.R;

public class DetailsActivity extends Activity {

    public String wall, wallTitle;
    private String saveWallLocation, picName;

    public int maxWidth = 800;
    public int maxHeight = 2400;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);

        final PhotoView image = findViewById(R.id.bigwall);
        Button set = findViewById(R.id.set_button);
        TextView title = findViewById(R.id.wallTitle);
        ImageButton exit = findViewById(R.id.wallExit);

        wallTitle = getIntent().getStringExtra("wallTitle");
        wall = getIntent().getStringExtra("wall");

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWallpaperDialog();
            }
        });

        title.setText(wallTitle);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private final Target wallTarget = new Target() {
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
            showNoPicDialog();
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private final Target wallCropTarget = new Target() {
        @Override
        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ImageView wall = findViewById(R.id.bigwall);
                        Uri wallUri = getLocalBitmapUri(wall);
                        if (wallUri != null) {
                            Intent setWall = new Intent(Intent.ACTION_ATTACH_DATA);
                            setWall.setDataAndType(wallUri, "image/*");
                            setWall.putExtra("png", "image/*");
                            startActivityForResult(Intent.createChooser(setWall, getString(R.string.set_as)), 1);
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

    public void showWallpaperDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(DetailsActivity.this).create();
        alertDialog.setTitle("Setting Drywall");
        alertDialog.setMessage(getString(R.string.set_wall_content));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Apply",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        final AlertDialog dialogApply = new SpotsDialog(DetailsActivity.this);
                        dialogApply.show();
                        dialogApply.setMessage("Applying...");
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                Picasso.with(DetailsActivity.this)
                                        .load(wall)
                                        .into(wallTarget);
                                dialogApply.dismiss();
                            }
                        }, 3000);
                    }
});

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cropping?",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(DetailsActivity.this, R.string.crop_not_available, Toast.LENGTH_LONG).show();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Adjust",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Picasso.with(DetailsActivity.this)
                                .load(wall)
                                .into(wallCropTarget);
                    }
                });
        alertDialog.show();

    }

    private void showNoPicDialog() {
        final AlertDialog alertDialog = new AlertDialog.Builder(DetailsActivity.this).create();
        alertDialog.setTitle(R.string.error);
        alertDialog.setMessage(getString(R.string.wall_error));

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                });
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
