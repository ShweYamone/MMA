package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.freelance.solutionhub.mma.fragment.First_Step_PM_Fragment;
import com.freelance.solutionhub.mma.fragment.Second_Step_PM_Fragment;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.ThirdPartyModel;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.ACTION_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.ACTION_TAKEN;
import static com.freelance.solutionhub.mma.util.AppConstant.CLEARANCE_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_ONE;
import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_TWO;
import static com.freelance.solutionhub.mma.util.AppConstant.CT_PERSONNEL;
import static com.freelance.solutionhub.mma.util.AppConstant.DOCKET_NUMBER;
import static com.freelance.solutionhub.mma.util.AppConstant.EXPECTED_COMPLETION_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_DETECTED_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_STATUS;
import static com.freelance.solutionhub.mma.util.AppConstant.NO;
import static com.freelance.solutionhub.mma.util.AppConstant.NO_TYPE;
import static com.freelance.solutionhub.mma.util.AppConstant.OFFICER;
import static com.freelance.solutionhub.mma.util.AppConstant.REFER_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.TELCO_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.THIRD_PARTY_NUMBER;
import static com.freelance.solutionhub.mma.util.AppConstant.TIME_SERVER;
import static com.freelance.solutionhub.mma.util.AppConstant.YES;
import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class TelcoActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.et_telco_no)
    EditText telcoNo;

    @BindView(R.id.et_docker_no)
    EditText dockerNo;

    @BindView(R.id.et_ct_personnel)
    EditText ctPersonnel;

    @BindView(R.id.btn_expected_completion_date_time)
    Button expectedCompletionDateTime;

    @BindView(R.id.btn_clearance_date_time)
    Button clearanceDateTime;

    @BindView(R.id.btn_fault_detected_date_time)
    Button faultDetectedDateTime;

    @BindView(R.id.btn_action_date_time)
    Button actionDateTime;

    @BindView(R.id.et_action_taken)
    EditText actionTaken;

    @BindView(R.id.et_telco_officer)
    EditText telcoOfficer;

    @BindView(R.id.et_telco_fault_status)
    EditText telcoFaultStatus;

    @BindView(R.id.btn_refer_date_time)
    Button referDateTime;

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

    private String preTelcoNo = "";
    private String preDockerNo = "";
    private String preCTPersonnel = "";
    private String preReferDate = "";
    private String preCompletionDate = "";
    private String preClearanceDate = "";
    private String preFaultStatus = "";
    private String preOfficer = "";
    private String preDetectedDate = "";
    private String preActionDate = "";
    private String preActionTaken = "";
    private ThirdPartyModel telcoObj = new ThirdPartyModel();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telco);
        ButterKnife.bind(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.setLock(false);

        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        telcoObj = (ThirdPartyModel)getIntent().getSerializableExtra("telco");
        Event tempEvent;
        if (!telcoObj.getThirdPartyType().equals(NO_TYPE)) {
            tempEvent = new Event(TELCO_UPDATE, "", "***");
            tempEvent.setEvent_id("telco_temp");
        }


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
        Log.v("PMID",cmID);
        //Click
        referDateTime.setOnClickListener(this);
        expectedCompletionDateTime.setOnClickListener(this);
        clearanceDateTime.setOnClickListener(this);
        faultDetectedDateTime.setOnClickListener(this);
        actionDateTime.setOnClickListener(this);
        save.setOnClickListener(this);

        displaySavedData();


        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(TelcoActivity.this, "user is inactive from last 5 minute",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(TelcoActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
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
     //   Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
            Intent intent = new Intent(TelcoActivity.this, PasscodeActivity.class);
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
     * Update event for all
     */
    public void save(){

        isMandatoryFieldLeft = false;
        mandatoryFieldsLeft = "";
        eventLists.clear();
        addEvents();
        Log.v("JOIN", eventLists.size() + "");

        if (isMandatoryFieldLeft) { // mandatory fields can't proceed
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
        } else { // mandatory fields are filled, update events
            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);

            UpdateEventBody updateEventBody;
            if (network.isNetworkAvailable() && dbHelper.eventDAO().getNumOfEventsToUploadByEventType(TELCO_UPDATE) > 0) {
                showProgressBar();
                updateEventBody = new UpdateEventBody(mSharePreferenceHelper.getUserName(),
                        mSharePreferenceHelper.getUserId(),
                        actualDateTime,
                        cmID,
                        dbHelper.eventDAO().getEventsToUploadByEventType(TELCO_UPDATE));
                new getCurrentNetworkTime(updateEventBody).execute();

            } else {
                mSharePreferenceHelper.setLock(false);
                finish();
            }

        }
    }

    class getCurrentNetworkTime extends AsyncTask<String, Void, Boolean> {
        UpdateEventBody eventBody;

        public getCurrentNetworkTime(UpdateEventBody eventBody) {
            this.eventBody = eventBody;
        }

        private void updateEvents(String datetime) {
            eventBody.setDate(datetime);
            Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(), eventBody);
            returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {

                    if (response.isSuccessful()) {

                //        Toast.makeText(getApplicationContext(), dbHelper.eventDAO().getNumOfEventsToUploadByEventType(TELCO_UPDATE)+" events uploaded at" +
                //                eventBody.getDate(), Toast.LENGTH_LONG).show();
                        dbHelper.eventDAO().updateByThirdParty(YES, CM_Step_TWO, TELCO_UPDATE);
                        hideProgressBar();
                        mSharePreferenceHelper.setLock(false);
                        finish();
                    } else {
                //        Toast.makeText(getApplicationContext(), response.errorBody()+"", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {
                //    Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        protected Boolean doInBackground(String... urls) {
            boolean is_locale_date = false;
            try {
                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.open();
                timeClient.setDefaultTimeout(5000);
                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                long localTime = timeInfo.getReturnTime();
                long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                Timestamp timestamp = new Timestamp(localTime);
                String localDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
                Log.i("Time__Local", "doInBackground: " + localTime + "--> " + localDateTime);
                timestamp = new Timestamp(serverTime);
                String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
                Log.i("Time__Server", "doInBackground:" + serverTime + "--> " + actualDateTime);
                //magic is here
                updateEvents(actualDateTime);

                if (new Date(localTime) != new Date(serverTime))
                    is_locale_date = true;

            } catch (UnknownHostException e) {
                e.printStackTrace();
                Log.e("UnknownHostException: ", e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("IOException: ", e.getMessage());
            }
            return is_locale_date;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            hideProgressBar();
            if(!aBoolean) {
                Log.e("Check ", "dates not equal");
            }
        }

    }




    private void displaySavedData() {
        boolean hasThirdPartyObj = true;
        if (telcoObj.getThirdPartyType().equals(NO_TYPE))
            hasThirdPartyObj = false;

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE , THIRD_PARTY_NUMBER) > 0)
            preTelcoNo = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, THIRD_PARTY_NUMBER);
        else if (hasThirdPartyObj && telcoObj.getThirdPartyNumber() != null)
            preTelcoNo = telcoObj.getThirdPartyNumber();
        telcoNo.setText(preTelcoNo);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, DOCKET_NUMBER) > 0)
            preDockerNo = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, DOCKET_NUMBER);
        else if (hasThirdPartyObj && telcoObj.getDocketNumber() != null)
            preDockerNo = telcoObj.getDocketNumber();
        dockerNo.setText(preDockerNo);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, CT_PERSONNEL) > 0)
            preCTPersonnel = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, CT_PERSONNEL);
        else if (hasThirdPartyObj && telcoObj.getCtPersonnel() != null)
            preCTPersonnel = telcoObj.getCtPersonnel();
        ctPersonnel.setText(preCTPersonnel);


        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, REFER_DATE) > 0)
            preReferDate = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, REFER_DATE);
        else if (hasThirdPartyObj && telcoObj.getReferDate() != null)
            preReferDate = telcoObj.getReferDate();
        if (!preReferDate.equals(""))
            referDateTime.setText(preReferDate);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, EXPECTED_COMPLETION_DATE) > 0)
            preCompletionDate = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, EXPECTED_COMPLETION_DATE);
        else if (hasThirdPartyObj && telcoObj.getExpectedCompletionDate() != null)
            preCompletionDate = telcoObj.getExpectedCompletionDate();
        if (!preCompletionDate.equals(""))
            expectedCompletionDateTime.setText(preCompletionDate);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, CLEARANCE_DATE) > 0)
            preClearanceDate = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, CLEARANCE_DATE);
        else if (hasThirdPartyObj && telcoObj.getClearanceDate() != null)
            preClearanceDate = telcoObj.getClearanceDate();
        if (!preClearanceDate.equals(""))
            clearanceDateTime.setText(preClearanceDate);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, FAULT_STATUS) > 0)
            preFaultStatus = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, FAULT_STATUS);
        else if (hasThirdPartyObj && telcoObj.getFaultStatus() != null)
            preFaultStatus = telcoObj.getFaultStatus();
        telcoFaultStatus.setText(preFaultStatus);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, OFFICER) > 0)
            preOfficer = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, OFFICER);
        else if (hasThirdPartyObj && telcoObj.getOfficer() != null)
            preOfficer = telcoObj.getOfficer();
        telcoOfficer.setText(preOfficer);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, FAULT_DETECTED_DATE) > 0)
            preDetectedDate = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, FAULT_DETECTED_DATE);
        else if (hasThirdPartyObj && telcoObj.getFaultDetectedDate() != null)
            preDetectedDate = telcoObj.getFaultDetectedDate();
        if (!preDetectedDate.equals(""))
            faultDetectedDateTime.setText(preDetectedDate);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, ACTION_DATE) > 0)
            preActionDate = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, ACTION_DATE);
        else if (hasThirdPartyObj && telcoObj.getActionDate() != null)
            preActionDate = telcoObj.getActionDate();
        if (!preActionDate.equals(""))
            actionDateTime.setText(preActionDate);

        if (dbHelper.eventDAO().getEventValueCount(TELCO_UPDATE, ACTION_TAKEN) > 0) {
            preActionTaken = dbHelper.eventDAO().getEventValue(TELCO_UPDATE, ACTION_TAKEN);
        }
        else if (hasThirdPartyObj && telcoObj.getActionTaken() != null)
            preActionTaken = telcoObj.getActionTaken();
        actionTaken.setText(preActionTaken);
    }
    private void addEvents(){
        Log.v("TELCO","H"+telcoNo.getText().toString()+"H");
        Event tempEvent; String tempStr;
        if(!isEmpty(telcoNo)) {
            tempStr = telcoNo.getText().toString();
            tempEvent = new Event(TELCO_UPDATE,THIRD_PARTY_NUMBER, tempStr);
            tempEvent.setEvent_id(TELCO_UPDATE + THIRD_PARTY_NUMBER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preTelcoNo.equals("") || !preTelcoNo.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nTelco No";
        }
        if(!isEmpty(dockerNo)) {
            tempStr = dockerNo.getText().toString();
            tempEvent = new Event(TELCO_UPDATE, DOCKET_NUMBER, tempStr);
            tempEvent.setEvent_id(TELCO_UPDATE + DOCKET_NUMBER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preDockerNo.equals("") || !preDockerNo.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft  = true;
            mandatoryFieldsLeft += "\nDocker No";
        }
        if(!isEmpty(ctPersonnel)) {
            tempStr = ctPersonnel.getText().toString();
            tempEvent = new Event(TELCO_UPDATE, CT_PERSONNEL, tempStr);
            tempEvent.setEvent_id(TELCO_UPDATE + CT_PERSONNEL);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preCTPersonnel.equals("") || !preCTPersonnel.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nCT Personnel";
        }
        tempStr = referDateTime.getText().toString();
        tempEvent = new Event(TELCO_UPDATE, REFER_DATE, tempStr);
        tempEvent.setEvent_id(TELCO_UPDATE + REFER_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preReferDate.equals("") || !preReferDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }
        tempStr = expectedCompletionDateTime.getText().toString();
        tempEvent = new Event(TELCO_UPDATE, EXPECTED_COMPLETION_DATE,tempStr);
        tempEvent.setEvent_id(TELCO_UPDATE + EXPECTED_COMPLETION_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preCompletionDate.equals("") || !preCompletionDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }
        tempStr = clearanceDateTime.getText().toString();
        tempEvent = new Event(TELCO_UPDATE, CLEARANCE_DATE, tempStr);
        tempEvent.setEvent_id(TELCO_UPDATE + CLEARANCE_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preClearanceDate.equals("") || !preClearanceDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        if(!isEmpty(telcoFaultStatus)) {
            tempStr = telcoFaultStatus.getText().toString();
            tempEvent = new Event(TELCO_UPDATE, FAULT_STATUS, tempStr);
            tempEvent.setEvent_id(TELCO_UPDATE + FAULT_STATUS);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preFaultStatus.equals("") || !preFaultStatus.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nFault Status";
        }

        if(!isEmpty(telcoOfficer)) {
            tempStr = telcoOfficer.getText().toString();
            tempEvent = new Event(TELCO_UPDATE, OFFICER, tempStr);
            tempEvent.setEvent_id(TELCO_UPDATE + OFFICER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preOfficer.equals("") || !preOfficer.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }

        tempStr = faultDetectedDateTime.getText().toString();
        tempEvent = new Event(TELCO_UPDATE, FAULT_DETECTED_DATE, tempStr);
        tempEvent.setEvent_id(TELCO_UPDATE + FAULT_DETECTED_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preDetectedDate.equals("") || !preDetectedDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        tempStr = actionDateTime.getText().toString();
        tempEvent = new Event(TELCO_UPDATE, ACTION_DATE, tempStr);
        tempEvent.setEvent_id(TELCO_UPDATE + ACTION_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preActionDate.equals("") || !preActionDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        if(!isEmpty(actionTaken)) {
            tempStr = actionTaken.getText().toString();
            tempEvent = new Event(TELCO_UPDATE, ACTION_TAKEN, tempStr);
            tempEvent.setEvent_id(TELCO_UPDATE + ACTION_TAKEN);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preActionTaken.equals("") || !preActionTaken.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nAction Taken By Telco";
        }

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
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
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