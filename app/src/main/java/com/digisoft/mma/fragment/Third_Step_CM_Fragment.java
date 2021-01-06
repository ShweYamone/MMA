package com.digisoft.mma.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.activity.CMActivity;
import com.digisoft.mma.activity.NFCReadingActivity;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.PMServiceInfoDetailModel;
import com.digisoft.mma.model.PhotoModel;
import com.digisoft.mma.model.ReturnStatus;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.model.UploadPhotoModel;
import com.digisoft.mma.model.VerificationReturnBody;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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

import static com.digisoft.mma.util.AppConstant.CM_Step_ONE;
import static com.digisoft.mma.util.AppConstant.CM_Step_THREE;
import static com.digisoft.mma.util.AppConstant.CM_Step_TWO;
import static com.digisoft.mma.util.AppConstant.JOBDONE;
import static com.digisoft.mma.util.AppConstant.POST_BUCKET_NAME;
import static com.digisoft.mma.util.AppConstant.PRE_BUCKET_NAME;
import static com.digisoft.mma.util.AppConstant.VERIFICATION_FAIL_MSG;
import static com.digisoft.mma.util.AppConstant.YES;

public class Third_Step_CM_Fragment extends Fragment implements View.OnClickListener{

    @BindView(R.id.et_remarks)
    EditText remarks;

