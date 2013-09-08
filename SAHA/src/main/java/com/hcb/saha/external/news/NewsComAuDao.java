/**
 *
 */
package com.hcb.saha.external.news;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * @author Steven Hadley
 * 
 */
@Singleton
public class NewsComAuDao implements Callback<List<NewsItem>> {

	private static final String TAG = NewsComAuDao.class.getSimpleName();

	// FIXME: Extract URLs
	private static final String NEWS_COM_AU_WEIRD_TRUE_FREAKY = "News.com.au, Weird True Freaky";
	private static final String NEWS_COM_AU_WORLD = "News.com.au, World";
	private static final String HTTP_FEEDS_FEEDBURNER_COM = "http://feeds.feedburner.com";
	private Bus eventBus;
	private NewsComAuClientInterface restClient;

	private ReentrantLock lock = new ReentrantLock();
	private List<NewsItem> cachedRssItems;
	private Date cacheTime;
	private int cacheRetentionTime = 1000 * 60 * 60; // 1 hour

	@Inject
	public NewsComAuDao(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setServer(HTTP_FEEDS_FEEDBURNER_COM)
				.setConverter(new RssNewsConverter()).build();
		restClient = restAdapter.create(NewsComAuClientInterface.class);
	}

	@Subscribe
	public void getNews(NewsEvents.HeadlineNewsRequest request) {
		lock.lock();
		if (null == cachedRssItems
				|| (null != cachedRssItems && (new Date().getTime() > (cacheTime
						.getTime() + cacheRetentionTime)))) {
			Log.d(TAG, "Requesting news");
			restClient.getWorldNews(this);
		} else {
			Log.d(TAG, "Using cached news");
			eventBus.post(new NewsEvents.HeadlineNewsResult(cachedRssItems
					.get(newsItemSelector(cachedRssItems.size())),
					NEWS_COM_AU_WORLD));

		}
		lock.unlock();

	}

	@Override
	public void success(List<NewsItem> items, Response response) {
		Log.d(TAG, "Received news");
		lock.lock();
		cacheTime = new Date();
		cachedRssItems = items;
		eventBus.post(new NewsEvents.HeadlineNewsResult(items
				.get(newsItemSelector(items.size())), NEWS_COM_AU_WORLD));
		lock.unlock();
	}

	@Override
	public void failure(RetrofitError error) {
		Log.e(TAG, "Failed to get news: " + NEWS_COM_AU_WORLD, error);
	}

	/**
	 * Random news item selector between one and max.
	 * 
	 * @param count
	 *            max value
	 * @return
	 */
	private int newsItemSelector(int count) {
		int number = (int) (Math.floor(Math.random() * count));
		Log.d(TAG, "News item selected: " + number);
		return number;
	}

}
