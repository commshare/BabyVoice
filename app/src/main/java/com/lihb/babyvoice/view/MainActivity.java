package com.lihb.babyvoice.view;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.lihb.babyvoice.R;
import com.lihb.babyvoice.action.ApiManager;
import com.lihb.babyvoice.action.ServiceGenerator;
import com.lihb.babyvoice.adapter.ContributorAdapter;
import com.lihb.babyvoice.model.Contributor;

import java.util.ArrayList;
import java.util.List;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    private SwipeRefreshLayout mSwipeRefreshLayout = null;

    private RecyclerView mRecyclerView = null;

    private ContributorAdapter mAdapter = null;

    private ProgressBar mProgressBar = null;

    private ArrayList<Contributor> mData = new ArrayList<Contributor>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layoyt);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        mAdapter = new ContributorAdapter(this, mData);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
//        mRecyclerView.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);


        ServiceGenerator.createService(ApiManager.class)
                .contributors("square", "retrofit")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Contributor>>() {
                    @Override
                    public void onCompleted() {
                        Log.i("MainActivity", "onCompleted");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i("MainActivity", "onError");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(List<Contributor> contributors) {
                        mProgressBar.setVisibility(View.GONE);
                        Log.i("MainActivity ", "size = " + contributors.size());
                        mAdapter.updateData(contributors);
                    }

                });

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
