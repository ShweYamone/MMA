package com.freelance.solutionhub.mma.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.PMCompletionActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Second_Step_PM_Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btnJobDone)
    Button btnJobDone;

    public Second_Step_PM_Fragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second__step__p_m_, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        btnJobDone.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnJobDone :
                Intent intent = new Intent(this.getContext(), PMCompletionActivity.class);
                startActivity(intent);
        }
    }
}