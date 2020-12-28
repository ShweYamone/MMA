package com.freelance.solutionhub.mma.fragment;

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
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.NFCReadingActivity;
import com.freelance.solutionhub.mma.activity.PMActivity;
import com.freelance.solutionhub.mma.activity.PMCompletionActivity;
import com.freelance.solutionhub.mma.model.Event;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PhotoModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.UploadPhotoModel;
import com.freelance.solutionhub.mma.model.VerificationReturnBody;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.Network;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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

import static com.freelance.solutionhub.mma.util.AppConstant.CM_Step_THREE;
import static com.freelance.solutionhub.mma.util.AppConstant.JOBDONE;
import static com.freelance.solutionhub.mma.util.AppConstant.PM_Step_ONE;
import static com.freelance.solutionhub.mma.util.AppConstant.PM_Step_TWO;
import static com.freelance.solutionhub.mma.util.AppConstant.POST_BUCKET_NAME;
import static com.freelance.solutionhub.mma.util.AppConstant.YES;
import static com.freelance.solutionhub.mma.util.AppConstant.pm;

public class Second_Step_PM_Fragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.btnJobDone)
    Button btnJobDone;

    @BindView(R.id.et_remarks)
    EditText remarks;

    @BindView(R.id.btn_verify)
    Button verify;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePreferenceHelper;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    private Date date;
    private Network network;
    private InitializeDatabase dbHelper;
    private String actualDateTime = "";


    public Second_Step_PM_Fragment(){}
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getArguments().getSerializable("object"));
        View view = inflater.inflate(R.layout.fragment_second__step__p_m_, container, false);
        // Inflate the layout for this fragment
        ButterKnife.bind(this, view);
        apiInterface = ApiClient.getClient(this.getContext());
        mSharePreferenceHelper = new SharePreferenceHelper(this.getContext());
        network = new Network(getContext());
        dbHelper = InitializeDatabase.getInstance(getContext());

        btnJobDone.setClickable(false);
        btnJobDone.setOnClickListener(this);
        verify.setOnClickListener(this);

        if (dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(PM_Step_TWO) > 0) {
            UpdateEventBody temp = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(PM_Step_TWO);
            remarks.setText(temp.getRemark() + "");
        }


        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify:
                if(mSharePreferenceHelper.userClickPMStepOneOrNot()){
                    UpdateEventBody updateEventBody = new UpdateEventBody(
                            mSharePreferenceHelper.getUserName(),
                            mSharePreferenceHelper.getUserId(),
                            actualDateTime,
                            pmServiceInfoDetailModel.getId()
                    );

                    updateEventBody.setId(PM_Step_TWO);
                    updateEventBody.setServiceOrderStatus(JOBDONE);
                    updateEventBody.setRemark(remarks.getText().toString()+"");
                    dbHelper.updateEventBodyDAO().insert(updateEventBody);
                    if (network.isNetworkAvailable()) {
                        Call<VerificationReturnBody> call = apiInterface.verifyWorks("Bearer " + mSharePreferenceHelper.getToken(), pmServiceInfoDetailModel.getId());
                        call.enqueue(new Callback<VerificationReturnBody>() {
                            @Override
                            public void onResponse(Call<VerificationReturnBody> call, Response<VerificationReturnBody> response) {
                                if (response.isSuccessful()) {
                                    VerificationReturnBody verificationReturnBody = response.body();
                                    if (verificationReturnBody.isFault_resolved()) {
                                        btnJobDone.setClickable(true);
                                        btnJobDone.setBackground(getResources().getDrawable(R.drawable.round_rect_shape_button));
                                    } else {
                                        //  Toast.makeText(getContext(), "Verification failed", Toast.LENGTH_SHORT).show();
                                        showDialog("Verification", "Verification Failed.");
                                        btnJobDone.setClickable(false);
                                        btnJobDone.setBackground(getResources().getDrawable(R.drawable.round_rectangle_shape_button_grey));
                                    }
                                    btnJobDone.setClickable(true);
                                    btnJobDone.setBackground(getResources().getDrawable(R.drawable.round_rect_shape_button));

                                }
                                else {
                                    Toast.makeText(getContext(), response.code() + "", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<VerificationReturnBody> call, Throwable t) {

                            }
                        });
                    }else {
                        showDialog("Network Connetion",
                                "The network connection is lost. Please, check your connectivity and try again");
                    }

                }
                else {
                    showDialog("Unsaved Work", "You have unsaved works in Step 1.");
                }
                break;
            case R.id.btnJobDone :
                updateEvent();
        }
    }

    private void updateEvent() {
        if (network.isNetworkAvailable()) {

            /**  STEP_ONE EVENT UPDATE */
            ArrayList<PhotoModel> postPhotoModels = new ArrayList<>();
            List<UploadPhotoModel> uploadPhotoModels = dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(POST_BUCKET_NAME);
            for (UploadPhotoModel photoModel: uploadPhotoModels) {
                postPhotoModels.add(new PhotoModel(getDecodedString(photoModel.getEncodedPhotoString()), 1));
            }

            ((PMActivity)getActivity()).showProgressBar();
            new LoadPOSTImage(
                    dbHelper.eventDAO().getEventsToUpload(PM_Step_ONE),
                    POST_BUCKET_NAME,
                    dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(PM_Step_ONE))
                    .execute(uploadPhoto(postPhotoModels));

        } else {
            showDialog("Network Connetion", "The network connection is lost. Please, check your connectivity and try again");
        }
    }

    /**
     *Upload one photo to server and get url id
     */
    private File[] uploadPhoto(ArrayList<PhotoModel> p) {
        File[] files = new File[p.size()];
        for(int i = 0 ; i < p.size(); i++) {
            File filesDir = getContext().getFilesDir();
            File fileName = new File(filesDir, mSharePreferenceHelper.getUserId()+getSaltString() + ".jpg");

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

    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        return img;
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

    private void updateSTEP_Two() {
        //completeWork();
        /** STEP_TWO EVENT UPDATE*/
        UpdateEventBody updateEventBody = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(PM_Step_TWO);
        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);
        updateEventBody.setDate(actualDateTime);
        dbHelper.updateEventBodyDAO().insert(updateEventBody);
        ((PMActivity)getActivity()).hideProgressBar();
        //  deleteWorkingData();
        completeWork();
        /*

        Call<ReturnStatus> call = apiInterface.updateStatusEvent("Bearer " + mSharePreferenceHelper.getToken(),
               updateEventBody);
        call.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),response.body().getStatus() + ": STEP_TWO_Event Uploaded.",Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                    ResponseBody errorReturnBody = response.errorBody();
                    try {
                        Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                        ((PMActivity)getActivity()).hideProgressBar();
                    } catch (IOException e) {

                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {
                Toast.makeText(getContext(), "response " + "FAILURE", Toast.LENGTH_LONG).show();
                ((PMActivity)getActivity()).hideProgressBar();
                Log.e("UPLOAD_ERROR", "onResponse: " + t.getMessage());
            }
        });*/
    }



    private void completeWork() {
        Intent intent = new Intent(this.getContext(), NFCReadingActivity.class);
        intent.putExtra("id", pmServiceInfoDetailModel.getId());
        intent.putExtra("panelId", pmServiceInfoDetailModel.getPanelId());
        intent.putExtra("schedule_date", pmServiceInfoDetailModel.getCreationDate());
        intent.putExtra("schedule_type", pmServiceInfoDetailModel.getPreventativeMaintenanceCheckType());
        intent.putExtra("start_time", getArguments().getString("start_time"));
        intent.putExtra("object", pmServiceInfoDetailModel);
        intent.putExtra("remarks", remarks.getText().toString()+"");
        Event tempEvent;
        tempEvent = new Event("panelId", "panelId", pmServiceInfoDetailModel.getPanelId()+"");
        tempEvent.setEvent_id("panelIdpanelId");
        dbHelper.eventDAO().insert(tempEvent);

        tempEvent = new Event("schedule_date", "schedule_date", pmServiceInfoDetailModel.getCreationDate());
        tempEvent.setEvent_id("schedule_datescheduledate");
        dbHelper.eventDAO().insert(tempEvent);

        tempEvent = new Event("schedule_type", "schedule_type", pmServiceInfoDetailModel.getPreventativeMaintenanceCheckType());
        tempEvent.setEvent_id("typetype");
        dbHelper.eventDAO().insert(tempEvent);

        tempEvent = new Event("start_time", "start_time", getArguments().getString("start_time"));
        tempEvent.setEvent_id("start_timestart_time");
        dbHelper.eventDAO().insert(tempEvent);

        tempEvent = new Event("remarks", "remarks", remarks.getText().toString() + "");
        tempEvent.setEvent_id("remarks");
        dbHelper.eventDAO().insert(tempEvent);

        intent.putExtra("JOB_DONE", 1);
        intent.putExtra("TAG_OUT", 1);

        startActivity(intent);
        getActivity().finish();

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
                        dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(PM_Step_ONE));
                Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePreferenceHelper.getToken(),
                        updateEventBody);
                call.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(),  response.body().getStatus()+ f.size() + " EVENTS :STEP_ONE UPLOADED" , Toast.LENGTH_SHORT).show();
                            mSharePreferenceHelper.userClickPMStepOne(true);
                            dbHelper.eventDAO().update(YES, PM_Step_ONE);
                            f.clear();
                            updateSTEP_Two();

                        } else {
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                                Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {

                            }
                            ((PMActivity)getActivity()).hideProgressBar();
                        }

                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {
                        ((PMActivity)getActivity()).hideProgressBar();
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
                            Toast.makeText(getContext(), returnStatus.getStatus() + ":PHOTO"+count, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("PhotoUpload", "doInBackground: " + response.message() + response.headers());
                            Toast.makeText(getContext(), "FAILED:" + response.code(), Toast.LENGTH_LONG).show();
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


    /**
     * Encode photo string to decode string
     */
    private String getDecodedString(String s){
        byte[] data = Base64.decode(s,Base64.DEFAULT);
        String s1 = new String(data);
        Log.v("DECODE", s1);
        return s1;
    }

    private void showDialog(String dialogTitle, String dialogBody) {
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




}