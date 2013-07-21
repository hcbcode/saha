package com.hcb.saha.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import com.hcb.saha.SahaConfig.Assets;
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

	/**
	 * Get the SAHA root folder on the sdcard
	 */
	public static File getSahaRoot() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File sdcard = Environment.getExternalStorageDirectory();
			File sahaRoot = new File(sdcard, FileSystem.SAHA_ROOT_DIR);
			if (!sahaRoot.exists()) {
				sahaRoot.mkdir();
			}
			return sahaRoot;
		}
		return null;
	}

	/**
	 * Get a top level directory in SAHA root
	 * 
	 * @param dirName
	 *            The top level directory
	 */
	private static File getTopLevelDir(String dirName) {
		File sahaRoot = getSahaRoot();
		File dir = new File(sahaRoot, dirName);
		if (!dir.exists()) {
			dir.mkdir();
		}
		return dir;
	}

	/**
	 * Get the tmp directory for temporary files
	 */
	public static File getTmpDir() {
		return getTopLevelDir(FileSystem.TMP_DIR);
	}

	/**
	 * Get the directory for haar cascade classifiers
	 */
	public static File getHaarDir() {
		return getTopLevelDir(FileSystem.HAAR_CLASSIFIERS_DIR);
	}

	/**
	 * Get the users directory
	 */
	public static File getUsersDir() {
		return getTopLevelDir(FileSystem.USERS_DIR);
	}

	/**
	 * Get the user directory for a user
	 * 
	 * @param user
	 *            the user directory name
	 */
	public static File getUserDir(User user) {
		File userFile = new File(getUsersDir(), user.getDirectory());
		if (!userFile.exists()) {
			userFile.mkdirs();
		}
		return userFile;
	}

	/**
	 * Get the face image directory for a user
	 * 
	 * @param user
	 *            the user
	 */
	public static File getUserFaceDir(User user) {
		File userDir = getUserDir(user);
		File faceDir = new File(userDir, FileSystem.USER_FACES_DIR);
		if (!faceDir.exists()) {
			faceDir.mkdir();
		}
		return faceDir;
	}

	/**
	 * Get an output stream for writing a new face image for a user
	 * 
	 * @param user
	 *            the user
	 */
	public static FileOutputStream getStreamForNewFaceImage(User user)
			throws IOException {
		File userFaceDir = getUserFaceDir(user);
		// Find the first available index
		int curNumFiles = userFaceDir.list().length;
		File newFace = new File(userFaceDir, FileSystem.FACE_IMAGE_PREFIX
				+ String.valueOf(curNumFiles) + FileSystem.FACE_IMAGE_EXT);
		newFace.createNewFile();
		return new FileOutputStream(newFace);
	}

	/**
	 * Get the file for the temporary image used for face identification
	 */
	public static File getFileForFaceIdentification() {
		File tmpDir = getTmpDir();
		File faceFile = new File(tmpDir, FileSystem.FACE_ID_IMAGE);
		return faceFile;
	}

	/**
	 * Create the file used by the face recognizer to store models
	 */
	public static void createFaceRecModelFile() {
		File usersDir = getUsersDir();
		File userFile = new File(usersDir, FileSystem.FACE_REC_MODEL);
		if (!userFile.exists()) {
			try {
				userFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get a representation of all the users and paths to their face images
	 * 
	 * @param users
	 *            List of all users
	 */
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
				//Log.d(TAG, images[i]);
				usersFaceImages[userIndex][i] = userFaceDir.getAbsolutePath()
						+ "/" + images[i];
			}
			userIndex++;
		}

		usersFaces.setUserIds(userIds);
		usersFaces.setUserImageFaces(usersFaceImages);

		return usersFaces;
	}

	/**
	 * Get the path to the face recognition model
	 */
	public static String getFaceRecModelPath() {
		return String.format("%s/%s/%s", getSahaRoot().getAbsolutePath(),
				FileSystem.USERS_DIR, FileSystem.FACE_REC_MODEL);
	}

	/**
	 * Copy the classifier to the sdcard if it doesn't exist there already
	 */
	public static void copyClassifierToSdCard(AssetManager assetManager) {
		File classifierDir = getTopLevelDir(FileSystem.HAAR_CLASSIFIERS_DIR);
		File faceClassifier = new File(classifierDir,
				FileSystem.HAAR_FACE_CLASSIFIER);
		if (!faceClassifier.exists() || faceClassifier.length() == 0) {
			try {
				faceClassifier.createNewFile();
				InputStream is = assetManager.open(Assets.HAAR_CLASSIFIERS_DIR
						+ "/" + Assets.HAAR_FACE_CLASSIFIER,
						AssetManager.ACCESS_BUFFER);
				OutputStream os = new FileOutputStream(faceClassifier);
				int copied = IOUtils.copy(is, os);
				os.flush();
				is.close();
				os.close();
				if (copied == 0) {
					Log.e(TAG, "Copy face classifier to sdcard failed!");
					faceClassifier.delete();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Get the face classifier path
	 */
	public static String getFaceClassifierPath() {
		return String.format("%s/%s/%s", getSahaRoot().getAbsolutePath(),
				FileSystem.HAAR_CLASSIFIERS_DIR,
				FileSystem.HAAR_FACE_CLASSIFIER);
	}
	
	/**
	 * Delete user directories. Note - this will delete all face images!
	 */
	public static void deleteUserDirs() {
		File users = getUsersDir();
		try {
			FileUtils.cleanDirectory(users);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
