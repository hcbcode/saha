package com.hcb.saha.event;

import android.content.Context;

/**
 * 
 * @author steven hadley
 * 
 */
public class EmailEvents {

	public static class QueryEmailRequest {

		private String name;
		private Context ctx;

		public QueryEmailRequest(String name, Context ctx) {
			this.name = name;
			this.ctx = ctx;
		}

		public String getName() {
			return name;
		}

		public Context getContext() {
			return ctx;
		}
	}

	public static class QueryEmailResult {

		private Integer unreadCount;

		public QueryEmailResult(Integer unreadCount) {
			this.unreadCount = unreadCount;
		}

		public Integer getUnreadCount() {
			return unreadCount;
		}
	}

}
