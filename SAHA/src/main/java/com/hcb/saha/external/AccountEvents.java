package com.hcb.saha.external;

/**
 * 
 * @author steven hadley
 * 
 */
public class AccountEvents {

	public static class QueryAccountsRequest {

		public QueryAccountsRequest() {
		}

	}

	/**
	 * 
	 * @author steven hadley
	 * 
	 */
	public static class QueryAccountsResult {
		private String[] names;

		public QueryAccountsResult(String[] names) {
			this.names = names;
		}

		public String[] getNames() {
			return names;
		}

	}

}
