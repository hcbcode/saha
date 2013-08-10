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
import com.hcb.saha.internal.event.SensorEvents;
import com.squareup.otto.Bus;


@Singleton
public class LightSensorProvider implements SensorEventListener {

	private static final String TAG = LightSensorProvider.class.getSimpleName();
	private float lastSensorValue = 0f;
	
	private Bus eventBus;
	
	@Inject
	public LightSensorProvider(Application context, Bus eventBus) {

		SensorManager sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		
		this.eventBus = eventBus;
		eventBus.register(this);
		
		Log.d(TAG, "light");
		Sensor lightSensor = null;
		if ((lightSensor = sm.getDefaultSensor(Sensor.TYPE_LIGHT)) != null) {
			sm.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_UI);
			Log.d(TAG, "light sensor listening");
		}

	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		float eventValue = event.values[0];
		if (lastSensorValue != eventValue){
			//Log.d(TAG, "onsensorchanged: " + eventValue);
			eventBus.post(new SensorEvents.SensorDetectionEvent(SensorEvents.SensorType.LIGHT, new float[] {eventValue}));
			lastSensorValue = eventValue;
		}

	}


	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


}
