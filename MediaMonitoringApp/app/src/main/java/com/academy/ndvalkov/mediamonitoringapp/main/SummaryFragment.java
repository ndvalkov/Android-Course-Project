package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.events.UpdateSummaryEvent;
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;
import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;
import com.squareup.otto.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

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
    private EditText mEtVendor;
    private CircularProgressBar mProgress;
    private RelativeLayout mContainerSummary;

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

        initializeViewElements(view);

        mPrimAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mPrimaryKeywords);
        mLvPrimary.setAdapter(mPrimAdapter);

        mSecAdapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_list_item_1, mSecondaryKeywords);
        mLvSecondary.setAdapter(mSecAdapter);

        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveConfiguration();
            }
        });

        return view;
    }

    private void initializeViewElements(View view) {
        mTvSource = (TextView) view.findViewById(R.id.tvSource);
        mTvCategory = (TextView) view.findViewById(R.id.tvCategory);
        mLvPrimary = (ListView) view.findViewById(R.id.lvPrimary);
        mLvSecondary = (ListView) view.findViewById(R.id.lvSecondary);
        mBtnSave = (Button) view.findViewById(R.id.btnSave);
        mEtVendor = (EditText) view.findViewById(R.id.etVendor);
        mProgress = (CircularProgressBar)view.findViewById(R.id.progress);
        mContainerSummary = (RelativeLayout)view.findViewById(R.id.container_summary);
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
    }

    private void SaveConfiguration() {
        String source = mTvSource.getText().toString();
        if (TextUtils.isEmpty(source) || source.equals(getResources().getString(R.string.main_tv_source))) {
            Notifications.showPositive(getActivity(), getString(R.string.msg_select_source));
            return;
        }

        String category = mTvCategory.getText().toString();
        if (TextUtils.isEmpty(category) || category.equals(getResources().getString(R.string.main_tv_category))) {
            Notifications.showPositive(getActivity(), getString(R.string.msg_select_category));
            return;
        }

        String vendor = mEtVendor.getText().toString();
        if (TextUtils.isEmpty(vendor)) {
            Notifications.showPositive(getActivity(), getString(R.string.msg_add_vendor));
            return;
        }

        String primKeywords = TextUtils.join(" ", mPrimaryKeywords);
        String secKeywords = TextUtils.join(" ", mSecondaryKeywords);
        String dateCreated = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Calendar.getInstance().getTime());

        final MonitoringConfig mc = new MonitoringConfig(source,
                category,
                vendor,
                primKeywords,
                secKeywords,
                dateCreated
                );


        mProgress.setVisibility(View.VISIBLE);
        mContainerSummary.setVisibility(View.GONE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                mDbProvider.saveConfig(mc);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setVisibility(View.GONE);
                        mContainerSummary.setVisibility(View.VISIBLE);
                        Notifications.showPositive(getActivity(), getResources().getString(R.string.msg_config_saved));
                    }
                });
            }
        }).start();
    }
}
