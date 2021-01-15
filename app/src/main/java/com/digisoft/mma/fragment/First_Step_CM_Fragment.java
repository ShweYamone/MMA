package com.digisoft.mma.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.activity.CMActivity;
import com.digisoft.mma.adapter.PhotoAdapter;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.PMServiceInfoDetailModel;
import com.digisoft.mma.model.PhotoAttachementModel;
import com.digisoft.mma.model.PhotoFilePathModel;
import com.digisoft.mma.model.PhotoModel;
import com.digisoft.mma.model.PreMaintenance;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.model.UploadPhotoModel;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.FileProvider.getUriForFile;
import static com.digisoft.mma.util.AppConstant.CM_Step_ONE;
import static com.digisoft.mma.util.AppConstant.DATE_FORMAT;
import static com.digisoft.mma.util.AppConstant.FAILURE;
import static com.digisoft.mma.util.AppConstant.PRE_BUCKET_NAME;
import static com.digisoft.mma.util.AppConstant.TIME_SERVER;
import static com.digisoft.mma.util.AppConstant.UPLOAD_ERROR;

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



    static final int REQUEST_PICTURE_CAPTURE = 1;
    private String pictureFilePath;

    private boolean isAttachment;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
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


        isAttachment = false;
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
                prePhotoModels.add(new PhotoModel(getDecodedString(uploadPhotoModel.getEncodedPhotoString()), 1, uploadPhotoModel.getPhotoFilePath()));
            }
            prePhotoAdapter.notifyDataSetChanged();
        }

        preMaintenancePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isAttachment){
                    dialogTitle = "Pre-maintenance Photo Unnecessary";
                    dialogBody = "Pre-maintenance photos are not necessary to upload again.";
                    showDialog();
                }else {
                    if (getActivity().checkPermission(Manifest.permission.CAMERA, 1, 1) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    } else {
                        sendTakePictureIntent();

                    }
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
                        isAttachment = true;
                        save.setClickable(false);
                        mSharePerferenceHelper.userClickCMStepOne(true);
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
                Log.e(UPLOAD_ERROR, FAILURE + "");

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
                        //The user clicks ok button
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
            String actualDateTime = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
            UpdateEventBody updateEventBody = new UpdateEventBody(
                    mSharePerferenceHelper.getUserName(),
                    mSharePerferenceHelper.getUserId(),
                    actualDateTime,
                    pmServiceInfoModel.getId()
            );

            updateEventBody.setId(CM_Step_ONE);
            dbHelper.updateEventBodyDAO().insert(updateEventBody);

            if (mNetwork.isNetworkAvailable()) {
                ((CMActivity)getActivity()).showProgressBar(false);
                new getCurrentNetworkTime().execute();
            }

            dbHelper.uploadPhotoDAO().deleteById(CM_Step_ONE);
            for (PhotoModel photoModel: prePhotoModels) {
                if(photoModel.getUid()==1)
                    saveEncodePhotoToDatabase(CM_Step_ONE, PRE_BUCKET_NAME, photoModel.getImage(), photoModel.getPhotoPath());
            }
            //Toast.makeText(this.getContext(), dbHelper.uploadPhotoDAO().getNumberOfPhotosToUpload()+" photos have been saved.", Toast.LENGTH_SHORT).show();
        }
        else {
            dialogTitle = "Photo";
            dialogBody = "Your photos must be minimum 2 and maximum 5.";
            showDialog();
        }
    }

    class getCurrentNetworkTime extends AsyncTask<String, Void, Boolean> {
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
                String localDateTime = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
                Log.i("Time__Local", "doInBackground: " + localTime + "--> " + localDateTime);
                timestamp = new Timestamp(serverTime);
                String actualDateTime = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
                Log.i("Time__Server", "doInBackground:" + serverTime + "--> " + actualDateTime);
                //after getting network time, update event with the network time
                dbHelper.updateEventBodyDAO().updateDateTime(actualDateTime, CM_Step_ONE);

                if (new Date(localTime) != new Date(serverTime))
                    is_locale_date = true;

            } catch (UnknownHostException e) {
                Log.e("UnknownHostException: ", e.getMessage());
            } catch (IOException e) {
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



    /**
     * //To Do save to database photo
     */
    private void saveEncodePhotoToDatabase(String updateEventKey, String bucketName, String sPhoto, String photoPath){
        byte[] bytes = sPhoto.getBytes();
        String encodeToString = Base64.encodeToString(bytes,Base64.DEFAULT);
        Log.v("ENCODE",encodeToString);
        dbHelper.uploadPhotoDAO().insert(new UploadPhotoModel(
                updateEventKey, bucketName, encodeToString, photoPath
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
    private void sendTakePictureIntent() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(getContext(),
                        "Photo file can't be created, please try again",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = getUriForFile(getContext(),
                        "com.digisoft.mma.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        }
    }
    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "PHOTO_" + timeStamp;
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        savePhotoFilePath(pictureFilePath);
        return image;
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
                sendTakePictureIntent();
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
    public void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        mSharePerferenceHelper.setLock(false);
        Log.i("Tracing......", "onActivityResult: " + mSharePerferenceHelper.getLock());
//        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK)
//        {
//        //    if (data != null) {
//                theImage = (Bitmap) data.getExtras().get("data");
//                photo = getEncodedString(theImage);
//                Log.v("ORI", photo);
//                prePhotoModels.add(new PhotoModel(photo, 1));
//                prePhotoAdapter.notifyDataSetChanged();
//         //   }
//
//        }
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                theImage = setPic(imgFile.getAbsolutePath());
                photo = getEncodedString(theImage);
                Log.v("ORI", photo);
                prePhotoModels.add(new PhotoModel(photo, 1,pictureFilePath));
                prePhotoAdapter.notifyDataSetChanged();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private Bitmap setPic(String currentPhotoPath) {
        // Get the dimensions of the View

        int targetW = 512;
        int targetH = 680;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

    /**
     * Save FilePath To DataBase
     */
    private void savePhotoFilePath(String photoFilePath) {
        dbHelper.photoFilePathDAO().insert(new PhotoFilePathModel(photoFilePath));
    }


}