package com.hcb.saha.internal.ui.activity;

import java.util.List;

import roboguice.inject.ContentView;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.core.SahaSystemState.State;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.data.model.UsersFaces;
import com.hcb.saha.internal.event.SystemEvents;
import com.hcb.saha.internal.facerec.FaceRecognizer;
import com.hcb.saha.internal.processor.CameraProcessor;
import com.hcb.saha.internal.ui.fragment.CarouselFragment;
import com.hcb.saha.internal.ui.fragment.HomeUserNearFragment;
import com.hcb.saha.internal.ui.fragment.HomeUserPersonalisedFragment;
import com.hcb.saha.internal.ui.fragment.widget.BaseWidgetFragment.StateType;
import com.hcb.saha.internal.ui.view.ViewUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Main activity.
 *
 * @author Andreas Borglin
 * @author Steven Hadley
 */
@ContentView(R.layout.activity_main)
public class MainActivity extends BaseFragmentActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	private Bus eventBus;
	@Inject
	private FaceRecognizer faceRecognizer;
	@Inject
	private CameraProcessor cameraProcessor;
	@Inject
	private SahaSystemState systemState;
	private MenuItem editUserItem;
	private boolean active;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		enableCrashReporting();

		// Use low profile for main
		ViewUtil.hideNavigation(this);
		// Use customized actionbar for main
		ViewUtil.customiseActionBar(this);

		ActionBar actionBar = getActionBar();
		Log.d(TAG, "ac view: " + actionBar.getCustomView());

		// OpenCV can't read assets, so need to copy over to sdcard
		SahaFileManager.copyClassifierToSdCard(this.getAssets());

		PreferenceManager
				.setDefaultValues(this, R.xml.debug_preferences, false);

		eventBus.register(this);
		eventBus.post(new SystemEvents.MainActivityCreated());
		showHomeCarousel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraProcessor.startCamera((SurfaceView) findViewById(R.id.surface));
		active = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		active = false;
	}

	/*
	 * If device is NOT connected via USB to a computer, enable crash reports
	 */
	private void enableCrashReporting() {
		Log.d(getClass().getSimpleName(), "enableCrashReporting()");
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.hardware.usb.action.USB_STATE");
		Intent intent = registerReceiver(null, filter);
		boolean connected = intent.getBooleanExtra("connected", false);
		if (!connected) {
			Crashlytics.start(this);
			Toast.makeText(this, "Crashlytics started", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cameraProcessor.tearDownCamera();
		eventBus.post(new SystemEvents.MainActivityDestroyed());
		eventBus.unregister(this);
	}

	@Subscribe
	public void onSystemStateChanged(
			final SystemEvents.SystemStateChangedEvent event) {
		Log.d(getClass().getSimpleName(), "onSystemStateChanged()");
		if (active) {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					State state = event.getState();

					switch (state) {
					case REGISTERED_USER: {
						editUserItem.setVisible(true);
						showHomeUserPersonalised();
						break;
					}
					case ANONYMOUS_USER: {
						editUserItem.setVisible(false);
						showHomeUserNear();
						break;
					}
					default: {
						editUserItem.setVisible(false);
						showHomeCarousel();
						ViewUtil.hideNavigation(MainActivity.this);
						break;
					}
					}
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(getClass().getSimpleName(), "onCreateOptionsMenu()");
		getMenuInflater().inflate(R.menu.main, menu);
		editUserItem = menu.findItem(R.id.action_edit_user);
		return true;
	}

	/**
	 * FIXME: Remove this stuff.
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_show_carousel:
			showHomeCarousel();
			return true;
		case R.id.action_show_user_near:
			showHomeUserNear();
			return true;
		case R.id.action_list_users:
			// FIXME Need an admin activity
			List<User> users = SahaUserDatabase.getAllUsers();
			for (User user : users) {
				Log.d("USER", "User: " + user.toString());
			}
			return true;
		case R.id.action_delete_users:
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle(R.string.delete_users_title);
			dialog.setMessage(R.string.delete_users_message);
			dialog.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							SahaUserDatabase.deleteAllUsers();
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
		case R.id.action_edit_user:
			startActivity(new Intent(MainActivity.this, UserActivity.class));
			return true;
		case R.id.action_train:
			List<User> trainUsers = SahaUserDatabase.getAllUsers();
			UsersFaces uf = SahaFileManager.getAllUsersFaceImages(trainUsers);
			faceRecognizer.trainRecognizer(uf.getUserIds(),
					uf.getUserImageFaces(), null);
			return true;
		case R.id.action_debug_settings:
			startActivity(new Intent(MainActivity.this, DebugActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showHomeCarousel() {
		replaceFragmentWithAnimation(CarouselFragment.create(StateType.FULL),
				R.id.home);
	}

	private void showHomeUserNear() {
		replaceFragmentWithAnimation(new HomeUserNearFragment(), R.id.home);
	}

	private void showHomeUserPersonalised() {
		replaceFragmentWithAnimation(new HomeUserPersonalisedFragment(),
				R.id.home);
	}

	/**
	 * Boiler plate replace fragment.
	 *
	 * @param fragmentTag
	 * @param newFragment
	 * @param fragmentToReplace
	 */
	private void replaceFragmentWithAnimation(Fragment newFragment,
			int fragmentToReplace) {

		if (null == getSupportFragmentManager().findFragmentByTag(
				newFragment.getClass().getSimpleName())) {
			getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.animator.anim_from_middle,
							R.animator.anim_to_middle)
					.replace(fragmentToReplace, newFragment,
							newFragment.getClass().getSimpleName()).commit();
		}
	}

}
