package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.core.SahaSystemState.State;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.event.CameraEvents;
import com.hcb.saha.internal.event.LifecycleEvents;
import com.hcb.saha.internal.event.SensorEvents;
import com.hcb.saha.internal.event.SensorEvents.SensorType;
import com.hcb.saha.internal.processor.CameraProcessor;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class HomeUserNearFragment extends RoboFragment {

	@Inject
	private Bus eventBus;
	@Inject
	private SahaSystemState systemState;

	@InjectView(R.id.face_val)
	private TextView faceText;

	@InjectView(R.id.light_val)
	private TextView lightText;

	@InjectView(R.id.user_name)
	private TextView userName;

	@Inject
	private CameraProcessor cameraProcessor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_user_near,
				container, false);
		eventBus.register(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
		cameraProcessor.tearDownCamera();
	}

	@Override
	public void onResume() {
		super.onResume();

		cameraProcessor.startCamera((SurfaceView) getView().findViewById(
				R.id.surface));
	}

	@Subscribe
	public void onSystemStateChanged(LifecycleEvents.SystemStateChangedEvent event) {
		State state = event.getState();
		switch (state) {
		case REGISTERED_USER: {
			User user = systemState.getCurrentUser();
			updateUserName(user.getName());
			break;
		}
		default: {
			updateUserName(getString(R.string.anonymous));
			break;
		}
		}
	}

	private void updateUserName(final String username) {
		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				userName.setText(username);
			}
		});
	}

	@Subscribe
	public void onFaceDetected(CameraEvents.FaceAvailableEvent event) {
		faceText.setText("H: " + event.getFaceHeight() + "\nW: "
				+ event.getFaceWidth());
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

}
