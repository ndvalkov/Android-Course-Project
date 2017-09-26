package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.FilterActionHideEvent;

public class MainFragment extends Fragment {

    private static final String TAG = MainFragment.class.getSimpleName();

    private int mCurrentFragmentPosition;
    private ViewPager mPager;
    private ScreenSlidePagerAdapter mPagerAdapter;
    private String[] mTitles;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main,
                container,
                false);

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        mTitles = getResources().getStringArray(R.array.mainTitles);

        /**
         * Instantiate a ViewPager and a PagerAdapter. Add a listener for
         * the sliding screen navigation.
         * ViewPager -> Pager widget, swiping pages(fragments) horizontally, handles animation.
         * PagerAdapter -> The pager adapter, which provides the pages to the view pager widget.
         */
        mPager = (ViewPager) view.findViewById(R.id.pager);
        // getChildFragmentManager() instead of getFragmentManager()!
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        // needed for having multiple fragments in a ViewPager
        mPager.setOffscreenPageLimit(4);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // keep track of the current page
                mCurrentFragmentPosition = position;
                ((BaseActivity)getActivity()).setCustomToolbarTitle(mTitles[position]);
                if (position == 0) {
                    BusProvider.getInstance().post(new FilterActionHideEvent(false));
                } else {
                    BusProvider.getInstance().post(new FilterActionHideEvent(true));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });




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
     * A simple pager adapter that represents several Fragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new SourcesFragment();
                case 1:
                    return new PrimaryFragment();
                case 2:
                    return new SecondaryFragment();
                case 3:
                    return new SummaryFragment();
                default:
                    throw new IllegalStateException("Invalid fragment position");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
