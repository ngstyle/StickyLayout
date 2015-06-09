package com.touch18.finaldemo.util;

import android.annotation.TargetApi;
import android.content.res.Resources;
import android.os.Build;

/**
 * 帮助类
 * 
 * @ClassName: UiUtils
 * @Description: TODO
 * @author wenxucheng
 * @date 2014-7-18
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UiUtils {
	/**
	 * 将dp转换为px
	 * 
	 * @param context
	 * @param dp
	 * @return
	 */
	public static int Dp2Px(int dp) {
		return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
	}
}
