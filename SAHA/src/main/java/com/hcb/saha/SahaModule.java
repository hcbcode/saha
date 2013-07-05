package com.hcb.saha;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Guice module
 * @author Andreas Borglin
 */
public class SahaModule implements Module {

    @Override
    public void configure(Binder binder) {
    }

    @Provides
    @Singleton
    Bus getBus() {
        return new Bus(ThreadEnforcer.ANY);
    }
}
