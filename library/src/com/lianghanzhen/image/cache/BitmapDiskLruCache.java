package com.lianghanzhen.image.cache;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.*;
import android.os.Process;
import com.jakewharton.DiskLruCache;
import com.lianghanzhen.image.utils.CacheDirUtils;
import com.lianghanzhen.image.utils.L;
import com.lianghanzhen.image.utils.MD5Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class BitmapDiskLruCache {

    private static final int DISK_CACHE_FLUSH_DELAY_SECS = 5;
    private static final Bitmap.CompressFormat DEFAULT_COMPRESS_FORMAT = Bitmap.CompressFormat.PNG;
    private static final int DEFAULT_COMPRESS_QUALITY = 100;

    private final Resources mResources;
    private DiskLruCache mDiskLruCache;
    private final Map<String, ReentrantLock> mLocks = new HashMap<String, ReentrantLock>();
    private DiskLruCacheFlushRunnable mFlushRunnable;
    private ScheduledExecutorService mFlushScheduledExecutor;
    private ScheduledFuture<?> mFlushScheduledFuture;

    private Bitmap.CompressFormat mCompressFormat = DEFAULT_COMPRESS_FORMAT;
    private int mCompressQuality = DEFAULT_COMPRESS_QUALITY;

    public BitmapDiskLruCache(final Context context, final long maxSize) {
        mResources = context.getResources();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    mDiskLruCache = DiskLruCache.open(CacheDirUtils.getIndividualCacheDirectory(context), 0, 1, maxSize);
                    mFlushRunnable = new DiskLruCacheFlushRunnable(mDiskLruCache);
                    mFlushScheduledExecutor = Executors.newScheduledThreadPool(1);
                } catch (IOException e) {
                    L.w("Cannot initialize disk cache.", e);
                }
                return null;
            }
        }.execute();
    }

    /**
     * Returns whether the Disk Cache contains the specified URL. You should not call this method
     * from main/UI thread.
     *
     * @param url the URL to search for.
     * @return {@code true} if the Disk Cache is enabled and contains the specified URL, {@code
     *         false} otherwise.
     */
    public boolean contains(final String url) {
        if (mDiskLruCache != null) {
            checkNotOnMainThread();
            try {
                return mDiskLruCache.get(generateDiskCacheKey(url)) != null;
            } catch (IOException e) {
                L.w(String.format("Check Disk Cache if contains URL: <%s> error.", url), e);
            }
        }
        return false;
    }

    /**
     * Returns the value for {@code url} in the disk cache only. You should not call this method
     * from main/UI thread. <p/> If enabled, the result of this method will be cached in the memory
     * cache.
     *
     * @param url        - String representing the URL of the image
     * @return Value for {@code url} from disk cache, or {@code null} if the disk cache is not
     *         enabled.
     */
    public CacheableBitmapDrawable get(final String url) {
        CacheableBitmapDrawable result = null;

        if (mDiskLruCache != null) {
            checkNotOnMainThread();
            try {
                final String key = generateDiskCacheKey(url);
                DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
                if (snapshot != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(snapshot.getInputStream(0));
                    if (bitmap != null) {
                        result = new CacheableBitmapDrawable(mResources, bitmap, url);
                    }
                } else {
                    // the file in cache cannot be decode, remove it
                    mDiskLruCache.remove(key);
                    scheduleDiskCacheFlush();
                }
            } catch (IOException e) {
                L.w(String.format("Cannot get image from disk cache for URL: <%s>", url), e);
            }
        }

        return result;
    }

    /**
     * Caches {@code bitmap} for {@code url} into disk caches.
     * you should not call this method from main/UI thread.
     *
     * @param url    - String representing the URL of the image.
     * @param bitmap - Bitmap which has been decoded from {@code url}.
     * @return CacheableBitmapDrawable which can be used to display the bitmap.
     */
    public CacheableBitmapDrawable put(final String url, final Bitmap bitmap) {
        final CacheableBitmapDrawable result = new CacheableBitmapDrawable(mResources, bitmap, url);

        if (mDiskLruCache != null) {
            checkNotOnMainThread();
            final String key = generateDiskCacheKey(url);
            final ReentrantLock lock = getLock(key);
            lock.lock();
            try {
                DiskLruCache.Editor editor = mDiskLruCache.edit(key);
                bitmap.compress(mCompressFormat, mCompressQuality, editor.newOutputStream(0));
                editor.commit();
            } catch (IOException e) {
                L.w(String.format("Exception occur when put image to disk cache for URL: <%s>", url), e);
            } finally {
                lock.unlock();
                scheduleDiskCacheFlush();
            }
        }

        return result;
    }

    /**
     * Removes the entry for {@code url} from disk caches.
     * you should not call this method from main/UI thread.
     */
    public void remove(final String url) {
        if (mDiskLruCache != null) {
            checkNotOnMainThread();
            try {
                mDiskLruCache.remove(generateDiskCacheKey(url));
                scheduleDiskCacheFlush();
            } catch (IOException e) {
                L.w(String.format("Exception occur when remove image to disk cache for URL: <%s>", url), e);
            }
        }
    }

    public void setCompressFormat(Bitmap.CompressFormat compressFormat) {
        mCompressFormat = compressFormat;
    }

    public void setCompressQuality(int compressQuality) {
        mCompressQuality = compressQuality;
    }

    private static String generateDiskCacheKey(final String url) {
        return MD5Util.md5Hex(url.getBytes());
    }

    private ReentrantLock getLock(final String key) {
        synchronized (mLocks) {
            ReentrantLock lock = mLocks.get(key);
            if (lock == null) {
                lock = new ReentrantLock();
                mLocks.put(key, lock);
            }
            return lock;
        }
    }

    private static void checkNotOnMainThread() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new IllegalStateException("This method should not be called from the main/UI thread.");
        }
    }

    private void scheduleDiskCacheFlush() {
        if (mFlushScheduledFuture != null) {
            // cancel if flushing
            mFlushScheduledFuture.cancel(false);
        }

        mFlushScheduledFuture = mFlushScheduledExecutor.schedule(mFlushRunnable, DISK_CACHE_FLUSH_DELAY_SECS, TimeUnit.SECONDS);
    }

    private static class DiskLruCacheFlushRunnable implements Runnable {

        private final DiskLruCache mDiskLruCache;

        private DiskLruCacheFlushRunnable(DiskLruCache diskLruCache) {
            this.mDiskLruCache = diskLruCache;
        }

        @Override
        public void run() {
            android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            if (mDiskLruCache != null) {
                L.d("Flushing Disk Cache.");
                try {
                    mDiskLruCache.flush();
                } catch (IOException e) {
                    L.w("Exception occur when flushing disk cache.", e);
                }
            }
        }
    }

}
