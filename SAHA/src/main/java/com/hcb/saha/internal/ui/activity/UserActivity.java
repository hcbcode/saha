package com.hcb.saha.internal.ui.activity;

import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.data.db.SahaUserDatabase;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.User;

/**
 * Activity representing the current user
 * @author Andreas Borglin
 */
@ContentView(R.layout.activity_user)
public class UserActivity extends RoboActivity {

	@InjectView(R.id.user_name)
	private TextView userName;
	@InjectView(R.id.user_num_photos)
	private TextView userNumPhotos;
	@InjectView(R.id.user_add_photos)
	private Button userAddPhotos;
	@InjectView(R.id.user_delete)
	private Button userDelete;

	@Inject
	private SahaSystemState systemState;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		final User user = systemState.getCurrentUser();
		if (user == null) {
			finish();
		}
		else {
			userName.setText(getString(R.string.user_name, user.getName()));
			String[] images = SahaFileManager.getUserFaceImages(user);
			userNumPhotos.setText(getString(R.string.user_num_photos, images.length));

			userAddPhotos.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserActivity.this, RegisterActivity.class);
					intent.putExtra(RegisterActivity.USER_ID, user.getId());
					startActivity(intent);
				}
			});

			userDelete.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
					dialog.setTitle(R.string.delete_users_title);
					dialog.setMessage(R.string.delete_users_message);
					dialog.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SahaUserDatabase.deleteUser(user);
							SahaFileManager.deleteUserDir(user);
							Toast.makeText(UserActivity.this,
									"User " + user.getName() + " deleted!", Toast.LENGTH_SHORT)
									.show();
						}
					});
					dialog.setNegativeButton(R.string.cancel, null);
					dialog.show();
				}
			});
		}
	}

}
