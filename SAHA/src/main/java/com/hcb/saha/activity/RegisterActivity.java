package com.hcb.saha.activity;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.data.SahaFileManager;
import com.hcb.saha.data.SahaUserDatabase;
import com.hcb.saha.data.model.User;
import com.hcb.saha.data.model.UsersFaces;
import com.hcb.saha.event.FaceRecognitionEvents;
import com.hcb.saha.event.RegistrationEvents;
import com.hcb.saha.fragment.FaceDetectionFragment;
import com.hcb.saha.fragment.UserRegistrationFragment;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Handles user registration process 
 * @author Andreas Borglin
 */
public class RegisterActivity extends RoboFragmentActivity {
	
	private UserRegistrationFragment userRegistrationFragment;
	private FaceDetectionFragment faceDetectionFragment;
	@Inject
	private Bus eventBus;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        eventBus.register(this);
        
        userRegistrationFragment = new UserRegistrationFragment();
        faceDetectionFragment = new FaceDetectionFragment();
        
        faceDetectionFragment.setMode(FaceDetectionFragment.Mode.REGISTRATION);
        
        getSupportFragmentManager().beginTransaction().add(R.id.register_layout, userRegistrationFragment).commit();
        //getSupportFragmentManager().beginTransaction().add(R.id.register_layout, faceDetectionFragment).commit();
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
    public void onUserCreated(RegistrationEvents.UserCreated event) {
    	
    	User user = event.getUser();
    	faceDetectionFragment.setCurrentUser(user);
    	getSupportFragmentManager().beginTransaction().replace(R.id.register_layout, faceDetectionFragment).commit();
    }
    
    @Subscribe
    public void onFaceRegistrationCompleted(RegistrationEvents.FaceRegistrationCompleted event) {
    	// TODO train the recognizer
    	UsersFaces usersFaces = SahaFileManager.getAllUsersFaceImages(SahaUserDatabase.getAllUsers(this));
    	eventBus.post(new FaceRecognitionEvents.TrainRecognizerRequest(usersFaces));
    	Toast.makeText(this, "Training recognizer...", Toast.LENGTH_SHORT).show();
    	finish();
    }
    
}
