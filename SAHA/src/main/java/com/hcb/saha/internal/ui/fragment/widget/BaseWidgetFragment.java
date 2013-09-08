package com.hcb.saha.internal.ui.fragment.widget;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Base widget which does view switching.
 * 
 * @author Steven Hadley
 * 
 */
public abstract class BaseWidgetFragment extends RoboFragment {

	public static enum StateType {
		FULL(1, "FULL"), COMPRESSED(2, "COMPRESSED");

		private int id;
		private String name;

		StateType(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	public final static String STATE_TYPE = "STATE_TYPE";

	protected abstract int getFullLayout();

	protected abstract int getCompressedLayout();

	protected ViewGroup getView(String layout, ViewGroup container,
			LayoutInflater inflater) {

		StateType stateType = StateType.valueOf(layout);

		ViewGroup view = null;
		switch (stateType) {
		case FULL:
			view = (ViewGroup) inflater.inflate(getFullLayout(), container,
					false);
			break;
		case COMPRESSED:
			view = (ViewGroup) inflater.inflate(getCompressedLayout(),
					container, false);
			break;
		default:
			view = (ViewGroup) inflater.inflate(getCompressedLayout(),
					container, false);
			break;
		}
		return view;
	}

	protected static Fragment addBundle(StateType state, Fragment fragment) {
		Bundle args = new Bundle();
		args.putString(STATE_TYPE, state.getName());
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d(getClass().getSimpleName(), "onDestroyView");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(getClass().getSimpleName(), "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d(getClass().getSimpleName(), "onPause");
	}

}
