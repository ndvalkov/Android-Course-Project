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
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;

public class MonitorFragment extends Fragment {

    private static final String TAG = MonitorFragment.class.getSimpleName();

    private final DbProvider mDbProvider;

    private BottomNavigationView mBottomNavigationView;

    public MonitorFragment() {
        // Required empty public constructor

        // DI
        mDbProvider = DbProvider.getInstance();
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
            Fragment workspaceFragment = new WorkspaceFragment();
            transaction.replace(R.id.container_monitor, workspaceFragment).commit();
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
                                Fragment workspaceFragment = new WorkspaceFragment();
                                transaction.replace(R.id.container_monitor, workspaceFragment).commit();
                                break;
                            case R.id.action_results:
                                Fragment resultsFragment = new ResultsFragment();
                                transaction.replace(R.id.container_monitor, resultsFragment).commit();
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
