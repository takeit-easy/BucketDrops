package com.example.easy.bucketdrops;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.example.easy.bucketdrops.adapters.AdapterDrops;
import com.example.easy.bucketdrops.adapters.AddListener;
import com.example.easy.bucketdrops.adapters.CompleteListener;
import com.example.easy.bucketdrops.adapters.Divider;
import com.example.easy.bucketdrops.adapters.Filter;
import com.example.easy.bucketdrops.adapters.MarkListener;
import com.example.easy.bucketdrops.adapters.SimpleTouchCallback;
import com.example.easy.bucketdrops.beans.Drop;
import com.example.easy.bucketdrops.widgets.BucketRecyclerView;
import com.example.easy.bucketdrops.widgets.DialogMark;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class ActivityMain extends AppCompatActivity{

    Toolbar mToolbar;
    Button mBtnAdd;
    BucketRecyclerView mRecycler;
    Realm mRealm;
    RealmResults<Drop> mResults;
    View mEmptyView;

    AdapterDrops mAdapter;

    private AddListener mAddListener = new AddListener() {
        @Override
        public void add() {
            showDialogAdd();
        }
    };

    private View.OnClickListener mBtnAddListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            showDialogAdd();
        }
    };

    private RealmChangeListener mChangeListener  = new RealmChangeListener() {
        @Override
        public void onChange(Object o) {
            mAdapter.update(mResults);
        }
    };

    private MarkListener mMarkListener = new MarkListener() {
        @Override
        public void onMark(int position) {
            showDialogMark(position);
        }
    };

    private CompleteListener mCompletelistener = new CompleteListener() {
        @Override
        public void onComplete(int position) {
            mAdapter.markComplete(position);
        }
    };

    private void showDialogAdd() {
        DialogAdd dialog = new DialogAdd();
        dialog.show(getSupportFragmentManager(),"Add");
    }

    private void showDialogMark(int position) {
        DialogMark dialog = new DialogMark();
        Bundle bundle = new Bundle();
        bundle.putInt("POSITION", position);
        dialog.setArguments(bundle);
        dialog.setCompleteListener(mCompletelistener);
        dialog.show(getSupportFragmentManager(),"Mark");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRealm = Realm.getDefaultInstance();
        int filterOption = AppBucketDrops.load(this);
        loadResults(filterOption);
        mResults = mRealm.where(Drop.class).findAll();
        mToolbar = findViewById(R.id.toolbar);
        mBtnAdd = findViewById(R.id.btn_add);
        mBtnAdd.setOnClickListener(mBtnAddListener);
        mRecycler = findViewById(R.id.rv_drops);
        mRecycler.addItemDecoration(new Divider(this, LinearLayoutManager.VERTICAL));
        mRecycler.setItemAnimator(new DefaultItemAnimator());
        mAdapter = new AdapterDrops(this, mRealm, mResults, mAddListener, mMarkListener);
        mAdapter.setHasStableIds(true);
        mAdapter.setAddListener(mAddListener);
        mEmptyView = findViewById(R.id.empty_drops);
        mRecycler.hideIfEmpty(mToolbar);
        mRecycler.showIfEmpty(mEmptyView);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(manager);
        mRecycler.setAdapter(mAdapter);
        setSupportActionBar(mToolbar);
        SimpleTouchCallback callback = new SimpleTouchCallback(mAdapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecycler);
        initBackgroundImage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        boolean handled = true;
        int filterOption = Filter.NONE;
        switch (id) {
            case R.id.action_add:
                showDialogAdd();
                break;
            case R.id.action_sort_ascending_date:
                filterOption = Filter.LEAST_TIME_LEFT;
                break;
            case R.id.acrion_sort_discending_date:
                filterOption = Filter.MOST_TIME_LEFT;
                break;
            case R.id.acrion_show_complete:
                filterOption = Filter.COMPLETE;
                break;
            case R.id.action_show_incomplete:
                filterOption = Filter.INCOMPLETE;
                break;
            default:
                handled = false;
                break;
        }
        loadResults(filterOption);
        AppBucketDrops.save(this, filterOption);
        return handled;
    }

    private void initBackgroundImage() {
        ImageView background = findViewById(R.id.iv_background);
        Glide.with(this)
                .load(R.drawable.background)
                .centerCrop()
                .into(background);
    }

    private void loadResults(int filterOption) {
        switch (filterOption) {
            case Filter.NONE:
                break;
            case Filter.LEAST_TIME_LEFT:
                mResults = mRealm.where(Drop.class).sort("when").findAll();
                break;
            case Filter.MOST_TIME_LEFT:
                mResults = mRealm.where(Drop.class).sort("when", Sort.DESCENDING).findAll();
                break;
            case Filter.COMPLETE:
                mResults = mRealm.where(Drop.class).equalTo("completed", true).findAllAsync();
                break;
            case Filter.INCOMPLETE:
                mResults = mRealm.where(Drop.class).equalTo("completed", false).findAllAsync();
                break;
        }
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mResults.addChangeListener(mChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mResults.removeChangeListener(mChangeListener);
    }

}
