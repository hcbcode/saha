package com.hcb.saha.internal.ui.fragment;

import android.graphics.Matrix;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcb.saha.R;

import roboguice.fragment.RoboFragment;

public class GenericCarouselFragment extends RoboFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_generic_carousel,
				container, false);
		return view;
	}

}
