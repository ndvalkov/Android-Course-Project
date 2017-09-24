package com.academy.ndvalkov.mediamonitoringapp.common;

import android.app.Activity;

import com.academy.ndvalkov.mediamonitoringapp.R;

import org.aviran.cookiebar2.CookieBar;

public class Notifications {

    public static void showPositive(Activity context, String message) {
        CookieBar.Build(context)
                .setMessage(message)
                .setIcon(R.drawable.ic_notify)
                .setIconAnimation(R.animator.iconspin)
                .setDuration(2000)
                .show();
    }

    public static void showNegative(Activity context, String message) {
        CookieBar.Build(context)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setBackgroundColor(R.color.red)
                .setDuration(2000)
                .show();
    }
}
