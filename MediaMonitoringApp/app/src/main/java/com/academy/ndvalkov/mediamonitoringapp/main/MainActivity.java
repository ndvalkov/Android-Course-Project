package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.widget.ListView;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.Notifications;
import com.academy.ndvalkov.mediamonitoringapp.common.views.adapters.SourcesAdapter;
import com.academy.ndvalkov.mediamonitoringapp.data.services.DataService;
import com.academy.ndvalkov.mediamonitoringapp.data.services.HttpDataService;
import com.academy.ndvalkov.mediamonitoringapp.data.tasks.HttpTask;
import com.academy.ndvalkov.mediamonitoringapp.models.NewsSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity {
    private ArrayList<NewsSource> sources;
    private SourcesAdapter adapter;
    private ListView lvSources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawerNavigation();

        // Test data services, move to fragment later

        sources = new ArrayList<>();
        adapter = new SourcesAdapter(this, sources);
        lvSources = (ListView) findViewById(R.id.lvSources);
        lvSources.setAdapter(adapter);

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
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}