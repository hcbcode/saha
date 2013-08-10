package com.hcb.saha.internal.core;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.event.UserIdentificationEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Early implementation of a system state
 * TODO a lot
 * @author Andreas Borglin
 */
@Singleton
public final class SahaSystemState {

	public static enum State {
		SLEEPING, // Device is in sleep mode (sensors, camera, etc off)
		IDLE, // Device is active and awaiting user interaction
		ANONYMOUS_USER, // A user is detected, but is not recognized
		REGISTERED_USER // A registered user has been detected
	}

	@Inject
	private Application context;
	private State currentState;
	private User currentUser;
	private TextToSpeech tts;

	@Inject
	public SahaSystemState(Bus eventBus) {
		eventBus.register(this);
		currentState = State.IDLE;
	}

	@Subscribe
	public void onRegisteredUserDetected(UserIdentificationEvents.RegisteredUserDetected event) {
		currentState = State.REGISTERED_USER;
		currentUser = event.getUser();

		tts = new TextToSpeech(context, new OnInitListener() {

			@Override
			public void onInit(int status) {
				String user = "User " + currentUser.getName() + " detected";
				tts.speak(user, TextToSpeech.QUEUE_ADD, null);
			}
		});
	}

	@Subscribe
	public void onAnonymousUserDetected(UserIdentificationEvents.AnonymousUserDetected event) {
		currentState = State.ANONYMOUS_USER;
		currentUser = null;
	}

	@Subscribe
	public void onUserInactivity(UserIdentificationEvents.UserInactivitityEvent event) {
		currentState = State.IDLE;
		currentUser = null;
	}

	public void setState(State state) {
		currentState = state;
	}

	public State getCurrentState() {
		return currentState;
	}

	public User getCurrentUser() {
		return currentUser;
	}


}
