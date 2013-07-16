package com.hcb.saha.jni;

import com.hcb.saha.data.SahaFileManager;

/**
 * Interface to native face recognizer class
 * 
 * @author Andreas Borglin
 */
public class NativeFaceRecognizer {

	private static String FACE_REC_MODEL_PATH = SahaFileManager
			.getFaceRecModelPath();

	/**
	 * Train the face recognizer with a set of images for a user
	 * 
	 * @param userId
	 *            The user id
	 * @param faceImagePaths
	 *            An array of paths to face images
	 * @return Success or failure
	 */
	public synchronized boolean trainRecognizer(String[][] usersImagePaths) {
		SahaFileManager.createFaceRecModelFile();
		return nativeTrainRecognizer(usersImagePaths, FACE_REC_MODEL_PATH);
	}

	/**
	 * Predict a user id based on a face image
	 * 
	 * @param faceImage
	 *            The face image
	 * @return A user id if successful, -1 if failure
	 */
	public synchronized int predictUserId(String faceImage) {
		return nativePredictUserId(faceImage, FACE_REC_MODEL_PATH);
	}

	/*
	 * Train the recognizer with images for a specific user
	 * String[][] -> Object[] to avoid confusing JNI
	 */
	private native boolean nativeTrainRecognizer(Object[] usersImagePaths,
			String modelFilePath);

	/*
	 * Predict a user based on an image
	 */
	private native int nativePredictUserId(String faceImage,
			String modelFilePath);

}
