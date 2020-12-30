package com.freelance.solutionhub.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.PMActivity;
import com.freelance.solutionhub.mma.adapter.CheckListAdapter;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.CheckListModel;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PhotoAttachementModel;
import com.freelance.solutionhub.mma.model.PhotoModel;
import com.freelance.solutionhub.mma.model.PreMaintenance;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.UploadPhotoModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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

import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_ONE;
import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_TWO;
import static com.freelance.solutionhub.mma.util.AppConstant.NO;
import static com.freelance.solutionhub.mma.util.AppConstant.PM_CHECK_LIST_DONE;
import static com.freelance.solutionhub.mma.util.AppConstant.PM_CHECK_LIST_REMARK;
import static com.freelance.solutionhub.mma.util.AppConstant.PM_FAULT_FOUND_UPDATE;
import static com.freelance.solutionhub.mma.util.AppConstant.PM_Step_ONE;
import static com.freelance.solutionhub.mma.util.AppConstant.POST_BUCKET_NAME;
import static com.freelance.solutionhub.mma.util.AppConstant.YES;


public class First_Step_PM_Fragment extends Fragment {

    @BindView(R.id.ll_general_design)
    LinearLayout generalDesign;

    @BindView(R.id.ll_whole_card_design)
    LinearLayout wholeCardDesign;

    @BindView(R.id.tv_panel_id)
    TextView tvPanelID;

    @BindView(R.id.tv_panel_id1)
    TextView tvPanelID1;

    @BindView(R.id.iv_arrow_drop_down)
    ImageView ivArrowDropDown;

    @BindView(R.id.iv_arrow_drop_up)
    ImageView ivArrowDropUp;

    @BindView(R.id.tv_performed_by)
    TextView tvPerformedBy;

    @BindView(R.id.tv_location)
    TextView tvLocation;

    @BindView(R.id.tv_bust_stop_number)
    TextView tvBustStopNumber;

    @BindView(R.id.tv_schedule_start_date_time)
    TextView tvScheduleStartDateTime;

    @BindView(R.id.tv_schedule_end_date_time)
    TextView tvScheduleEndDateTime;

    @BindView(R.id.tv_actual_start_date_time)
    TextView tvActualStartDateTime;

    @BindView(R.id.iv_attach_post_maintenance_photo)
    ImageView postMaintenancePhoto;

    @BindView(R.id.recyclerview_post_photo)
    RecyclerView postPhoto;

    @BindView(R.id.btn_pm_step1)
    Button pmStep1;

    @BindView(R.id.tvMSOStatus)
    TextView tvMSOStatus;

    @BindView(R.id.rv_check_list)
    RecyclerView checkList;

    @BindView(R.id.et_fault_found_remarks)
    EditText etFaultFoundRemarks;

    @BindView(R.id.iv_fault_found_remarks)
    ImageView ivFaultFoundRemarks;

    private ApiInterface apiInterface;
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //Bitmap photo;
    SharePreferenceHelper mSharePerferenceHelper;
    CheckListAdapter checkListAdapter;
    ArrayList<CheckListModel> checkListModels;
    ArrayList<CheckListModel> preCheckListModels;

    String photo;
    ArrayList<PhotoModel>  postPhotoModels;
    Bitmap theImage;
    PhotoAdapter postPhotoAdapter;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private Network network;
    private InitializeDatabase dbHelper;
    private Date date;
    private Timestamp ts;
    private Network mNetwork;
    private boolean isMandatory = false;
    private String mandatoryString = "";
    private String preFaultFoundRemarks = "";

