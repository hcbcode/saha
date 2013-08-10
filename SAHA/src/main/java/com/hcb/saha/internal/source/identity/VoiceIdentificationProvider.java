package com.hcb.saha.internal.source.identity;

import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Context;
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

// TODO Work in progress
public class VoiceIdentificationProvider {

	private Intent speechIntent;
	private SpeechRecognizer speechRec;
	private TextToSpeech tts;
	private AudioManager audioManager;

	@Inject
	public VoiceIdentificationProvider(final Application context) {

		audioManager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		speechIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				context.getPackageName());

		tts = new TextToSpeech(context, new OnInitListener() {

			@Override
			public void onInit(int status) {
				tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

					@Override
					public void onStart(String utteranceId) {
						Log.d("BLEH", "on tts start");
					}

					@Override
					public void onError(String utteranceId) {
						Log.d("BLEH", "on tts error");
					}

					@Override
					public void onDone(String utteranceId) {
						Log.d("BLEH", "on tts done");
						Handler handler = new Handler(context.getMainLooper());
						handler.post(new Runnable() {

							@Override
							public void run() {
								startSpeechRecognition();
							}
						});
					}
				});
			}
		});

		speechRec = SpeechRecognizer.createSpeechRecognizer(context);
		speechRec.setRecognitionListener(new RecognitionListener() {

			@Override
			public void onRmsChanged(float rmsdB) {
			}

			@Override
			public void onResults(Bundle results) {
				Log.d("REC", "onResults");
				List<String> values = results
						.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				parseSpeechInput(values);
				
			}

			@Override
			public void onReadyForSpeech(Bundle params) {
				Log.d("REC", "onReadyForSpeech");
				audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
			}

			@Override
			public void onPartialResults(Bundle partialResults) {

			}

			@Override
			public void onEvent(int eventType, Bundle params) {

			}

			@Override
			public void onError(int error) {
				Log.d("REC", "onError: " + error);
				startSpeechRecognition();
			}

			@Override
			public void onEndOfSpeech() {
				Log.d("REC", "onEndOfSpeech");
			}

			@Override
			public void onBufferReceived(byte[] buffer) {

			}

			@Override
			public void onBeginningOfSpeech() {
				Log.d("REC", "onBeginningOfSpeech");

			}
		});
		startSpeechRecognition();
		Log.d("REC", "start listening");
	}

	private void startSpeechRecognition() {
		audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
		speechRec.startListening(speechIntent);
	}

	private void parseSpeechInput(List<String> values) {
		
		String playString = null;
		for (String val : values) {
			Log.d("REC", val);
			if (val.equalsIgnoreCase("nexus")) {
				playString = "Saha at your command";
				break;
			} else if (val.equalsIgnoreCase("who am i")) {
				playString = "You are Andreas";
				break;
			} else if (val.equalsIgnoreCase("what are you")
					|| val.equalsIgnoreCase("who are you")) {
				playString = "I am the smart android household array at your service";
				break;
			} else if (val.equalsIgnoreCase("describe steven")) {
				playString = "Grumpy posh bastard";
				break;
			}

		}
		if (playString == null) {
			// playString = "Not sure what you want";
		}

		final String value1 = playString;

		if (value1 != null) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");
			tts.speak(value1, TextToSpeech.QUEUE_ADD, map);
		} else {
			startSpeechRecognition();
		}

	}

}
