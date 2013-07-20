package com.hcb.saha.event;

import com.hcb.saha.data.model.UsersFaces;

/**
 * Face recognition related events
 * @author Andreas Borglin
 */
public class FaceRecognitionEvents {

	public static class TrainRecognizerRequest {
		
		private UsersFaces usersFaces;
		
		public TrainRecognizerRequest(UsersFaces usersFaces) {
			this.usersFaces = usersFaces;
		}
		
		public UsersFaces getUsersFaces() {
			return usersFaces;
		}
		
	}
	
	public static class PredictUserRequest {
		
		private String imagePath;
		
		public PredictUserRequest(String imagePath) {
			this.imagePath = imagePath;
		}
		
		public String getImagePath() {
			return imagePath;
		}
	}
	
	public static class UserPredictionResult {
		private int userId;
		
		public UserPredictionResult(int userId) {
			this.userId = userId;
		}
		
		public int getUserId() {
			return userId;
		}
	}
	
	
}
