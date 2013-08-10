package com.hcb.saha.internal.source.identity;

import android.app.Application;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.core.SahaExceptions.CameraNotActiveException;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.event.CameraEvents;
import com.hcb.saha.internal.event.UserIdentificationEvents;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.hcb.saha.internal.facerec.FaceRecognizer.FaceRecognitionEventHandler;
import com.hcb.saha.internal.processor.CameraProcessor;
import com.hcb.saha.internal.processor.CameraProcessor.CameraDetectionMode;
import com.hcb.saha.internal.processor.CameraProcessor.CameraDetectionType;
import com.hcb.saha.internal.utils.CameraUtils.FacePictureTakenHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Responsible for identifying a face
 *
 * @author Andreas Borglin
 */
@Singleton
public class FaceIdentificationProvider implements FaceRecognitionEventHandler {

	@Inject
	private FaceRecognizer faceRecognizer;
	@Inject
	private Application context;
	private Bus eventBus;
	private CameraProcessor cameraProcessor;

	@Inject
	public FaceIdentificationProvider(Bus eventBus,
			CameraProcessor cameraProcessor) {
		this.eventBus = eventBus;
		this.cameraProcessor = cameraProcessor;
		eventBus.register(this);
		// TODO: temp for testing?
		// If camera is active, start face detection. If not, the event will
		// trigger it
		if (cameraProcessor.isCameraActive()) {
			identifyUser();
		}
	}

	private void identifyUser() {
		try {
			cameraProcessor.requestDetection(CameraDetectionType.FACE,
					CameraDetectionMode.ONE_OFF);
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
			// TODO
		} catch (CameraNotActiveException e) {
			e.printStackTrace();
			// TODO
		}
	}

	@Subscribe
	public void onCameraReady(CameraEvents.CameraActivatedEvent event) {
		identifyUser();
	}

	@Subscribe
	public void onFaceDetected(CameraEvents.FaceDetectedEvent event) {
		try {
			cameraProcessor.takeFacePicture(new FacePictureTakenHandler() {

				@Override
				public void onFacePictureTaken(String imagePath) {
					faceRecognizer.predictUserId(imagePath,
							FaceIdentificationProvider.this);
				}
			});
		} catch (CameraNotActiveException e) {
			e.printStackTrace();
			// TODO
		}
	}

	@Override
	public void onRecognizerTrainingCompleted() {

	}

	@Override
	public void onPredictionCompleted(int predictedUserId) {

		if (predictedUserId == -1) {
			eventBus.post(new UserIdentificationEvents.AnonymousUserDetected());
		} else {
			User user = SahaUserDatabase
					.getUserFromId(context, predictedUserId);
			eventBus.post(new UserIdentificationEvents.RegisteredUserDetected(
					user));
		}
	}
}
