package com.hcb.saha.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;
import com.google.inject.Inject;
import com.hcb.saha.R;
import com.hcb.saha.SahaConfig;
import com.hcb.saha.config.EnvConfig;
import com.hcb.saha.event.TestEvent;
import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

/**
 * Main activity
 * @author Andreas Borglin
 */
public class MainActivity extends RoboActivity {

    @InjectView(R.id.btn)
    private Button btn;
    @Inject
    private Bus eventBus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        eventBus.register(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (EnvConfig.USE_REPORTING) {
            BugSenseHandler.initAndStartSession(this, SahaConfig.BUGSENSE_KEY);
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventBus.post(new TestEvent());
            }
        });
    }

    @Subscribe
    public void incomingEvent(TestEvent event) {
        Toast.makeText(this, "Andrew, look! An event bus!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (EnvConfig.USE_REPORTING) {
            BugSenseHandler.closeSession(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
