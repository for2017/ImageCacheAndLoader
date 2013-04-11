package com.lianghanzhen.image.params;

public abstract class BaseImageParam {
	
	private boolean mEnableMemCache = true;
	private boolean mEnableDiskCache = true;
	
	public boolean isEnableMemCache() {
		return mEnableMemCache;
	}

	public void setEnableMemCache(boolean enableMemCache) {
		mEnableMemCache = enableMemCache;
	}

	public boolean isEnableDiskCache() {
		return mEnableDiskCache;
	}

	public void setEnableDiskCache(boolean enableDiskCache) {
		mEnableDiskCache = enableDiskCache;
	}

	public abstract String getUrl();

}
