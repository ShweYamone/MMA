package com.freelance.solutionhub.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Update;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.CMActivity;
import com.freelance.solutionhub.mma.activity.CaptureActivityPotrait;
import com.freelance.solutionhub.mma.model.Code_Description;
import com.freelance.solutionhub.mma.activity.OtherActivity;
import com.freelance.solutionhub.mma.activity.PowerGridActivity;
import com.freelance.solutionhub.mma.activity.TelcoActivity;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PhotoAttachementModel;
import com.freelance.solutionhub.mma.model.PhotoModel;
import com.freelance.solutionhub.mma.model.PreMaintenance;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.ThirdPartyModel;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.UploadPhotoModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.google.zxing.integration.android.IntentIntegrator;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.ACTUAL_PROBLEM;
import static com.freelance.solutionhub.mma.util.AppConstant.CAUSE;
import static com.freelance.solutionhub.mma.util.AppConstant.CM;
import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_ONE;
import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_TWO;
import static com.freelance.solutionhub.mma.util.AppConstant.COMMENT;
import static com.freelance.solutionhub.mma.util.AppConstant.FAULT_PART_CODE;
import static com.freelance.solutionhub.mma.util.AppConstant.NO;
import static com.freelance.solutionhub.mma.util.AppConstant.PART_REPLACEMENT_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.POST_BUCKET_NAME;
import static com.freelance.solutionhub.mma.util.AppConstant.PRE_BUCKET_NAME;
import static com.freelance.solutionhub.mma.util.AppConstant.REMEDY;
import static com.freelance.solutionhub.mma.util.AppConstant.REPLACEMENT_PART_CODE;
import static com.freelance.solutionhub.mma.util.AppConstant.REPORTED_PROBLEM;
import static com.freelance.solutionhub.mma.util.AppConstant.SERVICE_ORDER_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.THIRD_PARTY_COMMENT_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.TIME_SERVER;
import static com.freelance.solutionhub.mma.util.AppConstant.YES;
import static com.freelance.solutionhub.mma.util.AppConstant.pm;

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

    @BindView(R.id.iv_attach_post_maintenance_photo)
    ImageView postPhotoBtn;

    @BindView(R.id.recyclerview_post_photo)
    RecyclerView postRecyclerView;

    @BindView(R.id.etThirdPartyComment)
    EditText etThridPartyComment;

    @BindView(R.id.ll_telco)
    LinearLayout telco;

    @BindView(R.id.ll_power_grid)
    LinearLayout powerGrid;

    @BindView(R.id.ll_other)
    LinearLayout other;

    @BindView(R.id.btnSave)
    Button btnSave;

    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private Bitmap bitmap;
    private String photo;
    private PhotoAdapter postPhotoAdapter;
    private ArrayList<PhotoModel> postModelList;
    private PMServiceInfoDetailModel pmServiceInfoModel;
    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePreference;
    private Network network;
    private InitializeDatabase dbHelper;

    private ArrayAdapter actualProblemArrAdapter;
    private ArrayAdapter causeProblemArrAdapter;
    private ArrayAdapter remedyArrAdapter;
    private Date date;private Timestamp ts;
    String currentDateTime;

    //qr code scanner object
    private IntentIntegrator qrScan;



    List<Code_Description> problemList = new ArrayList<>();
    Map<String,List<Code_Description>> problemCauseMap = new HashMap<>();
    Map<String,List<Code_Description>> causeRemedyMap = new HashMap<>();

    List<String> problemArr = new ArrayList<>(); List<String> actualProblemCode = new ArrayList<>();
    List<String> causeArr = new ArrayList<>(); List<String> causeProblemCode = new ArrayList<>();
    List<String> remedyArr = new ArrayList<>(); List<String> remedyProblemCode = new ArrayList<>();

    private String actualProblem, causeCode, remedyCode = "";
    private boolean thirdPartyShow = false;
    private boolean replacementShow = false;

    private boolean qrScan1Click = true;
    private boolean isMandatoryFieldLeft = false;
    private String mandatoryFieldsLeft = "";

    private String preActualProblemCode = "";
    private String preCauseCode = "";
    private String preRemedyCode = "";
    private String preScan1Result = "";
    private String preScan2Result = "";
    private String preThirdPartyComment = "";
    private boolean spinnerCauseAutoSelect = false;
    private boolean spinnerRemedyAutoSelect = false;

    private ThirdPartyModel telcoObj = new ThirdPartyModel();
    private ThirdPartyModel powerGripObj = new ThirdPartyModel();
    private ThirdPartyModel otherObj = new ThirdPartyModel();

    Intent intent;
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
        apiInterface = ApiClient.getClient(getContext());
        network = new Network(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());

        ButterKnife.bind(this, view);
        postModelList = new ArrayList<>();
        setDataAdapter();


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
                causeArr.clear(); causeProblemCode.clear();
                if (problemCauseMap.containsKey(actualProblemCode.get(i))) {
                    //   causeProblemCode = new String[];
                    int size = problemCauseMap.get(actualProblemCode.get(i)).size();
                    Code_Description temp;
                    //   causeProblemCode.add(0, "");
                    for (int k = 0; k < size; k++) {
                        temp = problemCauseMap.get(actualProblemCode.get(i))
                                .get(k);
                        causeProblemCode.add(temp.getCode());
                        causeArr.add(temp.getDescription());
                    }
                }
                causeProblemCode.add(0, "Select Cause Code");
                causeArr.add(0, "Select Cause Code");
                causeProblemArrAdapter.notifyDataSetChanged();

                remedyArr.clear();remedyProblemCode.clear();
                remedyProblemCode.add(0,"Select Remedy Code");
                remedyArr.add(0, "Select Remedy Code");
                remedyArrAdapter.notifyDataSetChanged();

                if (spinnerCauseAutoSelect) {
                    spinnerCauseCode.setSelection(causeProblemCode.indexOf(preCauseCode));
                    spinnerCauseAutoSelect = false;
                } else {
                    spinnerCauseCode.setSelection(0);
                }


                actualProblem = actualProblemCode.get(i);
                Log.i("spinner", "onCreateView: " + causeProblemCode.size());
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerCauseCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.i("SPINNER__", "onItemSelected: " + causeProblemCode.get(i));
                remedyArr.clear();remedyProblemCode.clear();
                if (causeRemedyMap.containsKey(causeProblemCode.get(i))) {
                    // remedyProblemCode = new String[causeRemedyMap.get(causeProblemCode).size() + 1];
                    Code_Description temp;
                    for (int k = 0; k < causeRemedyMap.get(causeProblemCode.get(i)).size(); k++) {
                        temp = causeRemedyMap.get(causeProblemCode.get(i)).get(k);
                        remedyProblemCode.add(temp.getCode());
                        remedyArr.add(temp.getDescription());
                    }
                }
                remedyProblemCode.add(0,"Select Remedy Code");
                remedyArr.add(0, "Select Remedy Code");
                remedyArrAdapter.notifyDataSetChanged();

                if (spinnerRemedyAutoSelect) {
                    spinnerRemedyCode.setSelection(remedyProblemCode.indexOf(preRemedyCode));
                    spinnerRemedyAutoSelect = false;
                } else {
                    spinnerRemedyCode.setSelection(0);
                }
                causeCode = causeProblemCode.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerRemedyCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                remedyCode = remedyProblemCode.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        Call<PhotoAttachementModel> photoAttachementModelCall = apiInterface.getPhotoAttachment("Bearer "+mSharePreference.getToken(), pmServiceInfoModel.getId());
