package com.hcb.saha.internal.source.identity;

import java.util.HashMap;
import java.util.List;

import android.app.Application;
import android.content.Intent;
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

public class VoiceIdentificationProvider {

	private TextToSpeech tts;

	@Inject
	public VoiceIdentificationProvider(final Application context) {

		final SpeechRecognizer rec = SpeechRecognizer
				.createSpeechRecognizer(context);
		rec.setRecognitionListener(new RecognitionListener() {

			@Override
			public void onRmsChanged(float rmsdB) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onResults(Bundle results) {
				// TODO Auto-generated method stub
				Log.d("REC", "onResults");
				List<String> values = results
						.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
				String playString = null;
				for (String val : values) {
					Log.d("REC", val);
					if (val.equalsIgnoreCase("saha")) {
						playString = "Saha at your command";
						break;
					} else if (val.equalsIgnoreCase("who am i")) {
						playString = "You are Andreas";
						break;
					} else if (val.equalsIgnoreCase("what are you") || val.equalsIgnoreCase("who are you")) {
						playString = "I am the smart android household array at your service";
						break;
					}
					else if (val.equalsIgnoreCase("describe steven")) {
						playString = "Grumpy posh bastard";
						break;
					}

				}
				if (playString == null) {
					playString = "Not sure what you want";
				}

				final String value1 = playString;

				tts = new TextToSpeech(context, new OnInitListener() {

					@Override
					public void onInit(int status) {
						tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

							@Override
							public void onStart(String utteranceId) {
								// TODO Auto-generated method stub
								Log.d("BLEH", "on tts start");
							}

							@Override
							public void onError(String utteranceId) {
								// TODO Auto-generated method stub
								Log.d("BLEH", "on tts error");
							}

							@Override
							public void onDone(String utteranceId) {
								// TODO Auto-generated method stub
								Log.d("BLEH", "on tts done");
								Handler handler = new Handler(context.getMainLooper());
								handler.post(new Runnable() {

									@Override
									public void run() {
										// TODO Auto-generated method stub
										Intent intent = new Intent(
												RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
										intent.putExtra(
												RecognizerIntent.EXTRA_LANGUAGE_MODEL,
												RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
										intent.putExtra(
												RecognizerIntent.EXTRA_CALLING_PACKAGE,
												context.getPackageName());
										rec.startListening(intent);
									}
								});


							}
						});
						HashMap<String, String> map = new HashMap<String, String>();
						map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");
						tts.speak(value1, TextToSpeech.QUEUE_ADD, map);

					}
				});

			}

			@Override
			public void onReadyForSpeech(Bundle params) {
				// TODO Auto-generated method stub
				Log.d("REC", "onReadyForSpeech");
			}

			@Override
			public void onPartialResults(Bundle partialResults) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onEvent(int eventType, Bundle params) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onError(int error) {
				// TODO Auto-generated method stub
				Log.d("REC", "onError: " + error);
				Intent intent = new Intent(
						RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
				intent.putExtra(
						RecognizerIntent.EXTRA_LANGUAGE_MODEL,
						RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
				intent.putExtra(
						RecognizerIntent.EXTRA_CALLING_PACKAGE,
						context.getPackageName());
				rec.startListening(intent);
			}

			@Override
			public void onEndOfSpeech() {
				// TODO Auto-generated method stub
				Log.d("REC", "onEndOfSpeech");
			}

			@Override
			public void onBufferReceived(byte[] buffer) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onBeginningOfSpeech() {
				// TODO Auto-generated method stub
				Log.d("REC", "onBeginningOfSpeech");

			}
		});
		Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
				RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,
				context.getPackageName());
		rec.startListening(intent);
		Log.d("REC", "start listening");
	}

}
