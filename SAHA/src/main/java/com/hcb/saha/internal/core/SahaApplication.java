package com.hcb.saha.internal.core;

import roboguice.RoboGuice;
import android.app.Application;

/**
 * Saha Application instance
 * 
 * @author Andreas Borglin
 */
public class SahaApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE,
				RoboGuice.newDefaultRoboModule(this), new SahaModule());
	}

}
