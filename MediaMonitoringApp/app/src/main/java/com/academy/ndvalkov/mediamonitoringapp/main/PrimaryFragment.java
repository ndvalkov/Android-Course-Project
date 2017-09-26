package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
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

public class PrimaryFragment extends Fragment {

    private static final String TAG = PrimaryFragment.class.getSimpleName();

    private static final int SWIPE_RIGHT = 8;

    private BatRecyclerView mRecyclerView;
    private ArrayList<BatModel> mKeywords = new ArrayList<>();
    private BatItemAnimator mAnimator = new BatItemAnimator();

    private BatListener mListener = new BatListener() {
        @Override
        public void add(String string) {
            mKeywords.add(0, new Keyword(string));
            mBatAdapter.notify(AnimationType.ADD, 0);
        }

        @Override
        public void delete(int position) {
            mKeywords.remove(position);
            mBatAdapter.notify(AnimationType.REMOVE, position);
        }

        @Override
        public void move(int from, int to) {
            if (from >= 0 && to >= 0) {

                // if you use 'BatItemAnimator'
                mAnimator.setPosition(to);

                BatModel model = mKeywords.get(from);
                mKeywords.remove(model);
                mKeywords.add(to, model);
                mBatAdapter.notify(AnimationType.MOVE, from, to);

                if (from == 0 || to == 0) {
                    mRecyclerView.getView().scrollToPosition(Math.min(from, to));
                }

                // Added
                mRecyclerView.getView().scrollToPosition(Math.max(from, to));
            }
        }
    };

    private BatAdapter mBatAdapter = new BatAdapter(mKeywords, mListener, mAnimator);

    public BatRecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public ArrayList<BatModel> getKeywords() {
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

        mRecyclerView = (BatRecyclerView) view.findViewById(R.id.batRvSecondary);
        mRecyclerView.getView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.getView().setAdapter(mBatAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeBatCallback(mListener));
        itemTouchHelper.attachToRecyclerView(mRecyclerView.getView());
        mRecyclerView.setAddItemListener(mListener);

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

    public class SwipeBatCallback extends BatCallback {

        public SwipeBatCallback(BatListener listener) {
            super(listener);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            super.onSwiped(viewHolder, direction);

            if (direction == SWIPE_RIGHT) {
                if (mKeywords.isEmpty()) {
                    return;
                }

                final View itemView = viewHolder.itemView;
                final RelativeLayout container = (RelativeLayout) ((FrameLayout) itemView)
                        .getChildAt(0);
                final TextView textView = (TextView) container.getChildAt(2);

                String swipedKeyword = textView.getText().toString();

                List<BatModel> kws = new ArrayList<>(mKeywords);
                for (BatModel batModel: kws) {
                    if (batModel.getText().equals(swipedKeyword)) {
                        mKeywords.remove(batModel);
                        break;
                    }
                }

                mBatAdapter.notifyDataSetChanged();
            }
        }
    }
}
