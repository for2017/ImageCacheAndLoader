package com.lianghanzhen.image.cache;


import android.graphics.Bitmap;
import com.jakewharton.DiskLruCache;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class BitmapDiskLruCache {

    private final DiskLruCache mDiskLruCache;
    private final Map<String, ReentrantLock> locks = new HashMap<String, ReentrantLock>();

    public BitmapDiskLruCache(DiskLruCache mDiskLruCache) {
        this.mDiskLruCache = mDiskLruCache;
    }

    public synchronized CacheableBitmapDrawable put(String key, Bitmap bitmap) {
        return null; // TODO
    }

}
