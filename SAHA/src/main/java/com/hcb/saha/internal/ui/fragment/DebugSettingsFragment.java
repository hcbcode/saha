package com.hcb.saha.internal.ui.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.hcb.saha.R;

/**
 * Debug settings fragment
 * 
 * @author Andreas Borglin
 */
public class DebugSettingsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.debug_preferences);
	}

}
