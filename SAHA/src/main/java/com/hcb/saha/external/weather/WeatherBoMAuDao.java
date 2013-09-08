package com.hcb.saha.external.weather;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import android.os.AsyncTask;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.external.weather.WeatherEvents.WeatherRequest;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Bureau Of Meterology Au.
 * <p>
 * http://www.bom.gov.au/catalogue/data-feeds.shtml <br>
 * http://www.bom.gov.au/info/precis_forecasts.shtml
 * 
 * @author Steven Hadley
 * 
 */
@Singleton
public class WeatherBoMAuDao {

	private static final String TAG = WeatherBoMAuDao.class.getSimpleName();

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

	private ReentrantLock lock = new ReentrantLock();
	private WeatherForecast cachedWeatherForecast;
	private int cacheTime = 1000 * 60 * 60; // 1 hour

	private Bus eventBus;

	@Inject
	public WeatherBoMAuDao(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void getWeather(WeatherEvents.WeatherRequest request) {
		lock.lock();
		if (null == cachedWeatherForecast
				|| (null != cachedWeatherForecast && (new Date().getTime() > (cachedWeatherForecast
						.getReadDate().getTime() + cacheTime)))) {
			Log.d(TAG, "Requesting weather");
			new FetchWeather().execute(request);
		} else {
			Log.d(TAG, "Using cached weather");
			eventBus.post(new WeatherEvents.WeatherResult(
					cachedWeatherForecast, BOM_AU));

		}
		lock.unlock();

	}

	private class FetchWeather extends
			AsyncTask<WeatherRequest, Void, WeatherForecast> {

		@Override
		protected WeatherForecast doInBackground(WeatherRequest... params) {
			Log.d(FetchWeather.class.getSimpleName(),
					"Starting weather download");
			return downloadWeather(params);
		}

		@Override
		protected void onPostExecute(WeatherForecast result) {
			Log.d(FetchWeather.class.getSimpleName(),
					"Received weather download");
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

	/**
	 * Download and parse dat file.
	 * 
	 * TODO: Extract the ftp clientF bit and the data converter
	 * 
	 * @param params
	 * @return
	 */
	private WeatherForecast downloadWeather(WeatherRequest... params) {
		Log.d(TAG, "Downloading weather");
		WeatherForecast forecast = null;
		try {
			String result = WeatherBoMAuDao.this
					.getData(new URL(FTP_BOM_GOV_AU));

			BufferedReader br = new BufferedReader(new StringReader(result));
			String line;
			while ((line = br.readLine()) != null) {
				String[] values = line.split(DELIMITER);
				if (params[0].getLocation().equals(values[1])) {
					// FIXME: Get all fields for 7 day forecast

					SimpleDateFormat dateFormat = new SimpleDateFormat(
							"yyyyMMdd");

					forecast = new WeatherForecast()
							.minTemp(values[TODAY_TEMP_MIN])
							.maxTemp(values[TODAY_TEMP_MAX])
							.forecast(values[TODAY_FORECAST])
							.minTempPlus1(values[TODAY_TEMP_MIN_PLUS1])
							.maxTempPlus1(values[TODAY_TEMP_MAX_PLUS1])
							.forecastPlus1(values[TODAY_FORECAST_PLUS1])
							.readDate(new Date())
							.forecastDate(
									dateFormat.parse(values[FORECAST_DATE]));

					lock.lock();
					cachedWeatherForecast = forecast;
					lock.unlock();
					Log.d(TAG, "Found data for: " + params[0].getLocation());
					break;
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "Failed to get weather from BOM", e);
		}
		return forecast;
	}

	private String getData(URL url) throws IOException {
		Log.d(TAG, "Fetching raw data");
		InputStream in = null;
		try {
			URLConnection urlc = url.openConnection();
			in = urlc.getInputStream();
			byte[] response = readFully(in);
			return new String(response, "UTF-8");
		} finally {
			if (in != null) {
				in.close();
			}
		}

	}

	private byte[] readFully(InputStream in) throws IOException {
		Log.d(TAG, "Reading bytes");
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		for (int count; (count = in.read(buffer)) != -1;) {
			out.write(buffer, 0, count);
		}
		return out.toByteArray();
	}
}
