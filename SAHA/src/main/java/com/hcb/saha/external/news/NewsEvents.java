package com.hcb.saha.external.news;

/**
 * Event bus events.
 * 
 * @author steven hadley
 * 
 */
public class NewsEvents {

	public static class HeadlineNewsRequest {

	}

	public static class HeadlineNewsResult {

		private NewsItem item;
		private String source;

		public HeadlineNewsResult(NewsItem item, String source) {
			this.item = item;
			this.source = source;
		}

		public NewsItem getHeadline() {
			return item;
		}

		public String getSource() {
			return source;
		}
	}

}
