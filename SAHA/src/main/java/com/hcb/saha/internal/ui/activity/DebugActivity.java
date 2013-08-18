package com.hcb.saha.internal.ui.activity;

import roboguice.activity.RoboActivity;
import android.os.Bundle;

import com.hcb.saha.internal.ui.fragment.DebugSettingsFragment;

/**
 * Debug activity used for configuring app during runtime
 * @author Andreas Borglin
 */
public class DebugActivity extends RoboActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getFragmentManager().beginTransaction()
		.replace(android.R.id.content, new DebugSettingsFragment())
		.commit();
	}
}
