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
public class BOMAuProvider {

	private static final String FTP_BOM_GOV_AU = "ftp://ftp2.bom.gov.au/anon/gen/fwo/IDA00001.dat";
	private Bus eventBus;

	@Inject
	public BOMAuProvider(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	@Subscribe
	public void getWeather(WeatherEvents.WeatherRequest request) {
		new FetchWeather().execute(request);
	}

	private class FetchWeather extends
			AsyncTask<WeatherRequest, Void, WeatherForecast> {

		private static final int TODAY_TEMP_MIN = 8;
		private static final int TODAY_TEMP_MAX = 7;
		private static final String BOM_AU = "BOM AU";
		private static final String DELIMITER = "#";

		@Override
		protected WeatherForecast doInBackground(WeatherRequest... params) {
			WeatherForecast forecast = null;
			try {
				String result = BOMAuProvider.this.getData(new URL(
						FTP_BOM_GOV_AU));

				BufferedReader br = new BufferedReader(new StringReader(result));
				String line;
				while ((line = br.readLine()) != null) {
					String[] values = line.split(DELIMITER);
					if (params[0].getLocation().equals(values[1])) {
						forecast = new WeatherForecast();
						forecast.setMaxTemp(values[TODAY_TEMP_MAX]);
						forecast.setMinTemp(values[TODAY_TEMP_MIN]);
						break;
					}
				}

			} catch (Exception e) {
				Log.e(BOMAuProvider.class.getSimpleName(),
						"Failed to get weather from BOM", e);
			}
			return forecast;
		}

		@Override
		protected void onPostExecute(WeatherForecast result) {
			eventBus.post(new WeatherEvents.WeatherResult(result, BOM_AU));
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

}
