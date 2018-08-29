package sapphyx.gsd.com.drywall.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.prof.rssparser.Article;
import com.prof.rssparser.Parser;

import java.util.ArrayList;

import sapphyx.gsd.com.drywall.R;
import sapphyx.gsd.com.drywall.adapters.CategoryAdapter;
import sapphyx.gsd.com.drywall.util.AnimationHelper;
import sapphyx.gsd.com.drywall.util.GridSpacingItemDecoration;

/**
 * Created by ry on 2/14/18.
 */

public class CollectionsOne extends AppCompatActivity {

    private Toolbar toolbar;

    private RecyclerView.LayoutManager mLayoutManager;

    private RecyclerView mRecyclerView;
    private CategoryAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressBar progressBar;
    private String urlString = "https://raw.githubusercontent.com/rgocal/Drywall/Drywall2/CnL.rss";

    private static final int DEFAULT_COLUMNS_PORTRAIT = 2;
    private static final int DEFAULT_COLUMNS_LANDSCAPE = 3;

    private int mColumnCount;
    private int numColumns = 1;

    private boolean isLandscape() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
    }

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.collections_layout);

        toolbar = findViewById(R.id.toolbar);

        AppCompatActivity activity = this;
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle("CnL Photography");
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


            final boolean isLandscape = isLandscape();
            int mColumnCountPortrait = DEFAULT_COLUMNS_PORTRAIT;
            int mColumnCountLandscape = DEFAULT_COLUMNS_LANDSCAPE;
            int newColumnCount = isLandscape ? mColumnCountLandscape : mColumnCountPortrait;
            if (mColumnCount != newColumnCount) {
                mColumnCount = newColumnCount;
                numColumns = mColumnCount;
            }

            progressBar = findViewById(R.id.progressBar);
            mRecyclerView = findViewById(R.id.list);

            mLayoutManager = new GridLayoutManager(CollectionsOne.this, numColumns);

            mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(numColumns, dpToPx(10), true));

            AnimationHelper.quickViewReveal(findViewById(R.id.list), 400);

            mSwipeRefreshLayout = findViewById(R.id.container);
            mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorPrimaryDark);
            mSwipeRefreshLayout.canChildScrollUp();
            mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

                @Override
                public void onRefresh() {

                    mAdapter.clearData();
                    mAdapter.notifyDataSetChanged();
                    mSwipeRefreshLayout.setRefreshing(true);
                    loadFeed();
                }
            });

            if (!isNetworkAvailable()) {

                AlertDialog.Builder builder = new AlertDialog.Builder(CollectionsOne.this);
                builder.setMessage(R.string.alert_message)
                        .setTitle(R.string.alert_title)
                        .setCancelable(false)
                        .setPositiveButton(R.string.alert_positive,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        finish();
                                    }
                                });

                AlertDialog alert = builder.create();
                alert.show();

            } else if (isNetworkAvailable()) {
                loadFeed();
            }

        }

        public void loadFeed() {

            if (!mSwipeRefreshLayout.isRefreshing())
                progressBar.setVisibility(View.VISIBLE);

            Parser parser = new Parser();
            parser.execute(urlString);
            parser.onFinish(new Parser.OnTaskCompleted() {
                //what to do when the parsing is done
                @Override
                public void onTaskCompleted(ArrayList<Article> list) {
                    //list is an Array List with all article's information
                    //set the adapter to recycler view
                    mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                    mAdapter = new CategoryAdapter(list, R.layout.collection_category_item, CollectionsOne.this);
                    mRecyclerView.setHasFixedSize(true);
                    mRecyclerView.setAdapter(mAdapter);
                    mRecyclerView.setLayoutManager(mLayoutManager);

                    progressBar.setVisibility(View.GONE);
                    mSwipeRefreshLayout.setRefreshing(false);

                }

                //what to do in case of error
                @Override
                public void onError() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.GONE);
                            mSwipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(CollectionsOne.this, "Unable to load data.",
                                    Toast.LENGTH_LONG).show();
                            Log.i("Unable to load ", "articles");
                        }
                    });
                }
            });
        }


        public boolean isNetworkAvailable() {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }

        @Override
        public void onResume() {
            super.onResume();
            if (mAdapter != null)
                mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDestroy() {

            super.onDestroy();
            if (mAdapter != null)
                mAdapter.clearData();
        }

        private int dpToPx(int dp) {
            Resources r = getResources();
            return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}

