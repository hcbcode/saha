package com.hcb.saha.internal.source.sensor;

import com.hcb.saha.internal.source.BaseSourceManager;

/*
 * Always alive.
 * Providers registers themselves with the manager.
 * The manager has a timer that fires based on a time configuration
 * and collects data from all the providers and sends an timed sensor event for this
 */

/**
 * Gets injected into each provider and they register themselves with this. The
 * base class will keep a list of Providers that they collect data from in a
 * generic manner.
 * 
 * @author andreas
 * 
 */
public class SensorManager extends BaseSourceManager {

	/*
	 * Timer fires.
	 * 
	 * We call each provider, callback method in here that when called, creates
	 * an event based on the sensor type and value and sends it over the event
	 * bus
	 */

}
