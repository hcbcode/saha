package com.hcb.saha.event;

import com.hcb.saha.data.model.User;

/**
 * User registration related events
 * @author Andreas Borglin
 */
public class RegistrationEvents {
	
	public static final class UserCreated {
		private User user;
		
		public UserCreated(User user) {
			this.user = user;
		}
		
		public User getUser() {
			return user;
		}
	}
	
	public static final class FaceRegistrationCompleted {
		
	}

}
