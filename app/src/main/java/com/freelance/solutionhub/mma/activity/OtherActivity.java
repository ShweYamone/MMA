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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.chrono.IsoChronology;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.OTHER_CONTRACTOR_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.POWER_GRIP_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.REFER_DATE;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
        ButterKnife.bind(this);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

        /**show data from DB if have */
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
            if (network.isNetworkAvailable()) {
                showProgressBar();
                updateEventBody = new UpdateEventBody(mSharePreferenceHelper.getUserName(),
                        mSharePreferenceHelper.getUserId(),
                        actualDateTime,
                        cmID,
                        eventLists);

                Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(), updateEventBody);
                returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        ReturnStatus returnStatus = response.body();
                        Log.v("SUCCESS","error");
                        if (response.isSuccessful()) {
                            Log.v("SUCCESS","success");
                            Toast.makeText(getApplicationContext(), returnStatus.getStatus() + "", Toast.LENGTH_LONG).show();
                            hideProgressBar();
                            mSharePreferenceHelper.setLock(false);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                updateEventBody = new UpdateEventBody(
                        mSharePreferenceHelper.getUserName(),
                        mSharePreferenceHelper.getUserId(),
                        actualDateTime,
                        cmID
                );
                String key = mSharePreferenceHelper.getUserId() + cmID + actualDateTime;
                updateEventBody.setId(key);
                dbHelper.updateEventBodyDAO().insert(updateEventBody);
                for (Event event: eventLists) {
                    event.setUpdateEventBodyKey(key);
                }
                dbHelper.eventDAO().insertAll(eventLists);
                mSharePreferenceHelper.setLock(false);
                finish();
            }


            eventLists.clear();

        }


    }
    private void addEvents(){
        Event tempEvent;
        String tempStr;
      //  tempEvent = new Event(OTHER_CONTRACTOR_UPDATE, REFER_DATE)


        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "referDate",referDateTime.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "expectedCompletionDate",expectedCompletionDateTime.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "clearanceDate",clearanceDateTime.getText().toString()));
        if(!isEmpty(faultStatus))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "faultStatus",faultStatus.getText().toString()));
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\nFault Status";
        }
        if(!isEmpty(remarks))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE","remarkOnFault",remarks.getText().toString()));

        if(!isEmpty(thirdPartyContractorName))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "officer",thirdPartyContractorName.getText().toString()));
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\n3rd Party Contractor Name";
        }

        if(!isEmpty(thirdPartyCompanyName))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "companyName",thirdPartyCompanyName.getText().toString()));
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\n3rd Party Company Name";
        }

        if(!isEmpty(thirdPartyContractorNo))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "contactNumber",thirdPartyContractorNo.getText().toString()));
        else {
            isMandatoryFieldLeft = true;
            mandatoryFieldsLeft += "\n3rd Party Contractor No";
        }

        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "faultDetectedDate",faultDetectedDateTime.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "actionDate",actionDateTime.getText().toString()));

        if(!isEmpty(actionTaken))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "actionTaken",actionTaken.getText().toString()));
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
