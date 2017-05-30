package com.syslab.caddyless.filemanager.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Toast;

/**
 * Created by caddyless on 2017/5/15.
 */

public class AlertUtil {
    public static void toastMess(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void simpleAlertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context).setTitle(title)
                .setMessage(message)
                .setNegativeButton("确定", null).show();
    }

    public static AlertDialog judgeAlertDialog(Context context, String title, String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener cancleListener) {
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("确定", okListener)
                .setPositiveButton("取消", cancleListener).show();
        return alertDialog;
    }

    public static void showSnack(View anchor, String message) {
        Snackbar.make(anchor, message, Snackbar.LENGTH_SHORT).show();

    }
}

