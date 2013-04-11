package com.lianghanzhen.image.loaders;

import com.lianghanzhen.image.R;
import com.lianghanzhen.image.cache.CacheConfig;
import com.lianghanzhen.image.cache.CacheableBitmapDrawable;
import com.lianghanzhen.image.cache.CacheableImageView;
import com.lianghanzhen.image.concurrents.AsyncTask;
import com.lianghanzhen.image.concurrents.AsyncTaskScheduler;
import com.lianghanzhen.image.params.NetworkImageParam;
import com.lianghanzhen.image.tasks.NetworkImageAsyncTask;
import com.lianghanzhen.image.utils.ImageSize;

public class NetworkImageLoader extends ImageLoader<NetworkImageParam> {

	public NetworkImageLoader(CacheConfig config, boolean hideImage) {
		super(new AsyncTaskScheduler<NetworkImageParam, CacheableBitmapDrawable, AsyncTask<NetworkImageParam, CacheableBitmapDrawable>>(new NetworkImageAsyncTask(config)), config, hideImage);
	}

    public NetworkImageLoader(CacheConfig cacheConfig) {
        this(cacheConfig, false);
    }

	public CacheStatus fetchImage(CacheableImageView imageView, String url, boolean autoCalculateSize, int placeholderResId) {
		return fetchImage(imageView, new NetworkImageParam(url, autoCalculateSize ? ImageSize.calculate(imageView, config.maxImageSize) : ImageSize.ZERO), placeholderResId);
	}

    public CacheStatus fetchImage(CacheableImageView imageView, String url) {
        return fetchImage(imageView, url, true, R.drawable.placeholder);
    }

}
