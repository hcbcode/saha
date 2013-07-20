package com.hcb.saha.activity;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.event.FaceRecognitionEvents;
import com.hcb.saha.fragment.FaceDetectionFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Responsible for identification of a user
 * 
 * @author Andreas Borglin
 */
public class IdentificationActivity extends RoboFragmentActivity {

	private FaceDetectionFragment faceDetectionFragment;
	@Inject
	private Bus eventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		eventBus.register(this);

		faceDetectionFragment = new FaceDetectionFragment();
		faceDetectionFragment
				.setMode(FaceDetectionFragment.Mode.IDENTIFICATION);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.register_layout, faceDetectionFragment).commit();
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

	@Subscribe
	public void onUserIdPredicted(
			FaceRecognitionEvents.UserPredictionResult event) {
		if (event.getUserId() >= 0) {
			User user = SahaUserDatabase.getUserFromId(this, event.getUserId());
			Toast.makeText(this, "User: " + user.getName(), Toast.LENGTH_SHORT)
					.show();
		}
		else {
			Toast.makeText(this, "User not recognized!", Toast.LENGTH_SHORT)
			.show();
		}
	}
}
