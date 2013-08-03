package com.hcb.saha.internal.core;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hcb.saha.external.AccountsManager;
import com.hcb.saha.external.EmailManager;
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
		// binder.bind(NativeFaceRecognizer.class).in(Singleton.class);
		binder.bind(EmailManager.class).in(Singleton.class);
		binder.bind(AccountsManager.class).in(Singleton.class);
	}

	@Provides
	@Singleton
	Bus getBus() {
		return new Bus(ThreadEnforcer.ANY);
	}
}
