package com.hcb.saha.internal.data.model;

/**
 * Represents all users and paths to their face images
 * 
 * @author Andreas Borglin
 */
public class UsersFaces {

	private int[] userIds;
	private String[][] userImageFaces;

	public String[][] getUserImageFaces() {
		return userImageFaces;
	}

	public void setUserImageFaces(String[][] userImageFaces) {
		this.userImageFaces = userImageFaces;
	}

	public int[] getUserIds() {
		return userIds;
	}

	public void setUserIds(int[] userIds) {
		this.userIds = userIds;
	}

}
