package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.ListUtils;
import com.academy.ndvalkov.mediamonitoringapp.models.Keyword;
import com.yalantis.beamazingtoday.interfaces.AnimationType;
import com.yalantis.beamazingtoday.interfaces.BatModel;
import com.yalantis.beamazingtoday.listeners.BatListener;
import com.yalantis.beamazingtoday.ui.adapter.BatAdapter;
import com.yalantis.beamazingtoday.ui.animator.BatItemAnimator;
import com.yalantis.beamazingtoday.ui.callback.BatCallback;
import com.yalantis.beamazingtoday.ui.widget.BatRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PrimaryFragment extends Fragment implements BatListener {

    private static final String TAG = PrimaryFragment.class.getSimpleName();

    private BatRecyclerView mRecyclerView;
    private BatAdapter mAdapter;
    private List<BatModel> mKeywords;
    private BatItemAnimator mAnimator;
    private FloatingActionButton mFabDelete;

    public BatRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public List<BatModel> getKeywords() {
        return mKeywords;
    }

    public PrimaryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_primary,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        mFabDelete = (FloatingActionButton) view.findViewById(R.id.fabDelete);
        mFabDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ListUtils.filter(mKeywords, new ListUtils.Filter<BatModel>() {
                    @Override
                    public boolean keepItem(BatModel item) {
                        return !item.isChecked();
                    }
                });

                mAdapter.notifyDataSetChanged();
                mFabDelete.setVisibility(View.INVISIBLE);
            }
        });


        mRecyclerView = (BatRecyclerView) view.findViewById(R.id.batRvSecondary);
        mAnimator = new BatItemAnimator();

        mRecyclerView.getView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.getView().setAdapter(mAdapter = new BatAdapter(mKeywords = new ArrayList<>(), this, mAnimator));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new BatCallback(this));
        itemTouchHelper.attachToRecyclerView(mRecyclerView.getView());
        mRecyclerView.getView().setItemAnimator(mAnimator);
        mRecyclerView.setAddItemListener(this);

        mRecyclerView.setAddButtonColor(ContextCompat.getColor(getActivity(), R.color.secondaryLightColor));
        // mRecyclerView.setRadioButtonColor(ContextCompat.getColor(getActivity(), R.color.secondaryLightColor));

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            // mContext = getActivity();

        } catch (ClassCastException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void add(String string) {
        // workaround to style radios properly, neither xml attrs, nor programmatically seem to work
        // List<AppCompatCheckBox> checks = new ArrayList<>();
        // findAllViewByType(mRecyclerView, AppCompatCheckBox.class, checks);
        // for (AppCompatCheckBox cb : checks) {
        // cb.setButtonDrawable(R.drawable.ic_notify);
        // cb.setAlpha(0);
        // }

        mKeywords.add(0, new Keyword(string));
        mAdapter.notify(AnimationType.ADD, 0);
    }

    @Override
    public void delete(int position) {
        mKeywords.remove(position);
        mAdapter.notify(AnimationType.REMOVE, position);
    }

    @Override
    public void move(int from, int to) {
        mFabDelete.setVisibility(View.VISIBLE);
    }

    private <T extends View> void findAllViewByType(ViewGroup parent, Class<T> viewType, List<T> all) {
        int childCount = parent.getChildCount();
        T target = null;

        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            Class<?> ct = child.getClass();
            boolean b = ct.equals(viewType);
            if (b) {
                all.add((T) child);
                break;
            }

            if (child instanceof ViewGroup) {
                findAllViewByType((ViewGroup) child, viewType, all);
            }
        }
    }
}
