package com.lianghanzhen.image.params;

public abstract class BaseImageParam {
	
	private boolean enableMemCache = true;
	private boolean enableDiskCache = true;
	
	public boolean isEnableMemCache() {
		return enableMemCache;
	}

	public void setEnableMemCache(boolean enableMemCache) {
		this.enableMemCache = enableMemCache;
	}

	public boolean isEnableDiskCache() {
		return enableDiskCache;
	}

	public void setEnableDiskCache(boolean enableDiskCache) {
		this.enableDiskCache = enableDiskCache;
	}

	public abstract String getUrl();

}
