package com.hcb.saha.internal.data.fs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import android.app.Application;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.hcb.saha.internal.core.SahaConfig.Assets;
import com.hcb.saha.internal.core.SahaConfig.FileSystem;
import com.hcb.saha.internal.data.model.User;
import com.hcb.saha.internal.data.model.UsersFaces;

/**
 * Manages the Saha file system directories and files
 *
 * @author Andreas Borglin
 */
public final class SahaFileManager {

	private static final String TAG = SahaFileManager.class.getSimpleName();

	@Inject
	private static Provider<Application> contextProvider;

	private SahaFileManager() {
	}

	/**
	 * Get the SAHA root folder on the sdcard
	 */
	public static File getSahaRoot() {
		File internal = contextProvider.get().getFilesDir();
		File sahaRoot = new File(internal, FileSystem.SAHA_ROOT_DIR);
		if (!sahaRoot.exists()) {
			sahaRoot.mkdir();
		}
		return sahaRoot;
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
	 * Get the events directory for event collection files
	 */
	public static File getEventsDir() {
		return getTopLevelDir(FileSystem.EVENTS_DIR);
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
		File userFile = new File(getUsersDir(), user.getDirectoryId());
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
	public static File getFileForNewFaceImage(User user) throws IOException {
		File userFaceDir = getUserFaceDir(user);
		// Find the first available index
		int curNumFiles = userFaceDir.list().length;
		File newFace = new File(userFaceDir, FileSystem.FACE_IMAGE_PREFIX
				+ String.valueOf(curNumFiles) + FileSystem.FACE_IMAGE_EXT);
		newFace.createNewFile();
		return newFace;
	}

	/**
	 * Persist a face bitmap to file TODO Should this logic be here?
	 *
	 * @param bitmap
	 *            The face bitmap
	 */
	public static String persistFaceBitmap(Bitmap bitmap, User user) {

		try {
			File output = null;
			if (user == null) {
				output = getFileForFaceIdentification();
			} else {
				output = getFileForNewFaceImage(user);
			}
			FileOutputStream fos = new FileOutputStream(output);
			if (bitmap != null && fos != null) {

				boolean c = bitmap.compress(CompressFormat.JPEG, 100, fos);
				Log.d(TAG, "Persist bitmap to file status: " + c);
				fos.flush();
				fos.close();
				return output.getAbsolutePath();
			}

		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	/**
	 * Appends event (JSON String) to the event data file String should be a
	 * JSON representation of @SensorData object If event data file does not
	 * exist it will create it If event data file reaches 1MB it will be copied
	 * to a unique filename in the same directory ready for upload and a new
	 * file will be created
	 *
	 * @param event
	 *
	 */
	public static boolean appendEvent(String event) {
		File eventsDir = getEventsDir();

		File file = new File(eventsDir, FileSystem.EVENTS_DATA_FILE);

		if (file.exists() && FileUtils.sizeOf(file) > 1024 * 200) {
			Log.d(TAG, "Creating new events file");
			File newFile = new File(eventsDir, FileSystem.EVENTS_DATA_FILE
					+ "-" + System.currentTimeMillis() + ".upload");
			try {
				FileUtils.moveFile(file, newFile);
			} catch (IOException e) {
				Log.e(TAG, "New file creation failed: " + e.getMessage());
				return false;
			}
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e(TAG, "Could not create events file: " + e.getMessage());
				return false;
			}
		}

		try {
			FileUtils.write(file, event, true);
			FileUtils.write(file, "\n", true);
		} catch (IOException e) {
			Log.e(TAG, "Could not write event to file: " + e.getMessage());
			return false;
		}

		return true;
	}

	/**
	 * Returns list of absolute locations for all event collection files that
	 * match the upload pattern
	 *
	 */
	public static List<String> getEventFilesForUpload() {

		File eventsDir = getEventsDir();

		List<String> fileLocations = new ArrayList<String>();
		String[] files = eventsDir.list(new SuffixFileFilter(".upload"));
		for (int i = 0; i < files.length; i++) {
			String location = String.format("%s/%s/%s", getSahaRoot()
					.getAbsolutePath(), FileSystem.EVENTS_DIR, files[i]);
			Log.d(TAG, location);
			fileLocations.add(location);
		}
		return fileLocations;
	}

	/**
	 * Deletes the file specified by absolute fileLocation
	 *
	 * @param fileLocation
	 */
	public static void deleteUploadedFile(String fileLocation) {
		FileUtils.deleteQuietly(new File(fileLocation));
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
	 * Get face images for a user
	 */
	public static String[] getUserFaceImages(User user) {
		File userFaceDir = getUserFaceDir(user);
		String[] images = userFaceDir.list();
		return images;
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
				// Log.d(TAG, images[i]);
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
	 * Delete a user directory
	 */
	public static void deleteUserDir(User user) {
		File userDir = getUserDir(user);
		try {
			FileUtils.deleteDirectory(userDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
