package com.hcb.saha.event;

/**
 * 
 * @author steven hadley
 *
 */
public class EmailEvents {

	public static class OnEmailQueried {

		private Integer unreadCount;

		public OnEmailQueried(Integer unreadCount) {
			this.unreadCount = unreadCount;
		}

		public Integer getUnreadCount() {
			return unreadCount;
		}
	}

}
