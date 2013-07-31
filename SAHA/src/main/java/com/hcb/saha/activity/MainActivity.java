package com.hcb.saha.activity;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.SahaConfig;
import com.hcb.saha.config.EnvConfig;
import com.hcb.saha.data.EmailManager;
import com.hcb.saha.data.SahaFileManager;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.data.model.UsersFaces;
import com.hcb.saha.event.AccountEvents;
import com.hcb.saha.event.EmailEvents;
import com.hcb.saha.event.EmailEvents.QueryEmailRequest;
import com.hcb.saha.event.FaceRecognitionEvents;
import com.hcb.saha.event.LifecycleEvents;
import com.hcb.saha.jni.NativeFaceRecognizer;
import com.hcb.saha.view.ViewUtil;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Main activity.
 * 
 * @author Andreas Borglin
 * @author Steven Hadley
 */
public class MainActivity extends RoboActivity {

	private static final String TAG = MainActivity.class.getSimpleName();

	@Inject
	private Bus eventBus;
	@Inject
	private NativeFaceRecognizer faceRecognizer;
	@InjectView(R.id.email_address_text)
	private TextView emailAddress;
	@InjectView(R.id.email_unread_count)
	private TextView emailUnreadCount;
	@InjectView(R.id.container_2)
	private RelativeLayout container2;
	@Inject
	private EmailManager emailManager;
	@InjectView(R.id.container_home_screen)
	private LinearLayout homeScreen;

	private Handler mAnimationHandler = new Handler();
	private AnimationStep mAnimationStep = new AnimationStep();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ViewUtil.keepActivityAwake(this);
		ViewUtil.goFullScreen(this);
		ViewUtil.customiseActionBar(this);

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
		case R.id.action_list_users:
			startActivity(new Intent(MainActivity.this, UsersActivity.class));
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
			eventBus.post(new FaceRecognitionEvents.TrainRecognizerRequest(uf));
			return true;
		case R.id.action_recognize:
			startActivity(new Intent(MainActivity.this,
					IdentificationActivity.class));
			return true;
		case R.id.action_animate_home_in:
			// FIXME: remove
			mAnimationStep.setMoveIn(true);
			mAnimationHandler.post(mAnimationStep);
			return true;
		case R.id.action_animate_home_out:
			// FIXME: remove
			mAnimationStep.setMoveIn(false);
			mAnimationHandler.post(mAnimationStep);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Subscribe
	public void emailUnreadCountAvailable(
			final EmailEvents.QueryEmailResult email) {
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				emailUnreadCount.setText(email.getUnreadCount() + " "
						+ "Unread");
			}
		});
	}

	@Subscribe
	public void onAccountsQueried(AccountEvents.QueryAccountsResult accounts) {
		// FIXME: Just picking first one
		eventBus.post(new QueryEmailRequest(accounts.getNames()[0],
				getApplicationContext()));
		emailAddress.setText(accounts.getNames()[0]);
	}

	/**
	 * Temporary screen animation to provoke discussion.
	 * 
	 * @author Steven Hadley
	 * 
	 */
	private class AnimationStep implements Runnable {

		private boolean in;
		private float pivot;

		public void setMoveIn(boolean in) {
			this.in = in;
			if (in) {
				pivot = 0.5f;
			} else {
				pivot = 4f;
			}
		}

		@Override
		public void run() {

			float weight = ((LinearLayout.LayoutParams) container2
					.getLayoutParams()).weight;

			if (weight > pivot && in) {
				((LinearLayout.LayoutParams) container2.getLayoutParams()).weight = weight - 0.05f;
				container2.requestLayout();
				mAnimationHandler.postDelayed(mAnimationStep, 10);
			} else if (weight < pivot && in) {
				// FIXME: This should be done by face recognition not
				// here
				eventBus.post(new AccountEvents.QueryAccountsRequest(
						MainActivity.this.getApplicationContext()));
			}

			if (weight < pivot && !in) {
				((LinearLayout.LayoutParams) container2.getLayoutParams()).weight = weight + 0.05f;
				container2.requestLayout();
				mAnimationHandler.postDelayed(mAnimationStep, 10);

			} else if (weight >= pivot && !in) {
				emailAddress.setText("");
				emailUnreadCount.setText("");
			}

		}

	}
}
