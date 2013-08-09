package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.processor.CameraProcessor;

public class HomeUserCloseFragment extends RoboFragment {

	@Inject
	private CameraProcessor cameraProcessor;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_user_close,
				container, false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FIXME temp code
		cameraProcessor.startCamera((SurfaceView) getView().findViewById(
				R.id.surface));

	}

}
