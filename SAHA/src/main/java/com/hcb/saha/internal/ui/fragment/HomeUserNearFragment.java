package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.internal.ui.fragment.widget.EventFragment;
import com.hcb.saha.internal.ui.fragment.widget.NewsFragment;
import com.hcb.saha.internal.ui.fragment.widget.WeatherFragment;
import com.hcb.saha.internal.ui.fragment.widget.BaseWidgetFragment.StateType;
import com.squareup.otto.Bus;

/**
 * This fragment is displayed when a face is detected but not yet identified.
 * 
 * @author Steven Hadley
 * 
 */
public class HomeUserNearFragment extends RoboFragment {

	@Inject
	private Bus eventBus;

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

		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();

		addFragment(transaction, R.id.frag1,
				NewsFragment.create(StateType.COMPRESSED));
		addFragment(transaction, R.id.frag2,
				WeatherFragment.create(StateType.COMPRESSED));
		addFragment(transaction, R.id.frag3,
				EventFragment.create(StateType.COMPRESSED));

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	private void addFragment(FragmentTransaction transaction, int fragmentId,
			Fragment fragment) {
		transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(fragmentId, fragment);
		transaction.commit();
	}
}
