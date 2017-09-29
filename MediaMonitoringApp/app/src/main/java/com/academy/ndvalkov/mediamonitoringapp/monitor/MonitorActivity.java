package com.academy.ndvalkov.mediamonitoringapp.monitor;

/**
 * NOT USING THE SUPPORT FRAGMENTS! Animations.
 * // TODO: Reconsider, consistency/compatibility?
 */

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.articles.OpenSelectEvent;

public class MonitorActivity extends BaseActivity {

    private static final String TAG = MonitorActivity.class.getSimpleName();

    private MonitorFragment mMonitorFragment;
    private ImageButton mMonitorButton;

    private View.OnClickListener mToolbarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_monitor:
                    openMonitorDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor);
        setupDrawerNavigation();

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        /**
         * Toolbar action buttons.
         */
        mMonitorButton = (ImageButton) findViewById(R.id.action_monitor);
        mMonitorButton.setOnClickListener(mToolbarButtonListener);

        mMonitorFragment = new MonitorFragment();

        FragmentTransaction trans = getFragmentManager()
                .beginTransaction();

        trans.replace(R.id.container_monitor, mMonitorFragment);
        trans.commit();
    }

    public void openMonitorDialog() {
        if (mMonitorFragment.isAdded()) {
            BusProvider.getInstance().post(new OpenSelectEvent(true));
        }
    }
}
