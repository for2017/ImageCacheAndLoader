package com.lianghanzhen.image.loaders;

import android.widget.ImageView;
import com.lianghanzhen.image.cache.CacheConfig;
import com.lianghanzhen.image.cache.CacheableBitmapDrawable;
import com.lianghanzhen.image.cache.CacheableImageView;
import com.lianghanzhen.image.concurrents.AsyncTask;
import com.lianghanzhen.image.concurrents.AsyncTaskListener;
import com.lianghanzhen.image.concurrents.AsyncTaskScheduler;
import com.lianghanzhen.image.params.BaseImageParam;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ImageLoader<T extends BaseImageParam> implements AsyncTaskListener<T, CacheableBitmapDrawable> {

    public static enum CacheStatus {
        NONE, DISK, MEMORY;
    }

	private final AsyncTaskScheduler<T, CacheableBitmapDrawable, AsyncTask<T, CacheableBitmapDrawable>> mAsyncTaskScheduler;
	protected final CacheConfig mCacheConfig;
	private final Map<T, List<WeakReference<CacheableImageView>>> mTaskMap = new HashMap<T, List<WeakReference<CacheableImageView>>>();

	private boolean mHideImage;

	public ImageLoader(AsyncTaskScheduler<T, CacheableBitmapDrawable, AsyncTask<T, CacheableBitmapDrawable>> asyncTaskScheduler, CacheConfig cacheConfig, boolean hideImage) {
		mAsyncTaskScheduler = asyncTaskScheduler;
		mAsyncTaskScheduler.registerListener(this);
        mCacheConfig = cacheConfig;
		mHideImage = hideImage;
	}

	public CacheStatus loadImage(CacheableImageView imageView, T param, int placeholderResId) {
		if (placeholderResId > 0)
			imageView.setImageResource(placeholderResId);
		if (mHideImage)
			return CacheStatus.NONE;

        cancelLoad(imageView);

		CacheableBitmapDrawable result = longImageFromMemCache(param);
		if (result != null) {
			displayImage(imageView, result);
			return CacheStatus.MEMORY;
		}

        loadImageFromDiskCache(new ImageItem<T>(new WeakReference<CacheableImageView>(imageView), param));

        return CacheStatus.DISK;
	}

    private CacheableBitmapDrawable longImageFromMemCache(T param) {
        return mCacheConfig.mMemCache.get(param.getUrl());
    }

    private void displayImage(CacheableImageView imageView, CacheableBitmapDrawable result) {
        if (result != null)
            imageView.setImageDrawable(result);
    }

	private void loadImageFromDiskCache(ImageItem<T> imageItem) {
		CacheableImageView imageView = imageItem.ref.get();
		if (imageView == null)
			return;

        imageView.setTag(imageItem);
		List<WeakReference<CacheableImageView>> list = mTaskMap.get(imageItem.param);
		if (list == null) {
			list = new ArrayList<WeakReference<CacheableImageView>>();
			list.add(imageItem.ref);
			mTaskMap.put(imageItem.param, list);
			mAsyncTaskScheduler.addTask(imageItem.param);
		} else {
			list.add(imageItem.ref);
		}
	}

	private void cancelLoad(CacheableImageView imageView) {
		ImageItem<T> imageItem = (ImageItem<T>) imageView.getTag();
        if (imageItem != null && imageItem.ref.get() != null) {
            T params = imageItem.param;
            if (params != null) {
                imageView.setTag(null);
                List<WeakReference<CacheableImageView>> list = mTaskMap.get(params);
                if (list == null)
                    return;
                list.remove(imageItem.ref);
                if (list.isEmpty()) {
                    mAsyncTaskScheduler.removeTask(imageItem.param);
                    mTaskMap.remove(imageItem.param);
                }
            }
        }
	}

	@Override
	public void onTaskSuccess(T params, CacheableBitmapDrawable result) {
		if (params.isEnableMemCache() && result != null)
            mCacheConfig.mMemCache.put(result);

		List<WeakReference<CacheableImageView>> list = mTaskMap.remove(params);
		if (list == null)
			return;
		for (WeakReference<CacheableImageView> ref : list) {
			CacheableImageView imageView = ref.get();
			if (imageView == null)
				continue;
			imageView.setTag(null);
			if (result != null)
				displayImage(imageView, result);
		}
	}

	@Override
	public void onTaskError(T params, Throwable error) {
		List<WeakReference<CacheableImageView>> list = mTaskMap.remove(params);
		if (list == null)
			return;
		for (WeakReference<CacheableImageView> ref : list) {
			ImageView imageView = ref.get();
			if (imageView == null)
				continue;
			imageView.setTag(null);
		}
	}

	private static class ImageItem<T extends BaseImageParam> {
		private final WeakReference<CacheableImageView> ref;
		private final T param;

		private ImageItem(WeakReference<CacheableImageView> ref, T param) {
			super();
			this.ref = ref;
			this.param = param;
		}
	}
}
