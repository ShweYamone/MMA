package com.freelance.solutionhub.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.token;
import static com.freelance.solutionhub.mma.util.AppConstant.pmID;

public class First_Step_PM_Fragment extends Fragment implements FirstStepPMFragmentCallback {

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

    @BindView(R.id.iv_attach_pre_maintenance_photo)
    ImageView preMaintenancePhoto;

    @BindView(R.id.iv_attach_post_maintenance_photo)
    ImageView postMaintenancePhoto;

    @BindView(R.id.recyclerview_pre_photo)
    RecyclerView prePhoto;

    @BindView(R.id.recyclerview_post_photo)
    RecyclerView postPhoto;

    @BindView(R.id.btn_pm_step1)
    Button pmStep1;

    private ApiInterface apiInterface;


    private boolean isPreMaintenance = true;
    private static final int CAMERA_REQUEST = 1888;
    TextView text,text1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //Bitmap photo;
    SharePreferenceHelper mSharePerferenceHelper;
    String photo;
    ArrayList<PhotoModel> prePhotoModels, postPhotoModels;
    ArrayList<Event> preEventList, postEventList;
    Bitmap theImage;
    PhotoAdapter prePhotoAdapter, postPhotoAdapter;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private Date date;
    private Timestamp ts;

    public First_Step_PM_Fragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first_step_p_m_, container, false);
        ButterKnife.bind(this, view);
        apiInterface = ApiClient.getClient(this.getContext());
        mSharePerferenceHelper = new SharePreferenceHelper(this.getContext());
        prePhotoModels = new ArrayList<>();
        postPhotoModels = new ArrayList<>();
        preEventList = new ArrayList<>();
        postEventList = new ArrayList<>();
        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));

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

        //Pre Maintenance Photo Click
        preMaintenancePhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                isPreMaintenance = true;
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

        //Post Maintenance Photo Click
        postMaintenancePhoto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                isPreMaintenance = false;
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
                save();
            }
        });

        setDataAdapter();

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

    public void getPosition( int position , int preOrPost){
        if(preOrPost == 1){
            preEventList.remove(position);
        }else {
            postEventList.remove(position);
        }

    }

    /**
     * Update event for all photos
     */
    void save(){
        preEventList.addAll(postEventList);
        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(ts);
        UpdateEventBody updateEventBody = new UpdateEventBody(mSharePerferenceHelper.getUserName(),mSharePerferenceHelper.getUserId(), actualDateTime,pmServiceInfoDetailModel.getId(),preEventList);
        Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer "+mSharePerferenceHelper.getToken(),updateEventBody);
        returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                ReturnStatus returnStatus = response.body();
                if(response.isSuccessful()){
                    Toast.makeText(getContext(),returnStatus.getData().getFileUrl()+":Ok",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {

            }
        });

    }

    /**
     * Set Data to Card View
     */
    public void setDataToView(){
        Call<PMServiceInfoDetailModel> call = apiInterface.getPMServiceOrderByID(pmID,"Bearer "+token);
        call.enqueue(new Callback<PMServiceInfoDetailModel>() {
            @Override
            public void onResponse(Call<PMServiceInfoDetailModel> call, Response<PMServiceInfoDetailModel> response) {
                PMServiceInfoDetailModel infoModel = response.body();
                if(response.isSuccessful()){
                    tvPanelID1.setText(infoModel.getPanelId());
                    tvPanelID.setText(infoModel.getPanelId());
                    if(infoModel.getBusStopLocation() != null)
                        tvLocation.setText(infoModel.getBusStopLocation().toString());
                    if(infoModel.getBusStopId() != null)
                        tvBustStopNumber.setText(infoModel.getBusStopId().toString());
                    tvScheduleStartDateTime.setText(infoModel.getTargetResponseDate());
                    tvScheduleEndDateTime.setText(infoModel.getTargetEndDate());

                    Toast.makeText(getContext(), infoModel.getPanelId() + ", " + infoModel.getServiceOrderStatus(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PMServiceInfoDetailModel> call, Throwable t) {

            }
        });
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
        {
            theImage = (Bitmap) data.getExtras().get("data");
            // Check whether request message is pre or post
            if(isPreMaintenance) {
                LocalDateTime d = LocalDateTime.now();
                uploadPhoto(theImage, "pre-maintenance-photo" + mSharePerferenceHelper.getUserId() +d.toString(), isPreMaintenance);
                photo = getEncodedString(theImage);
                prePhotoModels.add(new PhotoModel(photo, 1));
                prePhotoAdapter.notifyDataSetChanged();
            }else {
                LocalDateTime d = LocalDateTime.now();
                uploadPhoto(theImage, "post-maintenance-photo" + mSharePerferenceHelper.getUserId()+d.toString(), isPreMaintenance);
                photo = getEncodedString(theImage);
                postPhotoModels.add(new PhotoModel(photo,2));
                postPhotoAdapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Set two recycler view with adapter
     */
    private void setDataAdapter() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3  );
        //Pre Maintenance Photo Adapter Setup
        prePhoto.setLayoutManager(layoutManager);
        prePhotoAdapter = new PhotoAdapter(getContext(), prePhotoModels, this);
        prePhoto.setAdapter(prePhotoAdapter);

        //Post Maintenance Photo Adapter Setup
        RecyclerView.LayoutManager postLayoutManager = new GridLayoutManager(getContext(),3);
        postPhoto.setLayoutManager(postLayoutManager);
        postPhotoAdapter = new PhotoAdapter(getContext(), postPhotoModels,this);
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
     *Upload one photo to server and get url id
     * @param bitmap
     * @param name
     */
    private void uploadPhoto(Bitmap bitmap, String name, Boolean preOrPost) {
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
        builder.addFormDataPart("file",fileName.getName(),RequestBody.create(MediaType.parse("multipart/form-data"),fileName));
        MultipartBody requestBody = builder.build();

        String bucketName = "pids-post-maintenance-photo";
        if(preOrPost)
            bucketName = "pids-pre-maintenance-photo";

        Call<ReturnStatus> returnStatusCall = apiInterface.uploadPhoto(bucketName,  requestBody);
        returnStatusCall.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                ReturnStatus returnStatus = response.body();
                if(response.isSuccessful()){
                    if(preOrPost){
                        preEventList.add(new Event("PRE_MAINTENANCE_PHOTO_UPDATE","postMaintenancePhotoUpdate",returnStatus.getData().getFileUrl()));
                    }else {
                        postEventList.add(new Event("POST_MAINTENANCE_PHOTO_UPDATE","postMaintenancePhotoUpdate",returnStatus.getData().getFileUrl()));
                    }
                    Toast.makeText(getContext(),returnStatus.getData().getFileUrl()+":Ok",Toast.LENGTH_SHORT).show();
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