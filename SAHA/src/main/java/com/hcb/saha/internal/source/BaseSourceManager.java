package com.hcb.saha.internal.source;

import java.util.ArrayList;

/**
 * Base class for all managers that keeps a list of registered providers
 * 
 * @author Andreas Borglin
 * 
 */
public abstract class BaseSourceManager {

	protected BaseSourceManager() {
	}

	protected ArrayList<Provider> providers;

	public void registerProvider(Provider provider) {
		providers.add(provider);
	}

}
