package com.academy.ndvalkov.mediamonitoringapp.articles;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.DialogFactory;
import com.academy.ndvalkov.mediamonitoringapp.common.ListUtils;
import com.academy.ndvalkov.mediamonitoringapp.common.events.articles.OpenSelectEvent;
import com.academy.ndvalkov.mediamonitoringapp.data.db.DbProvider;
import com.academy.ndvalkov.mediamonitoringapp.models.MonitoringConfig;
import com.farbod.labelledspinner.LabelledSpinner;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import fr.castorflex.android.circularprogressbar.CircularProgressBar;

public class ArticlesFragment extends Fragment {

    private static final String TAG = ArticlesFragment.class.getSimpleName();

    private final DbProvider mDbProvider;

    private CircularProgressBar mProgress;
    private RelativeLayout mContainerArticles;

    public ArticlesFragment() {
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
        View view = inflater.inflate(R.layout.fragment_articles,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        mProgress = (CircularProgressBar)view.findViewById(R.id.progress);
        mContainerArticles = (RelativeLayout) view.findViewById(R.id.container_articles);

        return view;
    }


//    @Override
//    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
//        Fragment sourcesFragment = new SourcesFragment();
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//        transaction.replace(R.id.container_sources, sourcesFragment).commit();
//    }

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
    public void onArticlesSelectEvent(OpenSelectEvent ev) {
        if(isAdded()){
            openSelectDialog();
        }
    }

    private void openSelectDialog() {

        mProgress.setVisibility(View.VISIBLE);
        mContainerArticles.setVisibility(View.GONE);

        final List<MonitoringConfig> configs = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                configs.addAll(mDbProvider.getAllConfigs());

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mProgress.setVisibility(View.GONE);
                        mContainerArticles.setVisibility(View.VISIBLE);

                        List<String> sources = ListUtils.map(configs, new ListUtils.Map<MonitoringConfig, String>() {
                            @Override
                            public String mapItem(MonitoringConfig item) {
                                return item.getSource();
                            }
                        });

                        showDialog(sources);

                    }
                });
            }
        }).start();
    }

    private void showDialog(List<String> sources) {
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final LinearLayout contentView = (LinearLayout)inflater.inflate(R.layout.dialog_select_source, null, false);
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
                .setOKButton(true);
        final Dialog dlg = new DialogFactory(getActivity()).createDialog(dlgParams);

        dlg.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),
                        spinner.getSpinner().getSelectedItem().toString(),
                        Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            }
        });
    }
}
