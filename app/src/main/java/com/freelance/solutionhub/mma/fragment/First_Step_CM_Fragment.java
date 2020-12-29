package com.freelance.solutionhub.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.CMActivity;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_ONE;
import static com.freelance.solutionhub.mma.util.AppConstant.PRE_BUCKET_NAME;

public class First_Step_CM_Fragment extends Fragment {

    @BindView(R.id.tv_mso_status)
    TextView tvMsoStatus;

    @BindView(R.id.tv_mso_number)
    TextView tvMsoNumber;

    @BindView(R.id.tv_fault_type)
    TextView tvFaultType;

    @BindView(R.id.tv_allowable_resolution_datetime)
    TextView tvAllowableResolutionDT;

    @BindView(R.id.tv_allowable_respond_datetime)
    TextView tvAllowableRespondDT;

    @BindView(R.id.tv_fault_detected_datetime)
    TextView tvFaultDetectedDT;

    @BindView(R.id.mso_notified_datetime)
    TextView tvMsoNotifiedDT;

    @BindView(R.id.tv_bus_stop_number)
    TextView tvBusStopNumber;

    @BindView(R.id.tv_location)
    TextView tvLocation;

    @BindView(R.id.tv_panel_id)
    TextView tvPanelId;

    @BindView(R.id.tv_mso_priority)
    TextView tvMsoPriority;

    @BindView(R.id.recyclerview_pre_photo)
    RecyclerView prePhoto;

    @BindView(R.id.iv_attach_pre_maintenance_photo)
    ImageView preMaintenancePhoto;

