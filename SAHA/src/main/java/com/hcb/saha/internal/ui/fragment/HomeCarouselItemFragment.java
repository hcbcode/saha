package com.hcb.saha.internal.ui.fragment;

import roboguice.fragment.RoboFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hcb.saha.R;

/**
 * 
 * @author Steven Hadley
 * 
 */
public class HomeCarouselItemFragment extends RoboFragment {

	public static final String ARG_PAGE = "page";
	private int pageNumber;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static HomeCarouselItemFragment create(int pageNumber) {
		HomeCarouselItemFragment fragment = new HomeCarouselItemFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, pageNumber);
		fragment.setArguments(args);
		return fragment;
	}

	public HomeCarouselItemFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		// TODO: Use a pre-loaded bunch of fragments

		// Inflate the fragment
		ViewGroup rootView = (ViewGroup) inflater.inflate(
				R.layout.fragment_home_carousel_weather, container, false);

		// Set the title view to show the page number.
		((TextView) rootView.findViewById(R.id.row1)).setText(pageNumber + "");

		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return pageNumber;
	}

}