    public First_Step_PM_Fragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_step_p_m_, container, false);
        ButterKnife.bind(this, view);
        apiInterface = ApiClient.getClient(this.getContext());
        mSharePerferenceHelper = new SharePreferenceHelper(this.getContext());
     //   mSharePerferenceHelper.setLock(false);

        network = new Network(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());
        mNetwork = new Network(getContext());
        postPhotoModels = new ArrayList<>();

        //Check List
        checkListModels = new ArrayList<>();
        checkListAdapter = new CheckListAdapter(getContext(), checkListModels, dbHelper);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
      //  checkList.setHasFixedSize(true);
        checkList.setLayoutManager(mLayoutManager);
        checkList.setAdapter(checkListAdapter);

        //add data to checklist recyclerview
        List<String> uniqueCLKeys = dbHelper.eventDAO().getUniqueKeyFromEvents(PM_Step_ONE, PM_FAULT_FOUND_UPDATE);
        Log.i("UniqueCheckListKeys", "onCreateView: " + uniqueCLKeys);
        for(String eventKey: uniqueCLKeys) {
            checkListModels.add(new CheckListModel(
                    Integer.parseInt(eventKey),
                    dbHelper.checkListDescDAO().getDesc(eventKey),
                    dbHelper.eventDAO().getEventValue(PM_CHECK_LIST_REMARK, eventKey),
                    Boolean.parseBoolean(dbHelper.eventDAO().getEventValue(PM_CHECK_LIST_DONE, eventKey)))
            );
        }
        preCheckListModels = cloneList(checkListModels);
        checkListAdapter.notifyDataSetChanged();

