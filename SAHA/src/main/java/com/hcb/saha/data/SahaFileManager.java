package com.hcb.saha.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.os.Environment;
import android.util.Log;

import com.hcb.saha.SahaConfig.FileSystem;
import com.hcb.saha.data.model.User;

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
				+ String.valueOf(curNumFiles) + ".jpg");
		newFace.createNewFile();
		return new FileOutputStream(newFace);
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

	public static String getFaceRecModelPath() {
		return String.format("%s/%s/%s", getSahaRoot().getAbsolutePath(),
				FileSystem.SAHA_USERS, FileSystem.FACE_REC_MODEL);
	}

}
