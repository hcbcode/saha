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
import com.hcb.saha.internal.ui.fragment.widget.WidgetFragment.StateType;
import com.hcb.saha.internal.ui.view.DepthPageTransformer;
import com.hcb.saha.internal.ui.view.FixedSpeedScroller;

public class HomeCarouselFragment extends RoboFragment {

	private static final String TAG = HomeCarouselFragment.class
			.getSimpleName();

	private static final int NUM_PAGES = 3;

	private PagerAdapter pagerAdapter;

	@InjectView(R.id.home_carousel_pager)
	private ViewPager pager;

	private Handler animationHandler = new Handler();
	private AnimationStep animationStep = new AnimationStep();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_home_carousel,
				container, false);
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

		animationHandler.post(animationStep);
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

			// FIXME: use a list of cached fragments
			switch (position) {
			case 0:
				return WeatherFragment.create(StateType.FULL);
			case 1:
				return NewsFragment.create(StateType.FULL);
			case 2:
				return EventFragment.create(StateType.FULL);
			default:
				return WeatherFragment.create(StateType.FULL);
			}

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

		private static final int PAGER_DELAY_MILLIS = 30000;

		@Override
		public void run() {
			if (pager.getCurrentItem() == NUM_PAGES - 1) {
				pager.setCurrentItem(0, false);
			} else {
				pager.setCurrentItem(pager.getCurrentItem() + 1, true);
			}
			animationHandler.postDelayed(animationStep, PAGER_DELAY_MILLIS);
		}

	}

}
