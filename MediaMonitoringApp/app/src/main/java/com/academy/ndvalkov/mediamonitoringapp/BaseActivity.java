package com.academy.ndvalkov.mediamonitoringapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.academy.ndvalkov.mediamonitoringapp.common.DialogFactory;
import com.academy.ndvalkov.mediamonitoringapp.main.MainActivity;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    private static final int PERMISSIONS_REQUEST_WRITE_EXTERNAL = 10523;

    public static boolean isActivityStarted;

    protected Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // http://stackoverflow.com/questions/23703778/exit-android-application-programmatically
        if (getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXIT", false)) {
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }

        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG, "Permission is granted");
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_WRITE_EXTERNAL);
            }
            // PERMISSIONS_REQUEST_WRITE_EXTERNAL is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        } else {
            // Permission is automatically granted on sdk<23 upon installation.


        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (isTaskRoot()) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setMessage(getString(R.string.msg_app_exit))
                    .setNegativeButton(android.R.string.no, null)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    }).create().show();

        } else {
            super.onBackPressed();
        }

    }

    /**
     * To be called after setContentView() in the child Activities.
     */
    protected void setupDrawerNavigation() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_action_open, R.string.navigation_action_close);
        assert drawer != null;
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        mToolbar.setNavigationIcon(R.drawable.xh_ic_nav);
    }

    public void startActivity(View v) {
        isActivityStarted = true;
        switch (v.getId()) {
            case R.id.main:
                new DelayActivityTransitionAsyncTask(MainActivity.class).execute();
                break;
//            case R.id.articles:
//                new DelayActivityTransitionAsyncTask(ArticlesActivity.class).execute();
//                break;
//            case R.id.profile:
//                new DelayActivityTransitionAsyncTask(ProfileActivity.class).execute();
//                break;
//            case R.id.monitor:
//                new DelayActivityTransitionAsyncTask(MonitorActivity.class).execute();
//                break;
        }
    }

    protected void setCustomToolbarTitle(int resId) {
        if (mToolbar != null) {
            ((TextView) mToolbar.findViewById(R.id.)).setText(resId);
        }


    }

    /**
     * Ensure smooth transition between the drawer closing, and the new Activity opening.
     * Delay + override of the default transition animation.
     */
    private class DelayActivityTransitionAsyncTask extends AsyncTask<Object, Void, Void> {
        private Class<?> selectedActivity;

        public DelayActivityTransitionAsyncTask(Class<?> activity) {
            super();
            selectedActivity = activity;
        }

        @Override
        protected Void doInBackground(Object... params) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (selectedActivity.equals(MainActivity.class)) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.animator.enter, R.animator.exit);
                finish();
//            } else if (selectedActivity.equals(ArticlesActivity.class)) {
//                startActivity(new Intent(getApplicationContext(), ArticlesActivity.class));
//                overridePendingTransition(R.animator.enter, R.animator.exit);
//                finish();
//            } else if (selectedActivity.equals(ProfileActivity.class)) {
//                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
//                overridePendingTransition(R.animator.enter, R.animator.exit);
//                finish();
//            } else if (selectedActivity.equals(MonitorActivity.class)) {
//                startActivity(new Intent(getApplicationContext(), MonitorActivity.class));
//                overridePendingTransition(R.animator.enter, R.animator.exit);
//                finish();
//            }
        }
    }

    public boolean isLargeScreen() {
        if ((getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {
            return true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_WRITE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted ...
                } else {
                    DialogFactory.DialogParams dlgParams = new DialogFactory.DialogParams();
                    dlgParams.setTitle()
                            .setIcon(this.getResources().getDrawable(android.R.drawable.stat_sys_warning))
                            .setContent("This application needs read/write permissions to operate with local files.")
                            .setOKButton(true);
                    final Dialog dlg = new DialogFactory(this).createDialog(dlgParams);
                    dlg.findViewById(R.id.okButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dlg.dismiss();
                        }
                    });
                }

                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
