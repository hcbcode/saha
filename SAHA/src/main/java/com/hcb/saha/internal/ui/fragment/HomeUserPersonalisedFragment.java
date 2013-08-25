package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcb.saha.R;
import com.hcb.saha.internal.ui.fragment.widget.SensorFragment;
import com.hcb.saha.internal.ui.fragment.widget.UserFragment;
import com.hcb.saha.internal.ui.fragment.widget.WidgetFragment.StateType;

/**
 * This fragment is displayed when a user has been identified.
 * 
 * @author Steven Hadley
 * 
 */
public class HomeUserPersonalisedFragment extends RoboFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_home_user_personalised,
				container, false);

		FragmentTransaction transaction = getChildFragmentManager()
				.beginTransaction();

		addFragment(transaction, R.id.frag1,
				UserFragment.create(StateType.COMPRESSED));
		addFragment(transaction, R.id.frag2,
				CarouselFragment.create(StateType.COMPRESSED));
		addFragment(transaction, R.id.frag3,
				SensorFragment.create(StateType.COMPRESSED));

		return view;
	}

	private void addFragment(FragmentTransaction transaction, int fragmentId,
			Fragment fragment) {
		transaction = getChildFragmentManager().beginTransaction();
		transaction.replace(fragmentId, fragment);
		transaction.commit();
	}

}
