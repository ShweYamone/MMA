package com.freelance.solutionhub.mma.fragment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
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

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.CMCompletionActivity;
import com.freelance.solutionhub.mma.activity.PMCompletionActivity;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.VerificationReturnBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.JOBDONE;

public class Third_Step_CM_Fragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.et_remarks)
    EditText remarks;

    @BindView(R.id.btn_job_done)
    Button jobDone;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.btn_verify)
    Button btnVerify;

    private RadioButton radioButton;
    private String weather;
    private Date date;
    private SharePreferenceHelper mSharePreferenceHelper;
    private Network network;
    private InitializeDatabase dbHelper;
    private ApiInterface apiInterface;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private String actualDateTime;
    private String remarksString;

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
        mSharePreferenceHelper = new SharePreferenceHelper(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());
        network = new Network(getContext());
        apiInterface = ApiClient.getClient(getContext());

        jobDone.setOnClickListener(this);
        btnVerify.setOnClickListener(this);

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
            case R.id.btn_verify:

                if (!mSharePreferenceHelper.userClickStepOneOrNot() && !mSharePreferenceHelper.userClickStepTwoOrNot() ) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.warning)
                            .setTitle("Unsaved Works")
                            .setMessage("You need to save works in Step One and Two.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }

                            })
                            .show();
                } else if (!mSharePreferenceHelper.userClickStepOneOrNot()) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.warning)
                            .setTitle("Unsaved Works")
                            .setMessage("You need to save works in Step One.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }

                            })
                            .show();
                } else if (!mSharePreferenceHelper.userClickStepTwoOrNot()) {
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.warning)
                            .setTitle("Unsaved Works")
                            .setMessage("You need to save works in Step Two.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }

                            })
                            .show();
                } else {


                    Call<VerificationReturnBody> call = apiInterface.verifyWorks("Bearer " + mSharePreferenceHelper.getToken(), "CM20205B6923C2");
                    call.enqueue(new Callback<VerificationReturnBody>() {
                        @Override
                        public void onResponse(Call<VerificationReturnBody> call, Response<VerificationReturnBody> response) {
                            if (response.isSuccessful()) {
                                VerificationReturnBody verificationReturnBody = response.body();
                                if (verificationReturnBody.isFault_resolved()) {
                                    jobDone.setClickable(true);
                                    jobDone.setBackground(getResources().getDrawable(R.drawable.round_rect_shape_button));
                                } else {
                                    Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                                    jobDone.setClickable(false);
                                    jobDone.setBackground(getResources().getDrawable(R.drawable.round_rectangle_shape_button_grey));
                                }
                            }
                            else {
                                Toast.makeText(getContext(), response.code() + "", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<VerificationReturnBody> call, Throwable t) {

                        }
                    });
                }


                break;


            case R.id.btn_job_done :
                updateEvent();
                Intent intent = new Intent(this.getContext(), CMCompletionActivity.class);
                intent.putExtra("start_time", getArguments().getString("start_time"));
                intent.putExtra("end_time", actualDateTime);
                intent.putExtra("acknowledge_time", pmServiceInfoDetailModel.getAcknowledgementDate());
                intent.putExtra("remarks", remarksString);
                startActivity(intent);
                getActivity().finish();
        }
    }


    public void updateEvent(){
        remarksString = "";
        if(remarks.getText() != null)
            remarksString = remarks.getText().toString();
        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        UpdateEventBody updateEventBody;
        if (network.isNetworkAvailable()) {
            updateEventBody = new
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
                    if(response.isSuccessful()) {
                        Toast.makeText(getContext(), returnStatus.getStatus(), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {

                }
            });
        } else {
            updateEventBody = new UpdateEventBody(
                    mSharePreferenceHelper.getUserName(),
                    mSharePreferenceHelper.getUserId(),
                    actualDateTime,
                    pmServiceInfoDetailModel.getId()
            );
            updateEventBody.setServiceOrderStatus(JOBDONE);
            updateEventBody.setRemark(remarksString);
            updateEventBody.setWeatherCondition(weather);
            updateEventBody.setId(mSharePreferenceHelper.getUserId() + pmServiceInfoDetailModel.getId() + actualDateTime);
            dbHelper.updateEventBodyDAO().insert(updateEventBody);
        }

    }

}