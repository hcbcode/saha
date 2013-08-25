package com.hcb.saha.external.weather;

/**
 * Event bus events.
 * 
 * @author steven hadley
 * 
 */
public class WeatherEvents {

	public static class WeatherRequest {
		String location;

		public WeatherRequest(String location) {
			this.location = location;
		}

		public String getLocation() {
			return location;
		}

	}

	public static class WeatherResult {

		private WeatherForecast item;
		private String source;

		public WeatherResult(WeatherForecast item, String source) {
			this.item = item;
			this.source = source;
		}

		public WeatherForecast getWeatherForecast() {
			return item;
		}

		public String getSource() {
			return source;
		}
	}

}
