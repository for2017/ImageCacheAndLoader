package com.lianghanzhen.image.loaders;

import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;
import com.lianghanzhen.image.R;
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

	private static final int KEY_ITEM_1 = R.id.image_item1;
	private static final int KEY_ITEM_2 = R.id.image_item2;

	private static final int WHAT_FETCH = 1;

	private final AsyncTaskScheduler<T, CacheableBitmapDrawable, AsyncTask<T, CacheableBitmapDrawable>> imageLoadManager;
	protected final CacheConfig config;
	private final Map<T, List<WeakReference<CacheableImageView>>> taskMap = new HashMap<T, List<WeakReference<CacheableImageView>>>();
	private final InternalHandler<T> handler = new InternalHandler<T>(this);

	/* 是否隐藏图片 */
	private boolean hideImage;
	/* 延迟加载图片的时间 */
	private long delay = 100L;

	public ImageLoader(AsyncTaskScheduler<T, CacheableBitmapDrawable, AsyncTask<T, CacheableBitmapDrawable>> imageLoadManager, CacheConfig config, boolean hideImage) {
		this.imageLoadManager = imageLoadManager;
		this.imageLoadManager.registerListener(this);
		this.config = config;
		this.hideImage = hideImage;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void shutdown() {
		handler.removeMessages(WHAT_FETCH);
		taskMap.clear();
		imageLoadManager.shutdown();
	}

	private void displayImage(CacheableImageView imageView, CacheableBitmapDrawable result) {
		if (result != null)
			imageView.setImageDrawable(result);
	}

	private CacheableBitmapDrawable fetchImageFromMemCache(T param) {
		return config.memCache.get(param.getUrl());
	}

	public CacheStatus fetchImage(CacheableImageView imageView, T param, int placeholderResId) {
		if (placeholderResId > 0)
			imageView.setImageResource(placeholderResId);
		if (hideImage)
			return CacheStatus.NONE;

        cancalFetch(imageView);

		CacheableBitmapDrawable result = fetchImageFromMemCache(param);
		if (result != null) {
			displayImage(imageView, result);
			return CacheStatus.MEMORY;
		}

		ImageItem<T> item = new ImageItem<T>(new WeakReference<CacheableImageView>(imageView), param);
		if (delay > 0) {
			imageView.setTag(KEY_ITEM_1, item);

			Message msg = new Message();
			msg.what = WHAT_FETCH;
			msg.obj = item;
			handler.sendMessageDelayed(msg, delay);
		} else {
			doFetchImage(item);
		}
        return CacheStatus.DISK;
	}

	private void doFetchImage(ImageItem<T> item) {
		ImageView imageView = item.ref.get();
		if (imageView == null)
			return;
		imageView.setTag(KEY_ITEM_2, item);
		List<WeakReference<CacheableImageView>> list = taskMap.get(item.param);
		if (list == null) {
			list = new ArrayList<WeakReference<CacheableImageView>>();
			list.add(item.ref);
			taskMap.put(item.param, list);
			imageLoadManager.addTask(item.param);
		} else {
			list.add(item.ref);
		}
	}

	public void cancalFetch(ImageView imageView) {
		@SuppressWarnings("unchecked")
		ImageItem<T> item1 = (ImageItem<T>) imageView.getTag(KEY_ITEM_1);
		if (item1 != null) {
			imageView.setTag(KEY_ITEM_1, null);
			handler.removeMessages(WHAT_FETCH, item1);
		}

		@SuppressWarnings("unchecked")
		ImageItem<T> item2 = (ImageItem<T>) imageView.getTag(KEY_ITEM_2);
		if (item2 != null) {
			imageView.setTag(KEY_ITEM_2, null);
			List<WeakReference<CacheableImageView>> list = taskMap.get(item2.param);
			if (list == null)
				return;
			list.remove(item2.ref);
			if (list.isEmpty()) {
				imageLoadManager.removeTask(item2.param);
				taskMap.remove(item2.param);
			}
		}
	}

	@Override
	public void onTaskSuccess(T params, CacheableBitmapDrawable result) {
		if (params.isEnableMemCache() && result != null)
			config.memCache.put(result);

		List<WeakReference<CacheableImageView>> list = taskMap.remove(params);
		if (list == null)
			return;
		for (WeakReference<CacheableImageView> ref : list) {
			CacheableImageView imageView = ref.get();
			if (imageView == null)
				continue;
			imageView.setTag(KEY_ITEM_2, null);
			if (result != null)
				displayImage(imageView, result);
		}
	}

	@Override
	public void onTaskError(T params, Throwable error) {
		List<WeakReference<CacheableImageView>> list = taskMap.remove(params);
		if (list == null)
			return;
		for (WeakReference<CacheableImageView> ref : list) {
			ImageView imageView = ref.get();
			if (imageView == null)
				continue;
			imageView.setTag(KEY_ITEM_2, null);
		}
	}

	private static class InternalHandler<T extends BaseImageParam> extends Handler {
		private final WeakReference<ImageLoader<T>> ref;

		private InternalHandler(ImageLoader<T> fetcher) {
			ref = new WeakReference<ImageLoader<T>>(fetcher);
		}

		@Override
		public void handleMessage(Message msg) {
			ImageLoader<T> fetcher = ref.get();
			if (fetcher == null)
				return;
			if (msg.what == WHAT_FETCH) {
				@SuppressWarnings("unchecked")
				ImageItem<T> item = (ImageItem<T>) msg.obj;
				ImageView imageView = item.ref.get();
				if (imageView != null) {
					imageView.setTag(KEY_ITEM_1, null);
					fetcher.doFetchImage(item);
				}
			}
		}
	};

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
