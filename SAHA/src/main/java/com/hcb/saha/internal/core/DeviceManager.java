package com.hcb.saha.internal.core;

import android.app.Activity;
import android.view.WindowManager;

/**
 * A central point for:
 * <ul>
 * <li>Querying device capabilities</li>
 * <li>Querying device settings</li>
 * <li>Updating device settings</li>
 * </ul>
 * 
 * Thus encapsulating and providing a simple interface for managing the device
 * without polluting the other code with various device checks and the API
 * version checks that come with this. </p> TODO: Expand this class when and as
 * methods are needed not guessing up front what is needed but structuring in a
 * way that allows the functionality to grow.
 * 
 * @author steven hadley
 * 
 */
public class DeviceManager {

	/**
	 * Keeps the activities window always on.
	 * 
	 * Due to FULL_WAKE_LOCK being deprecated this method uses
	 * FLAG_KEEP_SCREEN_ON which is best practice.
	 * 
	 * @param activity
	 */
	public static void keepScreenAwake(Activity activity) {
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

}
