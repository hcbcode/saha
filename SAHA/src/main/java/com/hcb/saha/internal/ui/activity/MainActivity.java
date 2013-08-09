package com.hcb.saha.internal.ui.activity;

import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.config.EnvConfig;
import com.hcb.saha.internal.core.SahaConfig;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.data.model.UsersFaces;
import com.hcb.saha.internal.event.LifecycleEvents;
import com.hcb.saha.internal.service.RemoteStorageService;
import com.hcb.saha.internal.ui.fragment.HomeCarouselFragment;
import com.hcb.saha.internal.ui.fragment.HomeUserCloseFragment;
import com.hcb.saha.internal.ui.fragment.HomeUserPersonalisedFragment;
import com.hcb.saha.internal.ui.view.ViewUtil;
import com.squareup.otto.Bus;

/**
 * Main activity.
 * 
 * @author Andreas Borglin
 * @author Steven Hadley
 */
public class MainActivity extends RoboFragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	private Bus eventBus;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtil.keepActivityAwake(this);
		ViewUtil.goFullScreen(this);
		ViewUtil.customiseActionBar(this);

		setContentView(R.layout.activity_main);

		// Just for testing - will move this to a better location later - Starts
		// event extraction (tbc and transfer)
		startService(new Intent(this, RemoteStorageService.class));
		// OpenCV can't read assets, so need to copy over to sdcard
		SahaFileManager.copyClassifierToSdCard(this.getAssets());

		eventBus.register(this);
		eventBus.post(new LifecycleEvents.MainActivityCreated());

		showHomeCarousel();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (EnvConfig.USE_REPORTING) {
			BugSenseHandler.initAndStartSession(this, SahaConfig.BUGSENSE_KEY);
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
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
		case R.id.action_show_user_personalised:
			showHomeUserPersonalised();
			return true;
		case R.id.action_show_carousel:
			showHomeCarousel();
			return true;
		case R.id.action_show_user_close:
			showHomeUserClose();
			return true;
		case R.id.action_list_users:
			// startActivity(new Intent(MainActivity.this,
			// UsersActivity.class));
			return true;
		case R.id.action_delete_users:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.delete_users_title);
			dialog.setMessage(R.string.delete_users_message);
			dialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SahaUserDatabase.deleteAllUsers(MainActivity.this);
							SahaFileManager.deleteUserDirs();
							Toast.makeText(MainActivity.this,
									"All users deleted.", Toast.LENGTH_SHORT)
									.show();
						}
					});
			dialog.setNegativeButton(R.string.cancel, null);
			dialog.show();
			return true;
		case R.id.action_add_user:
			startActivity(new Intent(MainActivity.this, RegisterActivity.class));
			return true;
		case R.id.action_train:
			List<User> trainUsers = SahaUserDatabase
					.getAllUsers(MainActivity.this);
			UsersFaces uf = SahaFileManager.getAllUsersFaceImages(trainUsers);
			// eventBus.post(new
			// FaceRecognitionEvents.TrainRecognizerRequest(uf));
			return true;
		case R.id.action_recognize:
			// startActivity(new Intent(MainActivity.this,
			// IdentificationActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showHomeCarousel() {
		Fragment newFragment = new HomeCarouselFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.home, newFragment);
		transaction.commit();
	}

	private void showHomeUserClose() {
		Fragment newFragment = new HomeUserCloseFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.home, newFragment);
		transaction.commit();
	}

	private void showHomeUserPersonalised() {
		Fragment newFragment = new HomeUserPersonalisedFragment();
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.home, newFragment);
		transaction.commit();
	}

}
