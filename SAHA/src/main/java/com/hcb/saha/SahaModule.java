package com.hcb.saha;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hcb.saha.data.AccountsManager;
import com.hcb.saha.data.EmailManager;
import com.hcb.saha.jni.NativeFaceRecognizer;
import com.hcb.saha.system.DeviceManager;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Guice module
 * 
 * @author Andreas Borglin
 */
public class SahaModule implements Module {

	@Override
	public void configure(Binder binder) {
		binder.bind(NativeFaceRecognizer.class).in(Singleton.class);
		binder.bind(EmailManager.class).in(Singleton.class);
		binder.bind(AccountsManager.class).in(Singleton.class);
		binder.bind(DeviceManager.class).in(Singleton.class);

	}

	@Provides
	@Singleton
	Bus getBus() {
		return new Bus(ThreadEnforcer.ANY);
	}
}
