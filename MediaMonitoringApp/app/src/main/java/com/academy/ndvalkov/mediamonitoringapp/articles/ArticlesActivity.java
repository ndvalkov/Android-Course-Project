package com.academy.ndvalkov.mediamonitoringapp.articles;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.main.MainFragment;

public class ArticlesActivity extends BaseActivity {
    private static final String TAG = ArticlesActivity.class.getSimpleName();

    private MainFragment mArticlesFragment;
    private ImageButton mConfigButton;

    private View.OnClickListener mToolbarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_filter:
                    openConfigDialog();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        setupDrawerNavigation();

        // Register for events from other classes and threads
        BusProvider.getInstance().register(this);

        /**
         * Toolbar action buttons.
         */
        mConfigButton = (ImageButton) findViewById(R.id.action_config);
        mConfigButton.setOnClickListener(mToolbarButtonListener);

//        mMainFragment = new MainFragment();
//
//        FragmentTransaction trans = getSupportFragmentManager()
//                .beginTransaction();
//
//        trans.replace(R.id.container_main, mMainFragment);
//        trans.commit();
    }

    private void openConfigDialog() {
//        if (mMainFragment.isAdded()) {
//            BusProvider.getInstance().post(new FilterOpenEvent(true));
//        }
    }
}
