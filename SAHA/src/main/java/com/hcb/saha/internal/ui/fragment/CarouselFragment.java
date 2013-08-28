package com.hcb.saha.internal.ui.fragment;

import java.lang.reflect.Field;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.hcb.saha.R;
import com.hcb.saha.internal.ui.fragment.widget.EventFragment;
import com.hcb.saha.internal.ui.fragment.widget.NewsFragment;
import com.hcb.saha.internal.ui.fragment.widget.WeatherFragment;
import com.hcb.saha.internal.ui.fragment.widget.WidgetFragment;
import com.hcb.saha.internal.ui.fragment.widget.WidgetFragment.StateType;
import com.hcb.saha.internal.ui.view.DepthPageTransformer;
import com.hcb.saha.internal.ui.view.FixedSpeedScroller;

/**
 * Rotatest widgets through x-axis.
 * 
 * @author Steven Hadley
 * 
 */
public class CarouselFragment extends RoboFragment {

	private static final String TAG = CarouselFragment.class.getSimpleName();
	private static final int NUM_PAGES = 3;
	private PagerAdapter pagerAdapter;
	private StateType stateType;
	@InjectView(R.id.home_carousel_pager)
	private ViewPager pager;
	private Handler animationHandler = new Handler();
	private AnimationStep animationStep = new AnimationStep();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		stateType = StateType.valueOf(getArguments().getString(
				WidgetFragment.STATE_TYPE));

		View view = inflater.inflate(R.layout.fragment_carousel, container,
				false);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		pagerAdapter = new CarouselAdapter(this.getChildFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setPageTransformer(true, new DepthPageTransformer());

		try {
			// Only way to override scroller speed.
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					pager.getContext(), new AccelerateInterpolator());
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			mScroller.set(pager, scroller);

		} catch (Exception e) {
			Log.e(TAG, "Can't set scroller speed", e);
		}

	}

	@Override
	public void onResume() {
		super.onResume();
		animationHandler.postDelayed(animationStep,
				AnimationStep.PAGER_DELAY_MILLIS);
	}

	@Override
	public void onPause() {
		super.onPause();
		animationHandler.removeCallbacks(animationStep);
	}

	/**
	 * Data adapter for Pager.
	 * 
	 * @author Steven Hadley
	 * 
	 */
	private class CarouselAdapter extends FragmentStatePagerAdapter {
		public CarouselAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			Fragment f = null;
			switch (position) {
			case 0:
				f = WeatherFragment.create(stateType);
				break;
			case 1:
				f = NewsFragment.create(stateType);
				break;
			case 2:
				f = EventFragment.create(stateType);
				break;
			default:
				f = WeatherFragment.create(stateType);
				break;
			}
			return f;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	/**
	 * Transition content fragments.
	 * 
	 * @author Steven Hadley
	 * 
	 */
	private class AnimationStep implements Runnable {

		private static final int PAGER_DELAY_MILLIS = 7000;

		@Override
		public void run() {
			// FIXME: This just jumps back to 0 and doesn't scroll
			if (pager.getCurrentItem() == NUM_PAGES - 1) {
				pager.setCurrentItem(0, false);
			} else {
				pager.setCurrentItem(pager.getCurrentItem() + 1, true);
			}
			animationHandler.postDelayed(animationStep, PAGER_DELAY_MILLIS);
		}

	}

	/**
	 * Use this factory to create the Carousel.
	 * 
	 * @param state
	 * @return
	 */
	public static Fragment create(StateType state) {
		Fragment fragment = new CarouselFragment();
		Bundle args = new Bundle();
		args.putString(WidgetFragment.STATE_TYPE, state.getName());
		fragment.setArguments(args);
		return fragment;
	}

}
