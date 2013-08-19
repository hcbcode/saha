package com.hcb.saha.external.source.news;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;

interface NewsComAuInterface {
	@GET("/newscomauworldnewsndm") void worldNews(Callback<List<RssItem>> cb);

	@GET("/newscomauwtfndm")
	void weirdTrueFreakyNews(Callback<List<RssItem>> cb);

}
