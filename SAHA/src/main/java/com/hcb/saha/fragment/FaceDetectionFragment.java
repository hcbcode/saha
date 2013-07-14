package com.hcb.saha.fragment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
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

import com.hcb.saha.R;
import com.hcb.saha.data.SahaFileManager;
import com.hcb.saha.data.model.User;
import com.hcb.saha.view.FaceDetectionView;

/**
 * This fragment is responsible for detecting faces from the camera preview,
 * take snapshots and crop out the face from the image and save that to the
 * sdcard for use as input to the face recognition algorithm.
 * 
 * @author Andreas Borglin
 */
public class FaceDetectionFragment extends RoboFragment implements
		SurfaceHolder.Callback, Camera.FaceDetectionListener,
		Camera.PictureCallback {

	// Front-facing camera is landscape, we're in portrait
	private static final int PREVIEW_ROTATION = 90;
	// For we're only supporting front facing camera
	private static final boolean ALWAYS_FRONT_FACING = true;

	@InjectView(R.id.surface)
	private SurfaceView surfaceView;
	@InjectView(R.id.overlay)
	private FaceDetectionView overlay;
	private Camera camera;
	private SurfaceHolder surfaceHolder;
	private Matrix matrix;
	private Camera.Face picFace;
	private boolean takenPic = false;

	private User currentUser;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_face_detection,
				container, false);
		matrix = new Matrix();
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

	public void setCurrentUser(User user) {
		this.currentUser = user;
	}

	@Override
	public void surfaceCreated(SurfaceHolder surfaceHolder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder surfaceHolder, int format,
			int width, int height) {
		try {

			Log.e("SAHA", "surface width: " + width + ", height: " + height);
			Camera.Parameters params = camera.getParameters();
			List<Camera.Size> sizes = params.getSupportedPreviewSizes();
			for (Camera.Size size : sizes) {
				Log.e("SAHA", "size: " + size.width + ":" + size.height);
			}

			// TODO: These should be calculated dynamically
			params.setPreviewSize(640, 480);
			params.setPictureSize(320, 240);

			camera.setParameters(params);

			Camera.Size size = camera.getParameters().getPreviewSize();
			Log.e("SAHA", "set size: " + size.width + ":" + size.height);

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
			Camera.Face face = faces[0];
			// Translate the face rect to our surfaceholder area
			updateTranslationMatrix(surfaceView.getWidth(),
					surfaceView.getHeight(), true, true);
			// Draw the face on the surface view overlay
			overlay.updateRect(face.rect, matrix);

			// TODO Temp logic for testing purposes
			if (!takenPic) {
				picFace = face;
				camera.takePicture(null, null, FaceDetectionFragment.this);
				takenPic = true;
			}
		}
		// No face - clear the canvas
		else {
			overlay.clearRect();
		}

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

	private void persistFaceBitmap(Bitmap bitmap) {

		if (bitmap != null) {

			try {
				FileOutputStream fos = SahaFileManager
						.getStreamForUserFaceImage(currentUser);
				boolean c = bitmap.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();
				Log.e("SAHA", "wrote bitmap: " + c);
			} catch (IOException e) {
				e.printStackTrace();
				Log.e("SAHA", e.getMessage());
			}
		}
	}

	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
		updateTranslationMatrix(bitmap.getWidth(), bitmap.getHeight(), true, false);
		Bitmap rotScaledBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		// TODO: Only saving full bitmap temp for now
		persistFaceBitmap(rotScaledBitmap);

		// Let the cropping attempts begin
		updateTranslationMatrix(bitmap.getWidth(), bitmap.getHeight(), false, true);
		RectF rectf = new RectF(picFace.rect);
		Log.e("SAHA", "rect t: " + rectf.top + ", l: " + rectf.left);
		RectF dst = new RectF();
		matrix.mapRect(dst, rectf);
		Log.e("SAHA", "rect t: " + dst.top + ", l: " + dst.left + ", width: "
				+ dst.width() + ", height: " + dst.height());
		updateTranslationMatrix(bitmap.getWidth(), bitmap.getHeight(), true, false);

		Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, (int) dst.left,
				(int) dst.top, (int) dst.width(), (int) dst.height(), matrix,
				true);

		persistFaceBitmap(croppedBitmap);

	}

}
