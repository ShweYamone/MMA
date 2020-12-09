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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.PhotoAdapter;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;
import com.freelance.solutionhub.mma.model.PhotoModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.token;
import static com.freelance.solutionhub.mma.util.AppConstant.pmID;

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

    @BindView(R.id.iv_attach_pre_maintenance_photo)
    ImageView preMaintenancePhoto;

    @BindView(R.id.recyclerview_pre_photo)
    RecyclerView prePhoto;

    private ApiInterface apiInterface;

    private static final int CAMERA_REQUEST = 1888;
    TextView text,text1;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    //Bitmap photo;
    String photo;
    ArrayList<PhotoModel> photoModels;
    Bitmap theImage;
    PhotoAdapter photoAdapter;

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
        photoModels = new ArrayList<>();

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
        setData();

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

//
//    private void setDataToDataBase() {
//        db = databaseHandler.getWritableDatabase();
//        ContentValues cv = new ContentValues();
//        cv.put(databaseHandler.KEY_IMG_URL,getEncodedString(theImage));
//
//        long id = db.insert(databaseHandler.TABLE_NAME, null, cv);
//        if (id < 0) {
//            Toast.makeText(getContext(), "Something went wrong. Please try again later...", Toast.LENGTH_LONG).show();
//        } else {
//            Toast.makeText(getContext(), "Add successful", Toast.LENGTH_LONG).show();
//        }
//    }

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
            theImage = (Bitmap) data.getExtras().get("data");
            photo=getEncodedString(theImage);
            photoModels.add(new PhotoModel(photo,1));
            photoAdapter.notifyDataSetChanged();
           // setDataToDataBase();
        }
    }

    private void setData() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),2  );
        prePhoto.setLayoutManager(layoutManager);
        photoAdapter = new PhotoAdapter(getContext(), photoModels);
        prePhoto.setAdapter(photoAdapter);


    }

    private String getEncodedString(Bitmap bitmap){

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG,100, os);

       /* or use below if you want 32 bit images

        bitmap.compress(Bitmap.CompressFormat.PNG, (0–100 compression), os);*/
        byte[] imageArr = os.toByteArray();

        return Base64.encodeToString(imageArr, Base64.URL_SAFE);

    }
}