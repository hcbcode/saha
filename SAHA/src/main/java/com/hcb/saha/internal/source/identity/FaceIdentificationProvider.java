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
import com.hcb.saha.R;
import com.hcb.saha.internal.core.SahaExceptions.CameraNotActiveException;
import com.hcb.saha.internal.core.SahaRuntimeConfig;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.event.CameraEvents;
import com.hcb.saha.internal.event.SystemEvents;
import com.hcb.saha.internal.event.UserIdentificationEvents;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.hcb.saha.internal.facerec.FaceRecognizer.FaceRecognitionEventHandler;
import com.hcb.saha.internal.processor.CameraProcessor;
import com.hcb.saha.internal.utils.CameraUtils.FaceDetectionHandler;
import com.hcb.saha.internal.utils.CameraUtils.FacePictureTakenHandler;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

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
	private SahaSystemState systemState;
	private Bus eventBus;

	private boolean predictUserOnFaceDetected;
	private int userAnonymousCount;
	private int userTimeout;

	private ScheduledExecutorService scheduler;
	private ScheduledFuture<?> scheduledFuture;
	private FaceTimeoutHandler faceTimeoutHandler;

	@Inject
	public FaceIdentificationProvider(SahaRuntimeConfig runtimeConfig, Bus eventBus) {
		this.eventBus = eventBus;
		predictUserOnFaceDetected = true;
		scheduler = Executors.newSingleThreadScheduledExecutor();
		faceTimeoutHandler = new FaceTimeoutHandler();
		userTimeout = runtimeConfig.getInt(R.string.system_user_timeout_key);
		eventBus.register(this);
	}

	@Subscribe
	public void onSystemsSettingChanged(SystemEvents.SystemSettingChangedEvent event) {
		// TODO: Check if this is the setting that has changed
		userTimeout = event.getRuntimeConfig().getInt(R.string.system_user_timeout_key);
		Log.d(TAG, "user timeout changed to: " + userTimeout);
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

		// TODO: Add support for userTimeout being "Never"
		if (scheduledFuture == null && systemState.inUserMode()) {
			scheduledFuture = scheduler.schedule(faceTimeoutHandler,
					userTimeout, TimeUnit.SECONDS);
			eventBus.post(new CameraEvents.FaceDisappearedEvent());
			Log.d(TAG,
					"Face disappeared - scheduling timeout (" + userTimeout + " seconds) and sending event");
		}
	}
}
