package com.hcb.saha.internal.jni;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.inject.Inject;
import com.hcb.saha.internal.core.SahaConfig;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.event.LifecycleEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Interface to native face recognizer class. We execute all face recognition
 * requests on a separate thread managed by a looper and a handler. Requests
 * comes in via the event bus and are placed on the handler messaging queue for
 * synchronous in-order request handling.
 * 
 * @author Andreas Borglin
 */
public class NativeFaceRecognizer {

	private static final String TAG = NativeFaceRecognizer.class
			.getSimpleName();
	private static final String FACE_REC_MODEL_PATH = SahaFileManager
			.getFaceRecModelPath();
	private static final String CLASSIFIER_PATH = SahaFileManager
			.getFaceClassifierPath();

	static {
		// Load the face recognizer library
		System.loadLibrary("facerec");
	}

	private long wrapperRef;
	private Bus eventBus;
	private Handler handler;

	@Inject
	public NativeFaceRecognizer(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
		// Set up a new thread for all face recognition requests
		// Use looper to create a message queue and handler for cross-thread
		// communication
		new Thread() {

			@Override
			public void run() {
				Looper.prepare();
				handler = new Handler();
				Looper.loop();
			}

		}.start();
	}

	/**
	 * Initialise the recogniser with a persisted data model
	 */
	@Subscribe
	public void initRecognizer(LifecycleEvents.MainActivityCreated event) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "Initialising recogniser");
				SahaFileManager.createFaceRecModelFile();
				wrapperRef = loadPersistedModel(FACE_REC_MODEL_PATH,
						SahaConfig.OpenCvParameters.class);
			}
		});
	}

	/**
	 * Close the recognizer and delete any references to it
	 */
	@Subscribe
	public void closeRecognizer(LifecycleEvents.MainActivityDestroyed event) {
		handler.post(new Runnable() {

			@Override
			public void run() {
				Log.d(TAG, "Closing recogniser");
				deleteWrapper(wrapperRef);
				wrapperRef = -1;
			}
		});
	}

	/**
	 * Train the face recognizer with a set of images for a user
	 * 
	 * @param userId
	 *            The user id
	 * @param faceImagePaths
	 *            An array of paths to face images
	 * @return Success or failure
	 */
	// FIXME
	// @Subscribe
	// public void trainRecognizer(
	// final FaceRecognitionEvents.TrainRecognizerRequest event) {
	// handler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// Log.d(TAG, "Training recognizer...");
	// nativeTrainRecognizer(event.getUsersFaces().getUserIds(), event
	// .getUsersFaces().getUserImageFaces(),
	// FACE_REC_MODEL_PATH, CLASSIFIER_PATH, wrapperRef);
	// }
	// });
	// }

	/**
	 * Predict a user id based on a face image
	 * 
	 * @param faceImage
	 *            The face image
	 * @return A user id if successful, -1 if failure
	 */
	// FIXME
	// @Subscribe
	// public void predictUserId(
	// final FaceRecognitionEvents.PredictUserRequest event) {
	// handler.post(new Runnable() {
	//
	// @Override
	// public void run() {
	// Log.d(TAG, "Starting prediction...");
	// int predictedId = nativePredictUserId(event.getImagePath(),
	// FACE_REC_MODEL_PATH, CLASSIFIER_PATH, wrapperRef);
	// Log.d(TAG, "Predicted user id: " + predictedId);
	// eventBus.post(new FaceRecognitionEvents.UserPredictionResult(
	// predictedId));
	// }
	// });
	// }

	/*
	 * Train the recognizer with images for a specific user String[][] ->
	 * Object[] to avoid confusing JNI
	 */
	private native boolean nativeTrainRecognizer(int[] userIds,
			Object[] usersImagePaths, String modelFilePath,
			String classifierPath, long wrapperRef);

	/*
	 * Predict a user based on an image
	 */
	private native int nativePredictUserId(String faceImage,
			String modelFilePath, String classifierPath, long wrapperRef);

	/*
	 * Load a persisted model into the face recognizer
	 */
	private native long loadPersistedModel(String modelFilePath,
			Class<SahaConfig.OpenCvParameters> params);

	/*
	 * Delete the native C++ face recognizer reference
	 */
	private native void deleteWrapper(long wrapperRef);

}
