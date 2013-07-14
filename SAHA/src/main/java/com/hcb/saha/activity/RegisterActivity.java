package com.hcb.saha.activity;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.view.Menu;

import com.hcb.saha.R;
import com.hcb.saha.data.model.User;
import com.hcb.saha.fragment.FaceDetectionFragment;
import com.hcb.saha.fragment.UserRegistrationFragment;

/**
 * Handles user registration process 
 * @author Andreas Borglin
 */
public class RegisterActivity extends RoboFragmentActivity {
	
	private UserRegistrationFragment userRegistrationFragment;
	private FaceDetectionFragment faceDetectionFragment;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        userRegistrationFragment = new UserRegistrationFragment();
        faceDetectionFragment = new FaceDetectionFragment();
        
        // TODO: hardcoded for now
        User andreas = new User(1, "andreas", "andreas");
        faceDetectionFragment.setCurrentUser(andreas);
        
        //getSupportFragmentManager().beginTransaction().add(R.id.register_layout, userRegistrationFragment).commit();
        getSupportFragmentManager().beginTransaction().add(R.id.register_layout, faceDetectionFragment).commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.face_detection, menu);
        return true;
    }
    
}
