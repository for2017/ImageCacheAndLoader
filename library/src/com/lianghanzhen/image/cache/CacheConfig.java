package com.lianghanzhen.image.cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import com.lianghanzhen.image.utils.ImageSize;

public class CacheConfig {
	public final Context context;
	public final ImageSize maxImageSize;
	public final BitmapMemoryLruCache memCache;
	public final BitmapDiskLruCache diskCache;
	public final CompressFormat compressFormat;
	public final int compressQuality;
	public final Bitmap.Config colorConfig;

	private CacheConfig(final Context context, final Builder builder) {
		this.context = context;
		maxImageSize = builder.maxImageSize;
		memCache = builder.memCache;
		diskCache = builder.diskCache;
		compressFormat = builder.compressFormat;
		compressQuality = builder.compressQuality;
		colorConfig = builder.colorConfig;
	}

	public static class Builder {
		private ImageSize maxImageSize = new ImageSize(0, 0);
		private BitmapMemoryLruCache memCache;
		private int memCacheMaxSize = 5 * 1024 * 1024;
		private BitmapDiskLruCache diskCache;
		private int diskCacheMaxSize = 20 * 1024 * 1024;
		private CompressFormat compressFormat = CompressFormat.PNG;
		private int compressQuality = 100;
		private Bitmap.Config colorConfig = Config.RGB_565;

		public CacheConfig build(Context context) {
			if (memCache == null)
				memCache = new BitmapMemoryLruCache(memCacheMaxSize);
			if (diskCache == null)
				diskCache = new BitmapDiskLruCache(context, diskCacheMaxSize);
            diskCache.setCompressFormat(compressFormat);
            diskCache.setCompressQuality(compressQuality);
			return new CacheConfig(context, this);
		}

		public Builder maxImageSize(ImageSize size) {
			this.maxImageSize = size;
			return this;
		}

		public Builder memCache(BitmapMemoryLruCache memCache) {
			this.memCache = memCache;
			return this;
		}

		public Builder memCacheMaxSize(int size) {
			this.memCacheMaxSize = size;
			return this;
		}

		public Builder diskCache(BitmapDiskLruCache diskCache) {
			this.diskCache = diskCache;
			return this;
		}

		public Builder diskCacheMaxSize(int size) {
			this.diskCacheMaxSize = size;
			return this;
		}

		public Builder compressFormat(CompressFormat format) {
			this.compressFormat = format;
			return this;
		}

		public Builder compressQuality(int quality) {
			this.compressQuality = quality;
			return this;
		}

		public Builder colorConfig(Bitmap.Config config) {
			this.colorConfig = config;
			return this;
		}
		
	}
	
}
