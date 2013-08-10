package com.hcb.saha.internal.event;

import android.hardware.Camera.Face;

/**
 * Camera events
 *
 * @author Andreas Borglin
 */
public class CameraEvents {
	
//	public static final class CameraClientInterestEvent {
//
//		public static enum Interest {
//			REGISTER, UNREGISTER
//		}
//		
//		private Interest interest;
//		private InterestType interestType;
//		
//		public CameraClientInterestEvent(InterestType interestType, Interest interest) {
//			this.interestType = interestType;
//			this.interest = interest;
//		}
//		
//		public Interest getInterest() {
//			return interest;
//		}
//		
//		public InterestType getInterestType() {
//			return interestType;
//		}
//		
//	}

	public static final class CameraActivatedEvent {

	}

	public static final class CameraInactivatedEvent {

	}

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
	
	public static final class FaceDisappearedEvent {
		
	}
}