    @BindView(R.id.btn_cm_step1)
    Button save;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private String id;
    private String actualDateTime;
    private Timestamp timestamp;
    ArrayList<Event> preEventList;
    ArrayList<PhotoModel> prePhotoModels;
    PhotoAdapter prePhotoAdapter;
    SharePreferenceHelper mSharePerferenceHelper;
    private Bitmap theImage;
    private String photo;
    private Date date;
    private PMServiceInfoDetailModel pmServiceInfoModel;
    private ApiInterface apiInterface;
    private Network mNetwork;
    public InitializeDatabase dbHelper;
    String dialogTitle = "";
    String dialogBody = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // get data from activity
        pmServiceInfoModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));


        View view = inflater.inflate(R.layout.fragment_first__step__c_m_, container, false);
        apiInterface = ApiClient.getClient(getContext());
        preEventList = new ArrayList<>();
        prePhotoModels = new ArrayList<>();
        mSharePerferenceHelper = new SharePreferenceHelper(getContext());
        mNetwork = new  Network(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());
        ButterKnife.bind(this, view);
        displayMSOInformation();
        setDataAdapter();
        Log.i(CM_Step_ONE, "onCreateView: " + dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(CM_Step_ONE));

        if (dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(CM_Step_ONE) > 0) {
            prePhotoModels.clear();
            // int imaCount = dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload();
            for (UploadPhotoModel uploadPhotoModel: dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(PRE_BUCKET_NAME)) {
                prePhotoModels.add(new PhotoModel(getDecodedString(uploadPhotoModel.getEncodedPhotoString()), 1));
            }
            prePhotoAdapter.notifyDataSetChanged();
        }

        preMaintenancePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getActivity().checkPermission(Manifest.permission.CAMERA,1,1) != PackageManager.PERMISSION_GRANTED)
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

        Call<PhotoAttachementModel> photoAttachementModelCall = apiInterface.getPhotoAttachment("Bearer "+mSharePerferenceHelper.getToken(), pmServiceInfoModel.getId());
        photoAttachementModelCall.enqueue(new Callback<PhotoAttachementModel>() {
            @Override
            public void onResponse(Call<PhotoAttachementModel> call, Response<PhotoAttachementModel> response) {
                PhotoAttachementModel photoAttachementModel = response.body();
                if(response.isSuccessful()){
                    List<PreMaintenance> preMaintenances = photoAttachementModel.getPreMaintenance();
                    if(preMaintenances != null) {
                        preMaintenancePhoto.setClickable(false);
                        for (PreMaintenance e : preMaintenances) {
                            Log.e("filepath", e.getFilePath());
                            prePhotoModels.add(new PhotoModel(e.getFilePath(), 2));

                        }
                        prePhotoAdapter.notifyDataSetChanged();
                    }
                }

            }

            @Override
            public void onFailure(Call<PhotoAttachementModel> call, Throwable t) {

            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (prePhotoModels.size() == 0) {
                    dialogTitle = "Mandatory Fields:";
                    dialogBody = "Attach Premaintenance Photos.";
                    showDialog();
                } else {
                    save();
                }

            }
        });

        return view;
    }
    /**
     * show dialog for mandatory fields
     */
    private void showDialog() {
        mSharePerferenceHelper.userClickCMStepOne(false);
        new AlertDialog.Builder(getContext())
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

    /**
     * Update event for all
     */
    public void save(){

        if (prePhotoModels.size() > 1 && prePhotoModels.size() < 6) {
            mSharePerferenceHelper.userClickCMStepOne(true);

            Log.v("JOIN", preEventList.size() + "");
            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            UpdateEventBody updateEventBody = new UpdateEventBody(
                    mSharePerferenceHelper.getUserName(),
                    mSharePerferenceHelper.getUserId(),
                    actualDateTime,
                    pmServiceInfoModel.getId()
            );

            updateEventBody.setId(CM_Step_ONE);
            dbHelper.updateEventBodyDAO().insert(updateEventBody);

            dbHelper.uploadPhotoDAO().deleteById(CM_Step_ONE);
            for (PhotoModel photoModel: prePhotoModels) {
                if(photoModel.getUid()==1)
                    saveEncodePhotoToDatabase(CM_Step_ONE, PRE_BUCKET_NAME, photoModel.getImage());
            }
            Toast.makeText(this.getContext(), dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload()+" photos have been saved.", Toast.LENGTH_SHORT).show();
        }
        else {
            dialogTitle = "Photo";
            dialogBody = "Your photos must be minimum 2 and maximum 5.";
            showDialog();
        }
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

    private void displayMSOInformation() {
        tvMsoStatus.setText(pmServiceInfoModel.getServiceOrderStatus());
        tvMsoNumber.setText(pmServiceInfoModel.getId());
        tvPanelId.setText(pmServiceInfoModel.getPanelId());
        tvFaultType.setText(pmServiceInfoModel.getReportedProblem());
        tvMsoPriority.setText(pmServiceInfoModel.getPriorityLevel());

        tvAllowableResolutionDT.setText(pmServiceInfoModel.getTargetEndDate());
        tvAllowableRespondDT.setText(pmServiceInfoModel.getTargetResponseDate());
        tvFaultDetectedDT.setText(pmServiceInfoModel.getFaultDetectedDate());
        tvMsoNotifiedDT.setText(pmServiceInfoModel.getNotificationDate());
        tvBusStopNumber.setText(pmServiceInfoModel.getBusStopId());
        tvLocation.setText(pmServiceInfoModel.getBusStopLocation());

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
        Log.i("Tracing......", "PermissionResultStart: " + mSharePerferenceHelper.getLock());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Log.i("Tracing......", "PermissionResultGranted: " + mSharePerferenceHelper.getLock());
                // Toast.makeText(getActivity(), "camera permission granted", Toast.LENGTH_LONG).show();
                mSharePerferenceHelper.setLock(false);
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
        Log.i("Tracing......", "onActivityResult: " + mSharePerferenceHelper.getLock());
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            theImage = (Bitmap) data.getExtras().get("data");
            photo = getEncodedString(theImage);
                Log.v("ORI", photo);
                prePhotoModels.add(new PhotoModel(photo, 1));
                prePhotoAdapter.notifyDataSetChanged();

        }
    }



    /**
     * Set two recycler view with adapter
     */
    private void setDataAdapter() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3);
        //Pre Maintenance Photo Adapter Setup
        prePhoto.setLayoutManager(layoutManager);
        prePhotoAdapter = new PhotoAdapter(getContext(), prePhotoModels);
        prePhoto.setAdapter(prePhotoAdapter);

    }

    /**
     * Conver bitmap image to string
     * @param bitmap
     * @return
     */
    private String getEncodedString(Bitmap bitmap){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,50, os);

       /* or use below if you want 32 bit images

        bitmap.compress(Bitmap.CompressFormat.PNG, (0â€“100 compression), os);*/
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