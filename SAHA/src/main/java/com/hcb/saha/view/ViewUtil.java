package com.hcb.saha.view;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

import com.hcb.saha.R;

/**
 * Common UI stuff.
 * 
 * @author steven hadley
 * 
 */
public class ViewUtil {

	/**
	 * Full screen and go low profile.
	 * 
	 * @param activity
	 */
	public static void goFullScreen(Activity activity) {
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			activity.getWindow().getDecorView()
					.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

		}
	}

	/**
	 * Setup common action bar.
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
