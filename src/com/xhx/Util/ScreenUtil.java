package com.xhx.Util;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtil {
	/**
	 * ��ȡ��Ļ��ز���
	 * @param context context
	 * @return DisplayMetrics ��Ļ���                metrics����
	 */
	public static DisplayMetrics getScreenSize(Context context){
		DisplayMetrics metrics=new DisplayMetrics();
		WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display=wm.getDefaultDisplay();
		display.getMetrics(metrics);
		return metrics;
		
	}
	
	/**
	 * ��ȡ��Ļdensity 
	 * 
	 * @param context
	 * @return density ��Ļdensity �ܶ�
	 */
	public static float  getDeviceDensity(Context context){
		DisplayMetrics metrics=new DisplayMetrics();
		WindowManager wm=(WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.density;
		
	}
}
