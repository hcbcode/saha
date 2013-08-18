package com.hcb.saha.internal.event;

import com.hcb.saha.internal.core.SahaRuntimeConfig;
import com.hcb.saha.internal.core.SahaSystemState.State;
import com.hcb.saha.internal.data.model.User;

/**
 * System related events
 * 
 * @author Andreas Borglin
 */
public class SystemEvents {

	public static class MainActivityCreated {

	}

	public static class MainActivityDestroyed {

	}

	public static class RegistrationInitiatedEvent {

	}

	public static class RegistrationCompletedEvent {
		private User user;

		public RegistrationCompletedEvent(User user) {
			this.user = user;
		}

		public User getUser() {
			return user;
		}
	}

	public static class SystemStateChangedEvent {
		private State state;

		public SystemStateChangedEvent(State state) {
			this.state = state;
		}

		public State getState() {
			return state;
		}
	}

	public static class SystemSettingChangedEvent {
		private SahaRuntimeConfig runtimeConfig;
		private String settingKey;

		public SystemSettingChangedEvent(SahaRuntimeConfig rc, String sk) {
			this.runtimeConfig = rc;
			this.settingKey = sk;
		}

		public SahaRuntimeConfig getRuntimeConfig() {
			return runtimeConfig;
		}

		public String getSettingKey() {
			return settingKey;
		}
	}

}
