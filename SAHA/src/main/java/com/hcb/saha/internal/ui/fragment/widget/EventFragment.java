package com.hcb.saha.internal.ui.fragment.widget;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcb.saha.R;

/**
 * Fragment displays any event data.
 * 
 * @author Steven Hadley
 * 
 */
public class EventFragment extends WidgetFragment {

	public EventFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return getView(getArguments().getString(STATE_TYPE), container,
				inflater);
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_event_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_event_compressed;
	}

	public static Fragment create(StateType state) {
		Fragment fragment = new EventFragment();
		WidgetFragment.addBundle(state, fragment);
		return fragment;
	}

}
