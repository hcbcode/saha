package com.hcb.saha.internal.ui.activity;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.data.model.UsersFaces;
import com.hcb.saha.internal.ui.fragment.FaceDetectionFragment;
import com.hcb.saha.internal.ui.fragment.UserRegistrationFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Handles user registration process
 * 
 * @author Andreas Borglin
 */
public class RegisterActivity extends RoboFragmentActivity {

	public static final String USER_ID = "userId";
	private UserRegistrationFragment userRegistrationFragment;
	private FaceDetectionFragment faceDetectionFragment;
	@Inject
	private Bus eventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		eventBus.register(this);

		int userId = 0;
		if (getIntent().getExtras() != null) {
			userId = getIntent().getExtras().getInt(USER_ID);
		}
		if (userId == 0) {
			userRegistrationFragment = new UserRegistrationFragment();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.register_layout, userRegistrationFragment)
					.commit();
		} else {
			User user = SahaUserDatabase.getUserFromId(this, userId);
			startFaceRegistration(user, false);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eventBus.unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.face_detection, menu);
		return true;
	}

	// FIXME
//	@Subscribe
//	public void onUserCreated(RegistrationEvents.UserCreated event) {
//		User user = event.getUser();
//		startFaceRegistration(user, true);
//	}

	private void startFaceRegistration(User user, boolean replace) {
		faceDetectionFragment = new FaceDetectionFragment();
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

	// FIXME
//	@Subscribe
//	public void onFaceRegistrationCompleted(
//			RegistrationEvents.FaceRegistrationCompleted event) {
//		UsersFaces usersFaces = SahaFileManager
//				.getAllUsersFaceImages(SahaUserDatabase.getAllUsers(this));
//		eventBus.post(new FaceRecognitionEvents.TrainRecognizerRequest(
//				usersFaces));
//		Toast.makeText(this, "Training recognizer...", Toast.LENGTH_SHORT)
//				.show();
//		finish();
//	}

}
