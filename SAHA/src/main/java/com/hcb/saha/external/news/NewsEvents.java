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

		private RssItem item;
		private String source;

		public HeadlineNewsResult(RssItem item, String source) {
			this.item = item;
			this.source = source;
		}

		public RssItem getHeadline() {
			return item;
		}

		public String getSource() {
			return source;
		}
	}

}
