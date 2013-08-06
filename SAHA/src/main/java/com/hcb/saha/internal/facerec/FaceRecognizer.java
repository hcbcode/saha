package com.hcb.saha.internal.facerec;

/**
 * Interface for face recognition
 * @author Andreas Borglin
 */
public interface FaceRecognizer {

	/**
	 * Event handler interface for face recognition callbacks
	 */
	public static interface FaceRecognitionEventHandler {
		void onRecognizerTrainingCompleted();

		void onPredictionCompleted(int predictedUserId);
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
	void trainRecognizer(final int[] userIds, final String[][] usersFaces,
			final FaceRecognitionEventHandler eventHandler);

	/**
	 * Predict a user id based on a face image
	 * 
	 * @param faceImage
	 *            The face image
	 * @return A user id if successful, -1 if failed to identify
	 */
	void predictUserId(final String imagePath,
			final FaceRecognitionEventHandler eventHandler);

}
