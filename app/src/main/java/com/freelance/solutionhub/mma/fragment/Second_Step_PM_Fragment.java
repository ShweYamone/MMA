package com.freelance.solutionhub.mma.fragment;

import android.app.usage.EventStats;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
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

public class Second_Step_PM_Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btnJobDone)
    Button btnJobDone;

    @BindView(R.id.et_remarks)
    EditText remarks;

    @BindView(R.id.btn_verify)
    Button verify;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePreferenceHelper;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private Date date;
    private Network network;
    private InitializeDatabase dbHelper;
    private String actualDateTime;
    private String remarksString;

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
        network = new Network(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());

        btnJobDone.setClickable(false);
        btnJobDone.setOnClickListener(this);
        verify.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                if(mSharePreferenceHelper.userClickPMStepOneOrNot()){
                    Call<VerificationReturnBody> call = apiInterface.verifyWorks("Bearer " + mSharePreferenceHelper.getToken(), "CM20205B6923C2");
                    call.enqueue(new Callback<VerificationReturnBody>() {
                        @Override
                        public void onResponse(Call<VerificationReturnBody> call, Response<VerificationReturnBody> response) {
                            if (response.isSuccessful()) {
                                VerificationReturnBody verificationReturnBody = response.body();
                                if (verificationReturnBody.isFault_resolved()) {
                                    btnJobDone.setClickable(true);
                                    btnJobDone.setBackground(getResources().getDrawable(R.drawable.round_rect_shape_button));
                                } else {
                                    Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                                    btnJobDone.setClickable(false);
                                    btnJobDone.setBackground(getResources().getDrawable(R.drawable.round_rectangle_shape_button_grey));
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
                }else {
                    new AlertDialog.Builder(this.getContext())
                            .setIcon(R.drawable.warning)
                            .setTitle("Unsaved Work")
                            .setMessage("You need to save works in Step 1.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }

                            })
                            .show();
                }
                break;
            case R.id.btnJobDone :
                updateEvent();
                Intent intent = new Intent(this.getContext(), PMCompletionActivity.class);
                intent.putExtra("start_time", getArguments().getString("start_time"));
                intent.putExtra("end_time", actualDateTime);
                intent.putExtra("remarks", remarksString);
                startActivity(intent);
                getActivity().finish();
        }
    }

    public void updateEvent(){
        remarksString = "";
        if(!remarks.getText().equals("") || remarks.getText() != null)
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
                    JOBDONE,
                    remarksString);


            Call<ReturnStatus> jobDoneCall = apiInterface.updateStatusEvent("Bearer "+mSharePreferenceHelper.getToken(), updateEventBody);
            jobDoneCall.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                    ReturnStatus returnStatus = response.body();
                    if(response.isSuccessful()) {
                        Toast.makeText(getContext(),returnStatus.getStatus(),Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {

                }
            });
        } else {
            updateEventBody = new UpdateEventBody(
              mSharePreferenceHelper.getUserName(), mSharePreferenceHelper.getUserId(), actualDateTime, pmServiceInfoDetailModel.getId()
            );
            updateEventBody.setServiceOrderStatus(JOBDONE);
            updateEventBody.setRemark(remarksString);
            updateEventBody.setId(mSharePreferenceHelper.getUserId() + pmServiceInfoDetailModel.getId() + actualDateTime);
            dbHelper.updateEventBodyDAO().insert(updateEventBody);
        }



    }
}