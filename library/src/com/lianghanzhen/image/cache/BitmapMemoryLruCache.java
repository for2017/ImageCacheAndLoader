package com.lianghanzhen.image.cache;


import android.support.v4.util.LruCache;

public class BitmapMemoryLruCache extends LruCache<String, CacheableBitmapDrawable> {

    public BitmapMemoryLruCache(int maxSize) {
        super(maxSize);
    }

    public CacheableBitmapDrawable put(CacheableBitmapDrawable value) {
        if (value != null) {
            value.setCached(true);
            return super.put(value.getUrl(), value);
        }
        return null;
    }

    @Override
    protected void entryRemoved(boolean evicted, String key, CacheableBitmapDrawable oldValue, CacheableBitmapDrawable newValue) {
        oldValue.setCached(false);
    }

    @Override
    protected int sizeOf(String key, CacheableBitmapDrawable value) {
        return value.getMemorySize();
    }
}
