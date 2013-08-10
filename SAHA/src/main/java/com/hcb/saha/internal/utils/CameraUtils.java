package com.hcb.saha.internal.utils;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.util.Log;

import com.hcb.saha.internal.processor.CameraProcessor;

/**
 * Camera utils class
 * 
 * @author Andreas Borglin
 */
public final class CameraUtils {

	private CameraUtils() {
		// Not intended
	}

	private static final String TAG = CameraUtils.class.getSimpleName();

	// Rotation on front facing camera
	public static final int FRONT_CAMERA_ROTATION = 90;
	public static final float PIC_ASPECT_RATIO = 0.75f;

	public static interface FacePictureTakenHandler {
		void onFacePictureTaken(Bitmap bitmap);
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

	public static Matrix getMatrixForCameraDriverTranslation(int dstWidth,
			int dstHeight) {
		Matrix matrix = new Matrix();
		matrix.setScale(-1, 1);
		matrix.postRotate(FRONT_CAMERA_ROTATION);
		// Camera driver coordinates range from (-1000, -1000) to (1000,
		// 1000).
		matrix.postScale(dstWidth / 2000f, dstHeight / 2000f);
		matrix.postTranslate(dstWidth / 2f, dstHeight / 2f);
		return matrix;
	}

	public static void setParameters(Camera camera) {
		Camera.Parameters params = camera.getParameters();
		// Get the first (highest res) preview size
		Camera.Size previewSize = params.getSupportedPreviewSizes().get(0);
		Log.d(TAG, "Setting preview size to: " + previewSize.width + ":"
				+ previewSize.height);
		params.setPreviewSize(previewSize.width, previewSize.height);

		// For output picture size, we want the smallest possible size
		// that maintains our desired aspect ratio
		List<Camera.Size> picSizes = params.getSupportedPictureSizes();
		Camera.Size preferredSize = picSizes.get(0);
		for (Camera.Size size : picSizes) {
			if ((float) size.height / (float) size.width == PIC_ASPECT_RATIO) {
				preferredSize = size;
			}
		}
		Log.d(TAG, "Setting picture size to: " + preferredSize.width + ":"
				+ preferredSize.height);
		params.setPictureSize(preferredSize.width, preferredSize.height);

		camera.setParameters(params);
	}

}
