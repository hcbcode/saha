package com.hcb.saha.external.weather;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class WeatherForecast {

	private Date readDate;

	private String maxTemp;
	private String minTemp;
	private String forecast;

	private String maxTempPlus1;
	private String minTempPlus1;
	private String forecastPlus1;

	private Date forecastDate;

	private String todaysDateDisplayFormat;
	private String todaysDatePlus1DispalyFormat;

	public final static String TEMPERATURE_DISPLAY_UNIT = "\u2103";

	public String getMaxTemp() {
		return maxTemp;
	}

	public WeatherForecast maxTemp(String maxTemp) {
		this.maxTemp = maxTemp;
		return this;
	}

	public String getMinTemp() {
		return minTemp;
	}

	public WeatherForecast minTemp(String minTemp) {
		this.minTemp = minTemp;
		return this;
	}

	public String getForecast() {
		return forecast;
	}

	public WeatherForecast forecast(String forecast) {
		this.forecast = forecast;
		return this;
	}

	public String getMaxTempPlus1() {
		return maxTempPlus1;
	}

	public WeatherForecast maxTempPlus1(String maxTempPlus1) {
		this.maxTempPlus1 = maxTempPlus1;
		return this;
	}

	public String getMinTempPlus1() {
		return minTempPlus1;
	}

	public WeatherForecast minTempPlus1(String minTempPlus1) {
		this.minTempPlus1 = minTempPlus1;
		return this;
	}

	public String getForecastPlus1() {
		return forecastPlus1;
	}

	public WeatherForecast forecastPlus1(String forecastPlus1) {
		this.forecastPlus1 = forecastPlus1;
		return this;
	}

	public Date getReadDate() {
		return readDate;
	}

	public WeatherForecast readDate(Date readDate) {
		this.readDate = readDate;
		return this;
	}

	public Date getForecastDate() {
		return forecastDate;
	}

	public WeatherForecast forecastDate(Date forecastDate) {
		this.forecastDate = forecastDate;

		SimpleDateFormat sdf = new SimpleDateFormat("dd LLL");
		Calendar cal = Calendar.getInstance().getInstance();
		cal.setTime(forecastDate);
		todaysDateDisplayFormat = sdf.format(cal.getTime());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		todaysDatePlus1DispalyFormat = sdf.format(cal.getTime());
		return this;
	}

	public String getTodaysDateDisplayFormat() {
		return todaysDateDisplayFormat;
	}

	public String getTodaysDatePlus1DispalyFormat() {
		return todaysDatePlus1DispalyFormat;
	}

}
