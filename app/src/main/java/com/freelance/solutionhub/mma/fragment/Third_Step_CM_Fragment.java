package com.freelance.solutionhub.mma.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class Third_Step_CM_Fragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.et_remarks)
    EditText remarks;

    @BindView(R.id.btn_job_done)
    Button jobDone;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    private RadioButton radioButton;
    private String weather;
    private Date date;
    private SharePreferenceHelper mSharePreferenceHelper;
    private ApiInterface apiInterface;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;

    public Third_Step_CM_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third__step__c_m_, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        mSharePreferenceHelper = new SharePreferenceHelper(this.getContext());
        apiInterface = ApiClient.getClient(this.getContext());
        jobDone.setOnClickListener(this);

        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(selectedId);
        weather = radioButton.getText().toString();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId)
                                                  {
                                                      radioButton = (RadioButton) view.findViewById(checkedId);
                                                      weather = radioButton.getText().toString();
                                                  }
                                              }
        );

        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_job_done :
                updateEvent(view);
                Intent intent = new Intent(this.getContext(), PMCompletionActivity.class);
                startActivity(intent);
                getActivity().finish();
        }
    }


    public void updateEvent(View view){
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
                remarksString,
                weather);
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