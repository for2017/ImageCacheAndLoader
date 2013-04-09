package com.lianghanzhen.image.cache;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Looper;
import com.lianghanzhen.image.L;

import java.lang.ref.WeakReference;


public class CacheableBitmapDrawable extends BitmapDrawable {

    private static final long CHECK_STATE_DELAY = 5000;

    private final String mUrl;
    private boolean mHasBeenDisplayed;
    private int mDisplayingCount = 0;
    private int mCachedCount = 0;
    private Runnable mCheckStateRunnable;
    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    public CacheableBitmapDrawable(Resources res, Bitmap bitmap, String url) {
        super(res, bitmap);
        mUrl = url;
    }

    /**
     * get the memory size of bitmap
     * @return size
     */
    public int getMemorySize() {
        int size = 0;
        if (isBitmapValid()) {
            Bitmap bitmap = getBitmap();
            size = bitmap.getHeight() * bitmap.getRowBytes();
        }
        return size;
    }

    public String getUrl() {
        return mUrl;
    }

    public synchronized void setDisplaying(boolean displaying) {
        if (displaying) {
            mDisplayingCount++;
            mHasBeenDisplayed = true;
        } else {
            mDisplayingCount--;
        }
        checkState();
    }

    public synchronized void setCached(boolean cached) {
        mCachedCount += cached ? 1 : -1;
        checkState();
    }

    public synchronized boolean isCaching() {
        return mCachedCount > 0;
    }

    public synchronized boolean isDisplaying() {
        return mDisplayingCount > 0;
    }

    public synchronized boolean isBitmapValid() {
        Bitmap bitmap = getBitmap();
        return bitmap != null && !bitmap.isRecycled();
    }

    private void checkState() {
        L.d(String.format("Check State for URL: %s, Displaying: %d, Cached: %d, Been Display: %b", mUrl, mDisplayingCount, mCachedCount, mHasBeenDisplayed));

        cancelCheckStateCallback();

        if (!isCaching() && !isDisplaying() && isBitmapValid()) {
            if (mHasBeenDisplayed) {
                L.d(String.format("Recycle Bitmap For URL: %s", mUrl));
                getBitmap().recycle();
            } else {
                L.d(String.format("Unused Bitmap which hasn't been displayed, delaying recycle(): %s", mUrl));
                mCheckStateRunnable = new CheckStateRunnable(this);
                mHandler.postDelayed(mCheckStateRunnable, CHECK_STATE_DELAY);
            }
        }
    }

    private void cancelCheckStateCallback() {
        if (mCheckStateRunnable != null) {
            L.w(String.format("Cancel Check State Callback For URL: %s", mUrl));
            mHandler.removeCallbacks(mCheckStateRunnable);
            mCheckStateRunnable = null;
        }
    }

    private static class CheckStateRunnable implements Runnable {

        private final WeakReference<CacheableBitmapDrawable> mCacheableBitmapDrawableRef;

        private CheckStateRunnable(CacheableBitmapDrawable cacheableBitmapDrawable) {
            mCacheableBitmapDrawableRef = new WeakReference<CacheableBitmapDrawable>(cacheableBitmapDrawable);
        }

        @Override
        public void run() {
            CacheableBitmapDrawable cacheableBitmapDrawable = mCacheableBitmapDrawableRef.get();
            if (cacheableBitmapDrawable != null) {
                cacheableBitmapDrawable.checkState();
            }
        }
    }

}
