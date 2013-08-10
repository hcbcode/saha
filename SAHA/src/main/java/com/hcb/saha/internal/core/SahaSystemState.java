package com.hcb.saha.internal.core;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.R;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.event.LifecycleEvents;
import com.hcb.saha.internal.event.TextSpeechEvents;
import com.hcb.saha.internal.event.UserIdentificationEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * Early implementation of a system state TODO a lot
 * 
 * @author Andreas Borglin
 */
@Singleton
public final class SahaSystemState {

	public static enum State {
		SLEEPING, // Device is in sleep mode (sensors, camera, etc off)
		DETECTION, // Device is active and awaiting user detection
		REGISTRATION, // Device is active and in process of registering a user
		ANONYMOUS_USER, // A user is detected, but is not recognized
		REGISTERED_USER // A registered user has been detected
	}

	@Inject
	private Application context;
	private State currentState;
	private User currentUser;
	private Bus eventBus;

	@Inject
	public SahaSystemState(Bus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
		currentState = State.DETECTION;
	}
	
	private void updateState(State state) {
		currentState = state;
		eventBus.post(new LifecycleEvents.SystemStateChangedEvent(currentState));
	}

	@Subscribe
	public void onRegisteredUserDetected(
			UserIdentificationEvents.RegisteredUserDetected event) {
		currentUser = event.getUser();
		updateState(State.REGISTERED_USER);
		eventBus.post(new TextSpeechEvents.TextToSpeechRequest(context
				.getString(R.string.user_recognized_speech,
						currentUser.getName())));
	}

	@Subscribe
	public void onAnonymousUserDetected(
			UserIdentificationEvents.AnonymousUserDetected event) {
		updateState(State.ANONYMOUS_USER);
		currentUser = null;
	}

	@Subscribe
	public void onUserInactivity(
			UserIdentificationEvents.UserInactivitityEvent event) {
		updateState(State.DETECTION);
		currentUser = null;
	}
	
	@Subscribe
	public void onRegistrationInitiated(LifecycleEvents.RegistrationInitiatedEvent event) {
		updateState(State.REGISTRATION);
		currentUser = null;
	}
	
	@Subscribe
	public void onRegistrationCompleted(LifecycleEvents.RegistrationCompletedEvent event) {
		currentUser = event.getUser();
		updateState(State.REGISTERED_USER);
	}

	public State getCurrentState() {
		return currentState;
	}

	public User getCurrentUser() {
		return currentUser;
	}
}
