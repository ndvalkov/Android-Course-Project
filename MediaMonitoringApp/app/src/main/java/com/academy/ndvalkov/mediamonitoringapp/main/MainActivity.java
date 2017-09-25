package com.academy.ndvalkov.mediamonitoringapp.main;

import android.os.Bundle;

import com.academy.ndvalkov.mediamonitoringapp.BaseActivity;
import com.academy.ndvalkov.mediamonitoringapp.R;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDrawerNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}