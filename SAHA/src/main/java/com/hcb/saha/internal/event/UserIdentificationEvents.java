package com.hcb.saha.internal.event;

import com.hcb.saha.internal.data.model.User;

/**
 * User identification events
 * @author Andreas Borglin
 */
public class UserIdentificationEvents {

	public static class AnonymousUserDetected {

	}

	public static class RegisteredUserDetected {
		private User user;

		public RegisteredUserDetected(User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static class UserInactivitityEvent {

	}

}
