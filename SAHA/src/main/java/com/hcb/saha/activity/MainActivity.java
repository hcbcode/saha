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
 */
public class MainActivity extends RoboActivity {

	@Inject
	private Bus eventBus;
	@InjectView(R.id.register)
	private Button register;
	@InjectView(R.id.recognize)
	private Button recognize;
	@InjectView(R.id.train)
	private Button train;
	@InjectView(R.id.listUsers)
	private Button listUsers;
	@InjectView(R.id.deleteUsers)
	private Button deleteUsers;
	@Inject
	NativeFaceRecognizer faceRecognizer;

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
				// SahaFileManager.createFaceRecModelFile();
				
				startActivity(new Intent(MainActivity.this, IdentificationActivity.class));
				
				// FIXME Temp test data until the cropping works

				//faceRecognizer.initRecognizer();
//				String[][] idImageArray = new String[2][];
//				idImageArray[0] = new String[] {
//						"/sdcard/face/andreas1.jpg", "/sdcard/face/andreas2.jpg", "/sdcard/face/andreas3.jpg" };
//				idImageArray[1] = new String[] {
//						"/sdcard/face/kate1.jpg", "/sdcard/face/kate2.jpg", "/sdcard/face/kate3.jpg" };
				
				//faceRecognizer.trainRecognizer(idImageArray);
				//eventBus.post(new FaceRecognitionEvents.TrainRecognizerRequest(idImageArray));
				//eventBus.post(new FaceRecognitionEvents.PredictUserRequest("/sdcard/face/predict2.jpg"));
				
				//faceRecognizer.closeRecognizer();
				
			}
		});
		
		train.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				List<User> users = SahaUserDatabase
						.getAllUsers(MainActivity.this);
				UsersFaces uf = SahaFileManager.getAllUsersFaceImages(users);
				eventBus.post(new FaceRecognitionEvents.TrainRecognizerRequest(uf));
			}
		});

		listUsers.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				List<User> users = SahaUserDatabase
						.getAllUsers(MainActivity.this);
				Log.d("SAHA", "users: " + users.size());
				for (User user : users) {
					Log.e("SAHA",
							"User #" + user.getId() + ", name: "
									+ user.getName() + ", dir: "
									+ user.getDirectory());
				}
			}
		});
		
		deleteUsers.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				SahaUserDatabase.deleteAllUsers(MainActivity.this);
			}
		});

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
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
