package com.hcb.saha.activity;

import java.util.List;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.SahaConfig;
import com.hcb.saha.config.EnvConfig;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.event.TestEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Main activity
 * 
 * @author Andreas Borglin
 */
public class MainActivity extends RoboActivity {

	@Inject
	private Bus eventBus;
	@InjectView(R.id.register)
	private Button register;
	@InjectView(R.id.recognize)
	private Button recognize;
	@InjectView(R.id.listUsers)
	private Button listUsers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						RegisterActivity.class));
			}
		});

		recognize.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
		
		listUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<User> users = SahaUserDatabase.getAllUsers(MainActivity.this);
				for (User user: users) {
					Log.e("SAHA", "User #" + user.getId() + ", name: " + user.getName() + ", dir: " + user.getDirectory());
				}
			}
		});

		eventBus.register(this);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (EnvConfig.USE_REPORTING) {
			BugSenseHandler.initAndStartSession(this, SahaConfig.BUGSENSE_KEY);
		}

	}

	@Subscribe
	public void incomingEvent(TestEvent event) {
		Toast.makeText(this, "Andrew, look! An event bus!", Toast.LENGTH_SHORT)
				.show();
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (EnvConfig.USE_REPORTING) {
			BugSenseHandler.closeSession(this);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
