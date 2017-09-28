package com.academy.ndvalkov.mediamonitoringapp.main;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.DialogFactory;
import com.academy.ndvalkov.mediamonitoringapp.common.ListUtils;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.events.main.FilterActionActivateEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.events.main.FilterOpenEvent;
import com.academy.ndvalkov.mediamonitoringapp.common.views.adapters.SourcesRVAdapter;
import com.academy.ndvalkov.mediamonitoringapp.data.services.DataService;
import com.academy.ndvalkov.mediamonitoringapp.data.services.HttpDataService;
import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;
import com.farbod.labelledspinner.LabelledSpinner;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class SourcesFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private List<NewsSource> sources;
    private List<NewsSource> all;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Set<String> mCategories = new HashSet<>();

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    public List<NewsSource> getSources() {
        return sources;
    }

    public SourcesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_sources,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvSources);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new VerticalSpacingDecoration((int) getResources().getDimension(R.dimen.activity_vertical_margin)));

        // override to improve the custom selected item animation
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);

        sources = new ArrayList<>();
        all = new ArrayList<>();
        mAdapter = new SourcesRVAdapter(sources);
        mRecyclerView.setAdapter(mAdapter);

        final ProgressBar progress = (CircularProgressBar) view.findViewById(R.id.progress);
        progress.setVisibility(View.VISIBLE);

        DataService<NewsSource> sourcesData = new HttpDataService<>("https://newsapi.org/v1/sources?language=en", NewsSource.class, NewsSource[].class);
        sourcesData.getAll(new HttpTask.OnHttpTaskResult<NewsSource[]>() {
            @Override
            public void call(final Exception ex, NewsSource[] result) {
                final List<NewsSource> newsSources = Arrays.asList(result);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ex != null) {
                            Notifications.showNegative(getActivity(), ex.getMessage());
                        } else {
                            sources.addAll(newsSources);
                            all.addAll(newsSources);
                            mAdapter.notifyDataSetChanged();

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    activateFilterActionButton();
                                }
                            }, 100);

                            progress.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            // fix bug in openFilterDialog(), getActivity() return null sometimes
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
    public void onFilterEvent(FilterOpenEvent ev) {
        if(isAdded()){
            openFilterDialog();
        }
    }

    private void activateFilterActionButton() {
        BusProvider.getInstance().post(new FilterActionActivateEvent(true));
    }

    private void openFilterDialog() {
        if (mCategories.isEmpty()) {
            for (NewsSource source : sources) {
                if (!mCategories.contains(source.getCategory())) {
                    mCategories.add(source.getCategory());
                }
            }
        }

        final List<String> namesOfSources = ListUtils.map(sources, new ListUtils.Map<NewsSource, String>() {
            @Override
            public String mapItem(NewsSource item) {
                return item.getName();
            }
        });

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final LinearLayout contentView = (LinearLayout)inflater.inflate(R.layout.dialog_filter, null, false);
        final LabelledSpinner spinner = (LabelledSpinner) contentView.findViewById(R.id.spinCategory);
        final AutoCompleteTextView acName = (AutoCompleteTextView) contentView.findViewById(R.id.acName);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(namesOfSources));
        acName.setAdapter(adapter);
        acName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                acName.showDropDown();
                return false;
            }
        });

        spinner.setItemsArray(new ArrayList<>(mCategories));
        spinner.setOnItemChosenListener(new LabelledSpinner.OnItemChosenListener() {
            @Override
            public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
                sources.clear();
                sources.addAll(all);
                ListUtils.filter(sources, new ListUtils.Filter<NewsSource>() {
                    @Override
                    public boolean keepItem(NewsSource item) {
                        boolean hasCategory = item.getCategory().equals(spinner.getSpinner().getSelectedItem());
                        boolean hasName;
                        String name = acName.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            hasName = true;
                        } else {
                            hasName = item.getName().equals(name);
                        }

                        return hasCategory && hasName;
                    }
                });

                List<String> namesByCategory = ListUtils.map(sources, new ListUtils.Map<NewsSource, String>() {
                    @Override
                    public String mapItem(NewsSource item) {
                        return item.getName();
                    }
                });

                namesOfSources.clear();
                namesOfSources.addAll(namesByCategory);
                adapter.clear();
                adapter.addAll(namesOfSources);
            }

            @Override
            public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {

            }
        });

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
        contentView.setLayoutParams(params);

        DialogFactory.DialogParams dlgParams = new DialogFactory.DialogParams();
        dlgParams.setTitle(getString(R.string.dialog_title_filter))
                .setIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_notify))
                .setContentWidget(contentView)
                .setOKButton(true);
        final Dialog dlg = new DialogFactory(getActivity()).createDialog(dlgParams);
        dlg.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sources.clear();
                sources.addAll(all);
                ListUtils.filter(sources, new ListUtils.Filter<NewsSource>() {
                    @Override
                    public boolean keepItem(NewsSource item) {
                        boolean hasCategory = item.getCategory().equals(spinner.getSpinner().getSelectedItem());
                        boolean hasName;
                        String name = acName.getText().toString();
                        if (TextUtils.isEmpty(name)) {
                            hasName = true;
                        } else {
                            hasName = item.getName().equals(name);
                        }

                        return hasCategory && hasName;
                    }
                });

                mAdapter.notifyDataSetChanged();
                dlg.dismiss();
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
