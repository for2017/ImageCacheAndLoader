package com.lianghanzhen.image.loaders;

import com.lianghanzhen.image.cache.CacheConfig;
import com.lianghanzhen.image.cache.CacheableBitmapDrawable;
import com.lianghanzhen.image.cache.CacheableImageView;
import com.lianghanzhen.image.concurrents.AsyncTask;
import com.lianghanzhen.image.concurrents.AsyncTaskScheduler;
import com.lianghanzhen.image.params.NetworkImageParam;
import com.lianghanzhen.image.tasks.NetworkImageAsyncTask;
import com.lianghanzhen.image.utils.ImageSize;

public class ImageNetworkFetcher extends ImageFetcher<NetworkImageParam> {

	public ImageNetworkFetcher(CacheConfig config, boolean hideImage) {
		super(new AsyncTaskScheduler<NetworkImageParam, CacheableBitmapDrawable, AsyncTask<NetworkImageParam, CacheableBitmapDrawable>>(new NetworkImageAsyncTask(config)), config, hideImage);
	}

	public void fetchImage(CacheableImageView imageView, String url, boolean autoCalculateSize, int placeholderResId) {
		fetchImage(imageView, new NetworkImageParam(url, autoCalculateSize ? ImageSize.calculate(imageView, config.maxImageSize) : ImageSize.ZERO), placeholderResId);
	}

}
