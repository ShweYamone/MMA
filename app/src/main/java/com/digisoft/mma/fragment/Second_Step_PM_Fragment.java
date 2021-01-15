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

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.activity.CMActivity;
import com.digisoft.mma.activity.NFCReadingActivity;
import com.digisoft.mma.activity.PMActivity;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.PMServiceInfoDetailModel;
import com.digisoft.mma.model.PhotoFilePathModel;
import com.digisoft.mma.model.PhotoModel;
import com.digisoft.mma.model.ReturnStatus;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.model.UploadPhotoModel;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
import static com.digisoft.mma.util.AppConstant.DATE_FORMAT;
import static com.digisoft.mma.util.AppConstant.JOBDONE;
import static com.digisoft.mma.util.AppConstant.PM_Step_ONE;
import static com.digisoft.mma.util.AppConstant.PM_Step_TWO;
import static com.digisoft.mma.util.AppConstant.POST_BUCKET_NAME;
import static com.digisoft.mma.util.AppConstant.TIME_SERVER;
import static com.digisoft.mma.util.AppConstant.YES;

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
    private  Random rnd;

    private OutputStream os;
    /* Assign a string that contains the set of characters you allow. */
    private static final String symbols = "ABCDEFGJKLMNPRSTUVWXYZ0123456789";

    private char[] buf;

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
        btnJobDone.setFocusable(false);
       // btnJobDone.setOnClickListener(this);
        verify.setOnClickListener(this);

        rnd =  new SecureRandom();
        buf = new char[6];
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
                    date = new Date();
                    Timestamp timestamp = new Timestamp(date.getTime());
                    actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);

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
                        ((PMActivity)getActivity()).showProgressBar(true);
                        try {
                            updateEvent();
                        } catch (IOException e) {
                            Log.i("ERROR_IN_PM_STEP_2",e.getMessage());
                        }
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
                try {
                    updateEvent();
                } catch (IOException e) {
                    Log.e("ERROR_IN_PM_STEP2",e.getMessage());
                }
        }
    }

    private void updateEvent() throws IOException {
        if (network.isNetworkAvailable()) {


            /**  STEP_ONE EVENT UPDATE */
            ArrayList<PhotoModel> postPhotoModels = new ArrayList<>();
            List<UploadPhotoModel> uploadPhotoModels = dbHelper.uploadPhotoDAO().getPhotosToUploadByBucketName(POST_BUCKET_NAME);
            for (UploadPhotoModel photoModel: uploadPhotoModels) {
                postPhotoModels.add(new PhotoModel(getDecodedString(photoModel.getEncodedPhotoString()), 1));
            }

            ((PMActivity)getActivity()).showProgressBar(false);
            new LoadPOSTImage(
                    dbHelper.eventDAO().getEventsToUpload(PM_Step_ONE),
                    POST_BUCKET_NAME,
                    dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(PM_Step_ONE))
                    .execute(uploadPhoto(postPhotoModels));

            File file;
            for(PhotoFilePathModel e : getPhotoFilePaths()){
                file = new File(e.getFilePath());
                if(file.exists())
                    file.delete();
            }

        } else {
            showDialog("Network Connetion", "The network connection is lost. Please, check your connectivity and try again");
        }
    }

    /**
     *Upload one photo to server and get url id
     */
    private File[] uploadPhoto(ArrayList<PhotoModel> p) throws IOException {
        File[] files = new File[p.size()];
        date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        String actualDateTime = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss").format(timestamp);
        for(int i = 0 ; i < p.size(); i++) {
            File filesDir = getContext().getFilesDir();
            File fileName = new File(filesDir, mSharePreferenceHelper.getUserId()+actualDateTime+ nextString() + ".jpg");

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

    private void updateSTEP_Two() {
        //completeWork();
        /** STEP_TWO EVENT UPDATE*/
    //    UpdateEventBody updateEventBody = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(PM_Step_TWO);

    //    updateEventBody.setDate(actualDateTime);
     //   dbHelper.updateEventBodyDAO().insert(updateEventBody);
     //   ((PMActivity)getActivity()).hideProgressBar();
        //  deleteWorkingData();
      //  completeWork();
        new getCurrentNetworkTime().execute();

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
                dbHelper.updateEventBodyDAO().updateDateTime(actualDateTime, PM_Step_TWO);
                completeWork();
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
            ((PMActivity)getActivity()).hideProgressBar();
            if(!aBoolean) {
                Log.e("Check ", "dates not equal");
            }
        }

    }



    private void completeWork() {
        Intent intent = new Intent(this.getContext(), NFCReadingActivity.class);
        intent.putExtra("id", pmServiceInfoDetailModel.getId());

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

        tempEvent = new Event("remarks", "remarks", remarks.getText().toString() + "");
        tempEvent.setEvent_id("remarks");
        dbHelper.eventDAO().insert(tempEvent);

        intent.putExtra("JOB_DONE", 1);
        intent.putExtra("TAG_OUT", 1);

        startActivity(intent);
        //getActivity().finish();

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
                     //       Toast.makeText(getContext(),  response.body().getStatus()+ f.size() + " EVENTS :STEP_ONE UPLOADED" , Toast.LENGTH_SHORT).show();
                            mSharePreferenceHelper.userClickPMStepOne(true);
                            dbHelper.eventDAO().update(YES, PM_Step_ONE);
                            f.clear();
                            updateSTEP_Two();

                        } else {
                            ResponseBody errorReturnBody = response.errorBody();
                            try {
                                Log.e("UPLOAD_ERROR", "onResponse: " + errorReturnBody.string());
                        //        Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                    Log.e("ERROR",e.getMessage());
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
                            Log.i("PHOTOPATH",returnStatus.getData().getFileUrl());
                    //        Toast.makeText(getContext(), returnStatus.getStatus() + ":PHOTO"+count, Toast.LENGTH_SHORT).show();
                        } else {
                            Log.i("PhotoUpload", "doInBackground: " + response.message() + response.headers());
                    //        Toast.makeText(getContext(), "FAILED:" + response.code(), Toast.LENGTH_LONG).show();
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

    /**
     * Get Photo File Paths from DB
     */
    private List<PhotoFilePathModel> getPhotoFilePaths() {
        return dbHelper.photoFilePathDAO().getPhotoFilePaths();
    }

}