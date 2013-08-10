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
import com.hcb.saha.internal.event.CameraEvents;
import com.hcb.saha.internal.processor.CameraProcessor;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

public class HomeUserCloseFragment extends RoboFragment {

	@Inject
	private Bus eventBus;

	@InjectView(R.id.face_measure)
	private TextView faceText;

	@Inject
	private CameraProcessor cameraProcessor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_user_close,
				container, false);
		eventBus.register(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FIXME temp code
		cameraProcessor.startCamera((SurfaceView) getView().findViewById(
				R.id.surface));

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

	@Subscribe
	public void onFaceDetected(CameraEvents.FaceAvailableEvent face) {
		faceText.setText("H: " + face.getFaceHeight()
				+ "\nW:" + face.getFaceWidth());
	}

}
