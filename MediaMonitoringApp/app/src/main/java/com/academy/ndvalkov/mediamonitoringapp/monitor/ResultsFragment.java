package com.academy.ndvalkov.mediamonitoringapp.monitor;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResultsFragment extends Fragment {

    private static final String TAG = ResultsFragment.class.getSimpleName();

    private Map<String, HashMap<String, Integer>> mPrimaryResults = new HashMap<>();
    private Map<String, HashMap<String, Integer>> mSecondaryResults = new HashMap<>();

    public void setPrimaryResults(Map<String, HashMap<String, Integer>> primaryResults) {
        mPrimaryResults = primaryResults;
    }

    public void setSecondaryResults(Map<String, HashMap<String, Integer>> secondaryResults) {
        mSecondaryResults = secondaryResults;
    }

    public ResultsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor_results,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        TableLayout tlResults = (TableLayout) view.findViewById(R.id.tlResults);



        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        final List<ArticleReport> reports = new ArrayList<>();
        for (String title : mPrimaryResults.keySet()) {
            ArticleReport currentReport = new ArticleReport(title);
            HashMap<String, Integer> keywordOccurrences = mPrimaryResults.get(title);
            for (String keyword : keywordOccurrences.keySet()) {
                int count = keywordOccurrences.get(keyword);
                if (count == 0) {
                    continue;
                }

                currentReport.getPrimary().add(String.format("%s(%d)", keyword, count));
            }

            if (!currentReport.isEmpty()) {
                reports.add(currentReport);
            }
        }

        for (final String title : mSecondaryResults.keySet()) {
            ArticleReport currentReport = new ArticleReport(title);
            int pos = reports.indexOf(currentReport);
            boolean alreadyExists = false;
            if (pos >= 0) {
                currentReport = reports.get(pos);
                alreadyExists = true;
            }

            HashMap<String, Integer> keywordOccurrences = mSecondaryResults.get(title);
            for (String keyword : keywordOccurrences.keySet()) {
                int count = keywordOccurrences.get(keyword);
                if (count == 0) {
                    continue;
                }

                assert currentReport != null;
                currentReport.getSecondary().add(String.format("%s(%d)", keyword, count));
            }

            assert currentReport != null;
            if (!currentReport.isEmpty() && !alreadyExists) {
                reports.add(currentReport);
            }
        }

        // Log.d("", "");
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

    private class ArticleReport {
        public ArticleReport(String title) {
            this.title = title;
            this.primary = new ArrayList<>();
            this.secondary = new ArrayList<>();
        }

        private String title;
        private List<String> primary;
        private List<String> secondary;

        public String getTitle() {
            return title;
        }

        public List<String> getPrimary() {
            return primary;
        }

        public List<String> getSecondary() {
            return secondary;
        }

        public boolean isEmpty() {
            return this.primary.isEmpty() && this.secondary.isEmpty();
        }

        @Override
        public boolean equals(Object ar) {
            return this.title.equals(((ArticleReport)ar).getTitle());
        }
    }
}
