package com.hcb.saha.internal.source.sensor;

import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LightSensorProvider implements SensorEventListener {

	@Inject
	public LightSensorProvider(Application context) {

		SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

		Log.d("LIGHT", "light");
		Sensor lightSensor = null;
		if ((lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT)) != null) {
			//sm.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
			Log.d("LIGHT", "light sensor listening");
		}

		Sensor proxSensor = null;
		if ((proxSensor = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY)) != null) {
			Log.d("PROX", "has prox");
			sm.registerListener(new SensorEventListener() {

				@Override
				public void onSensorChanged(SensorEvent event) {
					Log.d("PROX", "value: " + event.values[0]);
				}

				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {

				}
			}, proxSensor, SensorManager.SENSOR_DELAY_NORMAL);
			Log.d("LIGHT", "light sensor listening");
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		Log.d("LIGHT", "light accuracy: " + accuracy);

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		Log.d("LIGHT", "onsensorchanged: " + event.values[0]);

	}


}
