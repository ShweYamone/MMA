package com.freelance.solutionhub.mma.fragment;

import android.app.usage.EventStats;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.PMCompletionActivity;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Second_Step_PM_Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btnJobDone)
    Button btnJobDone;

    @BindView(R.id.et_remarks)
    EditText remarks;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePreferenceHelper;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private Date date;
    public Second_Step_PM_Fragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));
        View view = inflater.inflate(R.layout.fragment_second__step__p_m_, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        apiInterface = ApiClient.getClient(this.getContext());
        mSharePreferenceHelper = new SharePreferenceHelper(this.getContext());
        btnJobDone.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnJobDone :
                updateEvent();
                Intent intent = new Intent(this.getContext(), PMCompletionActivity.class);
                startActivity(intent);
                getActivity().finish();
        }
    }

    public void updateEvent(){
        String remarksString = "";
        if(remarks.getText() != null)
            remarksString = remarks.getText().toString();
            date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        UpdateEventBody updateEventBody = new
                UpdateEventBody(mSharePreferenceHelper.getUserName(),
                mSharePreferenceHelper.getUserId(),
                actualDateTime,
                pmServiceInfoDetailModel.getId(),
                "JOBDONE",
                remarksString);
        Call<ReturnStatus> jobDoneCall = apiInterface.updateStatusEvent("Bearer "+mSharePreferenceHelper.getToken(), updateEventBody);
        jobDoneCall.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                ReturnStatus returnStatus = response.body();
                if(response.isSuccessful())
                    Toast.makeText(getContext(),returnStatus.getStatus(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {

            }
        });

    }
}