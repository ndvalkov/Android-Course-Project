package com.academy.ndvalkov.mediamonitoringapp.monitor;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;

public class WorkspaceFragment extends Fragment {

    private static final String TAG = WorkspaceFragment.class.getSimpleName();

    private final DbProvider mDbProvider;

    public WorkspaceFragment() {
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
        View view = inflater.inflate(R.layout.fragment_monitor_workspace,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);



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
