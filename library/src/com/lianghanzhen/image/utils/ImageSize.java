package com.lianghanzhen.image.utils;

import android.util.DisplayMetrics;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import java.lang.reflect.Field;


public final class ImageSize {
	public static final ImageSize ZERO = new ImageSize(0, 0);
	
	public final int width;
	public final int height;
	
	public ImageSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	@Override
	public int hashCode() {
		return 31 + width*1000 + height;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof ImageSize))
			return false;
		ImageSize other = (ImageSize) o;
		return width == other.width && height == other.height;
	}
	
	public static ImageSize calculate(final ImageView imageView, ImageSize maxImageSize) {
		DisplayMetrics metrics = imageView.getContext().getResources().getDisplayMetrics();
		LayoutParams params = imageView.getLayoutParams();
		int width = params == null ? 0 : params.width;
		if (width <= 0)
			width = getImageSizeValue(imageView, "mMaxWidth");
		if (width <= 0)
			width = maxImageSize.width;
		if (width <= 0)
			width = metrics.widthPixels;
		
		int height = params == null ? 0 : params.height;
		if (height <= 0)
			height = getImageSizeValue(imageView, "mMaxHeight");
		if (height <= 0)
			height = maxImageSize.height;
		if (height <= 0)
			height = metrics.heightPixels;
		
		return new ImageSize(width, height);
	}
	
	private static int getImageSizeValue(ImageView imageView, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			value = field.getInt(imageView);
			if (value < 0 || value == Integer.MAX_VALUE)
				value = 0;
		} catch (Exception e) {
			L.e("Cannot get ImageView field value by refletion: " + fieldName);
		}
		return value;
	}
	
}
