package com.lianghanzhen.image.params;

import com.lianghanzhen.image.utils.CacheKeyUtils;
import com.lianghanzhen.image.utils.ImageSize;

public class NetworkImageParam extends BaseImageParam {
	private final String url;
	private final ImageSize imageSize;
	
	public NetworkImageParam(String url, ImageSize imageSize) {
		this.url = url;
		this.imageSize = imageSize;
	}

    @Override
	public String getUrl() {
		return url;
	}
	
	public ImageSize getImageSize() {
		return imageSize;
	}
	
	@Override
	public int hashCode() {
		return (url == null ? 31 : url.hashCode()) + (imageSize == null ? 32 : imageSize.hashCode());
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof NetworkImageParam))
			return false;
		NetworkImageParam other = (NetworkImageParam) o;
		return url.equals(other.url) && (imageSize != null ? imageSize.equals(other.imageSize) : other.imageSize == null);
	}

}
