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
import com.freelance.solutionhub.mma.adapter.CheckListAdapter;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.delegate.FirstStepPMFragmentCallback;
import com.freelance.solutionhub.mma.model.CheckListModel;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


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
    private String bucketName;
    ArrayList<Event> events = new ArrayList<>();

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
        events = new ArrayList<>();
        postPhotoModels = new ArrayList<>();
        //Check List
        checkListModels = new ArrayList<>();
        checkListAdapter = new CheckListAdapter(getContext(), checkListModels);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
      //  checkList.setHasFixedSize(true);
        checkList.setLayoutManager(mLayoutManager);
        checkList.setAdapter(checkListAdapter);

        Call<List<CheckListModel>> callCheckList = apiInterface.getCheckList("Bearer "+mSharePerferenceHelper.getToken(),pmServiceInfoDetailModel.getId());
        callCheckList.enqueue(new Callback<List<CheckListModel>>() {
            @Override
            public void onResponse(Call<List<CheckListModel>> call, Response<List<CheckListModel>> response) {
                List<CheckListModel> checkListModel = response.body();
                if(response.isSuccessful()){
                    checkListModels.addAll(checkListModel);
                    Log.i("CHECK_LIST",checkListModels.size()+"Size");
                    checkListAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<CheckListModel>> call, Throwable t) {

            }
        });

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
                addEvents();
                if(postPhotoModels.size() != 0)
                    save();
                else
                    uploadEvent();
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

    /**
     * Update event for all
     */
    public void save(){
        if( mNetwork.isNetworkAvailable() ) {
            if (postPhotoModels.size() > 1 && postPhotoModels.size() < 11) {
                //Upload photos to server
                new LoadImage(events).execute(uploadPhoto(postPhotoModels));

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
    private void addEvents(){
        if (!etFaultFoundRemarks.getText().toString().equals("")) {
            events.add(new Event(
                    "PM_FAULT_FOUND_UPDATE",
                    "PM_FAULT_FOUND_UPDATE",
                    etFaultFoundRemarks.getText().toString()
            ));
        }

       // events.addAll(checkListAdapter.getCheckListEvent());
        for(Event e:events){
            Log.i("VALUE",e.getValue()+",KEY:"+e.getKey()+",evenType:"+e.getEventType());
        }
        Log.i("DONE","Done");
    }
    private void uploadEvent(){
        if(network.isNetworkAvailable()){
            if(events.size() != 0)
                new LoadImage(events).execute(new File[0]);
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
     *Upload one photo to server and get url id
     * @param bitmap
     * @param name
     */

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

    /**
     * reduces the size of the image
     * @param image
     * @param maxSize
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }


    private Bitmap getBitmapFromEncodedString(String encodedString){

        byte[] arr = Base64.decode(encodedString, Base64.URL_SAFE);

        Bitmap img = BitmapFactory.decodeByteArray(arr, 0, arr.length);
        return img;


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

        private void updateEvents() {
            Log.i("EVENT_LIST",f.size()+"");
            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp);

            if (f.size() > 0) {
                UpdateEventBody eventBody = new UpdateEventBody(
                        mSharePerferenceHelper.getUserName(),
                        mSharePerferenceHelper.getUserId(),
                        actualDateTime,
                        pmServiceInfoDetailModel.getId(),
                        f
                );

                Call<ReturnStatus> call = apiInterface.updateEvent("Bearer " + mSharePerferenceHelper.getToken(),
                        eventBody);
                call.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(),  response.body().getStatus()+":ALL EVENTS UPLOADED" , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ReturnStatus> call, Throwable t) {

                    }
                });
                f.clear();
            }

        }


        @Override
        protected Boolean doInBackground(File... files) {
            for (File fileName : files) {
                MultipartBody.Builder builder = new MultipartBody.Builder();
                builder.setType(MultipartBody.FORM);
                builder.addFormDataPart("file", fileName.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), fileName));
                MultipartBody requestBody = builder.build();


                Call<ReturnStatus> returnStatusCall = apiInterface.uploadPhoto("pids-post-maintenance-photo", requestBody);
                returnStatusCall.enqueue(new Callback<ReturnStatus>() {
                    @Override
                    public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                        ReturnStatus returnStatus = response.body();
                        if (response.isSuccessful()) {
                            Log.v("PRE_EVENT", "Added post event");
                            f.add(new Event("POST_MAINTENANCE_PHOTO_UPDATE", "postMaintenancePhotoUpdate", returnStatus.getData().getFileUrl()));
                            count++;
                            if(files.length == count)
                                updateEvents();
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
                if(count == files.length)
                    updateEvents();
                return true;
            }
            return false;
        }

    }
        /**
         * //To Do save to database photo
         */
        private void saveEncodePhotoToDatabase (String bucketName, String photo){
            byte[] bytes = photo.getBytes();
            /**
             * encodeToString is encoded string
             */

            date = new Date();
            Timestamp timestamp = new Timestamp(date.getTime());
            String actualDateTime = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(timestamp);
            String encodeToString = Base64.encodeToString(bytes, Base64.DEFAULT);
            dbHelper.uploadPhotoDAO().insert(new UploadPhotoModel(bucketName, encodeToString,actualDateTime));
            Log.v("ENCODE", encodeToString);


    }
}