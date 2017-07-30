package com.example.android.quakereport;


import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.os.Handler;
import android.widget.Toast;

import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks<List<Earthquake>> {

    public static final String LOG_TAG = MainActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query";

    private static String getDefaultEndDate() {
        java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH);
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);
        String date = year + "-" + month + "-" + day;
        return date;
    }

    private static String getDefaultStartDate() {
        String date;
        java.util.Calendar c = java.util.Calendar.getInstance();
        int year = c.get(java.util.Calendar.YEAR);
        int month = c.get(java.util.Calendar.MONTH);
        int day = c.get(java.util.Calendar.DAY_OF_MONTH);
        if (month == 1) {
            month = 12;
            year--;
        }
        date = year + "-" + month + "-" + day;
        return date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);

        if (isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(0, null, MainActivity.this);
        } else {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.empty);
            textView.setText(R.string.no_internet);
        }

        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeView.setEnabled(false);
        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                TextView textView = (TextView) findViewById(R.id.empty);
                ListView listView = (ListView) findViewById(R.id.list);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
                listView.setVisibility(View.GONE);
                if (isConnected()) {
                    progressBar.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(0, null, MainActivity.this);
                } else {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.no_internet);
                    swipeView.setRefreshing(false);
                }
            }
        });

        ListView listView = (ListView) findViewById(R.id.list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView absListView, int i) {
                }

                @Override
                public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem == 0)
                        swipeView.setEnabled(true);
                    else
                        swipeView.setEnabled(false);
                }
            });
        }
    }

    public boolean isConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return (activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUi(final List<Earthquake> earthquakes) {
        final SwipeRefreshLayout swipeView = (SwipeRefreshLayout) findViewById(R.id.swipe);
        if (swipeView.isRefreshing()) {
            swipeView.setRefreshing(false);
        }

        EarthquakeAdapter adapter = new EarthquakeAdapter(this, earthquakes);
        ListView earthquakeListView = (ListView) findViewById(R.id.list);
        earthquakeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String link = earthquakes.get(position).getWebLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                }
            }
        });

        TextView emptyView = (TextView) findViewById(R.id.empty);
        earthquakeListView.setEmptyView(emptyView);
        earthquakeListView.setAdapter(adapter);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        earthquakeListView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {
        Log.v(LOG_TAG, "onCreateLoader called...");

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String minMagnitude = sharedPrefs.getString(
                getString(R.string.settings_min_magnitude_key),
                getString(R.string.settings_min_magnitude_default));

        String orderBy = sharedPrefs.getString(
                getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default)
        );

        String earthquakeLimitation = sharedPrefs.getString(
                getString(R.string.setting_earthquake_limit_key),
                getString(R.string.setting_earthquake_limit_default));

        String startTime = sharedPrefs.getString(
                getString(R.string.setting_start_time_key),
                getString(R.string.setting_start_time_default));

        String endTime = sharedPrefs.getString(
                getString(R.string.setting_end_time_key),
                getDefaultEndDate());

        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("format", "geojson");
        uriBuilder.appendQueryParameter("limit", earthquakeLimitation);
        uriBuilder.appendQueryParameter("minmag", minMagnitude);
        uriBuilder.appendQueryParameter("orderby", orderBy);
        uriBuilder.appendQueryParameter("starttime", startTime);
        uriBuilder.appendQueryParameter("endtime", endTime);

        return new EarthquakeLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> data) {
        Log.v(LOG_TAG, "onLoadFinished called...");
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        TextView emptyMessage = (TextView) findViewById(R.id.empty);
        emptyMessage.setText(R.string.empty_message);
        if (data != null && !data.isEmpty()) {
            updateUi(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.v(LOG_TAG, "onLoaderReset called...");
    }
}
