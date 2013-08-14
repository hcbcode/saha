package com.hcb.saha.internal.ui.fragment.widget;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.event.CameraEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author Steven Hadley
 * 
 */
public class WeatherFragment extends WidgetFragment {

	@Inject
	private Bus eventBus;

	@InjectView(R.id.row3)
	private TextView temp;

	@InjectView(R.id.row2)
	private TextView location;

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
				temp.setTextSize(25);
				location.setTextSize(25);
			} else if (height < 1300 && height > 1100) {
				temp.setTextSize(29);
				location.setTextSize(29);
			} else if (height < 1100 && height > 900) {
				temp.setTextSize(33);
				location.setTextSize(33);
			} else if (height < 900 && height > 700) {
				temp.setTextSize(37);
				location.setTextSize(37);
			} else if (height < 700 && height > 500) {
				temp.setTextSize(50);
				location.setTextSize(50);
			} else if (height < 500 && height > 300) {
				temp.setTextSize(52);
				location.setTextSize(52);
			} else if (height < 300 && height > 100) {
				temp.setTextSize(53);
				location.setTextSize(53);
			} else if (height < 100) {
				temp.setTextSize(54);
				location.setTextSize(54);
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
}
