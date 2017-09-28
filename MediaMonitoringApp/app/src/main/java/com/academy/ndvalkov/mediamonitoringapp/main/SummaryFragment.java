package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.UpdateSummaryEvent;
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;
import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class SummaryFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private NewsSource mSelectedSource;
    private List<String> mPrimaryKeywords = new ArrayList<>();
    private List<String> mSecondaryKeywords = new ArrayList<>();
    private TextView mTvSource;
    private TextView mTvCategory;
    private ListView mLvPrimary;
    private ListView mLvSecondary;
    private ArrayAdapter<String> mPrimAdapter;
    private ArrayAdapter<String> mSecAdapter;
    private Button mBtnSave;

    private DbProvider mDbProvider;

    public NewsSource getSelectedSource() {
        return mSelectedSource;
    }

    public void setSelectedSource(NewsSource mSelectedSource) {
        this.mSelectedSource = mSelectedSource;
    }

    public List<String> getPrimaryKeywords() {
        return mPrimaryKeywords;
    }

    public void setPrimaryKeywords(List<String> mPrimaryKeywords) {
        this.mPrimaryKeywords = mPrimaryKeywords;
    }

    public List<String> getSecondaryKeywords() {
        return mSecondaryKeywords;
    }

    public void setSecondaryKeywords(List<String> mSecondaryKeywords) {
        this.mSecondaryKeywords = mSecondaryKeywords;
    }

    public SummaryFragment() {
        // Required empty public constructor

        // DI?
        mDbProvider = DbProvider.getInstance();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_summary,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        mTvSource = (TextView) view.findViewById(R.id.tvSource);
        mTvCategory = (TextView) view.findViewById(R.id.tvCategory);
        mLvPrimary = (ListView) view.findViewById(R.id.lvPrimary);
        mLvSecondary = (ListView) view.findViewById(R.id.lvSecondary);
        mBtnSave = (Button) view.findViewById(R.id.btnSave);

        mPrimAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mPrimaryKeywords);
        mLvPrimary.setAdapter(mPrimAdapter);

        mSecAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mSecondaryKeywords);
        mLvSecondary.setAdapter(mSecAdapter);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    /**
     * Otto event library, callback method.
     * Must be public and have a Subscribe attribute.
     *
     * @param ev
     */
    @Subscribe
    public void onUpdateSummaryEvent(UpdateSummaryEvent ev) {
        mTvSource.setText(mSelectedSource != null ? mSelectedSource.getName() : "Source");
        mTvCategory.setText(mSelectedSource != null ? mSelectedSource.getCategory() : "Category");

        mPrimAdapter.notifyDataSetChanged();
        mSecAdapter.notifyDataSetChanged();

        // String[] prims = new String[mPrimaryKeywords.size()];
        // String[] secs = new String[mSecondaryKeywords.size()];
        MonitoringConfig mc = new MonitoringConfig("ddd",
                "ddd",
                "ddd",
                TextUtils.join(" ", mPrimaryKeywords),
                TextUtils.join(" ", mSecondaryKeywords),
                "ddd");

        // mDbProvider.saveConfig(mc);

        List<MonitoringConfig> conf = mDbProvider.getAllConfigs();
        Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
    }
}
