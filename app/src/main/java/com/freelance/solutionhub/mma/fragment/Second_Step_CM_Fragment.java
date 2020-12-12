package com.freelance.solutionhub.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.CaptureActivityPotrait;
import com.freelance.solutionhub.mma.activity.Code_Description;
import com.freelance.solutionhub.mma.activity.NFCReadingActivity;
import com.freelance.solutionhub.mma.activity.OtherActivity;
import com.freelance.solutionhub.mma.activity.PowerGridActivity;
import com.freelance.solutionhub.mma.activity.TelcoActivity;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PhotoModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.google.zxing.integration.android.IntentIntegrator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Second_Step_CM_Fragment extends Fragment implements View.OnClickListener, FirstStepPMFragmentCallback {

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
    private ArrayList<Event> postEventList;
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
        apiInterface = ApiClient.getClient(this.getContext());
        ButterKnife.bind(this, view);
        postEventList = new ArrayList<>();
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
                causeArr.clear();
                if (problemCauseMap.containsKey(actualProblemCode[i])) {
                    causeProblemCode = new String[problemCauseMap.get(actualProblemCode[i]).size() + 1];

                    Code_Description temp;
                    causeProblemCode[0] = "";
                    for (int k = 0; k < causeProblemCode.length-1; k++) {
                        temp = problemCauseMap.get(actualProblemCode[i]).get(k);
                        causeProblemCode[k + 1] = temp.getCode();
                        causeArr.add(temp.getDescription());
                    }
                } else {
                    causeProblemCode = new String[1];
                }
                causeProblemCode[0] = "Select Cause Code";
                causeArr.add(0, "Select Cause Code");
                causeProblemArrAdapter.notifyDataSetChanged();
                remedyArr.clear();
                remedyArr.add(0, "Select Remedy Code");
                remedyArrAdapter.notifyDataSetChanged();
                spinnerCauseCode.setSelection(0, true);
                spinnerRemedyCode.setSelection(0,true);
                actualProblem = problemArr.get(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        spinnerCauseCode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                remedyArr.clear();
                if (causeRemedyMap.containsKey(causeProblemCode[i])) {
                    Code_Description temp;
                    for (int k = 0; k < causeRemedyMap.get(causeProblemCode[i]).size(); k++) {
                        temp = causeRemedyMap.get(causeProblemCode[i]).get(k);
                        remedyArr.add(temp.getDescription());
                    }
                }
                remedyArr.add(0, "Select Remedy Code");
                remedyArrAdapter.notifyDataSetChanged();
                spinnerRemedyCode.setSelection(0);
                causeCode = causeArr.get(i);
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
            actualProblemCode = new String[problemList.size() + 1];
            actualProblemCode[0] = "";
            for (int i = 0; i < problemList.size(); i++) {
                actualProblemCode[i+1] = problemList.get(i).getCode();
                problemArr.add(problemList.get(i).getDescription());
            }
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
                if(postModelList.size() != 0)
                    save();
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
                getContext().startActivity(intent);
                break;
            case R.id.ll_power_grid:
                intent = new Intent(getContext(), PowerGridActivity.class);
                intent.putExtra("id", pmServiceInfoModel.getId());
                getContext().startActivity(intent);
                break;
            case R.id.ll_other:
                intent = new Intent(getContext(), OtherActivity.class);
                intent.putExtra("id", pmServiceInfoModel.getId());
                getContext().startActivity(intent);
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
        if (spinnerActualProbleCode.getSelectedItemPosition() > 0) {
            hasEventToUpdate = true;
            events.add(new Event(
                    "SERVICE_ORDER_UPDATE",
                    "actualProblem" ,
                    mSharePreference.getUserId() + " - " + actualProblem));
            Log.i("EventHappenend", "getProblemCodeEvent: " + actualProblem);
        }
        if (spinnerCauseCode.getSelectedItemPosition() > 0) {
            hasEventToUpdate = true;
            events.add(new Event(
                    "SERVICE_ORDER_UPDATE",
                    "cause",
                    mSharePreference.getUserId() + " - " + causeCode));
            Log.i("EventHappenend", "getProblemCodeEvent: " + causeCode);
        }
        if (spinnerRemedyCode.getSelectedItemPosition() > 0) {
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
                    Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {

            }
        });
    }
    /**
     * Update event for all photos
     */
    public void save(){
        //Maintenance photos attached ( max - 10 and min 2 )
        if(postEventList.size() > 1 && postEventList.size() <5) {
            Log.v("BEFORE_JOIN", "Before joining");
            Log.v("JOIN", postEventList.size() + "");

            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            UpdateEventBody updateEventBody = new UpdateEventBody(mSharePreference.getUserName(),
                    mSharePreference.getUserId(),
                    actualDateTime,
                    pmServiceInfoModel.getId(),
                    postEventList);
            Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePreference.getToken(), updateEventBody);
            returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                    ReturnStatus returnStatus = response.body();
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), returnStatus.getStatus() + "", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }else {
            new AlertDialog.Builder(this.getContext())
                    .setIcon(R.drawable.warning)
                    .setTitle("Photo")
                    .setMessage("Your photos must be minimum 2 and maximum 5.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {


                        }

                    })
                    .show();
        }

    }


    public void getPosition( int position , int preOrPost){
        if(postEventList.size() != 0) {
            postEventList.remove(position);
            Log.v("PRE_PHOTO", "Removed pre photo");
        }
    }

    /**
     * Reuqesting for premissons
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(getActivity(), "camera permission denied", Toast.LENGTH_LONG).show();
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
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            bitmap = (Bitmap) data.getExtras().get("data");
            // Check whether request message is pre or post
            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(timestamp);
            uploadPhoto(bitmap, "pre-maintenance-photo" + mSharePreference.getUserId() +actualDateTime);
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
        postPhotoAdapter = new PhotoAdapter(getContext(), postModelList, this);
        postRecyclerView.setAdapter(postPhotoAdapter);

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

        bitmap.compress(Bitmap.CompressFormat.PNG, (0–100 compression), os);*/
        byte[] imageArr = os.toByteArray();

        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }

    /**
     *Upload one photo to server and get url id
     * @param bitmap
     * @param name
     */
    private void uploadPhoto(Bitmap bitmap, String name) {
        File filesDir = getContext().getFilesDir();
        File fileName = new File(filesDir, name + ".jpg");

        OutputStream os;
        try {
            os = new FileOutputStream(fileName);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e("PHOTO", "Error writing bitmap", e);
        }

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("file",fileName.getName(), RequestBody.create(MediaType.parse("multipart/form-data"),fileName));
        MultipartBody requestBody = builder.build();

        String bucketName ="pids-pre-maintenance-photo";

        Call<ReturnStatus> returnStatusCall = apiInterface.uploadPhoto(bucketName,  requestBody);
        returnStatusCall.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                ReturnStatus returnStatus = response.body();
                if(response.isSuccessful()){
                    Log.v("PRE_EVENT","Added pre event");
                    postEventList.add(new Event("POST_MAINTENANCE_PHOTO_UPDATE","postMaintenancePhotoUpdate",returnStatus.getData().getFileUrl()));
                    Toast.makeText(getContext(),returnStatus.getStatus()+"",Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(),";",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {

            }
        });
    }


}
