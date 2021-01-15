package com.digisoft.mma.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.activity.LoginActivity;
import com.digisoft.mma.adapter.ServiceOrderAdapter;
import com.digisoft.mma.common.SmartScrollListener;
import com.digisoft.mma.delegate.HomeFragmentCallback;
import com.digisoft.mma.model.Data;
import com.digisoft.mma.model.FaultMappingJSONString;
import com.digisoft.mma.model.FilterModelBody;
import com.digisoft.mma.model.ServiceInfoModel;
import com.digisoft.mma.model.PMServiceListModel;
import com.digisoft.mma.model.ReturnStatus;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.model.UserProfile;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digisoft.mma.util.AppConstant.ACK;
import static com.digisoft.mma.util.AppConstant.ALL;
import static com.digisoft.mma.util.AppConstant.APPR;
import static com.digisoft.mma.util.AppConstant.BEARER;
import static com.digisoft.mma.util.AppConstant.CORRECTIVE;
import static com.digisoft.mma.util.AppConstant.DEFAULT;
import static com.digisoft.mma.util.AppConstant.FAILURE;
import static com.digisoft.mma.util.AppConstant.INPRG;
import static com.digisoft.mma.util.AppConstant.MONTHLY;
import static com.digisoft.mma.util.AppConstant.PREVENTATIVE;
import static com.digisoft.mma.util.AppConstant.QUARTERLY;
import static com.digisoft.mma.util.AppConstant.RESPONSE_UNSUCCESS;
import static com.digisoft.mma.util.AppConstant.TIME_SERVER;
import static com.digisoft.mma.util.AppConstant.UPLOAD_ERROR;
import static com.digisoft.mma.util.AppConstant.WEEKLY;
import static com.digisoft.mma.util.AppConstant.WSCH;
import static com.digisoft.mma.util.AppConstant.YEARLY;

public class HomeFragment extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, HomeFragmentCallback {

    @BindView(R.id.cvCorrectiveMaintenance)
    CardView cvCorrectiveMaintenance;

    @BindView(R.id.cvPreventiveMaintenance)
    CardView cvPreventiveMainennance;

    @BindView(R.id.layoutMaintenaceCV)
    LinearLayout layoutMaintenaceCV;

    @BindView(R.id.layoutMaintenaceList)
    LinearLayout layoutMaintenanceList;

    @BindView(R.id.recyclerviewMaintenaceList)
    RecyclerView recyclerViewMaintenance;

    @BindView(R.id.cvPreventive)
    CardView cvPreventive;

    @BindView(R.id.cvCorrective)
    CardView cvCorrective;

    @BindView(R.id.ivCorrective)
    ImageView ivCorrective;

    @BindView(R.id.ivPreventive)
    ImageView ivPreventive;

    @BindView(R.id.tvCorrective)
    TextView tvCorrective;

    @BindView(R.id.tvPreventive)
    TextView tvPreventive;

    @BindView(R.id.spinnerStatus)
    Spinner spinnerStatus;

    @BindView(R.id.tvPMCount)
    TextView tvPMCount;

    @BindView(R.id.tvCMCount)
    TextView tvCMCount;

    @BindView(R.id.progress_bar)
    RelativeLayout progressBar;

    @BindView(R.id.rgPM)
    RadioGroup rgPM;

    @BindView(R.id.rgCM)
    RadioGroup rgCM;

    @BindView(R.id.rbPMAll)
    RadioButton rbPMAll;

    @BindView(R.id.rbPMINPRG)
    RadioButton rbPMINPRG;

    @BindView(R.id.rbPMWCSH)
    RadioButton rbPMWCSH;

    @BindView(R.id.rbCMAll)
    RadioButton rbCMAll;

    @BindView(R.id.rbCMACK)
    RadioButton rbCMACK;

    @BindView(R.id.rbCMAPPR)
    RadioButton rbCMAPPR;

    @BindView(R.id.rbCMInprg)
    RadioButton rbCMInprg;

    @BindView(R.id.tvRecord)
    TextView tvRecord;

