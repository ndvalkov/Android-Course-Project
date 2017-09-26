package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.FilterActionActivateEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.events.FilterOpenEvent;
import com.squareup.otto.Subscribe;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainFragment.class.getSimpleName();

    private Fragment mMainFragment;
    private ImageButton mFilterButton;

    private View.OnClickListener toolbarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_filter:
                    openFilterDialog();
                    break;
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawerNavigation();

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        /**
         * Toolbar action buttons.
         */
        mFilterButton = (ImageButton) findViewById(R.id.action_filter);
        mFilterButton.setOnClickListener(toolbarButtonListener);
        mFilterButton.setEnabled(false);

        mMainFragment = new MainFragment();

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();

        trans.replace(R.id.container_fragments, mMainFragment);
        trans.commit();
    }

    /**
     * Otto event library, callback method.
     * Must be public and have a Subscribe attribute.
     *
     * @param ev
     */
    @Subscribe
    public void onFilterActionActivateEvent(FilterActionActivateEvent ev) {
        mFilterButton.setEnabled(true);
    }

    private void openFilterDialog() {
        BusProvider.getInstance().post(new FilterOpenEvent(true));
    }
}