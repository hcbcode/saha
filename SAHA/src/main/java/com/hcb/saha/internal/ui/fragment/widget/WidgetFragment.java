package com.hcb.saha.internal.ui.fragment.widget;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

/**
 * Base widget which does view switching.
 * 
 * @author Steven Hadley
 * 
 */
public abstract class WidgetFragment extends RoboFragment {

	public static enum StateType {
		FULL(1), COMPRESSED(2);

		private int id;

		StateType(int id) {
			this.id = id;
		}

		int getId() {
			return id;
		}
	}

	protected final static String STATE_TYPE = null;

	protected abstract int getFullLayout();

	protected abstract int getCompressedLayout();

	protected ViewGroup getView(int layout, ViewGroup container,
			LayoutInflater inflater) {

		if (0 == layout) {
			// default it
			layout = StateType.COMPRESSED.getId();
		}

		ViewGroup view = null;
		switch (layout) {
		case 1:
			view = (ViewGroup) inflater.inflate(getFullLayout(), container,
					false);
			break;
		case 2:
			view = (ViewGroup) inflater.inflate(getCompressedLayout(),
					container, false);
			break;
		}
		return view;
	}

	protected static Fragment addBundle(StateType state, Fragment fragment) {
		Bundle args = new Bundle();
		args.putInt(STATE_TYPE, state.getId());
		fragment.setArguments(args);
		return fragment;
	}

}
