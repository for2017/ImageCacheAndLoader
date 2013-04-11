package com.lianghanzhen.image.params;

import com.lianghanzhen.image.utils.ImageSize;

public class NetworkImageParam extends BaseImageParam {
	private final String mUrl;
	private final ImageSize mImageSize;
	
	public NetworkImageParam(String url, ImageSize imageSize) {
		mUrl = url;
		mImageSize = imageSize;
	}

    @Override
	public String getUrl() {
		return mUrl;
	}
	
	public ImageSize getImageSize() {
		return mImageSize;
	}
	
	@Override
	public int hashCode() {
		return (mUrl == null ? 31 : mUrl.hashCode()) + (mImageSize == null ? 32 : mImageSize.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof NetworkImageParam))
			return false;
		NetworkImageParam other = (NetworkImageParam) o;
		return mUrl.equals(other.mUrl) && (mImageSize != null ? mImageSize.equals(other.mImageSize) : other.mImageSize == null);
	}

}
