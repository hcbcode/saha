package com.hcb.saha.internal.ui.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.hcb.saha.R;
import com.hcb.saha.internal.core.DeviceManager;

/**
 * Common UI stuff that can be re-used across views.
 *
 * @author Steven Hadley
 *
 */
public final class ViewUtil {

	private ViewUtil() {
		// Not intended
	}

	/**
	 * Full screen
	 */
	public static void goFullScreen(Activity activity) {
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

	/**
	 * Hide navigation -> low profile
	 */
	public static void hideNavigation(Activity activity) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			activity.getWindow()
					.getDecorView()
					.setSystemUiVisibility(
							View.SYSTEM_UI_FLAG_LOW_PROFILE
									| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
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
	 */
	public static void keepActivityAwake(Activity activity) {
		DeviceManager.keepScreenAwake(activity);
	}

	/**
	 * Setup common SAHA action bar.
	 *
	 * FIXME: This is currently specific to home?
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
