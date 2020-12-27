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
import com.freelance.solutionhub.mma.model.ThirdPartyModel;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import static com.freelance.solutionhub.mma.util.AppConstant.COMPANY_NAME;
import static com.freelance.solutionhub.mma.util.AppConstant.CONTACT_NUMBER;
import static com.freelance.solutionhub.mma.util.AppConstant.EXPECTED_COMPLETION_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_DETECTED_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_STATUS;
import static com.freelance.solutionhub.mma.util.AppConstant.NO;
import static com.freelance.solutionhub.mma.util.AppConstant.NO_TYPE;
import static com.freelance.solutionhub.mma.util.AppConstant.OFFICER;
import static com.freelance.solutionhub.mma.util.AppConstant.OTHER_CONTRACTOR_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.REFER_DATE;
import static com.freelance.solutionhub.mma.util.AppConstant.REMARKS_ON_FAULT;
import static com.freelance.solutionhub.mma.util.AppConstant.YES;
import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class OtherActivity extends AppCompatActivity implements View.OnClickListener{

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.btn_refer_date_time)
    Button referDateTime;

    @BindView(R.id.btn_expected_completion_date_time)
    Button expectedCompletionDateTime;

    @BindView(R.id.btn_clearance_date_time)
    Button clearanceDateTime;

    @BindView(R.id.et_fault_status)
    EditText faultStatus;

    @BindView(R.id.et_remarks)
    EditText remarks;

    @BindView(R.id.et_third_party_company_name)
    EditText thirdPartyCompanyName;

    @BindView(R.id.et_third_party_contractor_name)
    EditText thirdPartyContractorName;

    @BindView(R.id.et_third_party_contractor_no)
    EditText thirdPartyContractorNo;

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
    private Network network;
    private InitializeDatabase dbHelper;
    private ApiInterface apiInterface;
    private ArrayList<Event> eventLists;
    private Date date;
    private String cmID;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean isMandatoryFieldLeft = false;
    private String mandatoryFieldsLeft = "";

    private String preReferDate = "";
    private String preCompletionDate = "";
    private String preClearanceDate = "";
    private String preFaultStatus = "";
    private String preRemarks = "";
    private String preCompanyName = "";
    private String preContractorName = "";
    private String preContractorNo = "";
    private String preDetectedDate = "";
    private String preActionDate = "";
    private String preActionTaken = "";
    private ThirdPartyModel otherObj = new ThirdPartyModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        otherObj = (ThirdPartyModel) getIntent().getSerializableExtra("other");
        eventLists = new ArrayList<>();
        apiInterface = ApiClient.getClient(this);
        mSharePreferenceHelper = new SharePreferenceHelper(this);
        mSharePreferenceHelper.setLock(false);

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

        displaySavedData();

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(OtherActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(OtherActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
    }

    private void displaySavedData() {
        boolean hasThirdPartyObj = true;
        if (otherObj.getThirdPartyType().equals(NO_TYPE))
            hasThirdPartyObj = false;

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, REFER_DATE) > 0) {
            preReferDate = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, REFER_DATE);
        } else if (hasThirdPartyObj && otherObj.getReferDate() != null)
            preReferDate = otherObj.getReferDate();
        if (!preReferDate.equals(""))
            referDateTime.setText(preReferDate);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, EXPECTED_COMPLETION_DATE) > 0) {
            preCompletionDate = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, EXPECTED_COMPLETION_DATE);
        }
        else if (hasThirdPartyObj && otherObj.getExpectedCompletionDate() != null)
            preCompletionDate = otherObj.getExpectedCompletionDate();
        if (!preCompletionDate.equals(""))
            expectedCompletionDateTime.setText(preCompletionDate);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, CLEARANCE_DATE) > 0) {
            preClearanceDate = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, CLEARANCE_DATE);
        }
        else if (hasThirdPartyObj && otherObj.getClearanceDate() != null)
            preClearanceDate = otherObj.getClearanceDate();
        if (!preClearanceDate.equals(""))
            clearanceDateTime.setText(preClearanceDate);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, FAULT_STATUS) > 0) {
            preFaultStatus = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, FAULT_STATUS);
        }
        else if (hasThirdPartyObj && otherObj.getFaultStatus() != null)
            preFaultStatus = otherObj.getFaultStatus();
        faultStatus.setText(preFaultStatus);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, REMARKS_ON_FAULT) > 0) {
            preRemarks = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, REMARKS_ON_FAULT);
        }
        else if (hasThirdPartyObj && otherObj.getRemarkOnFault() != null)
            preRemarks = otherObj.getRemarkOnFault();
        remarks.setText(preRemarks);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, COMPANY_NAME) > 0) {
            preCompanyName = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, COMPANY_NAME);
        }
        else if (hasThirdPartyObj && otherObj.getCompanyName() != null)
            preCompanyName = otherObj.getCompanyName();
        thirdPartyCompanyName.setText(preCompanyName);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, OFFICER) > 0) {
            preContractorName = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, OFFICER);
        }
        else if (hasThirdPartyObj && otherObj.getOfficer() != null)
            preContractorName = otherObj.getOfficer();
        thirdPartyContractorName.setText(preContractorName);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, CONTACT_NUMBER) > 0) {
            preContractorNo = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, CONTACT_NUMBER);
        }
        else if (hasThirdPartyObj && otherObj.getContactNumber() != null)
            preContractorNo = otherObj.getContactNumber();
        thirdPartyContractorNo.setText(preContractorNo);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, FAULT_DETECTED_DATE) > 0) {
            preDetectedDate = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, FAULT_DETECTED_DATE);
        }
        else if (hasThirdPartyObj && otherObj.getFaultDetectedDate() != null)
            preDetectedDate = otherObj.getFaultDetectedDate();
        if (!preDetectedDate.equals(""))
            faultDetectedDateTime.setText(preDetectedDate);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, ACTION_DATE) > 0) {
            preActionDate = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, ACTION_DATE);
        }
        else if (hasThirdPartyObj && otherObj.getActionDate() != null)
            preActionDate = otherObj.getActionDate();
        if (!preActionDate.equals(""))
            actionDateTime.setText(preActionDate);

        if (dbHelper.eventDAO().getEventValueCount(OTHER_CONTRACTOR_UPDATE, ACTION_TAKEN) > 0) {
            preActionTaken = dbHelper.eventDAO().getEventValue(OTHER_CONTRACTOR_UPDATE, ACTION_TAKEN);
        }
        else if (hasThirdPartyObj && otherObj.getActionTaken() != null)
            preActionTaken = otherObj.getActionTaken();
        actionTaken.setText(preActionTaken);
    }


    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        mSharePreferenceHelper.setLock(true);
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
        if (mSharePreferenceHelper.getLock()) {
            Intent intent = new Intent(OtherActivity.this, PasscodeActivity.class);
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

            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            UpdateEventBody updateEventBody;
            Log.i("Other", "save: " + dbHelper.eventDAO().getNumberOfEvents());
            List<Event> events = dbHelper.eventDAO().getEventsByEventType(OTHER_CONTRACTOR_UPDATE);
            String temp = "";
            for(Event e: events) {
                temp += e.getEvent_id() + " - " + e.getValue() + " - " + e.getAlreadyUploaded() + "\n";
            }
            Log.i("Other", "save: " + temp);


            if (network.isNetworkAvailable() && dbHelper.eventDAO().getNumOfEventsToUploadByEventType(OTHER_CONTRACTOR_UPDATE) > 0) {
                showProgressBar();
                updateEventBody = new UpdateEventBody(mSharePreferenceHelper.getUserName(),
                        mSharePreferenceHelper.getUserId(),
                        actualDateTime,
                        cmID,
                        dbHelper.eventDAO().getEventsToUploadByEventType(OTHER_CONTRACTOR_UPDATE));

                Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(), updateEventBody);
                returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), dbHelper.eventDAO().getNumOfEventsToUploadByEventType(OTHER_CONTRACTOR_UPDATE)+" events uploaded", Toast.LENGTH_LONG).show();
                            dbHelper.eventDAO().updateByThirdParty(YES, CM_Step_TWO, OTHER_CONTRACTOR_UPDATE);
                            hideProgressBar();
                            mSharePreferenceHelper.setLock(false);
                            finish();
                        }
                        else {
                            hideProgressBar();
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("OTHER", "onResponse: " + errorReturnBody.string());
                                Toast.makeText(getApplicationContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        hideProgressBar();
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

        tempStr = referDateTime.getText().toString();
        tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, REFER_DATE, tempStr);
        tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + REFER_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preReferDate.equals("") || !preReferDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        tempStr = expectedCompletionDateTime.getText().toString();
        tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, EXPECTED_COMPLETION_DATE, tempStr);
        tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + EXPECTED_COMPLETION_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preCompletionDate.equals("") || !preCompletionDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        tempStr = clearanceDateTime.getText().toString();
        tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, CLEARANCE_DATE, tempStr);
        tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + CLEARANCE_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preClearanceDate.equals("") || !preClearanceDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        if(!isEmpty(faultStatus)) {
            tempStr = faultStatus.getText().toString();
            tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, FAULT_STATUS, tempStr);
            tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + FAULT_STATUS);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preFaultStatus.equals("") || !preFaultStatus.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nFault Status";
        }

        if(!isEmpty(remarks)) {
            tempStr = remarks.getText().toString();
            tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, REMARKS_ON_FAULT, tempStr);
            tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + REMARKS_ON_FAULT);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preRemarks.equals("") || !preRemarks.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }

        if(!isEmpty(thirdPartyCompanyName)) {
            tempStr = thirdPartyCompanyName.getText().toString();
            tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, COMPANY_NAME, tempStr);
            tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + COMPANY_NAME);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preCompanyName.equals("") || !preCompanyName.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        } else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\n3rd Party Company Name";
        }

        if(!isEmpty(thirdPartyContractorName)) {
            tempStr = thirdPartyContractorName.getText().toString();
            tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, OFFICER, tempStr);
            tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + OFFICER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preContractorName.equals("") || !preContractorName.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\n3rd Party Contractor Name";
        }

        if(!isEmpty(thirdPartyContractorNo)) {
            tempStr = thirdPartyContractorNo.getText().toString();
            tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, CONTACT_NUMBER, tempStr);
            tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + CONTACT_NUMBER);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preContractorNo.equals("") || !preContractorNo.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        } else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\n3rd Party Contractor No";
        }

        tempStr = faultDetectedDateTime.getText().toString();
        tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, FAULT_DETECTED_DATE, tempStr);
        tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + FAULT_DETECTED_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preDetectedDate.equals("") || !preDetectedDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        tempStr = actionDateTime.getText().toString();
        tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, ACTION_DATE, tempStr);
        tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + ACTION_DATE);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preActionDate.equals("") || !preActionDate.equals(tempStr)) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        if(!isEmpty(actionTaken)) {
            tempStr = actionTaken.getText().toString();
            tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, ACTION_TAKEN, tempStr);
            tempEvent.setEvent_id(OTHER_CONTRACTOR_UPDATE + ACTION_TAKEN);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preActionTaken.equals("") || !preActionTaken.equals(tempStr)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
        }
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nAction Taken By Contractor";
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
        mSharePreferenceHelper.setLock(false);
    }
}
