package com.hcb.saha.external.news;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

interface NewsComAuClientInterface {

	// FIXME: Extract URLs

	@GET("/newscomauworldnewsndm")
	void getWorldNews(Callback<List<NewsItem>> cb);

	@GET("/newscomauwtfndm")
	void getWeirdTrueFreakyNews(Callback<List<NewsItem>> cb);

}
