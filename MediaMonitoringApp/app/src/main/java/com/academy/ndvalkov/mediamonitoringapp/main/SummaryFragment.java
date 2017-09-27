package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.UpdateSummaryEvent;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;
import com.squareup.otto.Subscribe;

import java.util.List;

public class SummaryFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private NewsSource mSelectedSource;
    private List<String> mPrimaryKeywords;
    private List<String> mSecondaryKeywords;
    private TextView mTvSource;
    private TextView mTvCategory;

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

        mTvSource = (TextView)view.findViewById(R.id.tvSource);
        mTvCategory = (TextView)view.findViewById(R.id.tvCategory);


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
            // mContext = getActivity();
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
        
    }
}
