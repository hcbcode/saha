package com.hcb.saha.internal.source.trigger;

import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.hcb.saha.internal.core.SahaSystemState;
import com.hcb.saha.internal.data.model.User;
import com.squareup.otto.Bus;

/**
 * Voice command provider using built in Android speech services
 * @author Andreas Borglin
 */
@Singleton
public class VoiceCommandProvider implements OnInitListener, RecognitionListener {

	private static final String TAG = VoiceCommandProvider.class.getSimpleName();

	@Inject
	private SahaSystemState systemState;
	private SpeechRecognizer speechRec;
	private AudioManager audioManager;
	private Application context;
	@Inject
	private Bus eventBus;

	private TextToSpeech tts;
	private Intent speechIntent;
	private HashMap<String, String> params;

	private class TtsListener extends UtteranceProgressListener {

		@Override
		public void onDone(String utteranceId) {
			Handler handler = new Handler(context.getMainLooper());
			handler.post(new Runnable() {

				@Override
				public void run() {
					startSpeechRecognition();
				}
			});
		}

		@Override
		public void onError(String utteranceId) {
			Log.e(TAG, "TTS onError");
		}

		@Override
		public void onStart(String utteranceId) {
		}
	}

	@Inject
	public VoiceCommandProvider(final Application context,
			SpeechRecognizer speechRec, final AudioManager audioManager) {

		this.speechRec = speechRec;
		this.audioManager = audioManager;
		this.context = context;

		speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				context.getPackageName());

		tts = new TextToSpeech(context, this);
		params = new HashMap<String, String>();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");

		speechRec.setRecognitionListener(this);
		startSpeechRecognition();
	}

	private void startSpeechRecognition() {
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		speechRec.startListening(speechIntent);
	}

	private void parseSpeechInput(List<String> values) {

		String textToSpeak = null;
		for (String val : values) {
			if (val.equalsIgnoreCase("nexus")) {
				textToSpeak = "Saha at your command";
				break;
			} else if (val.equalsIgnoreCase("who am i")) {
				User user = systemState.getCurrentUser();
				if (user != null) {
					textToSpeak = "You are " + user.getName();
				} else {
					textToSpeak = "You are anonymous";
				}
				break;
			} else if (val.equalsIgnoreCase("what are you")
					|| val.equalsIgnoreCase("who are you")) {
				textToSpeak = "I am the smart android household array at your service";
				break;
			} else if (val.equalsIgnoreCase("describe steven")) {
				textToSpeak = "Grumpy posh bastard";
				break;
			}
		}

		if (textToSpeak == null) {
			// TODO
		}

		if (textToSpeak != null) {
			tts.speak(textToSpeak, TextToSpeech.QUEUE_ADD, params);
		} else {
			startSpeechRecognition();
		}

	}

	@Override
	public void onResults(Bundle results) {
		Log.d(TAG, "onResults");
		List<String> values = results
				.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
		parseSpeechInput(values);

	}

	@Override
	public void onReadyForSpeech(Bundle params) {
		Log.d(TAG, "onReadyForSpeech");
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
	}

	@Override
	public void onPartialResults(Bundle partialResults) {
		// No op
	}

	@Override
	public void onEvent(int eventType, Bundle params) {
		Log.d(TAG, "onEvent: " + eventType);
	}

	@Override
	public void onError(int error) {
		Log.d(TAG, "onError: " + error);
		startSpeechRecognition();
	}

	@Override
	public void onEndOfSpeech() {
		Log.d(TAG, "onEndOfSpeech");
	}

	@Override
	public void onBufferReceived(byte[] buffer) {
		// No op
	}

	@Override
	public void onBeginningOfSpeech() {
		Log.d(TAG, "onBeginningOfSpeech");

	}

	@Override
	public void onInit(int status) {
		tts.setOnUtteranceProgressListener(new TtsListener());
	}

	@Override
	public void onRmsChanged(float rmsdB) {
		// No op
	}

}
