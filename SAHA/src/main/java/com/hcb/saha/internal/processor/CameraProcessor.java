package com.hcb.saha.internal.processor;

import java.io.IOException;

import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.event.CameraEvents.FaceDetectedEvent;
import com.hcb.saha.internal.utils.CameraUtils;
import com.squareup.otto.Bus;

/**
 * Responsible for all things camera. This can be set in different modes from
 * the client to indicate what the camera processor should be focusing on. By
 * default, the camera will be in movement detection mode.
 * 
 * As soon as movement is detected, an event will be fired that a client can
 * listen to and update the mode accordingly. For instance, we might want to try
 * to detect faces as soon as movement is detected to check whether someone has
 * approached the device and is ready to interact with it.
 * 
 * TODO Movement detection, barcode detection, "object detection"
 * 
 * @author Andreas Borglin
 */
@Singleton
public class CameraProcessor implements PreviewCallback, PictureCallback,
		Callback, FaceDetectionListener {

	private static final String TAG = CameraProcessor.class.getSimpleName();
	private static final int PRIO_NONE = 0;
	private static final int PRIO_LOW = 1;
	private static final int PRIO_MEDIUM = 2;
	private static final int PRIO_HIGH = 3;

	/**
	 * Type of detection
	 */
	public static enum CameraDetectionType {
		MOVEMENT(PRIO_LOW), FACE(PRIO_HIGH), BARCODE(PRIO_MEDIUM), OBJECT(
				PRIO_MEDIUM);

		private final int priority;

		CameraDetectionType(int priority) {
			this.priority = priority;
		}

		public int getPriority() {
			return priority;
		}

	}

	/**
	 * Detection mode
	 */
	public static enum CameraDetectionMode {
		ONE_OFF, STREAM
	}

	@Inject
	private Bus eventBus;
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private CameraDetectionType detectionType;
	private CameraDetectionMode detectionMode;

	/**
	 * This must be called in onCreate of the client activity for the
	 * surfaceholder callbacks to be called.
	 * 
	 * @param surfaceView
	 *            Surface view to render camera preview on
	 */
	public void startCamera(SurfaceView surfaceView)
			throws UnsupportedOperationException {
		Log.d(TAG, "startCamera");
		camera = CameraUtils.getFrontFacingCamera();
		if (camera == null) {
			throw new UnsupportedOperationException(
					"Device has no front-facing camera");
		}
		// Front facing camera found. Woho!
		resetDetectionState();
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setSizeFromLayout();
		surfaceHolder.addCallback(this);
	}

	/**
	 * Check if the camera is active.
	 * 
	 * @return True if active.
	 */
	public boolean isCameraActive() {
		return camera != null;
	}

	/**
	 * Request a change to the detection type and mode. Depending on current and
	 * new priority, the requests might be granted or denied.
	 * 
	 * @param type
	 *            Type of detection
	 * @param mode
	 *            Detection mode
	 * @return True if granted, false if denied
	 */
	public boolean requestDetection(CameraDetectionType type,
			CameraDetectionMode mode) throws UnsupportedOperationException {
		Log.d(TAG,
				"requestDetection type: " + type.name() + ", mode: "
						+ mode.name());
		// Check the type priority
		if (type.getPriority() >= detectionType.getPriority()) {
			// Check that the type is supported
			checkDetectionTypeSupport(type);
			this.detectionType = type;
			this.detectionMode = mode;
			switchDetectionType();
			return true;
		}
		return false;
	}

	/**
	 * Reset the camera detection state back to default
	 */
	public void resetDetectionState() {
		this.detectionType = CameraDetectionType.MOVEMENT;
		this.detectionMode = CameraDetectionMode.STREAM;
		switchDetectionType();
	}

	private void switchDetectionType() {
		// FIXME
		// Do we want to keep the last type/mode and swap back to that when
		// we're done?
	}

	private void checkDetectionTypeSupport(CameraDetectionType type)
			throws UnsupportedOperationException {
		if (type == CameraDetectionType.FACE
				&& !CameraUtils.isFaceDetectionSupported(camera)) {
			throw new UnsupportedOperationException(
					"Face detection not supported");
		}
	}

	/**
	 * Stop and tear down the camera. To restart, startCamera needs to be called
	 * again.
	 */
	public void tearDownCamera() {
		Log.d(TAG, "tearDownCamera");
		if (camera != null) {
			surfaceHolder.removeCallback(this);
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
			surfaceHolder = null;
		}
	}

	private void startPreview() {
		Log.d(TAG, "startPreview");
		try {
			camera.setPreviewDisplay(surfaceHolder);
			// FIXME commented out for now
			//camera.setPreviewCallback(this);
			camera.startPreview();
			Log.d(TAG, "camera started");
		} catch (IOException e) {
			e.printStackTrace();
			// TODO handle error
		}
	}

	private void startFaceDetection() {
		Log.d(TAG, "startFaceDetection");
		camera.setFaceDetectionListener(this);
		camera.startFaceDetection();
	}

	private void stopFaceDetection() {
		Log.d(TAG, "stopFaceDetection");
		camera.setFaceDetectionListener(null);
		camera.stopFaceDetection();
	}

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO
		Log.d(TAG, "onPreviewFrame");
		// Face detection is handled separately
		if (detectionType != CameraDetectionType.FACE) {

		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Log.d(TAG, "picture taken");
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		Log.d(TAG, "surfaceChanged");
		startPreview();
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated");
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		tearDownCamera();
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		if (faces.length > 0) {
			if (detectionMode == CameraDetectionMode.ONE_OFF) {
				stopFaceDetection();
			}
			// TODO Might spam the event bus quite a lot for STREAM here...
			eventBus.post(new FaceDetectedEvent(faces));
		}
	}

}
