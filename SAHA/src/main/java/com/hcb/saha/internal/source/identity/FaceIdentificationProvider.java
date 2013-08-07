package com.hcb.saha.internal.source.identity;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.event.CameraEvents;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Responsible for identifying a face
 * 
 * @author Andreas Borglin
 */
@Singleton
public class FaceIdentificationProvider {

	@Inject
	private FaceRecognizer faceRecognizer;
	@Inject
	private Bus eventBus;

	public FaceIdentificationProvider() {
		eventBus.register(this);
	}

	@Subscribe
	public void onFaceDetected(CameraEvents.FaceDetectedEvent event) {

	}

}
