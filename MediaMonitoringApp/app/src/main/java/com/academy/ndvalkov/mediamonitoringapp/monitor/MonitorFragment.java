package com.academy.ndvalkov.mediamonitoringapp.monitor;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;

public class MonitorFragment extends Fragment {

    private static final String TAG = MonitorFragment.class.getSimpleName();

    private BottomNavigationView mBottomNavigationView;
    private Fragment mWorkspaceFragment;
    private Fragment mResultsFragment;

    public MonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        mBottomNavigationView = (BottomNavigationView) view.findViewById(R.id.bottom_navigation);

        // load Workspace first
        if (savedInstanceState == null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            mWorkspaceFragment = new WorkspaceFragment();
            // transaction.add(R.id.container_monitor, workspaceFragment).commit();
            transaction.add(R.id.container_monitor, mWorkspaceFragment)
                    .addToBackStack(null)
                    .commit();
        }

        mBottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        FragmentTransaction transaction = getChildFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.animator.card_flip_right_in,
                                        R.animator.card_flip_right_out,
                                        R.animator.card_flip_left_in,
                                        R.animator.card_flip_left_out);

                        switch (item.getItemId()) {
                            case R.id.action_workspace:
                                if (mResultsFragment != null) {
                                    transaction.hide(mResultsFragment);
                                }

                                if (mWorkspaceFragment == null) {
                                    mWorkspaceFragment = new WorkspaceFragment();
                                    transaction.add(R.id.container_monitor, mWorkspaceFragment);
                                } else {
                                    transaction.show(mWorkspaceFragment);
                                }

                                transaction.commit();

//                                Fragment workspaceFragment = new WorkspaceFragment();
//                                transaction.replace(R.id.container_monitor, workspaceFragment)
//                                        .commit();
                                break;
                            case R.id.action_results:
                                if (mWorkspaceFragment != null) {
                                    transaction.hide(mWorkspaceFragment);
                                }

                                if (mResultsFragment == null) {
                                    mResultsFragment = new ResultsFragment();
                                    transaction.add(R.id.container_monitor, mResultsFragment);
                                } else {
                                    transaction.show(mResultsFragment);
                                }

                                transaction.commit();

//                                Fragment resultsFragment = new ResultsFragment();
//                                transaction.replace(R.id.container_monitor, resultsFragment)
//                                        .commit();
                                break;
                        }
                        return true;
                    }
                });


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            // ((MonitorActivity)getActivity()).openMonitorDialog();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }
}