//        Call<PhotoAttachementModel> photoAttachementModelCall = apiInterface.getPhotoAttachment("Bearer "+mSharePerferenceHelper.getToken(), pmServiceInfoDetailModel.getId());
//        photoAttachementModelCall.enqueue(new Callback<PhotoAttachementModel>() {
//            @Override
//            public void onResponse(Call<PhotoAttachementModel> call, Response<PhotoAttachementModel> response) {
//                PhotoAttachementModel photoAttachementModel = response.body();
//                if(response.isSuccessful()){
//                    List<PreMaintenance> preMaintenances = photoAttachementModel.getPreMaintenance();
//                    if(preMaintenances != null) {
//                        for (PreMaintenance e : preMaintenances) {
//                            Log.e("filepath", e.getFilePath());
//                            postPhotoModels.add(new PhotoModel(e.getFilePath(), 2));
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


        ivFaultFoundRemarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etFaultFoundRemarks.getVisibility()==View.VISIBLE) {
                    etFaultFoundRemarks.setVisibility(View.GONE);
                    Glide.with(getActivity())
                            .load(R.drawable.check_blank)
                            .into(ivFaultFoundRemarks);

                } else if (etFaultFoundRemarks.getVisibility()==View.GONE) {
                    etFaultFoundRemarks.setVisibility(View.VISIBLE);
                    Glide.with(getActivity())
                            .load(R.drawable.check)
                            .into(ivFaultFoundRemarks);
                }

            }
        });



        date = new Date();
        ts=new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);

        tvPerformedBy.setText(mSharePerferenceHelper.getUserId());
        tvActualStartDateTime.setText(actualDateTime);

        ivArrowDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalDesign.setVisibility(View.GONE);
                wholeCardDesign.setVisibility(View.VISIBLE);
            }
        });

        //Post Maintenance Photo Click
        postMaintenancePhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);

                }
            }
        });

        //Save Button Click
        pmStep1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMandatory();
                if(isMandatory){
                    showDialog("Mandatory Fields", mandatoryString);
                }else {
                    int i = 0; Event tempEvent; String tempStr;
                    tempStr = etFaultFoundRemarks.getText().toString();
                    Log.i("CheckListData", "onClick: here" );

                    if (!tempStr.equals("")) {
                        tempEvent = new Event(
                                PM_FAULT_FOUND_UPDATE,
                                PM_FAULT_FOUND_UPDATE,
                                tempStr);
                        tempEvent.setUpdateEventBodyKey(PM_Step_ONE);
                        tempEvent.setEvent_id(PM_FAULT_FOUND_UPDATE + PM_FAULT_FOUND_UPDATE);
                        if (preFaultFoundRemarks.equals("") || !tempStr.equals(preFaultFoundRemarks)) {
                            tempEvent.setAlreadyUploaded(NO);
                            dbHelper.eventDAO().insert(tempEvent);
                        }

                    }
                    Log.i("CheckListData", "onClick: there");

                    for (CheckListModel object: checkListModels) {
                        CheckListModel preCheckList = preCheckListModels.get(i++);
                        tempEvent = new Event(
                                PM_CHECK_LIST_REMARK, object.getId() + "", object.getMaintenanceRemark() + ""
                        );
                        tempEvent.setUpdateEventBodyKey(PM_Step_ONE);
                        tempEvent.setEvent_id(PM_CHECK_LIST_REMARK + object.getId());
                        if (object.getMaintenanceRemark() == null || object.getMaintenanceRemark().equals("") ||
                        object.getMaintenanceRemark().equals(preCheckList.getMaintenanceRemark())) {
                            tempEvent.setAlreadyUploaded(YES);
                        }
                        else {
                            tempEvent.setAlreadyUploaded(NO);
                        }
                        dbHelper.eventDAO().insert(tempEvent);


                        if (!preCheckList.isMaintenanceDone()) {
                            tempEvent = new Event(
                                    PM_CHECK_LIST_DONE, object.getId() + "", object.isMaintenanceDone + ""
                            );
                            tempEvent.setUpdateEventBodyKey(PM_Step_ONE);
                            tempEvent.setEvent_id(PM_CHECK_LIST_DONE + object.getId());
                            tempEvent.setAlreadyUploaded(NO);
                            dbHelper.eventDAO().insert(tempEvent);
                        }
                    }
                    preCheckListModels = cloneList(checkListModels);
                    preFaultFoundRemarks = etFaultFoundRemarks.getText().toString();
                    save();
                    mSharePerferenceHelper.userClickPMStepOne(true);
                }
                isMandatory = false;
                mandatoryString = "";
            }
        });

        setDataAdapter();
        if (dbHelper.uploadPhotoDAO().getNumberOfPhotosByStep(PM_Step_ONE) > 0) {
            displaySavedImage();
        }

        ivArrowDropUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wholeCardDesign.setVisibility(View.GONE);
                generalDesign.setVisibility(View.VISIBLE);
            }
        });
        setDataToView();

        return view;
    }

    /**
     * Update event for all
     */
    public void save(){

        if (postPhotoModels.size() > 1 && postPhotoModels.size() < 11) {
            savePhotosToDB();

            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            UpdateEventBody updateEventBody = new UpdateEventBody(
                    mSharePerferenceHelper.getUserName(),
                    mSharePerferenceHelper.getUserId(),
                    actualDateTime,
                    pmServiceInfoDetailModel.getId()
            );
            updateEventBody.setId(PM_Step_ONE);
            List<Event> events = dbHelper.eventDAO().getEventsToUpload(PM_Step_ONE);
            updateEventBody.setEvents(events);

            String temp = "";
            for (Event event: events) {
                temp += event.getEventType() + " - " + event.getKey() + " - " + event.getAlreadyUploaded() + "\n";
            }
            Log.e("Tring....", "save: " + temp);

            dbHelper.updateEventBodyDAO().insert(updateEventBody);

            if (mNetwork.isNetworkAvailable() && dbHelper.eventDAO().getNumberEventsToUpload(PM_Step_ONE) > 0) {
                ((PMActivity)getActivity()).showProgressBar();
                Log.e("Tracing....", "save: progress");
                Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePerferenceHelper.getToken() ,
                        updateEventBody);
                call.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        Log.e("Tracing....", "save: " + "response");
                        if(response.isSuccessful()) {
                            Log.e("Tracing....", "save: " + "success");
                            Toast.makeText(getContext(), dbHelper.eventDAO().getNumberEventsToUpload(PM_Step_ONE)+" events." , Toast.LENGTH_SHORT).show();
                            dbHelper.eventDAO().update(YES, PM_Step_ONE);
                        } else{
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("Tracing....", "onResponse: " + errorReturnBody.string());
                                Toast.makeText(getContext(), "response " + response.code(),  Toast.LENGTH_LONG).show();
                                //  ((CMActivity)getActivity()).hideProgressBar();
                            } catch (IOException e) {

                            }
                        }
                        ((PMActivity)getActivity()).hideProgressBar();
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        Toast.makeText(getContext(), "Failure" , Toast.LENGTH_SHORT).show();
                        ((PMActivity)getActivity()).hideProgressBar();
                    }
                });
            } else {
                Toast.makeText(getContext(), "No events to upload" , Toast.LENGTH_SHORT).show();
            }

            //  new LoadImage(events).execute(uploadPhoto(postPhotoModels));

        } else {
            showDialog("Photo", "Your photos must be minimum 2 and maximum 10.");

        }

    }

    private void displaySavedImage() {
        postPhotoModels.clear();
        // int imaCount = dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload();
        for (UploadPhotoModel uploadPhotoModel: dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(POST_BUCKET_NAME)) {
            postPhotoModels.add(new PhotoModel(getDecodedString(uploadPhotoModel.getEncodedPhotoString()), 1));
        }
        postPhotoAdapter.notifyDataSetChanged();
    }

    public void savePhotosToDB(){
        dbHelper.uploadPhotoDAO().deleteById(PM_Step_ONE);
        for (PhotoModel photoModel: postPhotoModels) {
            if(photoModel.getUid() == 1)
                saveEncodePhotoToDatabase(PM_Step_ONE, POST_BUCKET_NAME, photoModel.getImage());
        }
        Toast.makeText(this.getContext(), dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload()+" photos have been saved.", Toast.LENGTH_SHORT).show();
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


    private void showDialog(String dialogTitle, String dialogBody){
        mSharePerferenceHelper.userClickCMStepTwo(false);
        new AlertDialog.Builder(this.getContext())
                .setIcon(R.drawable.warning)
                .setTitle(dialogTitle)
                .setMessage(dialogBody)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }

                })
                .show();
    }
    private void checkMandatory(){

        for (CheckListModel checkListModel : checkListModels) {
            if (!checkListModel.isMaintenanceDone) {
                isMandatory = true;
                String desc = checkListModel.getCheckDescription();
                if (desc.length() > 18) {
                    mandatoryString += desc.substring(0, 17) + "....\n";
                } else {
                    mandatoryString += checkListModel.getCheckDescription() + "\n";
                }

            }
        }

        if(!(postPhotoModels.size() > 1 && postPhotoModels.size() < 6)){
            isMandatory = true;
            mandatoryString += "Add photo (between 2 and 5)\n";
        }

    }


    /**
     * Set Data to Card View
     */
    public void setDataToView(){


        tvPanelID1.setText(pmServiceInfoDetailModel.getPanelId());

        tvPanelID.setText(pmServiceInfoDetailModel.getPanelId());
        tvLocation.setText(pmServiceInfoDetailModel.getBusStopLocation());
        tvBustStopNumber.setText(pmServiceInfoDetailModel.getBusStopId());
        tvScheduleStartDateTime.setText(pmServiceInfoDetailModel.getTargetResponseDate());
        tvScheduleEndDateTime.setText(pmServiceInfoDetailModel.getTargetEndDate());
        tvPerformedBy.setText(mSharePerferenceHelper.getDisplayName());
        tvMSOStatus.setText(pmServiceInfoDetailModel.getServiceOrderStatus());

        if (dbHelper.eventDAO().getNumOfEventsByEventType(PM_FAULT_FOUND_UPDATE) > 0) {
            Log.i("CheckListUpdte", "setDataToView: Here" + dbHelper.eventDAO().getNumOfEventsToUploadByEventType(PM_FAULT_FOUND_UPDATE));
            preFaultFoundRemarks = dbHelper.eventDAO().getEventValue(PM_FAULT_FOUND_UPDATE, PM_FAULT_FOUND_UPDATE);
            etFaultFoundRemarks.setText(preFaultFoundRemarks);
        }
    }

    public static ArrayList<CheckListModel> cloneList(ArrayList<CheckListModel> list) {
        ArrayList<CheckListModel> clonedList = new ArrayList<CheckListModel>(list.size());
        for (CheckListModel obj : list) {
            clonedList.add(new CheckListModel(obj));
        }
        return clonedList;
    }

    /**
     * Reuqesting for premissons
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mSharePerferenceHelper.setLock(false);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                mSharePerferenceHelper.setLock(false);
               // Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
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
        mSharePerferenceHelper.setLock(false);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            theImage = (Bitmap) data.getExtras().get("data");
            photo = getEncodedString(theImage);
            Log.v("ORI",photo);
            postPhotoModels.add(new PhotoModel(photo, 1));
            postPhotoAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Set two recycler view with adapter
     */
    private void setDataAdapter() {

        //Post Maintenance Photo Adapter Setup
        RecyclerView.LayoutManager postLayoutManager = new GridLayoutManager(getContext(),3);
        postPhoto.setLayoutManager(postLayoutManager);
        postPhotoAdapter = new PhotoAdapter(getContext(), postPhotoModels);
        postPhoto.setAdapter(postPhotoAdapter);

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
     * Encode photo string to decode string
     */
    private String getDecodedString(String s){
        byte[] data = Base64.decode(s,Base64.DEFAULT);
        String s1 = new String(data);
        Log.v("DECODE", s1);
        return s1;
    }


}