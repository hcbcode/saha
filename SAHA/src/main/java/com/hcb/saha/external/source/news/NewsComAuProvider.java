/**
 * 
 */
package com.hcb.saha.external.source.news;

import java.util.List;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.util.Log;

import com.google.inject.Inject;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * @author Steven Hadley
 * 
 */
public class NewsComAuProvider implements Callback<List<RssItem>> {

	private static final String NEWS_COM_AU_WEIRD_TRUE_FREAKY = "News.com.au, Weird True Freaky";
	private static final String NEWS_COM_AU_WORLD = "News.com.au, World";
	private static final String HTTP_FEEDS_FEEDBURNER_COM = "http://feeds.feedburner.com";
	private Bus eventBus;
	private NewsComAuInterface service;

	@Inject
	public NewsComAuProvider(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setServer(HTTP_FEEDS_FEEDBURNER_COM)
				.setConverter(new RssNewsConverter()).build();
		service = restAdapter.create(NewsComAuInterface.class);
	}

	@Subscribe
	public void getNews(NewsEvents.HeadlineNewsRequest request) {
		service.worldNews(this);
	}

	@Override
	public void success(List<RssItem> t, Response response) {
		eventBus.post(new NewsEvents.HeadlineNewsResult(t.get(0),
				NEWS_COM_AU_WORLD));

	}

	@Override
	public void failure(RetrofitError error) {
		Log.e(NewsComAuProvider.class.getSimpleName(), "Failed to get news: "
				+ NEWS_COM_AU_WEIRD_TRUE_FREAKY, error);
	}

}
