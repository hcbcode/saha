package com.hcb.saha.internal.event;

public class SensorEvents {

	public static enum SensorType {
		MOVEMENT, LIGHT, TEMPERATURE, HUMIDITY, PRESSURE, PROXIMITY
	}

	private static abstract class SensorEvent {

		private SensorType sensorType;
		private float[] sensorValues;

		protected SensorEvent(SensorType sensorType, float[] sensorValues) {
			this.sensorType = sensorType;
			this.sensorValues = sensorValues;
		}

		public SensorType getSensorType() {
			return sensorType;
		}

		public float[] getSensorValues() {
			return sensorValues;
		}

	}

	public static final class SensorPollingEvent extends SensorEvent {

		public SensorPollingEvent(SensorType sensorType, float[] sensorValues) {
			super(sensorType, sensorValues);
		}

	}

	public static final class SensorDetectionEvent extends SensorEvent {

		public SensorDetectionEvent(SensorType sensorType,
				float[] sensorValues) {
			super(sensorType, sensorValues);
		}

	}

}
