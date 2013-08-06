package com.hcb.saha.internal.utils;

import android.hardware.Camera;

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

}
