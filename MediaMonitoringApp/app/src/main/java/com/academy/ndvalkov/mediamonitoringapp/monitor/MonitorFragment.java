package com.academy.ndvalkov.mediamonitoringapp.monitor;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.DialogFactory;
import com.academy.ndvalkov.mediamonitoringapp.common.ListUtils;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.events.articles.OpenSelectEvent;
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;
import com.academy.ndvalkov.mediamonitoringapp.data.services.DataService;
import com.academy.ndvalkov.mediamonitoringapp.data.services.HttpDataService;
import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;
import com.academy.ndvalkov.mediamonitoringapp.models.Article;
import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.farbod.labelledspinner.LabelledSpinner;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class MonitorFragment extends Fragment {

    private static final String TAG = MonitorFragment.class.getSimpleName();

    private final DbProvider mDbProvider;

    private CircularProgressBar mProgress;
    private RelativeLayout mContainerArticles;
    private List<String> mSources;
    private List<String> mSourceIds;
    private List<Article> mArticles;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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

        mProgress = (CircularProgressBar) view.findViewById(R.id.progress);
        mBottomNavigationView =  (BottomNavigationView) view.findViewById(R.id.bottom_navigation);

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

//        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvArticles);
//        mContainerArticles = (RelativeLayout) view.findViewById(R.id.container_articles);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        mRecyclerView.setHasFixedSize(false);
//
//        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(getActivity());
//        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        mRecyclerView.addItemDecoration(new VerticalSpacingDecoration((int) getResources()
//                .getDimension(R.dimen.activity_vertical_margin)));
//
//        mArticles = new ArrayList<>();
//        mAdapter = new ArticlesRVAdapter(mArticles);
//        mRecyclerView.setAdapter(mAdapter);

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
    public void onArticlesSelectEvent(OpenSelectEvent ev) {
        if (isAdded()) {
            openSelectDialog();
        }
    }

//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

//    }

    private void openSelectDialog() {
        final List<MonitoringConfig> configs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                configs.addAll(mDbProvider.getAllConfigs());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mSources = ListUtils.map(configs, new ListUtils.Map<MonitoringConfig, String>() {
                            @Override
                            public String mapItem(MonitoringConfig item) {
                                return item.getSource();
                            }
                        });

                        mSourceIds = ListUtils.map(configs, new ListUtils.Map<MonitoringConfig, String>() {
                            @Override
                            public String mapItem(MonitoringConfig item) {
                                return item.getSourceId();
                            }
                        });

                        showDialog(mSources);
                    }
                });
            }
        }).start();
    }

    private void showDialog(List<String> sources) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final LinearLayout contentView = (LinearLayout) inflater.inflate(R.layout.dialog_select_source, null, false);
        final LabelledSpinner spinner = (LabelledSpinner) contentView.findViewById(R.id.spinSources);

        // remove duplicate entries
        spinner.setItemsArray(new ArrayList<>(new HashSet<>(sources)));
        spinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {

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
                String sourceName = spinner.getSpinner().getSelectedItem().toString();
                int sourceIndex = mSources.indexOf(sourceName);
                getArticles(mSourceIds.get(sourceIndex));
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
