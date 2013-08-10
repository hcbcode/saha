package com.hcb.saha.internal.source.identity;

import android.app.Application;
import android.graphics.Rect;
import android.hardware.Camera.Face;

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
import com.hcb.saha.internal.utils.CameraUtils.FaceDetectionHandler;
import com.hcb.saha.internal.utils.CameraUtils.FacePictureTakenHandler;
import com.squareup.otto.Bus;

/**
 * Responsible for identifying a face
 * 
 * @author Andreas Borglin
 */
@Singleton
public class FaceIdentificationProvider implements FaceRecognitionEventHandler,
		FaceDetectionHandler {

	@Inject
	private FaceRecognizer faceRecognizer;
	@Inject
	private Application context;
	@Inject
	private Bus eventBus;
	private boolean predictUserOnFaceDetected;

	public FaceIdentificationProvider() {
		predictUserOnFaceDetected = true;
	}

	@Override
	public void onRecognizerTrainingCompleted() {
		// No op for now
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

	@Override
	public void onFaceDetected(Face[] faces, CameraProcessor cameraProcessor) {
		if (predictUserOnFaceDetected) {
			try {
				cameraProcessor.takeFacePicture(new FacePictureTakenHandler() {

					@Override
					public void onFacePictureTaken(String imagePath) {
						faceRecognizer.predictUserId(imagePath,
								FaceIdentificationProvider.this);
						// Now, don't predict again until the flag has been
						// reset
						predictUserOnFaceDetected = false;
					}
				});
			} catch (CameraNotActiveException e) {
				e.printStackTrace();
				// TODO
			}
		} else {
			// TODO Support multiple faces?
			Rect face = faces[0].rect;
			eventBus.post(new CameraEvents.FaceAvailableEvent(face.width(),
					face.height()));
		}
	}

	@Override
	public void onNoFaceDetected() {
		if (!predictUserOnFaceDetected) {
			eventBus.post(new CameraEvents.FaceDisappearedEvent());
			predictUserOnFaceDetected = true;
		}
	}
}
