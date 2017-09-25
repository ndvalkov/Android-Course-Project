package com.academy.ndvalkov.mediamonitoringapp.main;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.views.adapters.SourcesRVAdapter;
import com.academy.ndvalkov.mediamonitoringapp.data.services.DataService;
import com.academy.ndvalkov.mediamonitoringapp.data.services.HttpDataService;
import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {
//    private ArrayList<NewsSource> sources;
//    private SourcesAdapter adapter;
//    private ListView lvSources;

    private List<NewsSource> sources;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawerNavigation();


        mRecyclerView = (RecyclerView) findViewById(R.id.rvSources);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mRecyclerView.addItemDecoration(new VerticalSpacingDecoration((int)getResources().getDimension(R.dimen.spacing_large)));

        sources = new ArrayList<>();
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