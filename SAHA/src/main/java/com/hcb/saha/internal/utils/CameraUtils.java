package com.hcb.saha.internal.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Face;

import com.hcb.saha.internal.processor.CameraProcessor;

/**
 * Camera utils class
 * @author Andreas Borglin
 */
public final class CameraUtils {

	private CameraUtils() {
		// Not intended
	}

	// Rotation on front facing camera
	public static final int FRONT_CAMERA_ROTATION = 90;

	public static interface FacePictureTakenHandler {
		void onFacePictureTaken(String imagePath);
	}
	
	public static interface FaceDetectionHandler {
		void onFaceDetected(Face[] faces, CameraProcessor cameraProcessor);
		void onNoFaceDetected();
	}
	
	public static interface PreviewFrameHandler {
		void onPreviewFrame(byte[] data);
	}

	private static int detectCamera(int cameraType) {
		int cameras = Camera.getNumberOfCameras();
		Camera.CameraInfo camInfo = new Camera.CameraInfo();
		for (int i = 0; i < cameras; ++i) {
			Camera.getCameraInfo(i, camInfo);
			if (camInfo.facing == cameraType) {
				return i;
			}
		}
		return -1;
	}

	public static Camera getFrontFacingCamera() {
		int cameraId = detectCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);
		if (cameraId == -1) {
			return null;
		}

		Camera camera = Camera.open(cameraId);
		camera.setDisplayOrientation(FRONT_CAMERA_ROTATION);
		return camera;
	}

	public static Camera getBackFacingCamera() {
		int cameraId = detectCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
		if (cameraId == -1) {
			return null;
		}

		return Camera.open(cameraId);
	}

	public static boolean isFaceDetectionSupported(Camera camera) {
		Camera.Parameters params = camera.getParameters();
		return params.getMaxNumDetectedFaces() > 0;
	}

	public static Bitmap getBitmapFromFrontCameraData(byte[] data) {
		Matrix matrix = new Matrix();
		matrix.setScale(-1, 1);
		matrix.postRotate(FRONT_CAMERA_ROTATION);
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap rotScaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		bitmap.recycle();
		return rotScaledBitmap;
	}

}
