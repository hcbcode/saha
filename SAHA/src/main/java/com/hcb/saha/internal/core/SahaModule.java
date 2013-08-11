package com.hcb.saha.internal.core;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.hcb.saha.external.AccountsManager;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.hcb.saha.internal.facerec.NativeFaceRecognizer;
import com.hcb.saha.internal.service.DataPersistenceService;
import com.hcb.saha.internal.service.TextToSpeechService;
import com.hcb.saha.internal.source.identity.FaceIdentificationProvider;
import com.hcb.saha.internal.source.identity.VoiceIdentificationProvider;
import com.hcb.saha.internal.source.sensor.LightSensorProvider;
import com.hcb.saha.internal.utils.CameraUtils.FaceDetectionHandler;
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

		// Core
		binder.bind(SahaSystemState.class).asEagerSingleton();

		// External
		binder.bind(AccountsManager.class).asEagerSingleton();

		// Face recognition
		binder.bind(FaceRecognizer.class).to(NativeFaceRecognizer.class)
		.asEagerSingleton();

		// Services
		binder.bind(TextToSpeechService.class).asEagerSingleton();
		binder.bind(DataPersistenceService.class).asEagerSingleton();

		// Source providers
		binder.bind(FaceDetectionHandler.class).to(FaceIdentificationProvider.class).asEagerSingleton();
		binder.bind(VoiceIdentificationProvider.class).asEagerSingleton();
		binder.bind(LightSensorProvider.class).asEagerSingleton();
	}

	@Provides
	@Singleton
	Bus getBus() {
		return new Bus(ThreadEnforcer.ANY);
	}
}
