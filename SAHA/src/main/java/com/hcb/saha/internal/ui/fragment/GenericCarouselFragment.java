package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hcb.saha.R;

public class GenericCarouselFragment extends RoboFragment {

	int NUM_PAGES = 5;

	/**
	 * The pager widget, which handles animation.
	 */
	private ViewPager mPager;

	/**
	 * The pager adapter, which provides the pages to the view pager widget.
	 */
	private PagerAdapter mPagerAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_generic_carousel,
				container, false);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Instantiate a ViewPager and a PagerAdapter.
		mPager = (ViewPager) getView().findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(
				this.getChildFragmentManager());
		mPager.setAdapter(mPagerAdapter);
	}

	/**
	 * A simple pager adapter that represents 5 {@link ScreenSlidePageFragment}
	 * objects, in sequence.
	 */
	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return GenericCarouselItemFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

}
