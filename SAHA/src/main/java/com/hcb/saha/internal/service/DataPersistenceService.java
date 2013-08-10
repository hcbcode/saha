package com.hcb.saha.internal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.service.RoboIntentService;
import roboguice.service.RoboService;
import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.hcb.saha.internal.data.model.SensorData;
import com.hcb.saha.internal.data.model.SensorDataIndividualSensor;
import com.hcb.saha.internal.event.SensorEvents;
import com.hcb.saha.internal.event.SensorEvents.SensorType;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class DataPersistenceService {

	private static final String TAG = DataPersistenceService.class.getSimpleName();
	
	private Bus eventBus;

	@Inject
	public DataPersistenceService(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}



	@Subscribe
	public void onSensorEvent(SensorEvents.SensorDetectionEvent event) {
		if (event.getSensorType() == SensorType.LIGHT) {
			//Log.d(TAG, "Event sensor service listen: value: " + event.getSensorValues()[0]);
			SensorData sensorData = createDataSensorObject(event.getSensorType().getId(), event.getSensorValues()[0]);
			logSensorObject(sensorData);

		}
	}



	private void logSensorObject(SensorData sensorData) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			String jsonEvent = mapper.writeValueAsString(sensorData);
			//Log.d(TAG, jsonEvent);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//TODO: Write to file
	}



	private SensorData createDataSensorObject(Integer sensorCode, Float sensorValue){

		SensorData sensorData = new SensorData();

		sensorData.setHouseId("house12345678");
		sensorData.setDeviceId("deviceXYZ645");
		sensorData.setUserId("user123");
		sensorData.setReasonCode(1);
		sensorData.setDateTime(new Date(System.currentTimeMillis()));

		List<SensorDataIndividualSensor> sensors = new ArrayList<SensorDataIndividualSensor>();
		SensorDataIndividualSensor sdis = new SensorDataIndividualSensor();
		sdis.setSensorCode(sensorCode);
		sdis.setValue(sensorValue);
		
		sensors.add(sdis);
		sensorData.setSensors(sensors);
		return sensorData;
	}




}