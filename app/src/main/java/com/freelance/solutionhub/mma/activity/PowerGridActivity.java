package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.DateTimePickerAdapter;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.ACTION_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.ACTION_TAKEN;
import static com.freelance.solutionhub.mma.util.AppConstant.CLEARANCE_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_TWO;
import static com.freelance.solutionhub.mma.util.AppConstant.CT_PERSONNEL;
import static com.freelance.solutionhub.mma.util.AppConstant.EXPECTED_COMPLETION_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_DETECTED_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_STATUS;
import static com.freelance.solutionhub.mma.util.AppConstant.NO;
import static com.freelance.solutionhub.mma.util.AppConstant.OFFICER;
import static com.freelance.solutionhub.mma.util.AppConstant.POWER_GRIP_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.REFER_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.REMARKS_ON_FAULT;
import static com.freelance.solutionhub.mma.util.AppConstant.TELCO_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.THIRD_PARTY_NUMBER;
import static com.freelance.solutionhub.mma.util.AppConstant.YES;
import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class PowerGridActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_power_grid_no)
    EditText powerGridNo;

    @BindView(R.id.et_ct_personnel)
    EditText ctPersonnel;

    @BindView(R.id.btn_refer_date_time)
    Button referDateTime;

    @BindView(R.id.btn_expected_completion_date_time)
    Button expectedCompletionDateTime;

    @BindView(R.id.btn_clearance_date_time)
    Button clearanceDateTime;

    @BindView(R.id.et_power_grid_fault_status)
    EditText powerGridFaultStatus;

    @BindView(R.id.et_remarks_on_telco_fault)
    EditText remarksOnTelcoFault;

    @BindView(R.id.et_power_grid_officer)
    EditText powerGridOfficer;

    @BindView(R.id.btn_fault_detected_date_time)
    Button faultDetectedDateTime;

    @BindView(R.id.btn_action_date_time)
    Button actionDateTime;

    @BindView(R.id.et_action_taken)
    EditText actionTaken;

    @BindView(R.id.btn_save)
    Button save;

    @BindView(R.id.progress_bar)
    RelativeLayout progressBar;

    private SharePreferenceHelper mSharePreferenceHelper;
    private ApiInterface apiInterface;

    private Network network;
    private InitializeDatabase dbHelper;
    private ArrayList<Event> eventLists;
    private Date date;
    private String cmID;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;
    private boolean isMandatoryFieldLeft = false;
    private String mandatoryFieldsLeft = "";

    private String prePowerGridNo = "";
    private String preCTpersonnel = "";
    private String preReferDate = "";
    private String preExpectedCompletionDate = "";
    private String preClearanceDate = "";
    private String preFaultStatus = "";
    private String preRemarks = "";
    private String preOfficer = "";
    private String preFaultDetectedDate = "";
    private String preActionDate = "";
    private String preActionTaken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_power_grid);

        ButterKnife.bind(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.setLock(false);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        eventLists = new ArrayList<>();
        apiInterface = ApiClient.getClient(this);
        mSharePreferenceHelper = new SharePreferenceHelper(this);
        network = new Network(this);
        dbHelper = InitializeDatabase.getInstance(this);
        date = new Date();
        setDateTimeButton();
        if(getIntent().hasExtra("id")){
            cmID = getIntent().getStringExtra("id");
        }else {
            finish();
        }
        //Click
        referDateTime.setOnClickListener(this);
        expectedCompletionDateTime.setOnClickListener(this);
        clearanceDateTime.setOnClickListener(this);
        faultDetectedDateTime.setOnClickListener(this);
        actionDateTime.setOnClickListener(this);
        save.setOnClickListener(this);

        if (dbHelper.eventDAO().getNumOfEventsByEventType(POWER_GRIP_UPDATE) > 0) {
            displaySavedData();
        }

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(PowerGridActivity.this, "user is inactive from last 5 minute",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(PowerGridActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
    }

    private void displaySavedData() {
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, THIRD_PARTY_NUMBER) > 0) {
            prePowerGridNo = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, THIRD_PARTY_NUMBER);
            powerGridNo.setText(prePowerGridNo);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, CT_PERSONNEL) > 0){
            preCTpersonnel = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, CT_PERSONNEL);
            ctPersonnel.setText(preCTpersonnel);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, REFER_DATE) > 0) {
            preReferDate = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, REFER_DATE);referDateTime.setText(preReferDate);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, EXPECTED_COMPLETION_DATE) > 0) {
            preExpectedCompletionDate = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, EXPECTED_COMPLETION_DATE);expectedCompletionDateTime.setText(preExpectedCompletionDate);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, CLEARANCE_DATE) > 0) {
            preClearanceDate = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, CLEARANCE_DATE); clearanceDateTime.setText(preClearanceDate);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, FAULT_STATUS) > 0) {
            preFaultStatus = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, FAULT_STATUS); powerGridFaultStatus.setText(preFaultStatus);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, REMARKS_ON_FAULT) > 0) {
            preRemarks = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, REMARKS_ON_FAULT); remarksOnTelcoFault.setText(preRemarks);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, OFFICER) > 0) {
            preOfficer = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, OFFICER); powerGridOfficer.setText(preOfficer);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, FAULT_DETECTED_DATE) > 0) {
            preFaultDetectedDate = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, FAULT_DETECTED_DATE); faultDetectedDateTime.setText(preFaultDetectedDate);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, ACTION_DATE) > 0) {
            preActionDate = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, ACTION_DATE); actionDateTime.setText(preActionDate);
        }
        if (dbHelper.eventDAO().getEventValueCount(POWER_GRIP_UPDATE, ACTION_TAKEN) > 0) {
            preActionTaken = dbHelper.eventDAO().getEventValue(POWER_GRIP_UPDATE, ACTION_TAKEN); actionTaken.setText(preActionTaken);

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        sharePreferenceHelper.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
      //  Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
        stopHandler();//stop first and then start
        if (startHandler)
            startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, user_inactivity_time); //for 3 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharePreferenceHelper.getLock()) {
            Intent intent = new Intent(PowerGridActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_refer_date_time:
                new DateTimePickerAdapter(this,referDateTime).pickDateTime();
                break;
            case R.id.btn_expected_completion_date_time:
                new DateTimePickerAdapter(this,expectedCompletionDateTime).pickDateTime();
                break;
            case R.id.btn_clearance_date_time:
                new DateTimePickerAdapter(this, clearanceDateTime).pickDateTime();
                break;
            case R.id.btn_fault_detected_date_time:
                new DateTimePickerAdapter(this, faultDetectedDateTime).pickDateTime();
                break;
            case R.id.btn_action_date_time:
                new DateTimePickerAdapter(this,actionDateTime).pickDateTime();
                break;
            case R.id.btn_save:
                save();
                break;
        }
    }

    /**
     *   Set date time button to current time
     */
    private void setDateTimeButton(){
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        referDateTime.setText(actualDateTime);
        expectedCompletionDateTime.setText(actualDateTime);
        clearanceDateTime.setText(actualDateTime);
        faultDetectedDateTime.setText(actualDateTime);
        actionDateTime.setText(actualDateTime);

    }

    /**
     * Upload events
     */
    public void save(){

        isMandatoryFieldLeft = false;
        mandatoryFieldsLeft = "";
        eventLists.clear();
        addEvents();

        if (isMandatoryFieldLeft) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.warning)
                    .setTitle("Mandatory Fields")
                    .setMessage(mandatoryFieldsLeft)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }

                    })
                    .show();
        } else {
            Log.v("BEFORE_JOIN", "Before joining");
            Log.v("JOIN", eventLists.size() + "");

            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            UpdateEventBody updateEventBody;

            if (network.isNetworkAvailable() && dbHelper.eventDAO().getNumOfEventsToUploadByEventType(POWER_GRIP_UPDATE) > 0) {
                Log.i("POWER_Grid", "save: " + dbHelper.eventDAO().getNumOfEventsToUploadByEventType(POWER_GRIP_UPDATE));
                showProgressBar();
                updateEventBody = new UpdateEventBody(mSharePreferenceHelper.getUserName(),
                        mSharePreferenceHelper.getUserId(),
                        actualDateTime,
                        cmID,
                        dbHelper.eventDAO().getEventsToUploadByEventType(POWER_GRIP_UPDATE));

                Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(), updateEventBody);
                returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        ReturnStatus returnStatus = response.body();

                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), dbHelper.eventDAO().getNumOfEventsToUploadByEventType(POWER_GRIP_UPDATE)+" events uploaded", Toast.LENGTH_LONG).show();
                            dbHelper.eventDAO().updateByThirdParty(YES, CM_Step_TWO, POWER_GRIP_UPDATE);
                            hideProgressBar();
                            mSharePreferenceHelper.setLock(false);
                            finish();
                        }
                        else {
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("POWER_Grid", "onResponse: " + errorReturnBody.string());
                                Toast.makeText(getApplicationContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                                hideProgressBar();
                            } catch (IOException e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                mSharePreferenceHelper.setLock(false);
                finish();
            }
        }



    }
    private void addEvents(){
        Event tempEvent;
        String tempStr;
        if(!isEmpty(powerGridNo)) {
            tempStr = powerGridNo.getText().toString();
            tempEvent = new Event(POWER_GRIP_UPDATE, THIRD_PARTY_NUMBER, tempStr);
            tempEvent.setEvent_id(POWER_GRIP_UPDATE + THIRD_PARTY_NUMBER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (prePowerGridNo.equals("") || !prePowerGridNo.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        } else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nPower Grip Number";
        }

        if(!isEmpty(ctPersonnel)) {
            tempStr = ctPersonnel.getText().toString();
            tempEvent = new Event(POWER_GRIP_UPDATE, CT_PERSONNEL, tempStr);
            tempEvent.setEvent_id(POWER_GRIP_UPDATE + CT_PERSONNEL);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preCTpersonnel.equals("") || !preCTpersonnel.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        } else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nCT Personnel";
        }

        tempStr = referDateTime.getText().toString();
        tempEvent = new Event(POWER_GRIP_UPDATE, REFER_DATE, tempStr);
        tempEvent.setEvent_id(POWER_GRIP_UPDATE + REFER_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preReferDate.equals("") || !preReferDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }
        tempStr = expectedCompletionDateTime.getText().toString();
        tempEvent = new Event(POWER_GRIP_UPDATE, EXPECTED_COMPLETION_DATE, tempStr);
        tempEvent.setEvent_id(POWER_GRIP_UPDATE + EXPECTED_COMPLETION_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preExpectedCompletionDate.equals("") || !preExpectedCompletionDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }
        tempStr = clearanceDateTime.getText().toString();
        tempEvent = new Event(POWER_GRIP_UPDATE, CLEARANCE_DATE, tempStr);
        tempEvent.setEvent_id(POWER_GRIP_UPDATE + CLEARANCE_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preClearanceDate.equals("") || !preClearanceDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }
        if(!isEmpty(powerGridFaultStatus)) {
            tempStr = powerGridFaultStatus.getText().toString();
            tempEvent = new Event(POWER_GRIP_UPDATE, FAULT_STATUS, tempStr);
            tempEvent.setEvent_id(POWER_GRIP_UPDATE + FAULT_STATUS);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preFaultStatus.equals("") || !preFaultStatus.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nPower Grip Fault Status";
        }

        if(!isEmpty(remarksOnTelcoFault)) {
            tempStr = remarksOnTelcoFault.getText().toString();
            tempEvent = new Event(POWER_GRIP_UPDATE, REMARKS_ON_FAULT, tempStr);
            tempEvent.setEvent_id(POWER_GRIP_UPDATE + REMARKS_ON_FAULT);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preRemarks.equals("") || !preRemarks.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        if(!isEmpty(powerGridOfficer)) {
            tempStr = powerGridOfficer.getText().toString();
            tempEvent = new Event(POWER_GRIP_UPDATE, OFFICER, tempStr);
            tempEvent.setEvent_id(POWER_GRIP_UPDATE + OFFICER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preOfficer.equals("") || !preOfficer.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        tempStr = faultDetectedDateTime.getText().toString();
        tempEvent = new Event(POWER_GRIP_UPDATE, FAULT_DETECTED_DATE, tempStr);
        tempEvent.setEvent_id(POWER_GRIP_UPDATE + FAULT_DETECTED_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preFaultDetectedDate.equals("") || !preFaultDetectedDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        tempStr = actionDateTime.getText().toString();
        tempEvent = new Event(POWER_GRIP_UPDATE, ACTION_DATE, tempStr);
        tempEvent.setEvent_id(POWER_GRIP_UPDATE + ACTION_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preActionDate.equals("") || !preActionDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }
        if(!isEmpty(actionTaken)) {
            tempStr = actionTaken.getText().toString();
            tempEvent = new Event(POWER_GRIP_UPDATE, ACTION_TAKEN, tempStr);
            tempEvent.setEvent_id(POWER_GRIP_UPDATE + ACTION_TAKEN);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preActionTaken.equals("") || !preActionTaken.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nAction Taken by Power Grid";
        }
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mSharePreferenceHelper.setLock(false);
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharePreferenceHelper.setLock(false);
    }
}