//        photoAttachementModelCall.enqueue(new Callback<PhotoAttachementModel>() {
//            @Override
//            public void onResponse(Call<PhotoAttachementModel> call, Response<PhotoAttachementModel> response) {
//                PhotoAttachementModel photoAttachementModel = response.body();
//                if(response.isSuccessful()){
//                    List<PreMaintenance> preMaintenances = photoAttachementModel.getPreMaintenance();
//                    if(preMaintenances != null) {
//                        for (PreMaintenance e : preMaintenances) {
//                            Log.e("filepath", e.getFilePath());
//                            postModelList.add(new PhotoModel(e.getFilePath(), 2));
//
//                        }
//                        postPhotoAdapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<PhotoAttachementModel> call, Throwable t) {
//
//            }
//        });


        displayMaintenanceWorkInformation();


        displayData();
        displayImage();


        ivAddThirdPary.setOnClickListener(this);
        ivRequiredPartPlacement.setOnClickListener(this);
        btnScanFault.setOnClickListener(this);
        btnScanReplacement.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        postPhotoBtn.setOnClickListener(this);
        telco.setOnClickListener(this);
        powerGrid.setOnClickListener(this);
        other.setOnClickListener(this);

        //intializing scan object
        qrScan = new IntentIntegrator(this.getActivity());
        qrScan.setPrompt("Scan QR Code");
        qrScan.setCameraId(0);  // Use a specific camera of the device
        qrScan.setOrientationLocked(true);
        qrScan.setBeepEnabled(true);
        qrScan.setCaptureActivity(CaptureActivityPotrait.class);
        return view;
    }

    private void displayData() {
        spinnerCauseAutoSelect = true; spinnerRemedyAutoSelect = true;
        if (dbHelper.eventDAO().getEventValueCount(SERVICE_ORDER_UPDATE, ACTUAL_PROBLEM) > 0)
            preActualProblemCode = dbHelper.eventDAO().getEventValue(SERVICE_ORDER_UPDATE, ACTUAL_PROBLEM);
        else if (pmServiceInfoModel.getActualProblem() != null)
            preActualProblemCode = pmServiceInfoModel.getActualProblem();

        if (dbHelper.eventDAO().getEventValueCount(SERVICE_ORDER_UPDATE, CAUSE) > 0)
            preCauseCode = dbHelper.eventDAO().getEventValue(SERVICE_ORDER_UPDATE, CAUSE);
        else if (pmServiceInfoModel.getCause() != null)
            preCauseCode = pmServiceInfoModel.getCause();

        if (dbHelper.eventDAO().getEventValueCount(SERVICE_ORDER_UPDATE, REMEDY) > 0)
            preRemedyCode = dbHelper.eventDAO().getEventValue(SERVICE_ORDER_UPDATE, REMEDY);
        else if (pmServiceInfoModel.getRemedy() != null)
            preRemedyCode = pmServiceInfoModel.getRemedy();

        if (dbHelper.eventDAO().getEventValueCount(PART_REPLACEMENT_UPDATE, FAULT_PART_CODE) > 0)
            preScan1Result = dbHelper.eventDAO().getEventValue(PART_REPLACEMENT_UPDATE, FAULT_PART_CODE);
        else if (pmServiceInfoModel.isPartReplacement()) {
            if (pmServiceInfoModel.getPartReplacement().getFaultPartCode() != null) {
                preScan1Result = pmServiceInfoModel.getPartReplacement().getFaultPartCode()+"";
            }
        }

        if (dbHelper.eventDAO().getEventValueCount(PART_REPLACEMENT_UPDATE, REPLACEMENT_PART_CODE) > 0)
            preScan2Result = dbHelper.eventDAO().getEventValue(PART_REPLACEMENT_UPDATE, REPLACEMENT_PART_CODE);
        else if (pmServiceInfoModel.isPartReplacement()) {
            if (pmServiceInfoModel.getPartReplacement().getReplacementPartCode() != null)
                preScan2Result = pmServiceInfoModel.getPartReplacement().getReplacementPartCode()+"";
        }


        if (dbHelper.eventDAO().getEventValueCount(THIRD_PARTY_COMMENT_UPDATE, COMMENT) > 0)
            preThirdPartyComment = dbHelper.eventDAO().getEventValue(THIRD_PARTY_COMMENT_UPDATE, COMMENT);
        else if (pmServiceInfoModel.getThirdPartyFaultDescription() != null)
            preThirdPartyComment = pmServiceInfoModel.getThirdPartyFaultDescription();

        spinnerActualProbleCode.setSelection(actualProblemCode.indexOf(preActualProblemCode));

        tvScanFault.setText(preScan1Result);
        tvScanReplacement.setText(preScan2Result);
        etThridPartyComment.setText(preThirdPartyComment);
        if (preScan2Result.equals("")) {
            Glide.with(this)
                    .load(R.drawable.check_blank)
                    .into(ivRequiredPartPlacement);
            layoutPartReplacement.setVisibility(View.GONE);
            replacementShow = !replacementShow;
        }

        if (preThirdPartyComment.equals("")) {
            Glide.with(this)
                    .load(R.drawable.check_blank)
                    .into(ivAddThirdPary);
            layoutThirdParty.setVisibility(View.GONE);
            thirdPartyShow = !thirdPartyShow;
        }
    }

    private void displayImage() {
        postModelList.clear();
        // int imaCount = dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload();
        for (UploadPhotoModel uploadPhotoModel: dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(POST_BUCKET_NAME)) {
            postModelList.add(new PhotoModel(getDecodedString(uploadPhotoModel.getEncodedPhotoString()), 1));
        }
        postPhotoAdapter.notifyDataSetChanged();
    }

    private void displayMaintenanceWorkInformation() {
        tvAcknowledgeBy.setText(pmServiceInfoModel.getAcknowledgedBy());
        tvAcknowledgeDT.setText(pmServiceInfoModel.getAcknowledgementDate());
        tvReportedCode.setText(pmServiceInfoModel.getReportedProblemDescription());
        try {
            setFaultMappingSpinner(new JSONObject(dbHelper.faultMappingDAO().getFaultMappingJSON().get(0).getJsonString()));
        } catch (JSONException e) {
            Log.e("JSON Exception", "displayMaintenanceWorkInformation: " + e.getMessage() + "");
        }

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

            problemArr.clear(); actualProblemCode.clear();
            //actualProblemCode = new String[problemList.size() + 1];
            for (int i = 0; i < problemList.size(); i++) {
                actualProblemCode.add(problemList.get(i).getCode());
                problemArr.add(problemList.get(i).getDescription());
            }
            actualProblemCode.add(0, "Select Actual Problem Code");
            problemArr.add(0, "Select Actual Problem Code");
            actualProblemArrAdapter.notifyDataSetChanged();


        } catch (JSONException e) {
            Log.i("FaultMappingJSONEx", "setFaultMappingSpinner: " + e.getMessage());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_third_party:
                if (thirdPartyShow) {
                    layoutThirdParty.setVisibility(View.VISIBLE);
                } else {
                    layoutThirdParty.setVisibility(View.GONE);
                }
                thirdPartyShow = !thirdPartyShow;
                if (etThridPartyComment.getText().toString().equals("")) {
                    Glide.with(this)
                            .load(R.drawable.check_blank)
                            .into(ivAddThirdPary);
                } else {
                    Glide.with(this)
                            .load(R.drawable.check)
                            .into(ivAddThirdPary);
                }

                break;
            case R.id.iv_required_part_placement:
                if (replacementShow){
                    layoutPartReplacement.setVisibility(View.VISIBLE);
                } else {
                    layoutPartReplacement.setVisibility(View.GONE);
                }
                replacementShow = !replacementShow;
                if (tvScanReplacement.getText().toString().equals("")) {
                    Glide.with(this)
                            .load(R.drawable.check_blank)
                            .into(ivRequiredPartPlacement);
                } else {
                    Glide.with(this)
                            .load(R.drawable.check)
                            .into(ivRequiredPartPlacement);
                }
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
                isMandatoryFieldLeft = false;
                mandatoryFieldsLeft = "";
                getProblemCodeEvent();
                getQREvent();
                if (isMandatoryFieldLeft && postModelList.size() == 0) {
                    mSharePreference.userClickCMStepTwo(false);
                    mandatoryFieldsLeft += "\nAttach Post-Maintenance Photos";
                    showDialog();
                } else if (isMandatoryFieldLeft) {
                    mSharePreference.userClickCMStepTwo(false);
                    showDialog();
                } else if (postModelList.size() == 0) {
                    mSharePreference.userClickCMStepTwo(false);
                    mandatoryFieldsLeft = "\nAttach Post-Maintenance Photos";
                    showDialog();
                }
                else if (postModelList.size() < 2 || postModelList.size() > 6){
                    mSharePreference.userClickCMStepTwo(false);
                    mandatoryFieldsLeft = "Your photos must be minimum 2 and maximum 5.";
                    showDialog();
                }
                else {
                    //Mandatory QR and Problem event are choosen, can update events.......
                    //Mandatory Photos are attached, can update events.......
                    savePhotosToDB();
                    uploadEvents();



                    preActualProblemCode = actualProblem;
                    preCauseCode = causeCode;
                    preRemedyCode = remedyCode;
                    preThirdPartyComment = etThridPartyComment.getText().toString() + "";
                    preScan1Result = tvScanFault.getText().toString();
                    preScan2Result = tvScanReplacement.getText().toString();
                    Event tempEvent = new Event("faultpartcode", "faultpartcode", preScan1Result);
                    tempEvent.setEvent_id("faultpartcodefaultpartcode");
                    dbHelper.eventDAO().insert(tempEvent);

                    tempEvent = new Event("reported", "reported", tvReportedCode.getText().toString()+"");
                    tempEvent.setEvent_id("reportedreported");
                    dbHelper.eventDAO().insert(tempEvent);

                    tempEvent = new Event("remedy", "remedy", preRemedyCode);
                    tempEvent.setEvent_id("remedyremedy");
                    dbHelper.eventDAO().insert(tempEvent);


                }
                break;

            case R.id.iv_attach_post_maintenance_photo:
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }
                break;
            case R.id.ll_telco:
                intent = new Intent(getContext(), TelcoActivity.class);
                intent.putExtra("id", pmServiceInfoModel.getId());
                intent.putExtra("telco", getArguments().getSerializable("telco"));
                getContext().startActivity(intent);
                break;
            case R.id.ll_power_grid:
                intent = new Intent(getContext(), PowerGridActivity.class);
                intent.putExtra("id", pmServiceInfoModel.getId());
                intent.putExtra("powerGrid", getArguments().getSerializable("powerGrid"));
                getContext().startActivity(intent);
                break;
            case R.id.ll_other:
                intent = new Intent(getContext(), OtherActivity.class);
                intent.putExtra("id", pmServiceInfoModel.getId());
                intent.putExtra("other", getArguments().getSerializable("other"));
                getContext().startActivity(intent);
                break;

        }
    }

    private void uploadEvents(){
        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        UpdateEventBody eventBody = new UpdateEventBody(
                mSharePreference.getUserName(),
                mSharePreference.getUserId(),
                actualDateTime,
                pmServiceInfoModel.getId()
        );
        eventBody.setId(CM_Step_TWO);
        dbHelper.updateEventBodyDAO().insert(eventBody);
        mSharePreference.userClickCMStepTwo(true);


        if (network.isNetworkAvailable()) {
            ((CMActivity)getActivity()).showProgressBar(false);
            new getCurrentNetworkTime().execute();
        }
    }

    class getCurrentNetworkTime extends AsyncTask<String, Void, Boolean> {
        private void updateEventsWithNTPTime() {
            if (dbHelper.eventDAO().getNumberEventsToUpload(CM_Step_TWO) > 0) {
                UpdateEventBody eventBody = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(CM_Step_TWO);
                eventBody.setEvents(dbHelper.eventDAO().getEventsToUpload(CM_Step_TWO));

                Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePreference.getToken(), eventBody);
                call.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        if (response.isSuccessful()) {
                            Log.i("EventsUpload", "uploadEvents: " );
                            Toast.makeText(getContext(), eventBody.getEvents().size() + " Events Uploaded at " +
                                    eventBody.getDate(), Toast.LENGTH_SHORT).show();
                            dbHelper.eventDAO().update(YES, CM_Step_TWO);
                            //  Toast.makeText(getContext(), dbHelper.eventDAO().getEvents(CM_Step_TWO).get(0).alreadyUploaded , Toast.LENGTH_SHORT).show();
                          //  ((CMActivity)getActivity()).hideProgressBar();
                        } else {
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                                Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                             //   ((CMActivity)getActivity()).hideProgressBar();
                            } catch (IOException e) {

                            }
                        }

                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        Toast.makeText(getContext(), "Failure" , Toast.LENGTH_SHORT).show();
                    }
                });
            }
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
                //after getting network time, update event with the network time
                dbHelper.updateEventBodyDAO().updateDateTime(actualDateTime, CM_Step_TWO);
                updateEventsWithNTPTime();

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
            ((CMActivity)getActivity()).hideProgressBar();
            if(!aBoolean) {
                Log.e("Check ", "dates not equal");
            }
        }

    }



    private void getQREvent() {

        Event tempEvent;
        if (!etThridPartyComment.getText().toString().equals("")) { //third party comment
            tempEvent = new Event(THIRD_PARTY_COMMENT_UPDATE,
                    COMMENT,
                    "" + etThridPartyComment.getText().toString());
            tempEvent.setEvent_id(THIRD_PARTY_COMMENT_UPDATE + COMMENT);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);

            if (preThirdPartyComment.equals("") || !preThirdPartyComment.equals(etThridPartyComment.getText().toString())) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }

        }
        if (!tvScanFault.getText().toString().equals("")) {
            tempEvent = new Event(PART_REPLACEMENT_UPDATE,
                    FAULT_PART_CODE,
                    tvScanFault.getText().toString());
            tempEvent.setEvent_id(PART_REPLACEMENT_UPDATE + FAULT_PART_CODE);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preScan1Result.equals("") || !preScan1Result.equals(tvScanFault.getText().toString())) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }

        } else {
            mandatoryFieldsLeft += "\nScan Faulty Component";
            isMandatoryFieldLeft = true;
        }
        if (!tvScanReplacement.getText().toString().equals("")) {
            tempEvent = new Event(PART_REPLACEMENT_UPDATE,
                    REPLACEMENT_PART_CODE,
                    tvScanReplacement.getText().toString());
            tempEvent.setEvent_id(PART_REPLACEMENT_UPDATE + REPLACEMENT_PART_CODE);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preScan2Result.equals("") || !preScan2Result.equals(tvScanReplacement.getText().toString())) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }
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

        Event tempEvent = new Event(
                SERVICE_ORDER_UPDATE,
                REPORTED_PROBLEM,
                "" + pmServiceInfoModel.getReportedProblem()
        );
        tempEvent.setEvent_id(SERVICE_ORDER_UPDATE + REPORTED_PROBLEM);
        tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
        if (preActualProblemCode.equals("") && dbHelper.eventDAO().getEventValueCount(SERVICE_ORDER_UPDATE, REPORTED_PROBLEM) == 0) {
            tempEvent.setAlreadyUploaded(NO);
            dbHelper.eventDAO().insert(tempEvent);
        }

        if (spinnerActualProbleCode.getSelectedItemPosition() > 0) {
            tempEvent = new Event(
                    SERVICE_ORDER_UPDATE,
                    ACTUAL_PROBLEM ,
                    actualProblem);
            tempEvent.setEvent_id(SERVICE_ORDER_UPDATE + ACTUAL_PROBLEM);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preActualProblemCode.equals("") || !preActualProblemCode.equals(actualProblem)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }

        } else {
            mandatoryFieldsLeft += "\nSelect Actual Problem Code";
            isMandatoryFieldLeft = true;
        }

        if (spinnerCauseCode.getSelectedItemPosition() > 0) {
            tempEvent = new Event(
                    SERVICE_ORDER_UPDATE,
                    CAUSE,
                    causeCode);
            tempEvent.setEvent_id(SERVICE_ORDER_UPDATE + CAUSE);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preCauseCode.equals("") || !preCauseCode.equals(causeCode)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }

        } else {
            mandatoryFieldsLeft += "\nSelect Cause Code";
            isMandatoryFieldLeft = true;
        }

        if (spinnerRemedyCode.getSelectedItemPosition() > 0) {
            tempEvent = new Event(
                    SERVICE_ORDER_UPDATE,
                    REMEDY,
                    remedyCode);
            tempEvent.setEvent_id(SERVICE_ORDER_UPDATE + REMEDY);
            tempEvent.setUpdateEventBodyKey(CM_Step_TWO);
            if (preRemedyCode.equals("") || !preRemedyCode.equals(remedyCode)) {
                tempEvent.setAlreadyUploaded(NO);
                dbHelper.eventDAO().insert(tempEvent);
            }

        } else {
            mandatoryFieldsLeft += "\nSelect Remedy Code";
            isMandatoryFieldLeft = true;
        }

    }

    /**
     * Update event for all photos
     */
    public void savePhotosToDB(){
        dbHelper.uploadPhotoDAO().deleteById(CM_Step_TWO);
        for (PhotoModel photoModel: postModelList) {
            if(photoModel.getUid() == 1)
                saveEncodePhotoToDatabase(CM_Step_TWO, POST_BUCKET_NAME, photoModel.getImage());
        }
        Toast.makeText(this.getContext(), dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload()+" photos have been saved.", Toast.LENGTH_SHORT).show();

    }


    /**
     * Reuqesting for premissons
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mSharePreference.setLock(false);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                mSharePreference.setLock(false);
                //  Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                mSharePreference.setLock(false);
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                //   Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Start an activity for result
     * @param requestCode
     * @param resultCode
     * @param data
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        mSharePreference.setLock(false);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            bitmap = (Bitmap) data.getExtras().get("data");
            photo = getEncodedString(bitmap);
            postModelList.add(new PhotoModel(photo, 1));
            postPhotoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set two recycler view with adapter
     */
    private void setDataAdapter() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3  );
        //Pre Maintenance Photo Adapter Setup
        postRecyclerView.setLayoutManager(layoutManager);
        postPhotoAdapter = new PhotoAdapter(getContext(), postModelList);
        postRecyclerView.setAdapter(postPhotoAdapter);

    }


    private void showDialog() {
        new AlertDialog.Builder(getContext())
                .setIcon(R.drawable.warning)
                .setTitle("Mandatory Fields")
                .setMessage(mandatoryFieldsLeft)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }

                })
                .show();
    }

    /**
     * Conver bitmap image to string
     * @param bitmap
     * @return
     */
    private String getEncodedString(Bitmap bitmap){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);

       /* or use below if you want 32 bit images

        bitmap.compress(Bitmap.CompressFormat.PNG, (0â€“100 compression), os);*/
        byte[] imageArr = os.toByteArray();

        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }
    /**
     * //To Do save to database photo
     */
    private void saveEncodePhotoToDatabase(String updateEventKey, String bucketName, String sPhoto){
        byte[] bytes = sPhoto.getBytes();
        String encodeToString = Base64.encodeToString(bytes,Base64.DEFAULT);
        Log.v("ENCODE",encodeToString);
        dbHelper.uploadPhotoDAO().insert(new UploadPhotoModel(
                updateEventKey, bucketName, encodeToString
        ));

    }

    /**
     * Encode photo string to decode string
     */
    private String getDecodedString(String s){
        byte[] data = Base64.decode(s,Base64.DEFAULT);
        String s1 = new String(data);
        Log.v("DECODE", s1);
        return s1;
    }

}
