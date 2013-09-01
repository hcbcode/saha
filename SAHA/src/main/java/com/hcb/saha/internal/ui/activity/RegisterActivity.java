package com.hcb.saha.internal.ui.activity;

import roboguice.inject.ContentView;
import android.os.Bundle;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.data.model.UsersFaces;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.hcb.saha.internal.facerec.FaceRecognizer.FaceRecognitionEventHandler;
import com.hcb.saha.internal.ui.fragment.FaceDetectionFragment;
import com.hcb.saha.internal.ui.fragment.FaceDetectionFragment.FaceDetectionFragmentHandler;
import com.hcb.saha.internal.ui.fragment.UserRegistrationFragment;
import com.hcb.saha.internal.ui.fragment.UserRegistrationFragment.UserCreatedHandler;
import com.squareup.otto.Bus;

/**
 * Handles user registration process
 *
 * @author Andreas Borglin
 */
@ContentView(R.layout.activity_register)
public class RegisterActivity extends BaseFragmentActivity implements
		FaceDetectionFragmentHandler, UserCreatedHandler {

	public static final String USER_ID = "userId";
	private static final String TAG = RegisterActivity.class.getSimpleName();
	@Inject
	private Bus eventBus;
	@Inject
	private FaceRecognizer faceReognizer;

	private UserRegistrationFragment userRegistrationFragment;
	private FaceDetectionFragment faceDetectionFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		eventBus.register(this);

		int userId = 0;
		if (getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt(USER_ID);
		}
		if (userId == 0) {
			userRegistrationFragment = new UserRegistrationFragment();
			userRegistrationFragment.setUserCreatedHandler(this);
			getSupportFragmentManager().beginTransaction()
					.add(R.id.register_layout, userRegistrationFragment)
					.commit();
		} else {
			User user = SahaUserDatabase.getUserFromId(userId);
			startFaceRegistration(user, false);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eventBus.unregister(this);
	}

	private void startFaceRegistration(User user, boolean replace) {
		faceDetectionFragment = new FaceDetectionFragment();
		faceDetectionFragment.setFaceDetectionFragmentHandler(this);
		faceDetectionFragment.setMode(FaceDetectionFragment.Mode.REGISTRATION);
		faceDetectionFragment.setCurrentUser(user);
		if (replace) {
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.register_layout, faceDetectionFragment)
					.commit();
		} else {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.register_layout, faceDetectionFragment).commit();
		}

	}

	@Override
	public void onUserCreated(User user) {
		startFaceRegistration(user, true);
	}

	@Override
	public void onFaceRegistrationCompleted() {
		UsersFaces usersFaces = SahaFileManager
				.getAllUsersFaceImages(SahaUserDatabase.getAllUsers());
		Toast.makeText(this, "Training recognizer...", Toast.LENGTH_SHORT)
				.show();
		faceReognizer.trainRecognizer(usersFaces.getUserIds(),
				usersFaces.getUserImageFaces(),
				new FaceRecognitionEventHandler() {

					@Override
					public void onRecognizerTrainingCompleted() {
					}

					@Override
					public void onPredictionCompleted(int predictedUserId) {

					}
				});

		finish();
	}
}
