package com.hcb.saha.internal.event;

/**
 * Text and speech events
 * 
 * @author Andreas Borglin
 */
public class TextSpeechEvents {

	public static final class TextToSpeechRequest {
		private String text;

		public TextToSpeechRequest(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}

}
