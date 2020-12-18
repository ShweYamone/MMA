package com.freelance.solutionhub.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PhotoModel;
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
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(prePhotoModels.size() > 0)
                    save();
                else {
                    mSharePerferenceHelper.userClickCMStepOne(false);
                    new AlertDialog.Builder(getContext())
                            .setIcon(R.drawable.warning)
                            .setTitle("Photo")
                            .setMessage("You need to attach premaintenance photos.")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }

                            })
                            .show();
                }

            }
        });
        setDataAdapter();
        return view;
    }
    /**
     * Update event for all
     */
    public void save(){
            //For no photo upload

            Log.v("BEFORE_SIZE", "No zero");
            if( mNetwork.isNetworkAvailable() ) {
                if (prePhotoModels.size() > 1 && prePhotoModels.size() < 6) {
                    Log.v("JOIN", preEventList.size() + "");
                    //Upload photos to server
                    new LoadImage(preEventList).execute(uploadPhoto(prePhotoModels));

                } else {
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
            }//Network end

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
                Log.v("ORI",photo);
                prePhotoModels.add(new PhotoModel(photo, 1));
                prePhotoAdapter.notifyDataSetChanged();

        }
    }


    /**
     * Set two recycler view with adapter
     */
    private void setDataAdapter() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),3  );
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

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);

       /* or use below if you want 32 bit images

        bitmap.compress(Bitmap.CompressFormat.PNG, (0–100 compression), os);*/
        byte[] imageArr = os.toByteArray();

        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }

    /**
     *Upload one photo to server and get url id
     */
    private File[] uploadPhoto(ArrayList<PhotoModel> p) {
        File[] files = new File[p.size()];
        for(int i = 0 ; i < p.size(); i++) {
            File filesDir = getContext().getFilesDir();
            File fileName = new File(filesDir, mSharePerferenceHelper.getUserId()+getSaltString() + ".jpg");

            Log.i("FILE_NAME", fileName.toString());
            OutputStream os;
            try {
                os = new FileOutputStream(fileName);
                getBitmapFromEncodedString(p.get(i).getImage()).compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e("PHOTO", "Error writing bitmap", e);
            }
            files[i]= fileName;
        }

        return files;
    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 7) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }



    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        return img;


    }

    /**
     * //To Do save to database photo
     */
    private void saveEncodePhotoToDatabase(String bucketName, String sPhoto){
        byte[] bytes = sPhoto.getBytes();
        /**
         * encodeToString is encoded string
         */

        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyyMMddHH:mm:ss").format(timestamp);
        String encodeToString = Base64.encodeToString(bytes,Base64.DEFAULT);
        dbHelper.uploadPhotoDAO().insert(new UploadPhotoModel(bucketName, encodeToString,actualDateTime));
        Log.v("ENCODE",encodeToString);

    }

    class LoadImage extends AsyncTask<File, Void, Boolean> {

        private ArrayList<Event> f;
        private int count = 0;

        public LoadImage(ArrayList<Event> f) {
            this.f = f;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(f.size() == count  ){

            }
        }

        private void uploadEvent(){
            Log.i("ALLPH","onPostExecute"+f.size());
            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
            UpdateEventBody updateEventBody = new UpdateEventBody(mSharePerferenceHelper.getUserName(),
                    mSharePerferenceHelper.getUserId(),
                    actualDateTime,
                    pmServiceInfoModel.getId(),
                    f);

            Call<ReturnStatus> returnStatusCallEvent = apiInterface.updateEvent("Bearer " + mSharePerferenceHelper.getToken(), updateEventBody);
            returnStatusCallEvent.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                    ReturnStatus returnStatus = response.body();
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), returnStatus.getStatus() + ":URL", Toast.LENGTH_LONG).show();
                        mSharePerferenceHelper.userClickCMStepOne(true);

                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {
                    Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            f.clear();
        }

        @Override
        protected Boolean doInBackground(File... files) {
            for (File fileName : files) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", fileName.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fileName));
                MultipartBody requestBody = builder.build();


                Call<ReturnStatus> returnStatusCall = apiInterface.uploadPhoto("pids-pre-maintenance-photo", requestBody);
                returnStatusCall.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        ReturnStatus returnStatus = response.body();
                        if (response.isSuccessful()) {
                            Log.v("PRE_EVENT", "Added pre event");
                            f.add(new Event("PRE_MAINTENANCE_PHOTO_UPDATE", "preMaintenancePhotoUpdate", returnStatus.getData().getFileUrl()));
                            count++;
                            if(files.length == count)
                                uploadEvent();
                            Toast.makeText(getContext(), returnStatus.getStatus() + ":PHOTO"+count, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "FAILED", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {

                    }
                });
            }


            if(files.length == count) {
                return true;
            }
            return false;
        }

    }

}