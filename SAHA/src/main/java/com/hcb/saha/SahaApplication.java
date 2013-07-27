package com.hcb.saha;

import roboguice.RoboGuice;
import android.app.Application;

import com.google.inject.Inject;
import com.hcb.saha.system.DeviceManager;

/**
 * Saha Application instance
 * 
 * @author Andreas Borglin
 */
public class SahaApplication extends Application {

	@Inject
	private DeviceManager deviceManager;

	@Override
	public void onCreate() {
		super.onCreate();
		RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
				RoboGuice.newDefaultRoboModule(this), new SahaModule());
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		deviceManager.releaseWakeLock();
	}

}
