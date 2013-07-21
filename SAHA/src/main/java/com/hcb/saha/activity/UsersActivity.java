package com.hcb.saha.activity;

import java.util.List;

import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.hcb.saha.R;
import com.hcb.saha.data.SahaFileManager;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.data.model.UsersFaces;

public class UsersActivity extends RoboActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_users);
		ViewGroup layout = (ViewGroup) findViewById(R.id.users_layout);
		
		List<User> users = SahaUserDatabase.getAllUsers(this);
		UsersFaces usersFaces = SahaFileManager.getAllUsersFaceImages(users);
		
		int index = 0;
		String[][] faces = usersFaces.getUserImageFaces();
		for (final User user : users) {
			Button userView = new Button(this);
			String text = "#" + user.getId() + ", name: " + user.getName() + ", face images: " + faces[index++].length;
			userView.setText(text);
			userView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UsersActivity.this, RegisterActivity.class);
					intent.putExtra(RegisterActivity.USER_ID, user.getId());
					startActivity(intent);
					finish();
				}
			});
			layout.addView(userView);
		}
	}
}
