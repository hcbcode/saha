package com.hcb.saha.internal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import roboguice.service.RoboIntentService;
import roboguice.service.RoboService;
import android.app.AlarmManager;
import android.app.Application;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.inject.Inject;
import com.hcb.saha.internal.data.fs.SahaFileManager;
import com.hcb.saha.internal.data.model.SensorData;
import com.hcb.saha.internal.data.model.SensorDataIndividualSensor;
import com.hcb.saha.internal.event.SensorEvents;
import com.hcb.saha.internal.event.SensorEvents.SensorType;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class DataPersistenceService {

	private static final String TAG = DataPersistenceService.class.getSimpleName();
	
	private Bus eventBus;
	private Context context;
	
	@Inject
	private WifiManager wifiManager;
	
	@Inject
	public DataPersistenceService(Bus eventBus, Application context, AlarmManager alarmManager) {
		this.eventBus = eventBus;
		eventBus.register(this);
		this.context = context;
		
		//Start the Cloud Storage Service
		Intent intent = new Intent(context, RemoteStorageService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 15 * 60 *1000, pi);
	}



	@Subscribe
	public void onSensorEvent(SensorEvents.SensorDetectionEvent event) {
		if (event.getSensorType() == SensorType.LIGHT) {
			//If not connected to wifi and cant determine access point ID (houseId), dont log events
			WifiInfo wifiInfo = wifiManager.getConnectionInfo();
			String bssId = wifiInfo.getBSSID();
			if (bssId != null && !bssId.equals("")){
				SensorData sensorData = createDataSensorObject(event.getSensorType().getId(), event.getSensorValues()[0], bssId);
				logSensorObject(sensorData);
			}
		}
	}



	private void logSensorObject(SensorData sensorData) {
		ObjectMapper mapper = new ObjectMapper();
		
		String jsonEvent = null;
		try {
			jsonEvent = mapper.writeValueAsString(sensorData);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (jsonEvent != null){
			boolean result = SahaFileManager.appendEvent(jsonEvent);
			//Log.d(TAG, "Event append: " + result);
		}

	}



	private SensorData createDataSensorObject(Integer sensorCode, Float sensorValue, String bssId){
		
		
		bssId = bssId.replace(":", "");
		String androidId = Secure.getString(context.getContentResolver(),Secure.ANDROID_ID); 
		
		SensorData sensorData = new SensorData();

		sensorData.setHouseId(bssId);
		sensorData.setDeviceId(androidId);
		sensorData.setUserId("user1");
		sensorData.setReasonCode(1);
		sensorData.setDatetime(new Date(System.currentTimeMillis()));

		List<SensorDataIndividualSensor> sensors = new ArrayList<SensorDataIndividualSensor>();
		SensorDataIndividualSensor sdis = new SensorDataIndividualSensor();
		sdis.setSensorCode(sensorCode);
		sdis.setValue(sensorValue);
		
		sensors.add(sdis);
		sensorData.setSensors(sensors);
		return sensorData;
	}




}
