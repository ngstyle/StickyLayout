package com.touch18.finaldemo.util;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class InitUtils {
	public static void initImageLoader(Context context) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		// @formatter:off
		DisplayImageOptions defaultDisplayImageOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.ARGB_8888).build();
		int memoryCacheSize = (int) (Math.min(
				Runtime.getRuntime().maxMemory() / 8, 8 * 1024 * 1024));
//		MemoryCacheAware<String, Bitmap> memoryCache = new LRULimitedMemoryCache(
//				memoryCacheSize);
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//				.memoryCache(memoryCache)
				.defaultDisplayImageOptions(defaultDisplayImageOptions)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
//				.discCache(new UnlimitedDiscCache(new File("")))
				.tasksProcessingOrder(QueueProcessingType.FIFO).build();
		// @formatter:on
		imageLoader.init(config);
	}
}
