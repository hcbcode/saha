package com.hcb.saha.internal.ui.fragment;

import java.lang.reflect.Field;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;

import com.hcb.saha.R;
import com.hcb.saha.internal.ui.view.DepthPageTransformer;
import com.hcb.saha.internal.ui.view.FixedSpeedScroller;
import com.hcb.saha.internal.ui.view.ZoomOutPageTransformer;

public class HomeCarouselFragment extends RoboFragment {

	int NUM_PAGES = 6;
	private ViewPager pager;
	private PagerAdapter pagerAdapter;

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

		pager = (ViewPager) getView().findViewById(R.id.pager);
		pagerAdapter = new CarouselAdapter(this.getChildFragmentManager());
		pager.setAdapter(pagerAdapter);
		pager.setPageTransformer(true, new DepthPageTransformer());

		try {
			FixedSpeedScroller scroller = new FixedSpeedScroller(
					pager.getContext(), new AccelerateInterpolator());
			Field mScroller = ViewPager.class.getDeclaredField("mScroller");
			mScroller.setAccessible(true);
			mScroller.set(pager, scroller);

		} catch (Exception e) {
			// FIXME fix it
			e.printStackTrace();
		}

		animationHandler.post(animationStep);
	}

	/**
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
			return HomeCarouselItemFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	/**
	 * 
	 * @author Steven Hadley
	 * 
	 */
	private class AnimationStep implements Runnable {

		private static final int PAGER_DELAY_MILLIS = 5000;

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
