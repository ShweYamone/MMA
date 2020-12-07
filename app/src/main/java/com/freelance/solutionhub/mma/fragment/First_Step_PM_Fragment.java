package com.freelance.solutionhub.mma.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;

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

    private ApiInterface apiInterface;

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

        ivArrowDropDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generalDesign.setVisibility(View.GONE);
                wholeCardDesign.setVisibility(View.VISIBLE);
            }
        });

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
                 //   tvLocation.setText(infoModel.getBusStopLocation().toString);
//                    tvBustStopNumber.setText(infoModel.getBusStopId().toString());
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
}