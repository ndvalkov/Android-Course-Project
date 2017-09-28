package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.FilterActionActivateEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.events.FilterActionHideEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.events.FilterOpenEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.events.NextActionHideEvent;
import com.squareup.otto.Subscribe;

public class MainActivity extends BaseActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainFragment mMainFragment;
    private ImageButton mFilterButton;
    private ImageButton mNextButton;

    private View.OnClickListener mToolbarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_filter:
                    openFilterDialog();
                    break;
                case R.id.action_next:
                    navigateNext();
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
        mNextButton = (ImageButton) findViewById(R.id.action_next);
        mFilterButton.setOnClickListener(mToolbarButtonListener);
        mNextButton.setOnClickListener(mToolbarButtonListener);
        mFilterButton.setEnabled(false);
        mFilterButton.setAlpha((float) 0.6);

        mMainFragment = new MainFragment();

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();

        trans.replace(R.id.container_main, mMainFragment);
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
        mFilterButton.setAlpha((float) 1);
    }

    /**
     * Otto event library, callback method.
     * Must be public and have a Subscribe attribute.
     *
     * @param ev
     */
    @Subscribe
    public void onFilterActionHideEvent(FilterActionHideEvent ev) {
        if (ev.actionHide) {
            mFilterButton.setVisibility(View.INVISIBLE);
        } else {
            mFilterButton.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Otto event library, callback method.
     * Must be public and have a Subscribe attribute.
     *
     * @param ev
     */
    @Subscribe
    public void onNextActionHideEvent(NextActionHideEvent ev) {
        if (ev.nextHide) {
            mNextButton.setVisibility(View.INVISIBLE);
        } else {
            mNextButton.setVisibility(View.VISIBLE);
//            ObjectAnimator animator = ObjectAnimator
//                    .ofFloat(mNextButton, "rotation", mNextButton.getRotation() + 360);
//            animator.start();
        }
    }

    private void openFilterDialog() {
        if (mMainFragment.isAdded()) {
            BusProvider.getInstance().post(new FilterOpenEvent(true));
        }
    }

    private void navigateNext() {
        if (mMainFragment.isAdded()) {
            mMainFragment.getPager().setCurrentItem(mMainFragment.getPager().getCurrentItem() + 1, true);
        }
    }
}