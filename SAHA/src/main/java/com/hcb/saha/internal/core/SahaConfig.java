package com.hcb.saha.internal.core;

/**
 * Main configuration file
 * 
 * @author Andreas Borglin
 */
public final class SahaConfig {

	private SahaConfig() {
	}

	public static final class System {
		public static final int USER_TIMEOUT_SECONDS = 10;
	}

	public static final class Database {
		public static final String NAME = "saha.db";
		public static final int VERSION = 1;
	}

	public static final class FileSystem {
		public static final String SAHA_ROOT_DIR = "SAHA";
		public static final String TMP_DIR = "tmp";
		public static final String HAAR_CLASSIFIERS_DIR = "haar";
		public static final String USERS_DIR = "users";
		public static final String USER_FACES_DIR = "faces";
		public static final String FACE_IMAGE_PREFIX = "face";
		public static final String FACE_ID_IMAGE = "face_id.jpg";
		public static final String FACE_IMAGE_EXT = ".jpg";
		public static final String FACE_REC_MODEL = "facerec.xml";
		public static final String HAAR_FACE_CLASSIFIER = "faceclassifier.xml";
		public static final String EVENTS_DIR = "events";
		public static final String EVENTS_DATA_FILE = "events.dat";

	}

	public static final class Assets {
		public static final String HAAR_CLASSIFIERS_DIR = "haarclassifiers";
		public static final String HAAR_FACE_CLASSIFIER = "haarcascade_frontalface_default.xml";
	}

	public static final class Registration {
		public static final int NUM_FACE_PICS_REQUIRED = 3;
	}

	// NOTE: If you change these, make sure to update facerec.cpp as well!
	public static final class OpenCvParameters {
		public static final int CASCADE_MIN_NEIGHBOURS = 3;
		public static final float CASCADE_SCALE_FACTOR = 1.1f;
		public static final int CASCADE_MIN_FACE_WIDTH = 40;
		public static final int CASCADE_MIN_FACE_HEIGHT = 80;
		public static final int CASCADE_OUTPUT_FACE_WIDTH = 200;
		public static final int CASCADE_OUTPUT_FACE_HEIGHT = 200;
	}

	public static final class Sensor {
		public static final int LIGHT_CHANGE_THRESHOLD = 3;
		public static final int LIGHT_COUNT_THROTTLE = 10;

	}
}
