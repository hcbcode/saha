package com.hcb.saha.internal.service;

import roboguice.service.RoboIntentService;
import roboguice.service.RoboService;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.inject.Inject;
import com.hcb.saha.internal.data.model.SensorData;
import com.hcb.saha.internal.data.model.SensorDataIndividualSensor;
import com.hcb.saha.internal.event.SensorEvents;
import com.hcb.saha.internal.event.SensorEvents.SensorType;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class DataPersistenceService extends RoboIntentService {

	private static final String TAG = DataPersistenceService.class.getSimpleName();
	
	
	@Inject
	private Bus eventBus;
	
	public DataPersistenceService() {
		super("DataPersistenceService");
	}

	

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Data Persistence Service started");
		
		eventBus.register(this);
		
		
	}
	
	@Subscribe
	public void onSensorEvent(SensorEvents.SensorDetectionEvent event) {
		if (event.getSensorType() == SensorType.LIGHT) {
			Log.d(TAG, "Event sensor service listen: value: " + event.getSensorValues()[0]);
		}
	}



	private SensorData createDataSensorObject(Integer sensorCode, Float sensorValue){
		
		SensorData sensorData = new SensorData();
		
		sensorData.setHouseId("house12345678");
		sensorData.setDeviceId("deviceXYZ645");
		sensorData.setUserId("user123");
		sensorData.setReasonCode(1);
		
		List sensors = new ArrayList();
		SensorDataIndividualSensor sdis = new SensorDataIndividualSensor();
		sdis.setSensorCode(sensorCode);
		sdis.setValue(sensorValue);
		
		
		
		sensorData.setSensors(sensors);
		
		
		
		
		return
	}
	
	
	
	
}
