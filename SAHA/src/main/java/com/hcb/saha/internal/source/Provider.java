package com.hcb.saha.internal.source;

/**
 * Provider base class for all provider implementations to extend.
 * This registers itself with the manager implementation.
 *
 * @author Andreas Borglin
 */
public abstract class Provider {

	protected Provider(BaseSourceManager baseSourceManager) {
		baseSourceManager.registerProvider(this);
	}
}
