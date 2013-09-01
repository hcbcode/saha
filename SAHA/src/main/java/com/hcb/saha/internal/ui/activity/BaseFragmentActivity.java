package com.hcb.saha.internal.ui.activity;

import roboguice.activity.RoboFragmentActivity;
import android.os.Bundle;

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
		ViewUtil.goFullScreen(this);
		ViewUtil.keepActivityAwake(this);
	}

}
