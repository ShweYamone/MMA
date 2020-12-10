package com.freelance.solutionhub.mma.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.CaptureActivityPotrait;
import com.freelance.solutionhub.mma.activity.Code_Description;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Second_Step_CM_Fragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.tv_acknowledged_by)
    TextView tvAcknowledgeBy;

    @BindView(R.id.tv_acknowledge_datetime)
    TextView tvAcknowledgeDT;

    @BindView(R.id.tv_response_datetime)
    TextView tvResponseDT;

    @BindView(R.id.tvReportedProblemCode)
    TextView tvReportedCode;

    @BindView(R.id.spinnerActualProblemCode)
    Spinner spinnerActualProbleCode;

    @BindView(R.id.spinnerCauseCode)
    Spinner spinnerCauseCode;

    @BindView(R.id.spinnerRemedyCode)
    Spinner spinnerRemedyCode;

    @BindView(R.id.tv_third_party_fault)
    TextView tvThirdPartyFault;

    @BindView(R.id.iv_add_third_party)
    ImageView ivAddThirdPary;

    @BindView(R.id.layoutThirdParty)
    LinearLayout layoutThirdParty;

    @BindView(R.id.tv_third_party_fault_desc)
    TextView tvThirdPartyFaultDesc;

    @BindView(R.id.layourPartReplacement)
    LinearLayout layoutPartReplacement;

    @BindView(R.id.iv_required_part_placement)
    ImageView ivRequiredPartPlacement;

    @BindView(R.id.tv_scan_fault)
    TextView tvScanFault;

    @BindView(R.id.tv_scan_replacement)
    TextView tvScanReplacement;

    @BindView(R.id.btn_scan_fault)
    Button btnScanFault;

    @BindView(R.id.btn_scan_replacement)
    Button btnScanReplacement;

    @BindView(R.id.btnSave)
    Button btnSave;

    private PMServiceInfoDetailModel pmServiceInfoModel;
    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePreference;

    private ArrayAdapter actualProblemArrAdapter;
    private ArrayAdapter causeProblemArrAdapter;
    private ArrayAdapter remedyArrAdapter;
    private Date date;private Timestamp ts;
    String currentDateTime;

    //qr code scanner object
    private IntentIntegrator qrScan;

    private List<Event> events = new ArrayList<>();


    List<Code_Description> problemList = new ArrayList<>();
    Map<String,List<Code_Description>> problemCauseMap = new HashMap<>();
    Map<String,List<Code_Description>> causeRemedyMap = new HashMap<>();

    List<String> problemArr = new ArrayList<>(); String[] actualProblemCode = new String[0];
    List<String> causeArr = new ArrayList<>(); String[] causeProblemCode = new String[0];
    List<String> remedyArr = new ArrayList<>();

    private String actualProblem, causeCode, remedyCode = "";
    private boolean thirdPartyShow = true;
    private boolean replacementShow = true;

    private boolean qrScan1Click = true;
    private boolean hasEventToUpdate = false;

    public Second_Step_CM_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        pmServiceInfoModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));
        View view =  inflater.inflate(R.layout.fragment_second__step__c_m_, container, false);
        mSharePreference = new SharePreferenceHelper(getContext());
        apiInterface = ApiClient.getClient(this.getContext());
        ButterKnife.bind(this, view);

        actualProblemArrAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, problemArr);
        causeProblemArrAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, causeArr);
        remedyArrAdapter = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, remedyArr);
        actualProblemArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        causeProblemArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        remedyArrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerActualProbleCode.setAdapter(actualProblemArrAdapter);
        spinnerCauseCode.setAdapter(causeProblemArrAdapter);
        spinnerRemedyCode.setAdapter(remedyArrAdapter);

        spinnerActualProbleCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                causeProblemCode = new String[problemCauseMap.get(actualProblemCode[i]).size()];
                causeArr.clear();

                Code_Description temp;
                for (int k = 0; k < causeProblemCode.length; k++) {
                    temp = problemCauseMap.get(actualProblemCode[i]).get(k);
                    causeProblemCode[k] = temp.getCode();
                    causeArr.add(temp.getDescription());
                }
                causeProblemArrAdapter.notifyDataSetChanged();

                remedyArr.clear();
                for (int k = 0; k < causeRemedyMap.get(causeProblemCode[0]).size(); k++) {
                    temp = causeRemedyMap.get(causeProblemCode[0]).get(k);
                    remedyArr.add(temp.getDescription());
                }
                remedyArrAdapter.notifyDataSetChanged();

                actualProblem = problemArr.get(i);

                if (!causeArr.isEmpty()) {
                    causeCode = causeArr.get(0);
                } else {
                    causeCode = "";
                }
                if (!remedyArr.isEmpty()) {
                    remedyCode = remedyArr.get(0);
                } else {
                    remedyCode = "";
                }
                Log.i("FaultMappingJSONEx", actualProblem + ", " + causeCode + ", " + remedyCode);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerCauseCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                remedyArr.clear();
                Code_Description temp;
                for (int k = 0; k < causeRemedyMap.get(causeProblemCode[0]).size(); k++) {
                    temp = causeRemedyMap.get(causeProblemCode[0]).get(k);
                    remedyArr.add(temp.getDescription());
                }
                remedyArrAdapter.notifyDataSetChanged();

                causeCode = causeArr.get(i);

                if (!remedyArr.isEmpty()) {
                    remedyCode = remedyArr.get(0);
                } else {
                    remedyCode = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerRemedyCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                remedyCode = remedyArr.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        displayMaintenanceWorkInformation();

        ivAddThirdPary.setOnClickListener(this);
        ivRequiredPartPlacement.setOnClickListener(this);
        btnScanFault.setOnClickListener(this);
        btnScanReplacement.setOnClickListener(this);
        btnSave.setOnClickListener(this);

        //intializing scan object
        qrScan = new IntentIntegrator(this.getActivity());
        qrScan.setPrompt("Scan QR Code");
        qrScan.setCameraId(0);  // Use a specific camera of the device
        qrScan.setOrientationLocked(true);
        qrScan.setBeepEnabled(true);
        qrScan.setCaptureActivity(CaptureActivityPotrait.class);
        return view;
    }

    private void displayMaintenanceWorkInformation() {
        tvAcknowledgeBy.setText(pmServiceInfoModel.getAcknowledgedBy());
        tvAcknowledgeDT.setText(pmServiceInfoModel.getAcknowledgementDate());
        tvReportedCode.setText(pmServiceInfoModel.getReportedProblem());
        Call<ResponseBody> call = apiInterface.getFaultMappings("Bearer " + mSharePreference.getToken());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("FaultMapping", "onResponse: " + response.code());
                if (response.isSuccessful()) {
                    try {
                        setFaultMappingSpinner(new JSONObject(response.body().string()));

                    } catch (JSONException e) {
                        Log.i("Exception", "onResponse: " + e.getMessage());
                    } catch (IOException e) {
                        Log.i("Exception", "onResponse: " + e.getMessage());
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.i("FaultMapping", "onResponse: ");
            }
        });


    }

    private void setFaultMappingSpinner(JSONObject object) {
        Log.i("FaultMapping", "onResponse: ");
        try {
            JSONArray problemJSONArr = (JSONArray)object.get("problems");
            JSONObject problemCauseJSONObj = (JSONObject)object.get("problemCauseMap");
            JSONObject causeRemedyObj = (JSONObject)object.get("causeRemedyMap");

            JSONObject tempOjb; JSONArray tempArr;
            for (int i = 0; i < problemJSONArr.length(); i++) {
                tempOjb = (JSONObject)problemJSONArr.get(i);
                problemList.add(new Code_Description((String)tempOjb.get("code"), (String)tempOjb.get("description")));
            }
            Iterator<String> keys = problemCauseJSONObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                tempArr = (JSONArray)problemCauseJSONObj.get(key);
                List<Code_Description> tempList = new ArrayList<>();
                for (int i = 0; i < tempArr.length(); i++) {
                    tempOjb = (JSONObject)tempArr.get(i);
                    tempList.add(new Code_Description((String)tempOjb.get("code"), (String)tempOjb.get("description")));
                }
                problemCauseMap.put(key, tempList);
            }


            keys = causeRemedyObj.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                tempArr = (JSONArray)causeRemedyObj.get(key);
                List<Code_Description> tempList = new ArrayList<>();
                for (int i = 0; i < tempArr.length(); i++) {
                    tempOjb = (JSONObject)tempArr.get(i);
                    tempList.add(new Code_Description((String)tempOjb.get("code"), (String)tempOjb.get("description")));
                }
                causeRemedyMap.put(key, tempList);
            }

            problemArr.clear();
            actualProblemCode = new String[problemList.size()];
            for (int i = 0; i < problemList.size(); i++) {
                actualProblemCode[i] = problemList.get(i).getCode();
                problemArr.add(problemList.get(i).getDescription());
            }
            actualProblemArrAdapter.notifyDataSetChanged();
            actualProblem = problemArr.get(0);

        } catch (JSONException e) {
            Log.i("FaultMappingJSONEx", "setFaultMappingSpinner: " + e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_third_party:
                if (thirdPartyShow) {
                    Glide.with(this)
                            .load(R.drawable.check)
                            .into(ivAddThirdPary);
                    layoutThirdParty.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(this)
                            .load(R.drawable.check_blank)
                            .into(ivAddThirdPary);
                    layoutThirdParty.setVisibility(View.GONE);
                }
                thirdPartyShow = !thirdPartyShow;
                break;
            case R.id.iv_required_part_placement:
                if (replacementShow){
                    Glide.with(this)
                            .load(R.drawable.check)
                            .into(ivRequiredPartPlacement);
                    layoutPartReplacement.setVisibility(View.VISIBLE);
                } else {
                    Glide.with(this)
                            .load(R.drawable.check_blank)
                            .into(ivRequiredPartPlacement);
                    layoutPartReplacement.setVisibility(View.GONE);
                }
                replacementShow = !replacementShow;
                break;
            case R.id.btn_scan_fault:
                qrScan1Click = true;
                //initiating the qr code scan
                qrScan.initiateScan();
                break;
            case R.id.btn_scan_replacement:
                qrScan1Click = false;
                qrScan.initiateScan();
                break;
            case R.id.btnSave:
                date = new Date();
                ts=new Timestamp(date.getTime());
                currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
                getProblemCodeEvent();
                getQREvent();
                updateEvents();
                break;
        }
    }

    private void getQREvent() {
        List<Event> events = new ArrayList<>();
        if (!tvScanFault.getText().equals("")) {
            hasEventToUpdate = true;
            events.add(
                    new Event("PART_REPLACEMENT_UPDATE",
                            "faultPartCode",
                            tvScanFault.getText().toString())
            );
            Log.i("EventHappenend", "getProblemCodeEvent: " + tvScanFault.getText().toString());
        }
        if (!tvScanReplacement.getText().equals("")) {
            events.add(
                    new Event("PART_REPLACEMENT_UPDATE",
                    "replacementPartCode",
                    tvScanReplacement.getText().toString())
            );
            hasEventToUpdate = true;
            Log.i("EventHappenend", "getProblemCodeEvent: " + tvScanReplacement.getText().toString());
        }

    }

    public void updateQRCode(String qrCodeStr) {
        if (qrScan1Click) {
            tvScanFault.setText(qrCodeStr);
        } else {
            tvScanReplacement.setText(qrCodeStr);
        }
    }

    public void getProblemCodeEvent() {
        List<Event> events = new ArrayList<>();
        events.add(new Event(
                "SERVICE_ORDER_UPDATE",
                "reportedProblem",
                mSharePreference.getUserId() + " - " + pmServiceInfoModel.getReportedProblemDescription()
        ));
        if (spinnerActualProbleCode.getSelectedItemPosition() >= 0) {
            hasEventToUpdate = true;
            events.add(new Event(
                    "SERVICE_ORDER_UPDATE",
                    "actualProblem" ,
                    mSharePreference.getUserId() + " - " + actualProblem));
            Log.i("EventHappenend", "getProblemCodeEvent: " + actualProblem);
        }
        if (spinnerCauseCode.getSelectedItemPosition() >= 0) {
            hasEventToUpdate = true;
            events.add(new Event(
                    "SERVICE_ORDER_UPDATE",
                    "cause",
                    mSharePreference.getUserId() + " - " + causeCode));
            Log.i("EventHappenend", "getProblemCodeEvent: " + causeCode);
        }
        if (spinnerRemedyCode.getSelectedItemPosition() >= 0) {
            hasEventToUpdate = true;
            events.add(new Event(
                    "SERVICE_ORDER_UPDATE",
                    "remedy",
                    mSharePreference.getUserId() + " - " + remedyCode));
            Log.i("EventHappenend", "getProblemCodeEvent: " + remedyCode);
        }


    }

    private void updateEvents() {
        UpdateEventBody eventBody = new UpdateEventBody(
                mSharePreference.getUserName(),
                mSharePreference.getUserId(),
                currentDateTime,
                pmServiceInfoModel.getId(),
                events
        );

        Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePreference.getToken(),
                eventBody);
        call.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "ProblemUpdateEvent" + response.body().getStatus() , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {

            }
        });
    }


}
