package com.example.easy.bucketdrops.widgets;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.easy.bucketdrops.R;

import javax.annotation.Nullable;

public class DialogMark extends DialogFragment {
    private ImageButton mBtnClose;
    private Button mBtnCompleted;
    private View.OnClickListener mBtnClicklistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_completed:
                    break;

            }
            dismiss();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_mark, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBtnClose = (ImageButton) view.findViewById(R.id.btn_close);
        mBtnCompleted = (Button) view.findViewById(R.id.btn_completed);
        mBtnClose.setOnClickListener(mBtnClicklistener);
        mBtnCompleted.setOnClickListener(mBtnClicklistener);

        Bundle arguments = getArguments();
        if (arguments != null) {
            int position = arguments.getInt("POSITION");
            Toast.makeText(getActivity(), "position " + position,Toast.LENGTH_SHORT).show();
        }

    }
}
