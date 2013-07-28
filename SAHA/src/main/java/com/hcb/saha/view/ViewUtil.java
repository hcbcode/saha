package com.hcb.saha.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.hcb.saha.R;
import com.hcb.saha.system.DeviceManager;

/**
 * Common UI stuff that can be re-used across views.
 * 
 * @author steven hadley
 * 
 */
public class ViewUtil {

	/**
	 * Full screen and low profile.
	 * 
	 * @param activity
	 */
	public static void goFullScreen(Activity activity) {
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			activity.getWindow().getDecorView()
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
			/**
			 * Android: There is a limitation: because navigation controls are
			 * so important, the least user interaction will cause them to
			 * reappear immediately. When this happens, both this flag and
			 * SYSTEM_UI_FLAG_FULLSCREEN will be cleared automatically, so that
			 * both elements reappear at the same time.
			 * 
			 * Would have to use http://www.42gears.com/surelock/
			 */
		}
	}

	/**
	 * Keeps activity awake.
	 * 
	 * @param activity
	 */
	public static void keepActivityAwake(Activity activity) {
		DeviceManager.keepScreenAwake(activity);
	}

	/**
	 * Setup common SAHA action bar.
	 * 
	 * @param activity
	 */
	public static void customiseActionBar(Activity activity) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = activity.getActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			LayoutInflater inflator = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(R.layout.action_bar, null);
			actionBar.setCustomView(v);
		}
	}

}
