package com.lianghanzhen.image.sample;

import android.app.Application;
import com.lianghanzhen.image.cache.CacheConfig;
import com.lianghanzhen.image.loaders.ImageLoader;
import com.lianghanzhen.image.loaders.NetworkImageLoader;
import com.lianghanzhen.image.params.NetworkImageParam;


public class SampleApplication extends Application {

    private static SampleApplication mInstance;

    private static NetworkImageLoader mImageLoader;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static NetworkImageLoader getImageLoader() {
        if (mImageLoader == null) {
            synchronized (mInstance) {
                if (mImageLoader == null) {
                    mImageLoader = new NetworkImageLoader(newCacheConfig());
                }
            }
        }
        return mImageLoader;
    }

    private static CacheConfig newCacheConfig() {
        return new CacheConfig.Builder().memCacheMaxSize(20 * 1024 * 1024).build(mInstance);
    }

    public static SampleApplication getInstance() {
        return mInstance;
    }

}
