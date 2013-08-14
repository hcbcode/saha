package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.Camera.Face;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.core.SahaConfig.Registration;
import com.hcb.saha.internal.core.SahaExceptions.CameraNotActiveException;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.processor.CameraProcessor;
import com.hcb.saha.internal.ui.view.FaceDetectionView;
import com.hcb.saha.internal.utils.CameraUtils;
import com.hcb.saha.internal.utils.CameraUtils.FaceDetectionHandler;
import com.hcb.saha.internal.utils.CameraUtils.FacePictureTakenHandler;
import com.squareup.otto.Bus;

/**
 * This fragment is responsible for detecting faces from the camera preview, and
 * persist images to the file system. It support two modes, one for face
 * registration and one for face identification
 * 
 * @author Andreas Borglin
 */
public class FaceDetectionFragment extends RoboFragment implements
		FaceDetectionHandler {

	private static final String TAG = FaceDetectionFragment.class
			.getSimpleName();

	public static interface FaceDetectionFragmentHandler {
		void onFaceRegistrationCompleted();
	}

	public static enum Mode {
		REGISTRATION, IDENTIFICATION
	}

	@Inject
	private Bus eventBus;
	@InjectView(R.id.surface)
	private SurfaceView surfaceView;
	@InjectView(R.id.overlay)
	private FaceDetectionView overlay;
	@Inject
	private CameraProcessor cameraProcessor;
	private FaceDetectionFragmentHandler handler;

	private Mode mode;
	private User currentUser;
	private int imageCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_face_detection,
				container, false);
		eventBus.register(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		cameraProcessor.setFaceDetectionHandler(this);
		cameraProcessor.startCamera(surfaceView);
	}

	public void setFaceDetectionFragmentHandler(
			FaceDetectionFragmentHandler handler) {
		this.handler = handler;
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
	public void onFaceDetected(Face[] faces, CameraProcessor cameraProcessor) {
		if (imageCount++ < Registration.NUM_FACE_PICS_REQUIRED) {

			Matrix matrix = CameraUtils.getMatrixForCameraDriverTranslation(
					surfaceView.getWidth(), surfaceView.getHeight());
			// Draw the face on the surface view overlay
			overlay.updateRect(faces[0].rect, matrix);

			try {
				cameraProcessor.takeFacePicture(new FacePictureTakenHandler() {

					@Override
					public void onFacePictureTaken(Bitmap bitmap) {
						SahaFileManager.persistFaceBitmap(bitmap, currentUser);
					}
				});
			} catch (CameraNotActiveException e) {
				e.printStackTrace();
				// TODO handle
			}
		} else {
			cameraProcessor.tearDownCamera();
			// TODO temp hack
			cameraProcessor.revertToOriginalHandler();
			handler.onFaceRegistrationCompleted();
		}
	}

	@Override
	public void onNoFaceDetected() {
		// No op here
	}

}
