package com.hcb.saha.internal.ui.fragment.widget;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.external.weather.WeatherEvents;
import com.hcb.saha.external.weather.WeatherForecast;
import com.hcb.saha.internal.event.CameraEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Weather.
 * 
 * @author Steven Hadley
 * 
 */
public class WeatherFragment extends WidgetFragment {

	// FIXME: Should come from a config/preference
	private static final String SYDNEY = "Sydney";

	@Inject
	private Bus eventBus;
	@InjectView(R.id.row3)
	private TextView tempMax;
	@InjectView(R.id.row2)
	private TextView tempMin;
	@InjectView(R.id.row4)
	@Nullable
	private TextView forecast;
	@InjectView(R.id.row1)
	@Nullable
	private TextView title;
	private StateType stateType;
	private Handler weatherHandler = new Handler();
	private WeatherRunner weatherRunner = new WeatherRunner();

	public WeatherFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		stateType = StateType.valueOf(getArguments().getString(STATE_TYPE));
		return getView(getArguments().getString(STATE_TYPE), container,
				inflater);
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_weather_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_weather_compressed;
	}

	public static Fragment create(StateType state) {
		Fragment fragment = new WeatherFragment();
		WidgetFragment.addBundle(state, fragment);
		return fragment;
	}

	@Subscribe
	public void onFaceDetected(CameraEvents.FaceAvailableEvent event) {
		int height = event.getFaceHeight();

		if (stateType == StateType.COMPRESSED) {

			// FIXME: Experimental. Just playing.
			if (height < 1500 && height > 1300) {
				tempMax.setTextSize(25);
				tempMin.setTextSize(25);
			} else if (height < 1300 && height > 1100) {
				tempMax.setTextSize(29);
				tempMin.setTextSize(29);
			} else if (height < 1100 && height > 900) {
				tempMax.setTextSize(30);
				tempMin.setTextSize(30);
			} else if (height < 900 && height > 700) {
				tempMax.setTextSize(31);
				tempMin.setTextSize(31);
			} else if (height < 700 && height > 500) {
				tempMax.setTextSize(33);
				tempMin.setTextSize(33);
			} else if (height < 500 && height > 300) {
				tempMax.setTextSize(35);
				tempMin.setTextSize(35);
			} else if (height < 300 && height > 100) {
				tempMax.setTextSize(37);
				tempMin.setTextSize(37);
			} else if (height < 100) {
				tempMax.setTextSize(40);
				tempMin.setTextSize(40);
			}
		}
	}

	@Subscribe
	public void onFaceDisappeared(CameraEvents.FaceDisappearedEvent event) {
		// nothing. Mmm
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(getClass().getSimpleName(), "onResume");
		eventBus.post(new WeatherEvents.WeatherRequest(SYDNEY));
		weatherHandler.postDelayed(weatherRunner, WeatherRunner.DELAY_MILLIS);
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(getClass().getSimpleName(), "onPause");
		weatherHandler.removeCallbacks(weatherRunner);
	}

	@Subscribe
	public void onWeatherResult(WeatherEvents.WeatherResult result) {

		// FIXME: Use better logic. Use date from result to work out tmrws
		// weather.

		if ((null != result.getWeatherForecast().getMaxTemp() && result
				.getWeatherForecast().getMaxTemp().length() > 0)
				&& (null != result.getWeatherForecast().getMinTemp() && result
						.getWeatherForecast().getMinTemp().length() > 0)) {

			if (null != title) {
				title.setText("Today");
			}
			tempMax.setText("High: " + result.getWeatherForecast().getMaxTemp()
					+ WeatherForecast.TEMPERATURE_DISPLAY_UNIT);
			tempMin.setText("Low: " + result.getWeatherForecast().getMinTemp()
					+ WeatherForecast.TEMPERATURE_DISPLAY_UNIT);

			if (null != forecast) {
				forecast.setText(result.getWeatherForecast()
						.getTodaysDateDisplayFormat()
						+ ", "
						+ result.getWeatherForecast().getForecast());
			}

		} else {
			if (null != title) {
				title.setText("Tomorrow "
						+ result.getWeatherForecast()
								.getTodaysDatePlus1DispalyFormat());
			}
			tempMax.setText("High: "
					+ result.getWeatherForecast().getMaxTempPlus1()
					+ WeatherForecast.TEMPERATURE_DISPLAY_UNIT);
			tempMin.setText("Low: "
					+ result.getWeatherForecast().getMinTempPlus1()
					+ WeatherForecast.TEMPERATURE_DISPLAY_UNIT);

			if (null != forecast) {
				forecast.setText("Tomorrow, "+ result.getWeatherForecast()
						.getTodaysDatePlus1DispalyFormat()
						+ ", "
						+ result.getWeatherForecast().getForecastPlus1());
			}
		}
	}

	/**
	 * Gets weather every x minutes.
	 * 
	 * @author Steven Hadley
	 * 
	 */
	private class WeatherRunner implements Runnable {

		// FIXME: debug setting
		private static final int DELAY_MILLIS = 1000 * 60 * 60; // 1 hour

		@Override
		public void run() {
			eventBus.post(new WeatherEvents.WeatherRequest(SYDNEY));
			weatherHandler.postDelayed(weatherRunner, DELAY_MILLIS);
		}

	}
}
