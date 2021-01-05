package com.digisoft.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.model.CheckListDescModel;
import com.digisoft.mma.model.CheckListModel;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.PMServiceInfoDetailModel;
import com.digisoft.mma.model.ThirdPartyModel;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.SharePreferenceHelper;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digisoft.mma.util.AppConstant.OTHER_CONTRACTOR;
import static com.digisoft.mma.util.AppConstant.PM_CHECK_LIST_DONE;
import static com.digisoft.mma.util.AppConstant.PM_CHECK_LIST_REMARK;
import static com.digisoft.mma.util.AppConstant.PM_Step_ONE;
import static com.digisoft.mma.util.AppConstant.POWER_GRIP;
import static com.digisoft.mma.util.AppConstant.TELCO;
import static com.digisoft.mma.util.AppConstant.YES;
import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class LoadingActivity extends AppCompatActivity {

    @BindView(R.id.circularProgressBar)
    CircularProgressBar progressBar;

    @BindView(R.id.tvProgressPercent)
    TextView tvPercent;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePrefrence;
    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private InitializeDatabase dbHelper;
    private String msoId = "";
    private List<ThirdPartyModel> thirdPartyModels = new ArrayList<>();
    private ThirdPartyModel telco;
    private ThirdPartyModel powerGrid;
    private ThirdPartyModel other;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient(this);
        mSharePrefrence = new SharePreferenceHelper(this);
        dbHelper = InitializeDatabase.getInstance(this);
        mSharePrefrence.setLock(false);

        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        msoId = getIntent().getStringExtra("id");
        //check current working mso is changed. if, delete
        if (!msoId.equals(mSharePrefrence.getCurrentMSOID())) {
            dbHelper.updateEventBodyDAO().deleteAll();
            dbHelper.eventDAO().deleteAll();
            dbHelper.uploadPhotoDAO().deleteAll();
            dbHelper.checkListDescDAO().deleteAll();
            mSharePrefrence.setCurrentMSOID(msoId);
        }
        Event tempEvent = new Event("start_tag_in_time", "start_tag_in_time", getIntent().getStringExtra("start_time"));
        tempEvent.setEvent_id("start_timestart_time");
        dbHelper.eventDAO().insert(tempEvent);

        getServieOrderbyId(msoId);

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(LoadingActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(LoadingActivity.this, PasscodeActivity.class);
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
        mSharePrefrence.setLock(true);
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
        if (mSharePrefrence.getLock()) {
            Intent intent = new Intent(LoadingActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }


    private void getThirdPartInfo(PMServiceInfoDetailModel responseBody) {
    //    Toast.makeText(getApplicationContext(), "response enter 2"  + msoId,  Toast.LENGTH_LONG).show();

        Call<List<ThirdPartyModel>> callThirdParty = apiInterface.getThirdParties("Bearer " + mSharePrefrence.getToken(), msoId);
        callThirdParty.enqueue(new Callback<List<ThirdPartyModel>>() {
            @Override
            public void onResponse(Call<List<ThirdPartyModel>> call, Response<List<ThirdPartyModel>> response) {
                Intent intent = new Intent(LoadingActivity.this, CMActivity.class);
                if (response.isSuccessful()) {
                    thirdPartyModels = response.body();
                    storeThirdPartyInfo(thirdPartyModels, intent);
                    intent.putExtra("start_time", getIntent().getStringExtra("start_time"));
                    intent.putExtra("object", responseBody);
                    startActivity(intent);
                    finish();
                }
                else{
                    ResponseBody errorReturnBody = response.errorBody();
                    try {
                        Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                  //      Toast.makeText(getApplicationContext(), "response " + response.code() + msoId,  Toast.LENGTH_LONG).show();
                        //  ((CMActivity)getActivity()).hideProgressBar();
                    } catch (IOException e) {

                    }
                }

            }

            @Override
            public void onFailure(Call<List<ThirdPartyModel>> call, Throwable t) {

            }
        });
    }

    private void storeThirdPartyInfo(List<ThirdPartyModel> thirdPartyModels, Intent intent) {
        for (ThirdPartyModel obj : thirdPartyModels) {
            if (obj.getThirdPartyType().equals(TELCO)) {
                intent.putExtra("telco", obj);
            }

            else if (obj.getThirdPartyType().equals(POWER_GRIP)) {
                intent.putExtra("powerGrid", obj);
                powerGrid = obj;
            }

            else if (obj.getThirdPartyType().equals(OTHER_CONTRACTOR)) {
                intent.putExtra("other", obj);
                other = obj;
            }
        }

//        Log.i("ThirdParty", "storeThirdPartyInfo: " + telco.getId() + ", " + powerGrid.getId() + ", " + other.getId());
    }

    private void getPMCheckList(PMServiceInfoDetailModel responseBody) {
      //  Toast.makeText(getApplicationContext(), "response checklist", Toast.LENGTH_LONG).show();

        Call<List<CheckListModel>> callCheckList = apiInterface.getCheckList("Bearer "+ mSharePrefrence.getToken(), msoId);
        callCheckList.enqueue(new Callback<List<CheckListModel>>() {
            @Override
            public void onResponse(Call<List<CheckListModel>> call, Response<List<CheckListModel>> response) {

                if(response.isSuccessful()){
                    List<CheckListModel> checkListModels = response.body();

                    for (CheckListModel object: checkListModels) {
                        Event tempEvent;
                        tempEvent = new Event(
                                PM_CHECK_LIST_DONE, object.getId()+"", object.isMaintenanceDone()+""
                        );
                        tempEvent.setUpdateEventBodyKey(PM_Step_ONE);
                        tempEvent.setEvent_id(PM_CHECK_LIST_DONE + object.getId());
                        tempEvent.setAlreadyUploaded(YES);
                        dbHelper.eventDAO().insert(tempEvent);
                        dbHelper.eventDAO().insert(tempEvent);

                        if (object.getMaintenanceRemark() != null)
                            tempEvent = new Event(
                                    PM_CHECK_LIST_REMARK, object.getId()+"", object.getMaintenanceRemark()+""
                            );
                        else
                            tempEvent = new Event(
                                    PM_CHECK_LIST_REMARK, object.getId()+"", ""
                            );
                        tempEvent.setUpdateEventBodyKey(PM_Step_ONE);
                        tempEvent.setEvent_id(PM_CHECK_LIST_REMARK + object.getId());
                        tempEvent.setAlreadyUploaded(YES);
                        dbHelper.eventDAO().insert(tempEvent);
                        //After DB store, go to PM activity

                        dbHelper.checkListDescDAO().insert(
                                new CheckListDescModel(object.getId()+"", object.getCheckDescription())
                        );
                        Log.i("EVENTDAO", "onResponse: " + PM_CHECK_LIST_DONE + object.getId());
                    }
                    Intent intent = new Intent(LoadingActivity.this, PMActivity.class);
                    intent.putExtra("start_time", getIntent().getStringExtra("start_time"));
                    intent.putExtra("object", responseBody);
                    startActivity(intent);
                    finish();
                } else{
                    ResponseBody errorReturnBody = response.errorBody();
                    try {
                        Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                   //     Toast.makeText(getApplicationContext(), "response " + response.code() + msoId,  Toast.LENGTH_LONG).show();
                      //  ((CMActivity)getActivity()).hideProgressBar();
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<CheckListModel>> call, Throwable t) {

            }
        });
    }

    private void getServieOrderbyId(String id) {
      //  Toast.makeText(getApplicationContext(), "response " + "enter", Toast.LENGTH_LONG).show();

        Call<PMServiceInfoDetailModel> call = apiInterface.getPMServiceOrderByID("Bearer " + mSharePrefrence.getToken() , id);
        call.enqueue(new Callback<PMServiceInfoDetailModel>() {
            @Override
            public void onResponse(Call<PMServiceInfoDetailModel> call, Response<PMServiceInfoDetailModel> response) {
             //   Toast.makeText(getApplicationContext(), "response " + response.code(), Toast.LENGTH_LONG).show();

                if (response.isSuccessful()) {
                    Intent intent;
                    if (id.startsWith("CM")) {
                        /*
                        if (dbHelper.eventDAO().getNumOfEventsByEventType(TELCO_UPDATE) > 0 &&
                            dbHelper.eventDAO().getNumOfEventsByEventType(POWER_GRIP_UPDATE) > 0 &&
                            dbHelper.eventDAO().getNumOfEventsByEventType(OTHER_CONTRACTOR_UPDATE) > 0) {
                            intent = new Intent(LoadingActivity.this, CMActivity.class);
                            intent.putExtra("start_time", getIntent().getStringExtra("start_time"));
                            intent.putExtra("object", response.body());
                            startActivity(intent);
                            finish();
                        } else {*/
                            getThirdPartInfo(response.body());
                     //   }
                    } else {
                        //first get relevant PM Checklist.........
                        if (dbHelper.eventDAO().getNumberOfEventsByUpdateBodyKey(PM_Step_ONE) > 0) {
                            Log.i("logloglog", "Directly go to PM" + dbHelper.eventDAO().getNumberEventsToUpload(PM_Step_ONE));
                            intent = new Intent(LoadingActivity.this, PMActivity.class);
                            intent.putExtra("start_time", getIntent().getStringExtra("start_time"));
                            intent.putExtra("object", response.body());
                            startActivity(intent);
                            finish();
                        } else {
                            Log.i("logloglog", "can't go to PM" + dbHelper.eventDAO().getNumberEventsToUpload(PM_Step_ONE));

                            getPMCheckList(response.body());
                        }
                    }

                } else {
                  //  Toast.makeText(getApplicationContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                    ResponseBody errorReturnBody = response.errorBody();
                    try {
                        Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                      //  ((CMActivity)getActivity()).hideProgressBar();


                    } catch (IOException e) {

                    }
                }
            }
            @Override
            public void onFailure(Call<PMServiceInfoDetailModel> call, Throwable t) {
             //   Toast.makeText(getApplicationContext(), id + "response failure order by id", Toast.LENGTH_LONG).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharePrefrence.setLock(false);
    }

}
