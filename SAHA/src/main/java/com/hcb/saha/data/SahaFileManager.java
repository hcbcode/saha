package com.hcb.saha.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.os.Environment;
import android.util.Log;

import com.hcb.saha.SahaConfig.FileSystem;
import com.hcb.saha.data.model.User;
import com.hcb.saha.data.model.UsersFaces;

/**
 * Manages the Saha file system directories and files
 * 
 * @author Andreas Borglin
 */
public final class SahaFileManager {

	private static final String TAG = SahaFileManager.class.getSimpleName();

	private SahaFileManager() {
	}

	public static File getSahaRoot() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdcard = Environment.getExternalStorageDirectory();
			File sahaRoot = new File(sdcard, FileSystem.SDCARD_ROOT);
			if (!sahaRoot.exists()) {
				sahaRoot.mkdir();
			}
			return sahaRoot;
		}
		return null;
	}
	
	public static File getTmpDir() {
		File sahaRoot = getSahaRoot();
		File tmpFile = new File(sahaRoot, FileSystem.SAHA_TMP);
		if (!tmpFile.exists()) {
			tmpFile.mkdir();
		}
		return tmpFile;
	}

	public static File getUserDir(User user) {
		File sahaRoot = getSahaRoot();
		File userFile = new File(sahaRoot, FileSystem.SAHA_USERS + "/"
				+ user.getDirectory());
		if (!userFile.exists()) {
			userFile.mkdirs();
		}
		return userFile;
	}

	public static File getUserFaceDir(User user) {
		File userDir = getUserDir(user);
		File faceDir = new File(userDir, FileSystem.SAHA_USER_FACES);
		if (!faceDir.exists()) {
			faceDir.mkdir();
		}
		return faceDir;
	}

	public static FileOutputStream getStreamForUserFaceImage(User user)
			throws IOException {
		File userFaceDir = getUserFaceDir(user);
		int curNumFiles = userFaceDir.list().length;
		File newFace = new File(userFaceDir, FileSystem.FACE_PREFIX
				+ String.valueOf(curNumFiles) + FileSystem.FACE_IMAGE_EXT);
		newFace.createNewFile();
		return new FileOutputStream(newFace);
	}
	
	public static File getFileForFaceIdentification() {
		File tmpDir = getTmpDir();
		File faceFile = new File(tmpDir, FileSystem.FACE_ID_IMAGE);
		return faceFile;
	}

	public static void createFaceRecModelFile() {
		File sahaRoot = getSahaRoot();
		File userFile = new File(sahaRoot, FileSystem.SAHA_USERS + "/"
				+ FileSystem.FACE_REC_MODEL);
		if (!userFile.exists()) {
			try {
				userFile.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage());
			}
		}
	}
	
	public static UsersFaces getAllUsersFaceImages(List<User> users) {
		UsersFaces usersFaces = new UsersFaces();
		int[] userIds = new int[users.size()];
		String[][] usersFaceImages = new String[users.size()][];
		int userIndex = 0;
		for (User user : users) {
			File userFaceDir = getUserFaceDir(user);
			String[] images = userFaceDir.list();
			userIds[userIndex] = user.getId();
			usersFaceImages[userIndex] = new String[images.length];
			for (int i = 0; i < images.length; i++) {
				Log.d(TAG, images[i]);
				usersFaceImages[userIndex][i] = userFaceDir.getAbsolutePath() + "/" + images[i];
			}
			userIndex++;
		}
		
		usersFaces.setUserIds(userIds);
		usersFaces.setUserImageFaces(usersFaceImages);
		
		return usersFaces;
	}

	public static String getFaceRecModelPath() {
		return String.format("%s/%s/%s", getSahaRoot().getAbsolutePath(),
				FileSystem.SAHA_USERS, FileSystem.FACE_REC_MODEL);
	}

}
