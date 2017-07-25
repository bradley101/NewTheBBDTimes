package com.tbt.Tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;

/**
 * Created by bradley on 06-03-2017.
 */

public class DialogManager {
    public static final int TYPE_PROGRESS = 1;
    public static final int TYPE_ALERT = 0;
    private int type = 0;
    Context context;
    AlertDialog.Builder alertDialog;
    ProgressDialog progressDialog;

    public DialogManager(Context context) {
        this.context = context;
    }
    public void setType(int type) {
        switch (type) {
            case TYPE_ALERT:
                initAlert();
                break;
            case TYPE_PROGRESS:
                initProgress();
                break;
        }
    }
    private void initProgress() {
        progressDialog = new ProgressDialog(context);
        type = TYPE_PROGRESS;
    }
    private void initAlert() {
        alertDialog = new AlertDialog.Builder(context);
        type = TYPE_ALERT;
    }
    public void setTitle(String title) {
        setTitle(title, type);
    }
    private void setTitle(String t, int type) {
        if (type == TYPE_ALERT) {
            alertDialog.setTitle(t);
        } else {
            progressDialog.setTitle(t);
        }
    }
    public void setMessage(String message) {
        setMessage(message, type);
    }
    private void setMessage(String msg, int type) {
        if (type == TYPE_ALERT) {
            alertDialog.setMessage(msg);
        } else {
            progressDialog.setMessage(msg);
        }
    }
    public void show() {
        show(type);
    }
    private void show(int type) {
        if (type == TYPE_ALERT) {
            alertDialog.show();
        } else {
            progressDialog.show();
        }
    }
    public void setAutoCancel(boolean flag) {
        setAutoCancel(flag, type);
    }
    private void setAutoCancel(boolean f, int type) {
        if (type == TYPE_ALERT) {
            alertDialog.setCancelable(f);
        } else {
            progressDialog.setCancelable(f);
        }
    }
    public void dismiss() {
        if (progressDialog != null) {
            progressDialog.hide();
            progressDialog.dismiss();
        }
    }
}
