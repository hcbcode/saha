package com.hcb.saha.internal.source.identity;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Gets injected into each provider and they register themselves with this. The
 * base class will keep a list of Providers that they collect data from in a
 * generic manner.
 * 
 * @author Andreas Borglin
 * 
 */
@Singleton
public class UserIdentificationManager {

	@Inject
	private FaceIdentificationProvider faceIdentificationProvider;

	public UserIdentificationManager() {
	}

	/*
	 * 1. App requests that we want a user identified - or are we constantly
	 * trying to detect faces? What is the point of this class? Isn't the
	 * approach, 1. Listen to movement / sound 2. When movement / sound is
	 * detected - go into user detection mode (turn on face detection) 3. If
	 * face / voice is detected and recognized, propagate to rest of app
	 */
}