    @BindView(R.id.rgSchedulType)
    RadioGroup rgSchedultType;

    @BindView(R.id.rbScheduleAll)
    RadioButton rbScheduleAll;

    @BindView(R.id.rbScheduleWeek)
    RadioButton rbScheduleWeek;

    @BindView(R.id.rbScheduleMonth)
    RadioButton rbScheduleMonth;

    @BindView(R.id.rbScheduleQuarter)
    RadioButton rbScheduleQuarter;

    @BindView(R.id.rbScheduleYear)
    RadioButton rbScheduleYear;

    private ServiceOrderAdapter mAdapter;
    private List<ServiceInfoModel> serviceInfoModelList = new ArrayList<>();
    private ApiInterface apiInterface;
    private SmartScrollListener mSmartScrollListener;
    private SharePreferenceHelper mSharePreference;
    private FilterModelBody filterModelBody;
    private Network network;

    private int page = 1;
    private int totalPages = 0;
    private String enumValue;
    private String textValue;
    private String filterExpression;
    private InitializeDatabase dbHelper;

    PMServiceListModel pmServiceListModel;
    List<ServiceInfoModel> serviceInfoModels;
    List<String> textValueList = new ArrayList<>();
    private int totalRecords = 0;
    private String scheduleType = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mSharePreference = new SharePreferenceHelper(getContext());
        mSharePreference.userClickCMStepOne(false);
        mSharePreference.userClickCMStepTwo(false);
        mSharePreference.userClickPMStepOne(false);
        apiInterface = ApiClient.getClient(getContext());
        network = new Network(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());

        ButterKnife.bind(this, view);
        cvCorrectiveMaintenance.setOnClickListener(this);
        cvPreventiveMainennance.setOnClickListener(this);
        cvCorrective.setOnClickListener(this);
        cvPreventive.setOnClickListener(this);

        mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
            @Override
            public void onListEndReach() {
                page++;
                if (page <= totalPages)
                    getServiceOrders(scheduleType);
            }
        });

        mAdapter = new ServiceOrderAdapter(this.getContext().getApplicationContext(), serviceInfoModelList, this, mSharePreference);

        recyclerViewMaintenance.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewMaintenance.setLayoutManager(mLayoutManager);
        //recyclerViewMaintenance.setAdapter(mAdapter);
        recyclerViewMaintenance.addOnScrollListener(mSmartScrollListener);
        recyclerViewMaintenance.setAdapter(mAdapter);


        if (network.isNetworkAvailable()) {
            setServiceOrderCount();
            getFaultMappingData();
        } else {
            progressBar.setVisibility(View.GONE);
            //handle with local db
        }

        return view;
    }

    /**
     * Fault Mapping Data for Second Step CM , for later use
     */
    private void getFaultMappingData() {
        Call<ResponseBody> call = apiInterface.getFaultMappings(BEARER + mSharePreference.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("FaultMapping", "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    if (mSharePreference.getUserId().equals("")) {
                        getUserIdAndUserDisplayName();
                    }
                    try {
                        //first delete and insert new fault-mapping data for later use
                        dbHelper.faultMappingDAO().delete();
                        dbHelper.faultMappingDAO().insert(
                                new FaultMappingJSONString(
                                        response.body().string()));
                    } catch (IOException e) {
                        Log.e(UPLOAD_ERROR, "" + e.getMessage() );
                    }
                }
                progressBar.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.i(UPLOAD_ERROR, FAILURE + "");
            }
        });
    }

    private void getUserIdAndUserDisplayName() {
        Call<UserProfile> call1 = apiInterface.getUserProfile(BEARER + mSharePreference.getToken());
        call1.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                if (response.isSuccessful()) {
                    Data userData = response.body().getData();
                    mSharePreference.setUserIdAndDisplayName(userData.getUserId(), userData.getDisplayName());
                  //  Toast.makeText(getContext().getApplicationContext(), mSharePreference.getUserId() + ", " + mSharePreference.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    Log.e(UPLOAD_ERROR, RESPONSE_UNSUCCESS + "");
                    //Toast.makeText(getContext().getApplicationContext(), response.code() + ", ", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Log.e(UPLOAD_ERROR, FAILURE + "");
             //   Toast.makeText(getContext().getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });

    }


    @Override
    public boolean hasUserId() {
        return !(mSharePreference.getUserId().equals(""));
    }

    class getCurrentNetworkTime extends AsyncTask<String, Void, Boolean> {

        String serviceId;
        int position;

        public getCurrentNetworkTime(String serviceId, int position) {
            this.serviceId = serviceId;
            this.position = position;
        }

        //APPR TO ACK
        private void updateEvent(String date) {
            UpdateEventBody updateEventBody = new UpdateEventBody(mSharePreference.getUserName(), mSharePreference.getUserId(),
                    date,
                    serviceId,ACK
            );

            Call<ReturnStatus> call = apiInterface.updateStatusEvent(BEARER + mSharePreference.getToken(), updateEventBody);
            call.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                    if (response.isSuccessful()) {
                        ReturnStatus returnStatus = response.body();
                //        Toast.makeText(getContext().getApplicationContext(), returnStatus.getStatus()+"APPRtoACK at time" + date, Toast.LENGTH_SHORT).show();
                        update_APPR_To_ACK_UI(position);
                    } else {
                        Log.e(UPLOAD_ERROR, RESPONSE_UNSUCCESS + "");
                  //      Toast.makeText(getContext().getApplicationContext(), response.code()+"APPRtoACK", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {
                    Log.e(UPLOAD_ERROR, FAILURE + "");
                }
            });
        }

        protected Boolean doInBackground(String... urls) {
            boolean is_locale_date = false;
            try {
                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.open();
                timeClient.setDefaultTimeout(3000);
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
                //after getting network time, update event with the network time
                updateEvent(actualDateTime);
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

        protected void onPostExecute(boolean local_date) {
            if(!local_date) {
                Log.e("Check ", "dates not equal" + local_date);
            }
        }
    }

    public  void update_ARRP_To_ACK(String serviceOrderId, int position) {
        //get time from server first
       new getCurrentNetworkTime(serviceOrderId, position).execute();
    }

    public void update_APPR_To_ACK_UI(int position){
        serviceInfoModelList.get(position).setServiceOrderStatus(ACK);
        mAdapter.notifyItemChanged(position);
    }

    private void setServiceOrderCount() {
        textValueList.clear();
        textValueList.add(INPRG); textValueList.add(APPR); textValueList.add(ACK);
        filterModelBody = FilterModelBody.createFilterModel("in", CORRECTIVE, textValueList, 1);
        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders(BEARER + mSharePreference.getToken(), filterModelBody);
        call.enqueue(new Callback<PMServiceListModel>() {
            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    pmServiceListModel = response.body();
                    int tempCount = pmServiceListModel.getTotalElements();
                    if (tempCount == 0) {
                        tvCMCount.setText("0");
                    } else if (tempCount < 10) {
                        tvCMCount.setText("0" + tempCount);
                    } else if (tempCount < 100){
                        tvCMCount.setText(tempCount+"");
                    } else
                        tvCMCount.setText("99+");
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
                Log.e(UPLOAD_ERROR, FAILURE + "");
            }
        });

        textValueList.clear();
        textValueList.add(WSCH); textValueList.add(INPRG);
        filterModelBody = FilterModelBody.createFilterModel("in", PREVENTATIVE, textValueList, 1);
        call = apiInterface.getPMServiceOrders(BEARER + mSharePreference.getToken(), filterModelBody);
        call.enqueue(new Callback<PMServiceListModel>() {
            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    pmServiceListModel = response.body();
                    int tempCount = pmServiceListModel.getTotalElements();
                    if (tempCount == 0) {
                        tvPMCount.setText("0");
                    } else if (tempCount < 10) {
                        tvPMCount.setText("0" + tempCount);
                    } else if (tempCount < 100){
                        tvPMCount.setText(tempCount+"");
                    } else
                        tvPMCount.setText("99+");
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
                Log.e(UPLOAD_ERROR, FAILURE + "");
            }
        });
    }


    private void getServiceOrders(String scheduleType) {
        if (scheduleType.equals(""))
            filterModelBody = FilterModelBody.createFilterModel(filterExpression, enumValue, textValueList, page);
        else
            filterModelBody = FilterModelBody.createFilterModelWithScheduleType(scheduleType, filterExpression, enumValue, textValueList, page);

        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders(BEARER + mSharePreference.getToken(), filterModelBody);

        call.enqueue(new Callback<PMServiceListModel>() {

            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    PMServiceListModel pmList = response.body();
                    totalPages = pmList.getTotalPages();
                    if (page == 1) {
                        tvRecord.setText("   " + pmList.getTotalElements()+" records   ");
                    }
                   // totalRecords = pmList.getNumberOfElements();
                 //   Toast.makeText(getContext(), "totalPages : " + totalPages + ", Current Page:" + page + "->" + pmList.getItems().size(),Toast.LENGTH_SHORT).show();
                    serviceInfoModels = pmList.getItems();
                    serviceInfoModelList.addAll(serviceInfoModels);
                    mAdapter.notifyDataSetChanged();
                }
                else if (response.code()==401){
                  //  Intent intent = new Intent(getContext(), LoadingActivity.class);
                 //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                    mSharePreference.logoutSharePreference();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
                Log.e(UPLOAD_ERROR, FAILURE + "");
//                Toast.makeText(getContext(), "connection failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        page = 1;
        filterExpression = "in";
        switch (view.getId()) {
            case R.id.cvCorrectiveMaintenance:
                layoutMaintenaceCV.setVisibility(View.GONE);
                layoutMaintenanceList.setVisibility(View.VISIBLE);
                showCorrectiveMaintenance();
                break;
            case R.id.cvPreventiveMaintenance:
                layoutMaintenaceCV.setVisibility(View.GONE);
                layoutMaintenanceList.setVisibility(View.VISIBLE);
                showPreventiveMaintenance();
                break;
            case R.id.cvCorrective:
                showCorrectiveMaintenance();
                break;
            case R.id.cvPreventive:
                showPreventiveMaintenance();
                break;
            default:
                Log.i(DEFAULT, "onClick: ");
        }

        setRadioButtonColorToBlack();
        setScheduleRadioButtonColorToBlack();
        //textValue = JOBDONE;
        rgCM.setOnCheckedChangeListener(this);
        rgPM.setOnCheckedChangeListener(this);
        rgSchedultType.setOnCheckedChangeListener(this);
        rbCMAll.setTextColor(getResources().getColor(R.color.colorWhite));
        rbPMAll.setTextColor(getResources().getColor(R.color.colorWhite));
        rbScheduleAll.setChecked(true);
        rbScheduleAll.setTextColor(getResources().getColor(R.color.colorWhite));
    }

    private void showCorrectiveMaintenance() {
        textValueList.clear();
        enumValue = CORRECTIVE;
        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvCorrective.setTextColor(getResources().getColor(R.color.colorBlack));

        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvPreventive.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        rgCM.setVisibility(View.VISIBLE);
        rgPM.setVisibility(View.GONE);

        //list = new String[]{ALL, APPR, ACK, INPRG};
        textValueList.add(INPRG); textValueList.add(APPR); textValueList.add(ACK);
        rbCMAll.setChecked(true);
        rbCMAll.setTextColor(getResources().getColor(R.color.colorWhite));
        rgSchedultType.setVisibility(View.GONE);
        scheduleType = "";
        getServiceOrders(scheduleType);
    }

    private void showPreventiveMaintenance() {
        textValueList.clear();
        enumValue = PREVENTATIVE;
        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvPreventive.setTextColor(getResources().getColor(R.color.colorBlack));

        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvCorrective.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        rgCM.setVisibility(View.GONE);
        rgPM.setVisibility(View.VISIBLE);

        //list = new String[]{ALL, WSCH, INPRG};
        textValueList.add(WSCH); textValueList.add(INPRG);
        rbPMAll.setChecked(true);
        rbPMAll.setTextColor(getResources().getColor(R.color.colorWhite));
        rgSchedultType.setVisibility(View.VISIBLE);
        scheduleType = "";
        getServiceOrders(scheduleType);
    }

    private void setRadioButtonColorToBlack() {
        rbCMAll.setTextColor(getResources().getColor(R.color.colorBlack));
        rbCMACK.setTextColor(getResources().getColor(R.color.colorBlack));
        rbCMAPPR.setTextColor(getResources().getColor(R.color.colorBlack));
        rbCMInprg.setTextColor(getResources().getColor(R.color.colorBlack));
        rbPMAll.setTextColor(getResources().getColor(R.color.colorBlack));
        rbPMWCSH.setTextColor(getResources().getColor(R.color.colorBlack));
        rbPMINPRG.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    private void setScheduleRadioButtonColorToBlack() {
        rbScheduleAll.setTextColor(getResources().getColor(R.color.colorBlack));
        rbScheduleWeek.setTextColor(getResources().getColor(R.color.colorBlack));
        rbScheduleMonth.setTextColor(getResources().getColor(R.color.colorBlack));
        rbScheduleQuarter.setTextColor(getResources().getColor(R.color.colorBlack));
        rbScheduleYear.setTextColor(getResources().getColor(R.color.colorBlack));
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        page = 1;
        Log.i("CheckChanged", "onCheckedChanged: " + radioGroup.getId() + "int i = " + i);

        if (radioGroup.getId() == R.id.rgCM) {
            setRadioButtonColorToBlack();
            scheduleType = "";
            switch (i) {
                case R.id.rbCMAll:
                    filterExpression = "in";
                    textValueList.clear();
                    textValueList.add(INPRG); textValueList.add(APPR); textValueList.add(ACK);
                    rbCMAll.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbCMACK :
                    textValueList.clear();
                    filterExpression = "in";
                    textValueList.add(ACK);
                    rbCMACK.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbCMAPPR:
                    textValueList.clear();
                    filterExpression = "in";
                    textValueList.add(APPR);
                    rbCMAPPR.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbCMInprg:
                    textValueList.clear();
                    filterExpression = "in";
                    textValueList.add(INPRG);
                    rbCMInprg.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
            }

        } else if (radioGroup.getId() == R.id.rgPM){
            setRadioButtonColorToBlack();
            switch (i) {
                case R.id.rbPMAll:
                    filterExpression = "in";
                    textValueList.clear();
                    textValueList.add(WSCH); textValueList.add(INPRG);
                    rbPMAll.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbPMWCSH :
                    textValueList.clear();
                    filterExpression = "in";
                    textValueList.add(WSCH);
                    rbPMWCSH.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbPMINPRG:
                    textValueList.clear();
                    filterExpression = "in";
                    textValueList.add(INPRG);
                    rbPMINPRG.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                default:
                    Log.i(DEFAULT, "onCheckedChanged: ");
            }
        }
        else {
            setScheduleRadioButtonColorToBlack();
            switch (i) {
                case R.id.rbScheduleAll:
                    scheduleType = "";
                    rbScheduleAll.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbScheduleWeek: scheduleType = WEEKLY;
                    rbScheduleWeek.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbScheduleMonth: scheduleType = MONTHLY;
                    rbScheduleMonth.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbScheduleQuarter: scheduleType = QUARTERLY;
                    rbScheduleQuarter.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                case R.id.rbScheduleYear: scheduleType = YEARLY;
                    rbScheduleYear.setTextColor(getResources().getColor(R.color.colorWhite));
                    break;
                default:
                    Log.i(DEFAULT, "onCheckedChanged: ");
            }
        }
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        getServiceOrders(scheduleType);
    }

    @Override
    public void onResume() {
        super.onResume();
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        page = 1;
        getServiceOrders(scheduleType);
    }


}
