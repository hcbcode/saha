package com.hcb.saha.external.source.weather;

public class WeatherForecast {

	private String maxTemp;
	private String minTemp;
	public final static String TEMP_UNIT = "\u2103";

	public String getMaxTemp() {
		return maxTemp;
	}

	public void setMaxTemp(String maxTemp) {
		this.maxTemp = maxTemp;
	}

	public String getMinTemp() {
		return minTemp;
	}

	public void setMinTemp(String minTemp) {
		this.minTemp = minTemp;
	}

}
