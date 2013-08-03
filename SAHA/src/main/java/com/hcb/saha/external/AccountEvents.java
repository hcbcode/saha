package com.hcb.saha.external;

import android.content.Context;

/**
 * 
 * @author steven hadley
 * 
 */
public class AccountEvents {

	public static class QueryAccountsRequest {
		private Context ctx;

		public QueryAccountsRequest(Context ctx) {
			this.ctx = ctx;
		}

		public Context getContext() {
			return ctx;
		}

	}

	/**
	 * 
	 * @author steven hadley
	 * 
	 */
	public static class QueryAccountsResult {
		private String[] names;
		private Context ctx;

		public QueryAccountsResult(String[] names, Context ctx) {
			this.names = names;
			this.ctx = ctx;

		}

		public String[] getNames() {
			return names;
		}

		public Context getContext() {
			return ctx;
		}
	}

}
