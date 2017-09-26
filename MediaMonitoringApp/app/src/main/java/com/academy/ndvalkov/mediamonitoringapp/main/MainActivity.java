package com.academy.ndvalkov.mediamonitoringapp.main;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.DialogFactory;
import com.academy.ndvalkov.mediamonitoringapp.common.ListUtils;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.views.adapters.SourcesRVAdapter;
import com.academy.ndvalkov.mediamonitoringapp.data.services.DataService;
import com.academy.ndvalkov.mediamonitoringapp.data.services.HttpDataService;
import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;
import com.farbod.labelledspinner.LabelledSpinner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends BaseActivity {
//    private ArrayList<NewsSource> sources;
//    private SourcesAdapter adapter;
//    private ListView lvSources;

    private List<NewsSource> sources;
    private List<NewsSource> all;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Set<String> mCategories = new HashSet<>();

    private View.OnClickListener toolbarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_filter:
                    openFilterDialog();
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

        /**
         * Toolbar action buttons.
         */
        ImageButton filterButton = (ImageButton) findViewById(R.id.action_filter);
        filterButton.setOnClickListener(toolbarButtonListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.rvSources);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new VerticalSpacingDecoration((int) getResources().getDimension(R.dimen.activity_vertical_margin)));

        sources = new ArrayList<>();
        all = new ArrayList<>();
        // specify an adapter (see also next example)
        mAdapter = new SourcesRVAdapter(sources);
        mRecyclerView.setAdapter(mAdapter);


        DataService<NewsSource> sourcesData = new HttpDataService<>("https://newsapi.org/v1/sources?language=en", NewsSource.class, NewsSource[].class);
        sourcesData.getAll(new HttpTask.OnHttpTaskResult<NewsSource[]>() {
            @Override
            public void call(final Exception ex, NewsSource[] result) {
                final List<NewsSource> newsSources = Arrays.asList(result);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (ex != null) {
                            Notifications.showNegative(MainActivity.this, ex.getMessage());
                        } else {
                            sources.addAll(newsSources);
                            all.addAll(newsSources);
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });


        // Test data services, ListView, move to fragment later

//        sources = new ArrayList<>();
//        adapter = new SourcesAdapter(this, sources);
//        lvSources = (ListView) findViewById(R.id.lvSources);
//        lvSources.setAdapter(adapter);
//
//        DataService<NewsSource> sourcesData = new HttpDataService<>("https://newsapi.org/v1/sources?language=en", NewsSource.class, NewsSource[].class);
//        sourcesData.getAll(new HttpTask.OnHttpTaskResult<NewsSource[]>() {
//            @Override
//            public void call(final Exception ex, NewsSource[] result) {
//                final List<NewsSource> newsSources = Arrays.asList(result);
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (ex != null) {
//                            Notifications.showNegative(MainActivity.this, ex.getMessage());
//                        } else {
//                            sources.addAll(newsSources);
//                            adapter.notifyDataSetChanged();
//                        }
//                    }
//                });
//            }
//        });


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

        LayoutInflater inflater = getLayoutInflater();
        final LinearLayout contentView = (LinearLayout)inflater.inflate(R.layout.dialog_filter, null, false);
        final LabelledSpinner spinner = (LabelledSpinner) contentView.findViewById(R.id.spinCategory);
        final AutoCompleteTextView acName = (AutoCompleteTextView) contentView.findViewById(R.id.acName);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
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
                .setIcon(ContextCompat.getDrawable(this, R.drawable.ic_notify))
                .setContentWidget(contentView)
                .setOKButton(true);
        final Dialog dlg = new DialogFactory(this).createDialog(dlgParams);
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

    public class VerticalSpacingDecoration extends RecyclerView.ItemDecoration {

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