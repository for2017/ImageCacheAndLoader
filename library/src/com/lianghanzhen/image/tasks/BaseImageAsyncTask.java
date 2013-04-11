package com.lianghanzhen.image.tasks;


import android.graphics.Bitmap;
import com.lianghanzhen.image.cache.CacheConfig;
import com.lianghanzhen.image.cache.CacheableBitmapDrawable;
import com.lianghanzhen.image.concurrents.AsyncTask;
import com.lianghanzhen.image.params.BaseImageParam;

public abstract class BaseImageAsyncTask<T extends BaseImageParam> implements AsyncTask<T, CacheableBitmapDrawable> {

    protected final CacheConfig mCacheConfig;

    protected BaseImageAsyncTask(CacheConfig cacheConfig) {
        mCacheConfig = cacheConfig;
    }

    @Override
    public CacheableBitmapDrawable doAsyncTask(T params) {
        final String url = params.getUrl();
        CacheableBitmapDrawable result = mCacheConfig.mDiskCache.get(url);
        if (result == null) {
            Bitmap bitmap = loadImageFromOtherSource(params);
            if (bitmap != null && params.isEnableDiskCache()) {
                result = mCacheConfig.mDiskCache.put(url, bitmap);
            }
        }
        return result;
    }

    protected abstract Bitmap loadImageFromOtherSource(T params);

}
