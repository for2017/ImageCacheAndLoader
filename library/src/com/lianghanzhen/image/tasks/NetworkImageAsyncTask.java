package com.lianghanzhen.image.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import com.lianghanzhen.image.cache.CacheConfig;
import com.lianghanzhen.image.params.NetworkImageParam;
import com.lianghanzhen.image.utils.CacheKeyUtils;
import com.lianghanzhen.image.utils.ImageSize;
import com.lianghanzhen.image.utils.L;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class NetworkImageAsyncTask extends BaseImageAsyncTask<NetworkImageParam> {

    private static final int OOM_ATTEMPT_DECODE_TIMES = 3;
    private static final int BUFFER_SIZE = 8 * 1024;

    public NetworkImageAsyncTask(CacheConfig config) {
        super(config);
    }

    @Override
    protected Bitmap loadImageFromOtherSource(NetworkImageParam params) {

        L.d("Downloading image: " + CacheKeyUtils.getOriginalKey(params.getUrl(), params.getImageSize()));

        HttpURLConnection urlConnection = null;
        InputStream in = null;
        Bitmap image = null;
        try {
            urlConnection = (HttpURLConnection) new URL(params.getUrl()).openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream(), BUFFER_SIZE);
            BitmapFactory.Options options = getBitmapOptions(params.getUrl(), params.getImageSize());
            for (int i = 1; i <= OOM_ATTEMPT_DECODE_TIMES; i++) {
                try {
                    if (i > 1)
                        in.reset();
                    image = BitmapFactory.decodeStream(in, null, options);
                    if (image == null)
                        return image;
                } catch (OutOfMemoryError e) {
                    L.e(i + ": OutOfMemory.", e);
                    switch (i) {
                        case 1:
                            System.gc();
                            break;
                        case 2:
                            mCacheConfig.mMemCache.evictAll();
                            System.gc();
                            break;
                        case 3:
                            throw e;
                    }
                    SystemClock.sleep(i * 1000);
                    continue;
                }
                break;
            }

        } catch (final IOException e) {
            L.e("Error in downloadBitmap - " + e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    L.e("Cannot close input stream.", e);
                }
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }

        return image;
    }

    private BitmapFactory.Options getBitmapOptions(String url, ImageSize imageSize) throws IOException {
        BitmapFactory.Options options = new BitmapFactory.Options();

        if (imageSize != null && imageSize.width > 0 && imageSize.height > 0) {
            HttpURLConnection urlConnection = null;
            InputStream in = null;
            int outWidth = 0;
            int outHeight = 0;
            try {
                urlConnection = (HttpURLConnection) new URL(url).openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream(), BUFFER_SIZE);
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(in, null, options);
                outWidth = options.outWidth;
                outHeight = options.outHeight;
            } catch (IOException e) {
                L.e("Error in downloadBitmap - " + e.getMessage(), e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        L.e("Cannot close input stream.", e);
                    }
                }
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            if (outWidth > 0 && outHeight > 0) {// calculate scale
                int imageWidth = imageSize.width;
                int imageHeight = imageSize.height;
                int newWidth;
                int newHeight;
                int scale = 1;
                while ((newWidth = outWidth / scale) >= imageWidth || (newHeight = outHeight / scale) >= imageHeight) {
                    scale++;
                }
                if ((imageWidth - newWidth) > imageWidth / 4 || (imageHeight - newHeight) > imageHeight / 4) {
                    scale--;
                    if (scale < 1)
                        scale = 1;
                }
                options.inSampleSize = scale;
                L.d(String.format("Decode options - OutSize: %dX%d; TargetSize: %dX%d; Scale: %d", options.outWidth, options.outHeight, imageWidth, imageHeight, scale));
            }
        }

        options.inDither = false;
        options.inPreferredConfig = mCacheConfig.mColorConfig;
        options.inJustDecodeBounds = false;
        return options;
    }

}
