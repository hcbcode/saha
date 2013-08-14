package com.hcb.saha.internal.core;

/**
 * Saha defined exceptions
 * 
 * @author Andreas Borglin
 */
public class SahaExceptions {

	public static class CameraNotActiveException extends Exception {
		private static final long serialVersionUID = 1L;

		public CameraNotActiveException() {
			super();
		}

		public CameraNotActiveException(String msg) {
			super(msg);
		}
	}
}
