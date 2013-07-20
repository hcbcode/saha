package com.hcb.saha;


/**
 * Main configuration file
 * 
 * @author Andreas Borglin
 */
public final class SahaConfig {

	private SahaConfig() {
	}

	public static final String BUGSENSE_KEY = "1f39dbab";

	public static final class Database {
		public static final String NAME = "saha.db";
		public static final int VERSION = 1;
	}

	public static final class FileSystem {
		public static final String SDCARD_ROOT = "SAHA";
		public static final String SAHA_TMP = "tmp";
		public static final String SAHA_USERS = "users";
		public static final String SAHA_USER_FACES = "faces";
		public static final String FACE_PREFIX = "face";
		public static final String FACE_ID_IMAGE = "face_id.jpg";
		public static final String FACE_IMAGE_EXT = ".jpg";
		public static final String FACE_REC_MODEL = "facerec.xml";
	}
	
	public static final class Registration {
		public static final int NUM_FACE_PICS_REQUIRED = 3;
	}
}
