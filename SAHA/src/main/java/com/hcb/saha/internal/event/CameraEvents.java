package com.hcb.saha.internal.event;

import android.hardware.Camera.Face;

/**
 * Camera events
 *
 * @author Andreas Borglin
 */
public class CameraEvents {
	
	public static final class CameraActivatedEvent {

	}

	public static final class CameraInactivatedEvent {

	}

	public static final class MovementDetectedEvent {

	}

	/**
	 * Event sent when a face is detected when in idle/detection mode
	 */
	public static final class FaceDetectedEvent {
		private Face[] faces;

		public FaceDetectedEvent(Face[] faces) {
			this.faces = faces;
		}

		public Face[] getFaces() {
			return faces;
		}
	}

	/**
	 * Event sent when a face is available again after being detected (in user mode)
	 */
	public static final class FaceAvailableEvent {
		private int faceWidth;
		private int faceHeight;

		public FaceAvailableEvent(int fw, int fh) {
			faceWidth = fw;
			faceHeight = fh;
		}

		public int getFaceWidth() {
			return faceWidth;
		}

		public int getFaceHeight() {
			return faceHeight;
		}
	}
	
	/**
	 * Event sent when the face disappears from camera while in user mode
	 */
	public static final class FaceDisappearedEvent {
		
	}
}
