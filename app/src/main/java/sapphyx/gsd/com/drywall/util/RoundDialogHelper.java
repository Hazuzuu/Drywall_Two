package sapphyx.gsd.com.drywall.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import sapphyx.gsd.com.drywall.R;

public class RoundDialogHelper {

    //DialogHelper is a custom class we use to inflate a dialog
    // of which in this case is themed to use round corners
    // and designed for whatever use we intend it for
    //Later, Il try to adapt this to preference dialogs...maybe itl work

    public void showAlertDialog(Activity activity, String msg){

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.round_dialog);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("FirstTime", true);
                editor.apply();
                dialog.dismiss();
            }
        });

        AnimationHelper.animateGroup(
                dialog.findViewById(R.id.text_dialog),
                dialog.findViewById(R.id.btn_dialog)
        );

        dialog.show();
    }

    public void showMessageDialog(Activity activity, String msg){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.round_dialog);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        AnimationHelper.animateGroup(
                dialog.findViewById(R.id.text_dialog),
                dialog.findViewById(R.id.btn_dialog)
        );

        dialog.show();
    }

    public void showCategoriesDialog(Activity activity, String msg){

        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(activity);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.round_dialog);

        TextView text = dialog.findViewById(R.id.text_dialog);
        text.setText(msg);

        Button dialogButton = dialog.findViewById(R.id.btn_dialog);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean("CategoriesMsg", true);
                editor.apply();
                dialog.dismiss();
            }
        });

        AnimationHelper.animateGroup(
                dialog.findViewById(R.id.text_dialog),
                dialog.findViewById(R.id.btn_dialog)
        );

        dialog.show();
    }

}
