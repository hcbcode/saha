package com.hcb.saha.external.source.weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;

import android.os.AsyncTask;
import android.util.Log;

import com.google.inject.Inject;
import com.hcb.saha.external.source.weather.WeatherEvents.WeatherRequest;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Bureau Of Meterology Au.
 * 
 * @author Steven Hadley
 * 
 */
public class WeatherBoMAuProvider {

	private static final String FTP_BOM_GOV_AU = "ftp://ftp2.bom.gov.au/anon/gen/fwo/IDA00001.dat";

	private static final int ID = 0;
	private static final int LOCATION = 1;
	private static final int STATE = 2;
	private static final int FORECAST_DATE = 3;
	private static final int ISSUE_DATE = 4;
	private static final int ISSUE_TIME = 5;
	private static final int TODAY_TEMP_MIN = 6;
	private static final int TODAY_TEMP_MAX = 7;
	private static final int TODAY_TEMP_MIN_PLUS1 = 8;
	private static final int TODAY_TEMP_MAX_PLUS1 = 9;
	private static final int TODAY_TEMP_MIN_PLUS2 = 10;
	private static final int TODAY_TEMP_MAX_PLUS2 = 11;
	private static final int TODAY_TEMP_MIN_PLUS3 = 12;
	private static final int TODAY_TEMP_MAX_PLUS3 = 13;
	private static final int TODAY_TEMP_MIN_PLUS4 = 14;
	private static final int TODAY_TEMP_MAX_PLUS4 = 15;
	private static final int TODAY_TEMP_MIN_PLUS5 = 16;
	private static final int TODAY_TEMP_MAX_PLUS5 = 17;
	private static final int TODAY_TEMP_MIN_PLUS6 = 18;
	private static final int TODAY_TEMP_MAX_PLUS6 = 19;
	private static final int TODAY_TEMP_MIN_PLUS7 = 20;
	private static final int TODAY_TEMP_MAX_PLUS7 = 21;
	private static final int TODAY_FORECAST = 22;
	private static final int TODAY_FORECAST_PLUS1 = 23;
	private static final int TODAY_FORECAST_PLUS2 = 24;
	private static final int TODAY_FORECAST_PLUS3 = 25;
	private static final int TODAY_FORECAST_PLUS4 = 26;
	private static final int TODAY_FORECAST_PLUS5 = 27;
	private static final int TODAY_FORECAST_PLUS6 = 28;
	private static final int TODAY_FORECAST_PLUS7 = 29;

	private static final String BOM_AU = "BOM AU";
	private static final String DELIMITER = "#";

	private Bus eventBus;

	@Inject
	public WeatherBoMAuProvider(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void getWeather(WeatherEvents.WeatherRequest request) {
		new FetchWeather().execute(request);
	}

	private class FetchWeather extends
			AsyncTask<WeatherRequest, Void, WeatherForecast> {

		@Override
		protected WeatherForecast doInBackground(WeatherRequest... params) {
			return parseResult(params);
		}

		@Override
		protected void onPostExecute(WeatherForecast result) {
			if (null != result) {
				eventBus.post(new WeatherEvents.WeatherResult(result, BOM_AU));
			}
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onProgressUpdate(Void... values) {
		}
	}

	private String getData(URL url) throws IOException {
		InputStream in = null;
		try {
			URLConnection urlc = url.openConnection();
			in = urlc.getInputStream();
			byte[] response = readFully(in);
			return new String(response, "UTF-8");
		} finally {
			if (in != null)
				in.close();
		}

	}

	private byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int count; (count = in.read(buffer)) != -1;) {
			out.write(buffer, 0, count);
		}
		return out.toByteArray();
	}

	/**
	 * Parse dat file.
	 * 
	 * @param params
	 * @return
	 */
	private WeatherForecast parseResult(WeatherRequest... params) {
		WeatherForecast forecast = null;
		try {
			String result = WeatherBoMAuProvider.this.getData(new URL(
					FTP_BOM_GOV_AU));

			BufferedReader br = new BufferedReader(new StringReader(result));
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(DELIMITER);
				if (params[0].getLocation().equals(values[1])) {
					forecast = new WeatherForecast()
							.minTemp(values[TODAY_TEMP_MIN])
							.maxTemp(values[TODAY_TEMP_MAX])
							.forecast(values[TODAY_FORECAST])
							.minTempPlus1(values[TODAY_TEMP_MIN_PLUS1])
							.maxTempPlus1(values[TODAY_TEMP_MAX_PLUS1])
							.forecastPlus1(values[TODAY_FORECAST_PLUS1]);
					break;
				}
			}

		} catch (Exception e) {
			Log.e(WeatherBoMAuProvider.class.getSimpleName(),
					"Failed to get weather from BOM", e);
		}
		return forecast;
	}

}
