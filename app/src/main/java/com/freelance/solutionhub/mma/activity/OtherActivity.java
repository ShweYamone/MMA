package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    private SharePreferenceHelper mSharePreferenceHelper;
    private Network network;
    private InitializeDatabase dbHelper;
    private ApiInterface apiInterface;
    private ArrayList<Event> eventLists;
    private Date date;
    private String cmID;

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

        addEvents();
        Log.v("BEFORE_JOIN", "Before joining");
        Log.v("JOIN", eventLists.size() + "");

        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        UpdateEventBody updateEventBody;
        if (network.isNetworkAvailable()) {
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
        }


        eventLists.clear();


    }
    private void addEvents(){
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "referDate",referDateTime.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "expectedCompletionDate",expectedCompletionDateTime.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "clearanceDate",clearanceDateTime.getText().toString()));
        if(!isEmpty(faultStatus))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "faultStatus",faultStatus.getText().toString()));
        if(!isEmpty(remarks))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE","remarkOnFault",remarks.getText().toString()));
        if(!isEmpty(thirdPartyContractorName))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "officer",thirdPartyContractorName.getText().toString()));
        if(!isEmpty(thirdPartyCompanyName))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "companyName",thirdPartyCompanyName.getText().toString()));
        if(!isEmpty(thirdPartyContractorNo))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "contactNumber",thirdPartyContractorNo.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "faultDetectedDate",faultDetectedDateTime.getText().toString()));
        eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "actionDate",actionDateTime.getText().toString()));
        if(!isEmpty(actionTaken))
            eventLists.add(new Event("OTHER_CONTRACTOR_UPDATE", "actionTaken",actionTaken.getText().toString()));
    }
    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }
}
