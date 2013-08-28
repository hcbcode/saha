package com.hcb.saha.internal.ui.fragment.widget;

import javax.annotation.Nullable;

import roboguice.inject.InjectView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
public class NewsFragment extends WidgetFragment {

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

	public NewsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		eventBus.register(this);
		return getView(getArguments().getString(STATE_TYPE), container,
				inflater);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		eventBus.post(new NewsEvents.HeadlineNewsRequest());
	}

	@Override
	public int getFullLayout() {
		return R.layout.fragment_widget_news_full;
	}

	@Override
	public int getCompressedLayout() {
		return R.layout.fragment_widget_news_compressed;
	}

	public static Fragment create(StateType state) {
		Fragment fragment = new NewsFragment();
		WidgetFragment.addBundle(state, fragment);
		return fragment;
	}

	@Override
	public void onResume() {
		super.onResume();
		eventBus.post(new NewsEvents.HeadlineNewsRequest());
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		eventBus.unregister(this);
	}

	@Subscribe
	public void onNewsResult(NewsEvents.HeadlineNewsResult result) {
		newsTitle.setText(result.getHeadline().getTitle());
		Picasso.with(this.getActivity()).load(result.getHeadline().getImage())
				.into(newsImage);
		if (null != newsPubDate) {
			newsPubDate.setText(result.getHeadline().getPubDate());
		}
		if (null != newsSource) {
			newsSource.setText(result.getSource());
		}
	}
}