    @BindView(R.id.btn_job_done)
    Button jobDone;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.rbCloudy)
    RadioButton rbCloudy;

    @BindView(R.id.rbHeavyRaining)
    RadioButton rbHeavyRaining;

    @BindView(R.id.rbRaining)
    RadioButton rbRaining;

    @BindView(R.id.rbSunny)
    RadioButton rbSunny;

    @BindView(R.id.btn_verify)
    Button btnVerify;

    private RadioButton radioButton;
    private String weather;
    private Date date;
    private SharePreferenceHelper mSharePreferenceHelper;
    private Network network;
    private InitializeDatabase dbHelper;
    private ApiInterface apiInterface;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private String actualDateTime;
    private  Timestamp timestamp;
    private  Random rnd;
    private OutputStream os;

    private boolean stepOneUploaded = false;
    private boolean stepTwoUploaded = false;

    private String dialogTitle = "";
    private String dialogBody = "";
    /* Assign a string that contains the set of characters you allow. */
    private static final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ0123456789";

    private char[] buf;

    private UpdateEventBody updateEventBody;

    public Third_Step_CM_Fragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_third__step__c_m_, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        mSharePreferenceHelper = new SharePreferenceHelper(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());
        network = new Network(getContext());
        apiInterface = ApiClient.getClient(getContext());

        jobDone.setClickable(false);
        jobDone.setFocusable(false);
        btnVerify.setOnClickListener(this);

        rnd =  new SecureRandom();
        buf = new char[6];

        // get selected radio button from radioGroup
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = view.findViewById(selectedId);
        weather = radioButton.getText().toString();

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                                  @Override
                                                  public void onCheckedChanged(RadioGroup group, int checkedId)
                                                  {
                                                      radioButton = (RadioButton) view.findViewById(checkedId);
                                                      weather = radioButton.getText().toString();
                                                  }
                                              }
        );

        if (dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(CM_Step_THREE) > 0) {
            UpdateEventBody temp = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(CM_Step_THREE);
            remarks.setText(temp.getRemark() + "");
            switch (temp.getWeatherCondition()) {
                case "Sunny" : rbSunny.setChecked(true);break;
                case "Cloudy" : rbCloudy.setChecked(true);break;
                case "Raining" : rbRaining.setChecked(true);break;
                case "Heavy Rain": rbHeavyRaining.setChecked(true);break;
            }
        }

        return view;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                if (!mSharePreferenceHelper.userClickStepOneOrNot() && !mSharePreferenceHelper.userClickStepTwoOrNot() ) {
                    dialogTitle = "Unsaved Work";
                    dialogBody = "You have unsaved works in Step 1 & 2.";
                    showDialog();
                } else if (!mSharePreferenceHelper.userClickStepOneOrNot()) {
                    dialogTitle = "Unsaved Work";
                    dialogBody = "You have unsaved works in Step 1.";
                    showDialog();
                } else if (!mSharePreferenceHelper.userClickStepTwoOrNot()) {
                    dialogTitle = "Unsaved Work";
                    dialogBody = "You have unsaved works in Step 2.";
                    showDialog();
                } else {
                    date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
                    UpdateEventBody updateEventBody = new UpdateEventBody(
                            mSharePreferenceHelper.getUserName(),
                            mSharePreferenceHelper.getUserId(),
                            actualDateTime,
                            pmServiceInfoDetailModel.getId()
                    );

                    updateEventBody.setId(CM_Step_THREE);
                    updateEventBody.setServiceOrderStatus(JOBDONE);
                    updateEventBody.setRemark(remarks.getText().toString());
                    updateEventBody.setWeatherCondition(weather);
                    dbHelper.updateEventBodyDAO().insert(updateEventBody);

                    if(network.isNetworkAvailable()) {

                        ((CMActivity)getActivity()).showProgressBar(true);
                        Call<VerificationReturnBody> call = apiInterface.verifyWorks("Bearer " + mSharePreferenceHelper.getToken(), pmServiceInfoDetailModel.getId());
                        call.enqueue(new Callback<VerificationReturnBody>() {
                            @Override
                            public void onResponse(Call<VerificationReturnBody> call, Response<VerificationReturnBody> response) {
                                if (response.isSuccessful()) {
                                    VerificationReturnBody verificationReturnBody = response.body();
                                    if (verificationReturnBody.isFault_resolved()) {
                                        jobDone.setClickable(true);
                                        jobDone.setFocusable(true);
                                        jobDone.setBackground(getResources().getDrawable(R.drawable.round_rect_shape_button));
                                        jobDone.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                try {
                                                    updateEvent();
                                                } catch (IOException e) {
                                                    Log.e("ERROR_IN_CM_STEP3",e.getMessage());
                                                }
                                            }
                                        });

                                    } else {
                                        dialogTitle = "Verification Fail";
                                        dialogBody = VERIFICATION_FAIL_MSG;
                                        showDialog();
                                       // Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                                        jobDone.setClickable(false);
                                        jobDone.setFocusable(false);
                                        jobDone.setBackground(getResources().getDrawable(R.drawable.round_rectangle_shape_button_grey));
                                    }


                                }
                                else {
                                //    Toast.makeText(getContext(), response.code() + "", Toast.LENGTH_SHORT).show();
                                }
                                ((CMActivity)getActivity()).hideProgressBar();
                            }


                            @Override
                            public void onFailure(Call<VerificationReturnBody> call, Throwable t) {
                                ((CMActivity)getActivity()).hideProgressBar();
                            }
                        });
                    } else {
                        dialogTitle = "Network Connetion";
                        dialogBody = "The network connection is lost. Please, check your connectivity and try again";
                        showDialog();
                    }

                }


                break;


            case R.id.btn_job_done :
                try {
                    updateEvent();
                } catch (IOException e) {
                    Log.e("ERROR_IN_CM_STEP3",e.getMessage());
                }
        }
    }

    private void showDialog() {
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


    public void updateEvent() throws IOException {

        if (network.isNetworkAvailable()) {

            /**  STEP_ONE EVENT UPDATE */
            ArrayList<PhotoModel>  prePhotoModels = new ArrayList<>();
            List<UploadPhotoModel> uploadPhotoModels = dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(PRE_BUCKET_NAME);
            for (UploadPhotoModel photoModel: uploadPhotoModels) {
                prePhotoModels.add(new PhotoModel(getDecodedString(photoModel.getEncodedPhotoString()), 1));
            }

            ((CMActivity)getActivity()).showProgressBar(false);
            if (dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(CM_Step_ONE) > 0) {
                List<Event> events = new ArrayList<>();
                new LoadPREImage(
                        events,
                        PRE_BUCKET_NAME,
                        dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(CM_Step_ONE))
                        .execute(uploadPhoto(prePhotoModels));
            } else {

                /**  STEP_TWO EVENT UPDATE */
                ArrayList<PhotoModel>  postPhotoModels = new ArrayList<>();
                uploadPhotoModels = dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(POST_BUCKET_NAME);
                for (UploadPhotoModel photoModel: uploadPhotoModels) {
                    postPhotoModels.add(new PhotoModel(getDecodedString(photoModel.getEncodedPhotoString()), 1));
                }
                new LoadPOSTImage(
                        dbHelper.eventDAO().getEventsToUpload(CM_Step_TWO),
                        POST_BUCKET_NAME,
                        dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(CM_Step_TWO))
                        .execute(uploadPhoto(postPhotoModels));
            }



        } else {
            dialogTitle = "Network Connetion";
            dialogBody = "The network connection is lost. Please, check your connectivity and try again";
            showDialog();
        }

    }

    /**
     *Upload one photo to server and get url id
     */
    private File[] uploadPhoto(ArrayList<PhotoModel> p) throws IOException {
        File[] files = new File[p.size()];
        date = new Date();
       timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(timestamp);
        for(int i = 0 ; i < p.size(); i++) {
            File filesDir = getContext().getFilesDir();
            File fileName = new File(filesDir, mSharePreferenceHelper.getUserId()+actualDateTime+ nextString()+ ".jpg");

            Log.i("FILE_NAME", fileName.toString());
            try {
                os = new FileOutputStream(fileName);
                getBitmapFromEncodedString(p.get(i).getImage()).compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
            } catch (Exception e) {
                Log.e("PHOTO", "Error writing bitmap", e);
            }finally {
                os.close();
            }
            files[i]= fileName;
        }

        return files;
    }

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        return img;


    }

    public String nextString()
    {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols.charAt(rnd.nextInt(symbols.length()));
        return new String(buf);
    }

    class LoadPREImage extends AsyncTask<File, Void, Boolean> {

        private List<Event> f;
        private int count = 0;
        private String buckerName;
        private UpdateEventBody updateEventBody;

        public LoadPREImage(List<Event> f, String buckerName, UpdateEventBody updateEventBody) {
            this.f = f;
            this.buckerName = buckerName;
            this.updateEventBody = updateEventBody;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(f.size() == count  ){

            }
        }

        private void updatePreEvents() {

            if (f.size() > 0) {
                updateEventBody.setEvents(f);

                Log.e("UPLOAD_ERROR", "updateEvents: " +
                        dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(CM_Step_ONE));
                Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(),
                        updateEventBody);
                call.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        if (response.isSuccessful()) {
                        //    Toast.makeText(getContext(),  response.body().getStatus()+ ", " + f.size() + " PRE_EVENTS UPLOADED at " +
                        //            updateEventBody.getDate(), Toast.LENGTH_SHORT).show();

                            mSharePreferenceHelper.userClickCMStepOne(true);
                            dbHelper.eventDAO().update(YES, CM_Step_ONE);
                            f.clear();


                            /**  STEP_TWO EVENT UPDATE */
                            ArrayList<PhotoModel>  postPhotoModels = new ArrayList<>();
                            List<UploadPhotoModel> uploadPhotoModels = dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(POST_BUCKET_NAME);
                            for (UploadPhotoModel photoModel: uploadPhotoModels) {
                                postPhotoModels.add(new PhotoModel(getDecodedString(photoModel.getEncodedPhotoString()), 1));
                            }
                            try {
                                new LoadPOSTImage(
                                        dbHelper.eventDAO().getEventsToUpload(CM_Step_TWO),
                                        POST_BUCKET_NAME,
                                        dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(CM_Step_TWO))
                                        .execute(uploadPhoto(postPhotoModels));
                            } catch (IOException e) {
                                Log.e("ERROR_IN_CM_STEP3",e.getMessage());
                            }

                        } else {
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                             //   Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                                ((CMActivity)getActivity()).hideProgressBar();
                            } catch (IOException e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        ((CMActivity)getActivity()).hideProgressBar();
                    }
                });

            }

        }

        @Override
        protected Boolean doInBackground(File... files) {
            for (File fileName : files) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", fileName.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fileName));
                MultipartBody requestBody = builder.build();
                Log.i("PhotoUpload", "doInBackground: " + requestBody.type());

                Call<ReturnStatus> returnStatusCall = apiInterface.uploadPhoto("Bearer " + mSharePreferenceHelper.getToken(),
                        buckerName, requestBody);
                returnStatusCall.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        ReturnStatus returnStatus = response.body();
                        if (response.isSuccessful()) {
                            f.add(new Event("PRE_MAINTENANCE_PHOTO_UPDATE", "preMaintenancePhotoUpdate", returnStatus.getData().getFileUrl()));

                            count++;
                            if(files.length == count)
                                updatePreEvents();
                            Log.i("PHOTOPATH",returnStatus.getData().getFileUrl());
                        //    Toast.makeText(getContext(), returnStatus.getStatus() + ":PHOTO"+count, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("PhotoUpload", "doInBackground: " + response.message() + response.headers());
                        //    Toast.makeText(getContext(), "FAILED:" + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {

                    }
                });
            }


            if(files.length == count) {
                if(f.size() != 0) {
                    updatePreEvents();
                }
                return true;
            }
            return false;
        }

    }

    class LoadPOSTImage extends AsyncTask<File, Void, Boolean> {

        private List<Event> f;
        private int count = 0;
        private String buckerName;
        private UpdateEventBody updateEventBody;

        public LoadPOSTImage(List<Event> f, String buckerName, UpdateEventBody updateEventBody) {
            this.f = f;
            this.buckerName = buckerName;
            this.updateEventBody = updateEventBody;
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            super.onPostExecute(aVoid);
            if(f.size() == count  ){

            }
        }

        private void updatePreEvents() {

            if (f.size() > 0) {
                updateEventBody.setEvents(f);

                Log.e("UPLOAD_ERROR", "updateEvents: " +
                        dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(CM_Step_TWO));
                Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(),
                        updateEventBody);
                call.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        if (response.isSuccessful()) {
                        //    Toast.makeText(getContext(),  response.body().getStatus()+ ", " + f.size() + " POST_EVENTS UPLOADED at" +
                        //            updateEventBody.getDate(), Toast.LENGTH_SHORT).show();

                            mSharePreferenceHelper.userClickCMStepTwo(true);
                            dbHelper.eventDAO().update(YES, CM_Step_TWO);
                            ((CMActivity)getActivity()).hideProgressBar();
                            f.clear();
                            updateSTEP_Three();

                        } else {
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                            //    Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                                ((CMActivity)getActivity()).hideProgressBar();
                            } catch (IOException e) {

                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        ((CMActivity)getActivity()).hideProgressBar();
                    }
                });

            }

        }

        @Override
        protected Boolean doInBackground(File... files) {
            for (File fileName : files) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", fileName.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fileName));
                MultipartBody requestBody = builder.build();
                Log.i("PhotoUpload", "doInBackground: " + requestBody.type());

                Call<ReturnStatus> returnStatusCall = apiInterface.uploadPhoto("Bearer " + mSharePreferenceHelper.getToken(),
                        buckerName, requestBody);
                returnStatusCall.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        ReturnStatus returnStatus = response.body();
                        if (response.isSuccessful()) {
                            f.add(new Event("POST_MAINTENANCE_PHOTO_UPDATE", "postMaintenancePhotoUpdate", returnStatus.getData().getFileUrl()));

                            count++;
                            if(files.length == count)
                                updatePreEvents();
                            Log.i("PHOTOPATH",returnStatus.getData().getFileUrl());
                        //    Toast.makeText(getContext(), returnStatus.getStatus() + ":PHOTO"+count, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("PhotoUpload", "doInBackground: " + response.message() + response.headers());
                         //   Toast.makeText(getContext(), "FAILED:" + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {

                    }
                });
            }


            if(files.length == count) {
                if(f.size() != 0) {
                    updatePreEvents();
                }
                return true;
            }
            return false;
        }

    }

    private void updateSTEP_Three() {
       // completeWork();
        /** STEP_THREE EVENT UPDATE*/
        UpdateEventBody updateEventBody = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(CM_Step_THREE);
        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        updateEventBody.setDate(actualDateTime);
        dbHelper.updateEventBodyDAO().insert(updateEventBody);

        ((CMActivity)getActivity()).hideProgressBar();
        completeWork();
    }

    private void completeWork() {
        Intent intent = new Intent(this.getContext(), NFCReadingActivity.class);
        intent.putExtra("id", pmServiceInfoDetailModel.getId());
        intent.putExtra("panelId", pmServiceInfoDetailModel.getPanelId()+"");
        intent.putExtra("remarks", remarks.getText().toString()+"");
        intent.putExtra("location", pmServiceInfoDetailModel.getBusStopLocation()+"");
        intent.putExtra("object", pmServiceInfoDetailModel);
        Event tempEvent;
        tempEvent = new Event("panelId", "panelId", pmServiceInfoDetailModel.getPanelId());
        tempEvent.setEvent_id("panelIdpanelId");
        dbHelper.eventDAO().insert(tempEvent);
        tempEvent = new Event("remarks", "remarks", remarks.getText().toString()+"");
        tempEvent.setEvent_id("remarksremarks");
        dbHelper.eventDAO().insert(tempEvent);
        tempEvent = new Event("location", "location", pmServiceInfoDetailModel.getBusStopLocation());
        tempEvent.setEvent_id("locationlocation");
        dbHelper.eventDAO().insert(tempEvent);

        intent.putExtra("TAG_OUT", 1);
        intent.putExtra("JOB_DONE", 1);
        startActivity(intent);
     //   getActivity().finish();

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