package com.lianghanzhen.image.utils;


import android.util.Log;

public final class L {

    private static final String TAG = "ImageCacheAndLoader";

    private static final boolean FLAG = true;

    public static void d(String message) {
        if (FLAG) {
            Log.d(TAG, message);
        }
    }

    public static void d(String message, Throwable throwable) {
        if (FLAG) {
            Log.d(TAG, message, throwable);
        }
    }

    public static void w(String message) {
        if (FLAG) {
            Log.w(TAG, message);
        }
    }

    public static void w(Throwable throwable) {
        if (FLAG) {
            Log.w(TAG, throwable);
        }
    }

    public static void w(String message, Throwable throwable) {
        if (FLAG) {
            Log.w(TAG, message, throwable);
        }
    }

    public static void e(String message) {
        if (FLAG) {
            Log.e(TAG, message);
        }
    }

    public static void e(String message, Throwable throwable) {
        if (FLAG) {
            Log.e(TAG, message, throwable);
        }
    }

}
