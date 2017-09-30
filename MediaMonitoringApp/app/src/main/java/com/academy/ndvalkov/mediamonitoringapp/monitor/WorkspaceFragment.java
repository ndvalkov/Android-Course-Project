package com.academy.ndvalkov.mediamonitoringapp.monitor;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.DialogFactory;
import com.academy.ndvalkov.mediamonitoringapp.common.ListUtils;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.events.monitor.OpenMonitorConfigEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.views.adapters.WorkspaceRVAdapter;
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;
import com.academy.ndvalkov.mediamonitoringapp.data.services.DataService;
import com.academy.ndvalkov.mediamonitoringapp.data.services.HttpDataService;
import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;
import com.academy.ndvalkov.mediamonitoringapp.models.Article;
import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.farbod.labelledspinner.LabelledSpinner;
import com.konifar.fab_transformation.FabTransformation;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class WorkspaceFragment extends Fragment {

    private static final String TAG = WorkspaceFragment.class.getSimpleName();

    private final DbProvider mDbProvider;

    private RelativeLayout mContainerArticles;
    private CircularProgressBar mProgress;

    private List<String> mVendors = new ArrayList<>();
    private List<MonitoringConfig> mConfigs = new ArrayList<>();
    private List<Article> mArticles = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FloatingActionButton mFabProcess;
    private CardView mSheet;
    private Button mBtnProcess;

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

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvArticles);
        mContainerArticles = (RelativeLayout) view.findViewById(R.id.container_articles);
        mProgress = (CircularProgressBar) view.findViewById(R.id.progress);
        mFabProcess = (FloatingActionButton) view.findViewById(R.id.fabProcess);
        mSheet = (CardView) view.findViewById(R.id.sheet);
        mBtnProcess = (Button) mSheet.findViewById(R.id.btnProcess);

        mSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFabProcess.getVisibility() != View.VISIBLE) {
                    FabTransformation.with(mFabProcess).transformFrom(mSheet);
                }
            }
        });

        mFabProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFabProcess.getVisibility() == View.VISIBLE) {
                    FabTransformation.with(mFabProcess).transformTo(mSheet);
                }
            }
        });

        mBtnProcess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processArticles();
            }
        });

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(false);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new VerticalSpacingDecoration((int) getResources()
                .getDimension(R.dimen.activity_vertical_margin)));

        mArticles = new ArrayList<>();
        mAdapter = new WorkspaceRVAdapter(mArticles);
        mRecyclerView.setAdapter(mAdapter);

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

    /**
     * Otto event library, callback method.
     * Must be public and have a Subscribe attribute.
     *
     * @param ev
     */
    @Subscribe
    public void onMonitorConfigEvent(OpenMonitorConfigEvent ev) {
        if (isAdded()) {
            openMonitorConfigDialog();
        }
    }

    private void openMonitorConfigDialog() {
        final List<MonitoringConfig> configs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                configs.addAll(mDbProvider.getAllConfigs());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mConfigs.clear();
                        mConfigs.addAll(configs);

                        showDialog(mConfigs);
                    }
                });
            }
        }).start();
    }

    private void showDialog(List<MonitoringConfig> configs) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final LinearLayout contentView = (LinearLayout) inflater.inflate(R.layout.dialog_monitor_config, null, false);
        final LabelledSpinner spinner = (LabelledSpinner) contentView.findViewById(R.id.spinConfigs);
        final TextView tvSource = (TextView) contentView.findViewById(R.id.tvSource);
        final TextView tvPrimary = (TextView) contentView.findViewById(R.id.tvPrimary);
        final TextView tvSecondary = (TextView) contentView.findViewById(R.id.tvSecondary);

        MonitoringConfig mc = mConfigs.get(0);
        tvSource.setText(mc.getSource());
        String prims = "Primary: " + mc.getPrimaryKeywords();
        tvPrimary.setText(prims);
        String secs = "Secondary: " + mc.getSecondaryKeywords();
        tvSecondary.setText(secs);

        mVendors = ListUtils.map(configs, new ListUtils.Map<MonitoringConfig, String>() {
            @Override
            public String mapItem(MonitoringConfig item) {
                return item.getVendor();
            }
        });

        spinner.setItemsArray(new ArrayList<>(mVendors));
        spinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                MonitoringConfig mc = mConfigs.get(position);
                tvSource.setText(mc.getSource());
                tvPrimary.setText(mc.getPrimaryKeywords());
                tvSecondary.setText(mc.getSecondaryKeywords());
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        contentView.setLayoutParams(params);

        DialogFactory.DialogParams dlgParams = new DialogFactory.DialogParams();
        dlgParams.setTitle(getString(R.string.dialog_title_select_source))
                .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notify))
                .setContentWidget(contentView)
                .setOKButton(true)
                .setCancelButton(true);
        final Dialog dlg = new DialogFactory(getActivity()).createDialog(dlgParams);

        dlg.findViewById(R.id.cancelButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dlg.dismiss();
            }
        });

        dlg.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String vendorName = spinner.getSpinner().getSelectedItem().toString();
                int vendorIndex = mVendors.indexOf(vendorName);

                TextView tvSource = (TextView) mSheet.findViewById(R.id.tvSource);
                TextView tvVendor = (TextView) mSheet.findViewById(R.id.tvVendor);
                tvSource.setText(mConfigs.get(vendorIndex).getSource());
                tvVendor.setText(vendorName);

                getArticles(mConfigs.get(vendorIndex).getSourceId());
                dlg.dismiss();
            }
        });
    }

    private void getArticles(String sourceId) {
        mProgress.setVisibility(View.VISIBLE);
        mContainerArticles.setVisibility(View.GONE);
        String format = "https://newsapi.org/v1/articles?source=%s&apiKey=%s";
        String apiKey = "76dcecb58f2b437a9c6beb9b0bad10fb";
        String baseUrl = String.format(format, sourceId, apiKey);

        DataService<Article> sourcesData = new HttpDataService<>(baseUrl, "articles", Article.class, Article[].class);
        sourcesData.getAll(new HttpTask.OnHttpTaskResult<Article[]>() {
            @Override
            public void call(final Exception ex, Article[] result) {
                final List<Article> articles = Arrays.asList(result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ex != null) {
                            Notifications.showNegative(getActivity(), ex.getMessage());
                        } else {
                            mArticles.clear();
                            mArticles.addAll(articles);
                            mAdapter.notifyDataSetChanged();

                            mProgress.setVisibility(View.GONE);
                            mContainerArticles.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void processArticles() {
        final WorkspaceRVAdapter workspaceAdapter = (WorkspaceRVAdapter)mAdapter;
        workspaceAdapter.getPrimaryKeywords().add("paying");

        // force redraw of all
        List<Article> newArticleList = new ArrayList<>(mArticles);
        mArticles.clear();
        mArticles.addAll(newArticleList);
        workspaceAdapter.setProcessed(true);
        workspaceAdapter.notifyDataSetChanged();

        mRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                workspaceAdapter.setProcessed(false);
            }
        });
    }

    private class VerticalSpacingDecoration extends RecyclerView.ItemDecoration {

        private int spacing;

        public VerticalSpacingDecoration(int spacing) {
            this.spacing = spacing;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = spacing;
        }
    }
}
