package com.hcb.saha.internal.data.model;

import java.util.Date;
import java.util.List;

public class SensorData {

	private String houseId;
	private String deviceId;
	private String userId;
	private Date datetime;
	private Integer reasonCode;
	private List<SensorDataIndividualSensor> sensors;

	public String getHouseId() {
		return houseId;
	}

	public void setHouseId(String houseId) {
		this.houseId = houseId;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Date getDatetime() {
		return datetime;
	}

	public void setDatetime(Date date) {
		this.datetime = date;
	}

	public Integer getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(Integer reasonCode) {
		this.reasonCode = reasonCode;
	}

	public List<SensorDataIndividualSensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<SensorDataIndividualSensor> sensors) {
		this.sensors = sensors;
	}

}
