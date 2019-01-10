package com.example.easy.bucketdrops.widgets;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;

import com.example.easy.bucketdrops.extras.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BucketRecyclerView extends RecyclerView {
    private List<View> mNonEmptyViews = Collections.emptyList();
    private List<View> mEmptyViews = Collections.emptyList();
    private AdapterDataObserver mObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            toggleViews();
        }
        
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            toggleViews();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            toggleViews();
        }
    };

    private void toggleViews() {
        if (getAdapter()!=null && !mEmptyViews.isEmpty() && !mNonEmptyViews.isEmpty()) {
            if (getAdapter().getItemCount() == 0) {

                //show all the empty views
                Util.showViews(mEmptyViews);

                //hide the RecyclerView
                setVisibility(View.GONE);

                //hide all the views which are meant to be hidden
                Util.hideViews(mNonEmptyViews);
            }
            else {
                //hide all the empty views
                Util.hideViews(mEmptyViews);

                //show the RecyclerView
                setVisibility(View.VISIBLE);

                //show all the views which are meant to be shown
                Util.showViews(mNonEmptyViews);
            }

        }
    }

    public BucketRecyclerView(@NonNull Context context) {
        super(context);
    }

    public BucketRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BucketRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);
        if (adapter!=null) {
            adapter.registerAdapterDataObserver(mObserver);
        }
        mObserver.onChanged();

    }

    public void hideIfEmpty(View ...views) {
        mNonEmptyViews = Arrays.asList(views);

    }

    public void showIfEmpty(View ...emptyViews) {
        mEmptyViews = Arrays.asList(emptyViews);
    }
}
