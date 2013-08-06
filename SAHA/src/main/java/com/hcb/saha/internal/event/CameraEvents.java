package com.hcb.saha.internal.event;

import android.hardware.Camera.Face;

/**
 * Camera events
 * @author Andreas Borglin
 */
public class CameraEvents {
	
	public static final class MovementDetectedEvent {
		
	}
	
	public static final class FaceDetectedEvent {
		private Face[] faces;
		
		public FaceDetectedEvent(Face[] faces) {
			this.faces = faces;
		}
		
		public Face[] getFaces() {
			return faces;
		}
	}

}
