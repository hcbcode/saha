package com.hcb.saha.system;

import android.app.Activity;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.WindowManager;

/**
 * Keep Android device specific queries, control and management here to stop the
 * bleeding of device logic into the code.
 * 
 * @author steven hadley
 * 
 */
public class DeviceManager {

	private WakeLock wakeLock;

	/**
	 * Keeps the activities window always on.
	 * 
	 * @param activity
	 */
	@SuppressWarnings("deprecation")
	public void keepAwake(Activity activity) {

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			activity.getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else {
			PowerManager pm = (PowerManager) activity
					.getSystemService(Context.POWER_SERVICE);
			wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK,
					DeviceManager.class.getSimpleName());
			wakeLock.acquire();
		}

	}

	public void releaseWakeLock() {
		if (null != wakeLock) {
			wakeLock.release();
			wakeLock = null;
		}
	}
}
