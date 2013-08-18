package com.hcb.saha.internal.ui.activity;

import java.util.List;

import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.ContentView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.widget.Toast;

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
import com.hcb.saha.internal.ui.fragment.HomeCarouselFragment;
import com.hcb.saha.internal.ui.fragment.HomeUserNearFragment;
import com.hcb.saha.internal.ui.fragment.HomeUserPersonalisedFragment;
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
public class MainActivity extends RoboFragmentActivity {

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Crashlytics.start(this);
		// FIXME: crap
		enableCrashReporting();

		ViewUtil.keepActivityAwake(this);
		ViewUtil.goFullScreen(this);
		ViewUtil.customiseActionBar(this);

		// OpenCV can't read assets, so need to copy over to sdcard
		SahaFileManager.copyClassifierToSdCard(this.getAssets());

		PreferenceManager.setDefaultValues(this, R.xml.debug_preferences, false);

		eventBus.register(this);
		eventBus.post(new SystemEvents.MainActivityCreated());

		showHomeCarousel();
	}

	@Override
	protected void onResume() {
		super.onResume();
		cameraProcessor.startCamera((SurfaceView) findViewById(R.id.surface));
	}

	/*
	 * If device is NOT connected via USB to a computer, enable crash reports
	 */
	private void enableCrashReporting() {
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.hardware.usb.action.USB_STATE");
		Intent intent = registerReceiver(null, filter);
		boolean connected = intent.getBooleanExtra("connected", false);
		if (!connected) {
			// Crashlytics.start(this);
			// FIXME: Heap of crap
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
	public void onSystemStateChanged(final SystemEvents.SystemStateChangedEvent event) {
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
					break;
				}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
			// startActivity(new Intent(MainActivity.this,
			// UsersActivity.class));
			List<User> users = SahaUserDatabase.getAllUsers();
			for (User user : users) {
				Log.d("USER", "User: " + user.getName());
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
		replaceFragmentWithAnimation("homecarousel",
				new HomeCarouselFragment(), R.id.home);
	}

	private void showHomeUserNear() {
		replaceFragmentWithAnimation("homeusernear",
				new HomeUserNearFragment(), R.id.home);
	}

	private void showHomeUserPersonalised() {
		replaceFragmentWithAnimation("homeuserpersonalised",
				new HomeUserPersonalisedFragment(), R.id.home);
	}

	private void replaceFragmentWithAnimation(String fragmentTag,
			Fragment newFragment, int fragmentToReplace) {
		if (null == getSupportFragmentManager().findFragmentByTag(fragmentTag)) {
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction()
					.setCustomAnimations(R.animator.anim_from_middle,
							R.animator.anim_to_middle)
							.replace(fragmentToReplace, newFragment, fragmentTag);
			transaction.commit();
		}
	}

}
