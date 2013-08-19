package com.hcb.saha.internal.ui.fragment.widget;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.external.source.weather.WeatherEvents;
import com.hcb.saha.external.source.weather.WeatherForecast;
import com.hcb.saha.internal.event.CameraEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author Steven Hadley
 * 
 */
public class WeatherFragment extends WidgetFragment {

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

	private int stateType;

	public WeatherFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		stateType = getArguments().getInt(STATE_TYPE);
		return getView(stateType, container, inflater);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		eventBus.post(new WeatherEvents.WeatherRequest(SYDNEY));
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

		if (stateType == StateType.COMPRESSED.getId()) {

			// FIXME: Experimental. Just playing.
			if (height < 1500 && height > 1300) {
				tempMax.setTextSize(25);
				tempMin.setTextSize(25);
			} else if (height < 1300 && height > 1100) {
				tempMax.setTextSize(29);
				tempMin.setTextSize(29);
			} else if (height < 1100 && height > 900) {
				tempMax.setTextSize(33);
				tempMin.setTextSize(33);
			} else if (height < 900 && height > 700) {
				tempMax.setTextSize(37);
				tempMin.setTextSize(37);
			} else if (height < 700 && height > 500) {
				tempMax.setTextSize(50);
				tempMin.setTextSize(50);
			} else if (height < 500 && height > 300) {
				tempMax.setTextSize(52);
				tempMin.setTextSize(52);
			} else if (height < 300 && height > 100) {
				tempMax.setTextSize(53);
				tempMin.setTextSize(53);
			} else if (height < 100) {
				tempMax.setTextSize(54);
				tempMin.setTextSize(54);
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
		eventBus.post(new WeatherEvents.WeatherRequest(SYDNEY));
	}

	@Subscribe
	public void onWeatherResult(WeatherEvents.WeatherResult result) {

		// FIXME: Use better logic. Use date from result to work out tmrws
		// weather.

		if (null != result.getWeatherForecast().getMaxTemp()
				&& result.getWeatherForecast().getMaxTemp().length() > 0) {

			tempMax.setText("Max " + result.getWeatherForecast().getMaxTemp()
					+ WeatherForecast.TEMP_UNIT);
			tempMin.setText("Min " + result.getWeatherForecast().getMinTemp()
					+ WeatherForecast.TEMP_UNIT);

			if (null != forecast) {
				forecast.setText(result.getWeatherForecast().getForecast());
			}
		} else {
			tempMax.setText("Tmrw Max "
					+ result.getWeatherForecast().getMaxTempPlus1()
					+ WeatherForecast.TEMP_UNIT);
			tempMin.setText("Tmrw Min "
					+ result.getWeatherForecast().getMinTempPlus1()
					+ WeatherForecast.TEMP_UNIT);

			if (null != forecast) {
				forecast.setText(result.getWeatherForecast().getForecastPlus1());
			}
		}
	}
}
