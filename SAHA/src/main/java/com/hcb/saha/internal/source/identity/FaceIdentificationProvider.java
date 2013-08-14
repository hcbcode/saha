package com.hcb.saha.internal.source.identity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera.Face;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.core.SahaConfig;
import com.hcb.saha.internal.core.SahaExceptions.CameraNotActiveException;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
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

	private static final String TAG = FaceIdentificationProvider.class
			.getSimpleName();
	private static final int USER_ANON_RETRY_COUNT = 10;

	private class FaceTimeoutHandler implements Runnable {

		@Override
		public void run() {
			Log.d(TAG,
					"Face timout handler fired. Posting user inactivity event");
			scheduledFuture = null;
			predictUserOnFaceDetected = true;
			eventBus.post(new UserIdentificationEvents.UserInactivitityEvent());
		}

	}

	@Inject
	private FaceRecognizer faceRecognizer;
	@Inject
	private Bus eventBus;
	@Inject
	private SahaSystemState systemState;

	private boolean predictUserOnFaceDetected;
	private int userAnonymousCount;

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> scheduledFuture;
	private FaceTimeoutHandler faceTimeoutHandler;

	public FaceIdentificationProvider() {
		predictUserOnFaceDetected = true;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		faceTimeoutHandler = new FaceTimeoutHandler();
	}

	@Override
	public void onRecognizerTrainingCompleted() {
		// No op for now
	}

	@Override
	public void onPredictionCompleted(int predictedUserId) {

		if (predictedUserId == -1) {
			eventBus.post(new UserIdentificationEvents.AnonymousUserDetected());
			userAnonymousCount = 1;
		} else {
			User user = SahaUserDatabase.getUserFromId(predictedUserId);
			if (user != null) {
				Log.d("USER", "user detected: " + user.getName());
				eventBus.post(new UserIdentificationEvents.RegisteredUserDetected(
						user));
				userAnonymousCount = -1;
			} else {
				Log.e(TAG, "Predicted user object is null");
			}
		}
	}

	@Override
	public void onFaceDetected(Face[] faces, CameraProcessor cameraProcessor) {

		if (scheduledFuture != null) {
			scheduledFuture.cancel(false);
			scheduledFuture = null;
		}

		if (predictUserOnFaceDetected) {
			try {
				Log.d(TAG, "Face found and performing user identification");
				// Now, don't predict again until the flag has been
				// reset
				predictUserOnFaceDetected = false;
				cameraProcessor.takeFacePicture(new FacePictureTakenHandler() {

					@Override
					public void onFacePictureTaken(Bitmap bitmap) {
						String imagePath = SahaFileManager.persistFaceBitmap(
								bitmap, null);
						faceRecognizer.predictUserId(imagePath,
								FaceIdentificationProvider.this);
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

			if (systemState.inAnonymousUserMode()) {
				if (userAnonymousCount >= USER_ANON_RETRY_COUNT) {
					predictUserOnFaceDetected = true;
					userAnonymousCount = 0;
				} else if (userAnonymousCount != -1) {
					userAnonymousCount++;
				}
			}
		}
	}

	@Override
	public void onNoFaceDetected() {

		if (scheduledFuture == null && systemState.inUserMode()) {
			scheduledFuture = scheduler.schedule(faceTimeoutHandler,
					SahaConfig.System.USER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
			eventBus.post(new CameraEvents.FaceDisappearedEvent());
			Log.d(TAG,
					"Face disappeared - scheduling timeout and sending event");
		}
	}
}
