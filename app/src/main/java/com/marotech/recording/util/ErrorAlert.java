package com.marotech.recording.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;

public class ErrorAlert implements OnKeyListener {

    private final Context context;
    private AlertDialog alertDialog;

    public ErrorAlert(final Context context) {
        this.context = context;
    }

    public void showErrorDialog(final String title, final String message) {
        alertDialog = new AlertDialog.Builder(context)
                .setMessage(message).setTitle(title)
                .setPositiveButton("Ok", new OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int which) {

                        if (alertDialog != null) {
                            alertDialog.dismiss();
                            alertDialog = null;
                        }
                    }
                })
                .setNeutralButton("Close", new OnClickListener() {
                    public void onClick(final DialogInterface dialog,
                                        final int which) {

                        if (alertDialog != null) {
                            alertDialog.dismiss();
                            alertDialog = null;
                        }
                    }
                }).create();
        alertDialog.setOnKeyListener(this);
        alertDialog.show();
    }

    public void dismiss() {
        if (alertDialog != null) {
            if (alertDialog.isShowing())
                alertDialog.dismiss();
            alertDialog = null;
        }
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        }
        return true;
    }
}