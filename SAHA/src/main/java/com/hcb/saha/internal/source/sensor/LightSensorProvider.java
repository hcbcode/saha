package com.hcb.saha.internal.source.sensor;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.common.collect.Range;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.core.SahaConfig;
import com.hcb.saha.internal.event.SensorEvents;
import com.squareup.otto.Bus;

@Singleton
public class LightSensorProvider implements SensorEventListener {

	private static final String TAG = LightSensorProvider.class.getSimpleName();
	private int lastSensorValue = 0;

	// TODO: Not sure if we need this yet
	// private Date lastSensorEvent = new Date(System.currentTimeMillis());

	private Bus eventBus;

	@Inject
	public LightSensorProvider(Application context, Bus eventBus) {

		SensorManager sm = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);

		this.eventBus = eventBus;
		eventBus.register(this);
		Sensor lightSensor = null;

		if ((lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT)) != null) {
			sm.registerListener(this, lightSensor,
					SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "light sensor listening");
		}

	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		// Turn into integer
		int eventValue = Math.round(event.values[0]);

		// Create range that is above and below previous value
		Range<Integer> range = Range.closed(lastSensorValue
				- SahaConfig.Sensor.LIGHT_CHANGE_THRESHOLD, lastSensorValue
				+ SahaConfig.Sensor.LIGHT_CHANGE_THRESHOLD);

		// if new value is not within range, then send event
		if (!range.contains(eventValue)) {
			eventBus.post(new SensorEvents.SensorDetectionEvent(
					SensorEvents.SensorType.LIGHT, new float[] { eventValue }));
		}

		/*
		 * update old sensor value everytime to allow it to progressively
		 * increase without triggering event
		 */
		lastSensorValue = eventValue;

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// NA
	}

}
