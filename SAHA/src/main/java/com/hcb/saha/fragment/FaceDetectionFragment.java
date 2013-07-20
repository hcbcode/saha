package com.hcb.saha.fragment;

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
import com.hcb.saha.SahaConfig.Registration;
import com.hcb.saha.data.SahaFileManager;
import com.hcb.saha.data.model.User;
import com.hcb.saha.event.FaceRecognitionEvents;
import com.hcb.saha.event.RegistrationEvents;
import com.hcb.saha.view.FaceDetectionView;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * This fragment is responsible for detecting faces from the camera preview,
 * and persist images to the file system. It support two modes, one for
 * face registration and one for face identification
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

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.setSizeFromLayout();
		surfaceHolder.addCallback(this);

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
		camera.setDisplayOrientation(PREVIEW_ROTATION);
	}

	@Override
	public void onResume() {
		super.onResume();
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

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
			int width, int height) {
		try {

			Log.d(TAG, "surface width: " + width + ", height: " + height);
			Camera.Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPreviewSizes();
			for (Camera.Size size : sizes) {
				Log.d(TAG, "size: " + size.width + ":" + size.height);
			}

			// TODO: These should be calculated dynamically
			params.setPreviewSize(640, 480);
			params.setPictureSize(320, 240);

			camera.setParameters(params);

			Camera.Size size = camera.getParameters().getPreviewSize();
			Log.d(TAG, "set size: " + size.width + ":" + size.height);

			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
			camera.setFaceDetectionListener(this);
			camera.startFaceDetection();
		} catch (IOException e) {
			Log.e("SAHA", e.toString());
		}
	}

	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {

		if (faces.length > 0) {
			// Only care about one face for now
			// FIXME: We need logic here to determine whether the rect is of appropriate
			// size in relation to our opencv classifier settings
			Camera.Face face = faces[0];
			// Translate the face rect to our surfaceholder area
			updateTranslationMatrix(surfaceView.getWidth(),
					surfaceView.getHeight(), true, true);
			// Draw the face on the surface view overlay
			overlay.updateRect(face.rect, matrix);

			camera.takePicture(null, null, FaceDetectionFragment.this);
		}
		// No face - clear the canvas
		else {
			overlay.clearRect();
		}

	}

	private void restartPreview() {
		camera.startPreview();
		camera.startFaceDetection();
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

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		camera.stopPreview();
		camera.release();
	}

	private void persistFaceBitmap(Bitmap bitmap, FileOutputStream fos) {

		if (bitmap != null && fos != null) {

			try {
				boolean c = bitmap.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
				Log.d(TAG, "wrote bitmap: " + c);
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
						SahaFileManager.getStreamForUserFaceImage(currentUser));
				// If we don't have enough pics yet, restart the preview
				if (++imageCount < Registration.NUM_FACE_PICS_REQUIRED) {
					Log.d(TAG, "image count: " + imageCount);
					restartPreview();
				}
				// Otherwise, send an event to let client know we've done our job!
				else {
					eventBus.post(new RegistrationEvents.FaceRegistrationCompleted());
				}

			} else {
				File idImageFile = SahaFileManager
						.getFileForFaceIdentification();
				persistFaceBitmap(rotScaledBitmap, new FileOutputStream(
						idImageFile));
				// Up to client to handle this
				eventBus.post(new FaceRecognitionEvents.PredictUserRequest(
						idImageFile.getAbsolutePath()));
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
	}

}
