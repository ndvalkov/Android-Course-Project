package com.academy.ndvalkov.mediamonitoringapp.monitor;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

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
    private List<ArticleReport> mReportData = new ArrayList<>();

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
        /*tlResults.setColumnStretchable(0, true);
        tlResults.setColumnStretchable(1, true);
        tlResults.setStretchAllColumns(true);
        tlResults.bringToFront();*/

        mReportData = extractReportData();
        for (ArticleReport ar : mReportData) {
            TableRow.LayoutParams lp = new TableRow.LayoutParams(
                    TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.WRAP_CONTENT
            );

            TableRow trTitle = new TableRow(getActivity());
            trTitle.setLayoutParams(lp);
            // trTitle.setBackgroundColor(Color.parseColor("#123456"));

            TextView tvTitle = createHeader(ar.getTitle());
            TextView tvTonality = createCell("Tonality: NEUTRAL");

            TableRow.LayoutParams param = new TableRow.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.MATCH_PARENT
            );
            tvTonality.setLayoutParams(param);

            trTitle.addView(tvTitle);
            trTitle.addView(tvTonality);
            tlResults.addView(trTitle);

            TableRow trPrimSec = new TableRow(getActivity());
            TextView hPrim = createCell("Primary");
            TextView hSec = createCell("Secondary");
            hPrim.setTextSize(11);
            hSec.setTextSize(11);
            trPrimSec.addView(hPrim);
            trPrimSec.addView(hSec);
            tlResults.addView(trPrimSec);

            TableRow trKeywords = new TableRow(getActivity());

            List<String> prims = ar.getPrimary();
            TextView tvPrim;
            if (prims.isEmpty()) {
                tvPrim = createCell("No keyword matches");
            } else {
                tvPrim = createCell(TextUtils.join(" ", ar.getPrimary()));
            }
            List<String> secs = ar.getSecondary();
            TextView tvSec;
            if (secs.isEmpty()) {
                tvSec = createCell("No keyword matches");
            } else {
                tvSec = createCell(TextUtils.join(" ", ar.getSecondary()));
            }

//            LinearLayout ll = new LinearLayout(getActivity());
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams
//                    (LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//            ll.setOrientation(LinearLayout.HORIZONTAL);
//            ll.setLayoutParams(params);
//            ll.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);
//
//            ll.addView(tvPrim);
//            ll.addView(tvSec);

//            TableRow.LayoutParams param = new TableRow.LayoutParams (
//                    TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT,
//                    1.0f
//            );
//
//            tvPrim.setLayoutParams(param);
//            tvSec.setLayoutParams(param);

            trKeywords.addView(tvPrim);
            trKeywords.addView(tvSec);

            tlResults.addView(trKeywords);
        }

        return view;
    }

    private TextView createCell(String text) {
        TextView textView = new TextView(getActivity());
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_translucent));
        textView.setBackgroundResource(R.drawable.shape_border_cell);
        textView.setPadding(10, 10, 10, 10);
        textView.setGravity(Gravity.CENTER);
        textView.setMaxLines(10);

        return textView;
    }

    private TextView createHeader(String text) {
        TextView textView = (TextView) getActivity().getLayoutInflater().inflate(
                R.layout.table_cell_title, null);
        textView.setText(text);
        textView.setTextColor(ContextCompat.getColor(getActivity(), R.color.black_translucent));
        textView.setBackgroundResource(R.drawable.shape_border_cell);
        return textView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mReportData = extractReportData();
    }

    private List<ArticleReport> extractReportData() {
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

        return reports;
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
            return this.title.equals(((ArticleReport) ar).getTitle());
        }
    }
}
