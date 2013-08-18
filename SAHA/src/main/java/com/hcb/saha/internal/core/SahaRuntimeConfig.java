package com.hcb.saha.internal.core;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.event.SystemEvents;
import com.squareup.otto.Bus;

/**
 * Runtime config that can be injected to get access to configurable values
 *
 * This class is implemented with the assumption that settings are
 * implemented using Android Preferences for setting and reading activity
 * 
 * @author Andreas Borglin
 */
@Singleton
public class SahaRuntimeConfig implements OnSharedPreferenceChangeListener {

	private SharedPreferences sharedPref;
	private Application context;
	private Bus eventBus;

	@Inject
	public SahaRuntimeConfig(Application context, Bus eventBus) {
		this.context = context;
		this.eventBus = eventBus;
		sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		sharedPref.registerOnSharedPreferenceChangeListener(this);
	}

	private String getPref(int keyId) {
		String key = context.getString(keyId);
		if (key != null) {
			String pref = sharedPref.getString(key, null);
			return pref;
		}
		return null;
	}

	public int getInt(int keyId) {
		try {
			String pref = getPref(keyId);
			if (pref != null) {
				int val = Integer.parseInt(pref);
				return val;
			}
		} catch (NumberFormatException e) {

		}
		return -1;
	}

	public float getFloat(int keyId) {
		try {
			String pref = getPref(keyId);
			if (pref != null) {
				float val = Float.parseFloat(pref);
				return val;
			}
		} catch (NumberFormatException e) {

		}
		return -1f;
	}

	public boolean getBoolean(int keyId) {
		String key = context.getString(keyId);
		if (key != null) {
			return sharedPref.getBoolean(key, false);
		}
		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		eventBus.post(new SystemEvents.SystemSettingChangedEvent(this, key));
	}

}
