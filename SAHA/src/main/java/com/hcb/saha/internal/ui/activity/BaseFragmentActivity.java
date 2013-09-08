package com.hcb.saha.internal.ui.activity;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.hcb.saha.internal.ui.view.ViewUtil;

/**
 * Base fragment activity
 * 
 * @author Andreas Borglin
 */
public class BaseFragmentActivity extends RoboFragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(getClass().getSimpleName(), "onCreate()");
		ViewUtil.goFullScreen(this);
		ViewUtil.keepActivityAwake(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.d(getClass().getSimpleName(), "onResume()");
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.d(getClass().getSimpleName(), "onPause()");
	}

	@Override
	public void onRestart() {
		super.onRestart();
		Log.d(getClass().getSimpleName(), "onRestart()");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d(getClass().getSimpleName(), "onStart()");
	}

	@Override
	public void onStop() {
		Log.d(getClass().getSimpleName(), "onStop()");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.d(getClass().getSimpleName(), "onDestroy()");
	}

}
