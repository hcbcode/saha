package com.hcb.saha.internal.core;

import android.util.Log;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hcb.saha.external.AccountsManager;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.hcb.saha.internal.facerec.NativeFaceRecognizer;
import com.hcb.saha.internal.source.identity.FaceIdentificationProvider;
import com.hcb.saha.internal.source.sensor.LightSensorProvider;
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
		Log.d("GUICE", "configure");
		// binder.bind(Application.class).toInstance(application);
		binder.bind(FaceRecognizer.class).to(NativeFaceRecognizer.class)
				.asEagerSingleton();
		binder.bind(SahaSystemState.class).asEagerSingleton();
		binder.bind(AccountsManager.class).asEagerSingleton();
		// binder.bind(UserIdentificationManager.class).
		binder.bind(FaceIdentificationProvider.class).asEagerSingleton();
		//binder.bind(VoiceIdentificationProvider.class).asEagerSingleton();
		binder.bind(LightSensorProvider.class).asEagerSingleton();

	}

	@Provides
	@Singleton
	Bus getBus() {
		return new Bus(ThreadEnforcer.ANY);
	}
}
