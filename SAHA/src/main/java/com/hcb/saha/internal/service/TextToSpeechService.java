package com.hcb.saha.internal.service;

import android.app.Application;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.event.TextSpeechEvents;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

/**
 * App wide text to speech service
 * @author Andreas Borglin
 */
@Singleton
public class TextToSpeechService implements OnInitListener {

	private TextToSpeech tts;
	
	@Inject
	public TextToSpeechService(Bus eventBus, Application context) {
		eventBus.register(this);
		tts = new TextToSpeech(context, this);
	}

	@Override
	public void onInit(int status) {
		// No-op
	}
	
	@Subscribe
	public void onTextToSpeechRequest(TextSpeechEvents.TextToSpeechRequest req) {
		String text = req.getText();
		if (text != null && text.length() > 0) {
			tts.speak(text, TextToSpeech.QUEUE_ADD, null);
		}
	}
	
}
