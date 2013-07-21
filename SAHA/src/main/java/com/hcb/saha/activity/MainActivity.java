package com.hcb.saha.activity;

import java.util.List;

import roboguice.activity.RoboActivity;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.SahaConfig;
import com.hcb.saha.config.EnvConfig;
import com.hcb.saha.data.SahaFileManager;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.data.model.UsersFaces;
import com.hcb.saha.event.FaceRecognitionEvents;
import com.hcb.saha.event.LifecycleEvents;
import com.hcb.saha.jni.NativeFaceRecognizer;
import com.squareup.otto.Bus;

/**
 * Main activity
 * 
 * @author Andreas Borglin
 * @author Steven Hadley
 */
public class MainActivity extends RoboActivity {
	
	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	private Bus eventBus;
	@Inject
	NativeFaceRecognizer faceRecognizer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		goFullScreen();
		customiseActionBar();
		setContentView(R.layout.activity_main);
		
		// OpenCV can't read assets, so need to copy over to sdcard
		SahaFileManager.copyClassifierToSdCard(this.getAssets());

		eventBus.register(this);
		eventBus.post(new LifecycleEvents.MainActivityCreated());
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (EnvConfig.USE_REPORTING) {
			BugSenseHandler.initAndStartSession(this, SahaConfig.BUGSENSE_KEY);
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if (EnvConfig.USE_REPORTING) {
			BugSenseHandler.closeSession(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		eventBus.post(new LifecycleEvents.MainActivityDestroyed());
		eventBus.unregister(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_list_users:
			List<User> users = SahaUserDatabase.getAllUsers(MainActivity.this);
			Log.d(TAG, "users: " + users.size());
			for (User user : users) {
				Log.d(TAG,
						"User #" + user.getId() + ", name: " + user.getName()
								+ ", dir: " + user.getDirectory());
			}
			return true;
		case R.id.action_delete_users:
			SahaUserDatabase.deleteAllUsers(MainActivity.this);
			return true;
		case R.id.action_add_user:
			startActivity(new Intent(MainActivity.this, RegisterActivity.class));
			return true;
		case R.id.action_train:
			List<User> trainUsers = SahaUserDatabase
					.getAllUsers(MainActivity.this);
			UsersFaces uf = SahaFileManager.getAllUsersFaceImages(trainUsers);
			eventBus.post(new FaceRecognitionEvents.TrainRecognizerRequest(uf));
			return true;
		case R.id.action_recognize:

			startActivity(new Intent(MainActivity.this,
					IdentificationActivity.class));

			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Sample action bar we could use. Discuss.
	 */
	private void customiseActionBar() {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
			ActionBar actionBar = getActionBar();
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			LayoutInflater inflator = (LayoutInflater) this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View v = inflator.inflate(R.layout.action_bar, null);
			actionBar.setCustomView(v);
		}
	}

	/**
	 * The 'main' server application needs to be full screen. It doesn't need to
	 * follow Android convention.
	 * 
	 * Discuss.
	 */
	private void goFullScreen() {
		getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
			getWindow().getDecorView().setSystemUiVisibility(
					View.SYSTEM_UI_FLAG_LOW_PROFILE);

		}
	}
}
