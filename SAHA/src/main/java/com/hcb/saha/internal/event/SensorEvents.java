package com.hcb.saha.internal.event;

/**
 * Sensor events
 * 
 * @author Andreas Borglin
 */
public class SensorEvents {

	public static enum SensorType {
		MOVEMENT(1), LIGHT(2), TEMPERATURE(3), HUMIDITY(4), PRESSURE(5), PROXIMITY(
				6);

		private int id;

		private SensorType(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}
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

		public SensorDetectionEvent(SensorType sensorType, float[] sensorValues) {
			super(sensorType, sensorValues);
		}

	}

}
