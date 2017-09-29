package com.academy.ndvalkov.mediamonitoringapp.articles;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;
import com.academy.ndvalkov.mediamonitoringapp.common.BusProvider;
import com.academy.ndvalkov.mediamonitoringapp.common.events.articles.OpenSelectEvent;

public class ArticlesActivity extends BaseActivity {
    private static final String TAG = ArticlesActivity.class.getSimpleName();

    private ArticlesFragment mArticlesFragment;
    private ImageButton mConfigButton;

    private View.OnClickListener mToolbarButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.action_config:
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

        mArticlesFragment = new ArticlesFragment();

        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();

        trans.replace(R.id.container_articles, mArticlesFragment);
        trans.commit();
    }

    public void openConfigDialog() {
        if (mArticlesFragment.isAdded()) {
            BusProvider.getInstance().post(new OpenSelectEvent(true));
        }
    }
}
