package com.hcb.saha;

import android.app.Application;

import roboguice.RoboGuice;

/**
 * Saha Application instance
 * @author Andreas Borglin
 */
public class SahaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RoboGuice.setBaseApplicationInjector(this, RoboGuice.DEFAULT_STAGE, RoboGuice.newDefaultRoboModule(this), new SahaModule());
    }
}
