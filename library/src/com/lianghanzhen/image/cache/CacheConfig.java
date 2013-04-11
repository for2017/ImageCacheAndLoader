package com.lianghanzhen.image.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import com.lianghanzhen.image.utils.ImageSize;

public class CacheConfig {
	public final Context mContext;
	public final ImageSize mMaxImageSize;
	public final BitmapMemoryLruCache mMemCache;
	public final BitmapDiskLruCache mDiskCache;
	public final CompressFormat mCompressFormat;
	public final int mCompressQuality;
	public final Bitmap.Config mColorConfig;

	private CacheConfig(final Context context, final Builder builder) {
		mContext = context;
		mMaxImageSize = builder.mMaxImageSize;
		mMemCache = builder.mMemCache;
		mDiskCache = builder.mDiskCache;
		mCompressFormat = builder.mCompressFormat;
		mCompressQuality = builder.mCompressQuality;
		mColorConfig = builder.mColorConfig;
	}

	public static class Builder {
		private ImageSize mMaxImageSize = new ImageSize(0, 0);
		private BitmapMemoryLruCache mMemCache;
		private int mMemCacheMaxSize = 5 * 1024 * 1024;
		private BitmapDiskLruCache mDiskCache;
		private int mDiskCacheMaxSize = 20 * 1024 * 1024;
		private CompressFormat mCompressFormat = CompressFormat.PNG;
		private int mCompressQuality = 100;
		private Bitmap.Config mColorConfig = Config.RGB_565;

		public CacheConfig build(Context context) {
			if (mMemCache == null)
				mMemCache = new BitmapMemoryLruCache(mMemCacheMaxSize);
			if (mDiskCache == null)
				mDiskCache = new BitmapDiskLruCache(context, mDiskCacheMaxSize);
            mDiskCache.setCompressFormat(mCompressFormat);
            mDiskCache.setCompressQuality(mCompressQuality);
			return new CacheConfig(context, this);
		}

		public Builder maxImageSize(ImageSize size) {
			mMaxImageSize = size;
			return this;
		}

		public Builder memCache(BitmapMemoryLruCache memCache) {
			this.mMemCache = memCache;
			return this;
		}

		public Builder memCacheMaxSize(int size) {
			mMemCacheMaxSize = size;
			return this;
		}

		public Builder diskCache(BitmapDiskLruCache diskCache) {
			mDiskCache = diskCache;
			return this;
		}

		public Builder diskCacheMaxSize(int size) {
			mDiskCacheMaxSize = size;
			return this;
		}

		public Builder compressFormat(CompressFormat format) {
			mCompressFormat = format;
			return this;
		}

		public Builder compressQuality(int quality) {
			mCompressQuality = quality;
			return this;
		}

		public Builder colorConfig(Bitmap.Config config) {
			mColorConfig = config;
			return this;
		}
		
	}
	
}
