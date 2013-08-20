/**
 * 
 */
package com.hcb.saha.external.source.news;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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

	private ReentrantLock lock = new ReentrantLock();
	private RssItem cachedRssItem;
	private int cacheTime = 1000 * 60 * 60; // 1 hour

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
		// FIXME: Caching
		lock.lock();
		if (null == cachedRssItem
				|| (null != cachedRssItem && (new Date().getTime() > (cachedRssItem
						.getReadDate().getTime() + cacheTime)))) {
			// too old
			service.worldNews(this);
		} else {
			eventBus.post(new NewsEvents.HeadlineNewsResult(cachedRssItem,
					NEWS_COM_AU_WORLD));

		}
		lock.unlock();

	}

	@Override
	public void success(List<RssItem> t, Response response) {
		lock.lock();
		RssItem item = (t.get(0));
		item.setReadDate(new Date());
		cachedRssItem = item;
		eventBus.post(new NewsEvents.HeadlineNewsResult(item, NEWS_COM_AU_WORLD));
		lock.unlock();
	}

	@Override
	public void failure(RetrofitError error) {
		Log.e(NewsComAuProvider.class.getSimpleName(), "Failed to get news: "
				+ NEWS_COM_AU_WEIRD_TRUE_FREAKY, error);
	}

}
