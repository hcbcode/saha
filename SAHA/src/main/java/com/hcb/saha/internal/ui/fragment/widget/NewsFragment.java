package com.hcb.saha.internal.ui.fragment.widget;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.external.news.NewsEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

/**
 * Displays the news.
 * 
 * @author Steven Hadley
 * 
 */
public class NewsFragment extends BaseWidgetFragment {

	@Inject
	private Bus eventBus;
	@InjectView(R.id.row1)
	private TextView newsTitle;
	@InjectView(R.id.row2)
	private ImageView newsImage;
	@InjectView(R.id.row3)
	@Nullable
	private TextView newsPubDate;
	@InjectView(R.id.row4)
	@Nullable
	private TextView newsSource;
	private Handler newsHandler = new Handler();
	private NewsRunner newsRunner = new NewsRunner();
	private Picasso picasso;

	public NewsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		picasso = Picasso.with(this.getActivity());
		// FIXME: make this a debug setting.
		picasso.setDebugging(true);
		return getView(getArguments().getString(STATE_TYPE), container,
				inflater);
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_news_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_news_compressed;
	}

	/**
	 * Constructs the fragment with the required parameters.
	 * 
	 * @param state
	 * @return fragment
	 */
	public static Fragment create(StateType state) {
		Fragment fragment = new NewsFragment();
		BaseWidgetFragment.addBundle(state, fragment);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		eventBus.post(new NewsEvents.HeadlineNewsRequest());
		newsHandler.postDelayed(newsRunner, NewsRunner.DELAY_MILLIS);
	}

	@Override
	public void onPause() {
		super.onPause();
		newsHandler.removeCallbacks(newsRunner);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

	@Subscribe
	public void onNewsResult(NewsEvents.HeadlineNewsResult result) {
		newsTitle.setText(result.getHeadline().getTitle());
		newsImage.setImageDrawable(null);
		picasso.load(result.getHeadline().getImage()).into(newsImage);

		if (null != newsPubDate) {
			newsPubDate.setText(result.getHeadline().getPubDate());
		}
		if (null != newsSource) {
			newsSource.setText(result.getSource());
		}
	}

	/**
	 * Gets news stories every x minutes.
	 * 
	 * @author Steven Hadley
	 * 
	 */
	private class NewsRunner implements Runnable {

		// FIXME: debug setting
		private static final int DELAY_MILLIS = 1000 * 60; // 1 min

		@Override
		public void run() {
			eventBus.post(new NewsEvents.HeadlineNewsRequest());
			newsHandler.postDelayed(newsRunner, DELAY_MILLIS);
		}

	}
}
