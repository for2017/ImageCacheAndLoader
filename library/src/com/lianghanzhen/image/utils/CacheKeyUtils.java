package com.lianghanzhen.image.utils;


public final class CacheKeyUtils {
	private static final String kEY_AND_SIZE_SEPARATOR = "_";
	private static final String CACHE_KEY_FORMAT = "%1$s" + kEY_AND_SIZE_SEPARATOR + "%2$dX%3$d";

	private CacheKeyUtils() {}
	
	public static String generate(String key, ImageSize size) {
		return MD5Util.md5Hex(getOriginalKey(key, size).getBytes());
	}
	
	public static String getOriginalKey(String key, ImageSize size) {
		if (size == null)
			return key;
		else
			return String.format(CACHE_KEY_FORMAT, key, size.width, size.height);
	}
	
}
