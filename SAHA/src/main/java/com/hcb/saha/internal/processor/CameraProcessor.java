package com.hcb.saha.internal.processor;

import java.io.IOException;

import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.PreviewCallback;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.core.SahaExceptions.CameraNotActiveException;
import com.hcb.saha.internal.event.CameraEvents;
import com.hcb.saha.internal.ui.fragment.FaceDetectionFragment.FaceDetectionFragmentHandler;
import com.hcb.saha.internal.utils.CameraUtils;
import com.hcb.saha.internal.utils.CameraUtils.FaceDetectionHandler;
import com.hcb.saha.internal.utils.CameraUtils.FacePictureTakenHandler;
import com.hcb.saha.internal.utils.CameraUtils.PreviewFrameHandler;
import com.squareup.otto.Bus;

/**
 * Responsible for all things camera.
 * 
 * @author Andreas Borglin
 */
@Singleton
public class CameraProcessor implements PreviewCallback,
		SurfaceHolder.Callback, FaceDetectionListener {

	private static final String TAG = CameraProcessor.class.getSimpleName();

	@Inject
	private Bus eventBus;
	@Inject(optional = true)
	private FaceDetectionHandler faceDetectionHandler;
	@Inject(optional = true)
	private PreviewFrameHandler previewFrameHandler;

	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private boolean cameraActive;

	private FaceDetectionHandler faceBackupHandler;

	@Inject
	public CameraProcessor(Bus eventBus) {
		eventBus.register(this);
	}

	/**
	 * This must be called in onCreate of the client activity for the
	 * surfaceholder callbacks to be called.
	 * 
	 * @param surfaceView
	 *            Surface view to render camera preview on
	 */
	public void startCamera(SurfaceView surfaceView)
			throws UnsupportedOperationException {
		if (!cameraActive) {
			Log.d(TAG, "startCamera");
			camera = CameraUtils.getFrontFacingCamera();
			if (camera == null) {
				throw new UnsupportedOperationException(
						"Device has no front-facing camera");
			}
			// Front facing camera found. Woho!
			surfaceHolder = surfaceView.getHolder();
			surfaceHolder.setSizeFromLayout();
			surfaceHolder.addCallback(this);
		}
	}

	public void setFaceDetectionHandler(FaceDetectionHandler handler) {
		faceBackupHandler = faceDetectionHandler;
		this.faceDetectionHandler = handler;
	}

	// TODO temp hack
	public void revertToOriginalHandler() {
		this.faceDetectionHandler = faceBackupHandler;
	}

	/**
	 * Check if the camera is active.
	 * 
	 * @return True if active.
	 */
	public boolean isCameraActive() {
		return cameraActive;
	}

	private void checkCameraActive() throws CameraNotActiveException {
		if (!cameraActive) {
			throw new CameraNotActiveException("Camera not active");
		}
	}

	/**
	 * Stop and tear down the camera. To restart, startCamera needs to be called
	 * again.
	 */
	public void tearDownCamera() {
		Log.d(TAG, "tearDownCamera");
		cameraActive = false;
		eventBus.post(new CameraEvents.CameraInactivatedEvent());
		if (camera != null) {
			surfaceHolder.removeCallback(this);
			camera.setPreviewCallback(null);
			camera.stopPreview();
			camera.release();
			camera = null;
			surfaceHolder = null;
		}
	}

	/**
	 * Take a picture during face detection
	 * 
	 * @param handler
	 *            Callback interface
	 */
	public void takeFacePicture(final FacePictureTakenHandler handler)
			throws CameraNotActiveException {
		checkCameraActive();
		stopFaceDetection();
		camera.takePicture(null, null, new PictureCallback() {

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				Bitmap bitmap = CameraUtils.getBitmapFromFrontCameraData(data);
				if (bitmap != null) {
					handler.onFacePictureTaken(bitmap);
				}
				// Restart preview and face detection
				startPreview();
				startFaceDetection();
			}
		});
	}

	private void startPreview() {
		Log.d(TAG, "startPreview");
		try {
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
			cameraActive = true;
			eventBus.post(new CameraEvents.CameraActivatedEvent());

			if (previewFrameHandler != null) {
				camera.setPreviewCallback(this);
			}
			if (faceDetectionHandler != null) {
				startFaceDetection();
			}

		} catch (IOException e) {
			e.printStackTrace();
			// TODO handle error
		}
	}

	private void startFaceDetection() {
		Log.d(TAG, "startFaceDetection");
		try {
			camera.setFaceDetectionListener(this);
			camera.startFaceDetection();
		} catch (Exception e) {
			// FIXME
		}
	}

	private void stopFaceDetection() {
		Log.d(TAG, "stopFaceDetection");
		camera.setFaceDetectionListener(null);
		camera.stopFaceDetection();
	}

	// FIXME - all these public callbacks should be wrapped in internal classes

	@Override
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO
		Log.d(TAG, "onPreviewFrame");
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
			faceDetectionHandler.onFaceDetected(faces, this);
		} else {
			faceDetectionHandler.onNoFaceDetected();
		}
	}
}
