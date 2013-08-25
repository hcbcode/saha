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
import com.hcb.saha.internal.event.SensorEvents;
import com.hcb.saha.internal.event.SensorEvents.SensorType;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * 
 * @author Steven Hadley
 * 
 */
public class SensorFragment extends WidgetFragment {

	@InjectView(R.id.face_val)
	private TextView faceText;
	@InjectView(R.id.light_val)
	private TextView lightText;
	@Inject
	private Bus eventBus;

	public SensorFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		return getView(getArguments().getString(STATE_TYPE), container,
				inflater);
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_sensor_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_sensor_compressed;
	}

	public static Fragment create(StateType state) {
		Fragment fragment = new SensorFragment();
		WidgetFragment.addBundle(state, fragment);
		return fragment;
	}

	@Subscribe
	public void onFaceDetected(CameraEvents.FaceAvailableEvent event) {
		int height = event.getFaceHeight();
		faceText.setText("H: " + height + "\nW: " + event.getFaceWidth());

	}

	@Subscribe
	public void onFaceDisappeared(CameraEvents.FaceDisappearedEvent event) {
		faceText.setText("No face");
	}

	@Subscribe
	public void onSensorEvent(SensorEvents.SensorDetectionEvent event) {
		if (event.getSensorType() == SensorType.LIGHT) {
			lightText.setText("Light: " + event.getSensorValues()[0]);
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}
}
