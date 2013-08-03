package com.hcb.saha.internal.ui.fragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.core.SahaConfig.Registration;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.ui.view.FaceDetectionView;
import com.squareup.otto.Bus;

/**
 * This fragment is responsible for detecting faces from the camera preview, and
 * persist images to the file system. It support two modes, one for face
 * registration and one for face identification
 * 
 * @author Andreas Borglin
 */
public class FaceDetectionFragment extends RoboFragment implements
		SurfaceHolder.Callback, Camera.FaceDetectionListener,
		Camera.PictureCallback {

	private static final String TAG = FaceDetectionFragment.class
			.getSimpleName();

	public static enum Mode {
		REGISTRATION, IDENTIFICATION
	}

	// Front-facing camera is landscape, we're in portrait
	private static final int PREVIEW_ROTATION = 90;
	// For we're only supporting front facing camera
	private static final boolean ALWAYS_FRONT_FACING = true;
	// We want the output picture to be in this height/width aspect ratio
	private static final float PIC_ASPECT_RATIO = 0.75f;

	@Inject
	private Bus eventBus;
	@InjectView(R.id.surface)
	private SurfaceView surfaceView;
	@InjectView(R.id.overlay)
	private FaceDetectionView overlay;
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private Matrix matrix;

	private Mode mode;
	private User currentUser;
	private int imageCount;
	private boolean detectionActive;
	private Toast toast;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_face_detection,
				container, false);
		matrix = new Matrix();
		eventBus.register(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Find the front-facing camera
		int frontFacingId = -1;
		int cameras = Camera.getNumberOfCameras();
		Camera.CameraInfo camInfo = new Camera.CameraInfo();
		for (int i = 0; i < cameras; ++i) {
			Camera.getCameraInfo(i, camInfo);
			if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
				frontFacingId = i;
				break;
			}
		}

		if (frontFacingId == -1) {
			Toast.makeText(getActivity(), "No front facing camera!!!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		camera = Camera.open(frontFacingId);
		Camera.Parameters params = camera.getParameters();
		// Is face detection supported?
		if (params.getMaxNumDetectedFaces() == 0) {
			Toast.makeText(getActivity(), "No face detection support!",
					Toast.LENGTH_SHORT).show();
			return;
		}

		camera.setDisplayOrientation(PREVIEW_ROTATION);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setSizeFromLayout();
		surfaceHolder.addCallback(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		// FIXME Need to stop preview and release camera here
	}

	@Override
	public void onResume() {
		super.onResume();
		// FIXME Need to start things up again
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		eventBus.unregister(this);
	}

	public void setCurrentUser(User user) {
		this.currentUser = user;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	private void setCameraParameters() {
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

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {
		// Nothing to do
	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
			int width, int height) {
		Log.d(TAG, "surfaceChanged");
		try {

			if (detectionActive) {
				camera.stopFaceDetection();
				camera.stopPreview();
				detectionActive = false;
			}

			setCameraParameters();

			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
			camera.setFaceDetectionListener(this);
			camera.startFaceDetection();
			detectionActive = true;

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		Log.d(TAG, "surfaceDestroyed");
		try {
			camera.setFaceDetectionListener(null);
			camera.stopFaceDetection();
			camera.stopPreview();
		} catch (Throwable t) {
			// On some devices, face detection is stopped automatically and not
			// on others
			// If it's already stopped, it will throw a RuntimeException here.
			// Unfortunately
			// there is no way to check if it's stopped already or not...
			Log.e(TAG, t.getMessage());
		}
		surfaceHolder.removeCallback(this);
		detectionActive = false;
		camera.release();
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {

		// If one face
		if (faces.length == 1) {
			// FIXME: We need logic here to determine whether the rect is of
			// appropriate size in relation to our opencv classifier settings
			Camera.Face face = faces[0];
			// Translate the face rect to our surfaceholder area
			updateTranslationMatrix(surfaceView.getWidth(),
					surfaceView.getHeight(), true, true);
			// Draw the face on the surface view overlay
			overlay.updateRect(face.rect, matrix);

			camera.stopFaceDetection();
			camera.takePicture(null, null, FaceDetectionFragment.this);
			detectionActive = false;
		}
		// If more than 1 face
		else if (faces.length > 1) {
			if (toast != null) {
				toast.cancel();
			}
			toast = Toast.makeText(getActivity(),
					"Multiple faces detected! Only one is supported.",
					Toast.LENGTH_SHORT);
			toast.show();
			overlay.clearRect();
		}
		// No face - clear the canvas
		else {
			overlay.clearRect();
		}

	}

	private void restartPreview() {
		camera.startPreview();
		camera.startFaceDetection();
		detectionActive = true;
	}

	private void updateTranslationMatrix(int dstWidth, int dstHeight,
			boolean mirrorAndRotate, boolean scaleAndTranslate) {
		matrix.reset();
		if (mirrorAndRotate) {
			matrix.setScale(ALWAYS_FRONT_FACING ? -1 : 1, 1);
			matrix.postRotate(PREVIEW_ROTATION);
		}
		if (scaleAndTranslate) {
			// Camera driver coordinates range from (-1000, -1000) to (1000,
			// 1000).
			matrix.postScale(dstWidth / 2000f, dstHeight / 2000f);
			matrix.postTranslate(dstWidth / 2f, dstHeight / 2f);
		}
	}

	private void persistFaceBitmap(Bitmap bitmap, FileOutputStream fos) {

		if (bitmap != null && fos != null) {

			try {
				boolean c = bitmap.compress(CompressFormat.JPEG, 100, fos);
				Log.d(TAG, "Persist bitmap to file status: " + c);
				fos.flush();
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
				Log.e(TAG, e.getMessage());
			}
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// Rotate and scale bitmap
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		updateTranslationMatrix(bitmap.getWidth(), bitmap.getHeight(), true,
				false);
		Bitmap rotScaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		// Persist it to file system
		try {
			if (mode == Mode.REGISTRATION) {
				persistFaceBitmap(rotScaledBitmap,
						SahaFileManager.getStreamForNewFaceImage(currentUser));
				// If we don't have enough pics yet, restart the preview
				if (++imageCount < Registration.NUM_FACE_PICS_REQUIRED) {
					Log.d(TAG, "Image count: " + imageCount);
					restartPreview();
				}
				// Otherwise, send an event to let client know we've done our
				// job!
				else {
					// FIXME
					// eventBus.post(new
					// RegistrationEvents.FaceRegistrationCompleted());
				}

			} else {
				File idImageFile = SahaFileManager
						.getFileForFaceIdentification();
				persistFaceBitmap(rotScaledBitmap, new FileOutputStream(
						idImageFile));
				// Up to client to handle this
				// eventBus.post(new FaceRecognitionEvents.PredictUserRequest(
				// idImageFile.getAbsolutePath()));
				// FIXME
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
