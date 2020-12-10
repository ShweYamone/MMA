package com.freelance.solutionhub.mma.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.LoadingActivity;
import com.freelance.solutionhub.mma.activity.LoginActivity;
import com.freelance.solutionhub.mma.activity.MainActivity;
import com.freelance.solutionhub.mma.adapter.ServiceOrderAdapter;
import com.freelance.solutionhub.mma.common.SmartScrollListener;
import com.freelance.solutionhub.mma.delegate.HomeFragmentCallback;
import com.freelance.solutionhub.mma.model.Data;
import com.freelance.solutionhub.mma.model.FilterModelBody;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;
import com.freelance.solutionhub.mma.model.ReturnStatus;
import com.freelance.solutionhub.mma.model.UpdateEventBody;
import com.freelance.solutionhub.mma.model.UserProfile;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.freelance.solutionhub.mma.util.TokenManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.ACK;
import static com.freelance.solutionhub.mma.util.AppConstant.ALL;
import static com.freelance.solutionhub.mma.util.AppConstant.APPR;
import static com.freelance.solutionhub.mma.util.AppConstant.CM;
import static com.freelance.solutionhub.mma.util.AppConstant.INPRG;
import static com.freelance.solutionhub.mma.util.AppConstant.JOBDONE;
import static com.freelance.solutionhub.mma.util.AppConstant.PM;
import static com.freelance.solutionhub.mma.util.AppConstant.WSCH;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener, HomeFragmentCallback {

    @BindView(R.id.cvCorrectiveMaintenance)
    CardView cvCorrectiveMaintenance;

    @BindView(R.id.cvPreventiveMaintenance)
    CardView cvPreventiveMainennance;

    @BindView(R.id.layoutMaintenaceCV)
    LinearLayout layoutMaintenaceCV;

    @BindView(R.id.layoutMaintenaceList)
    LinearLayout layoutMaintenanceList;

    @BindView(R.id.recyclerviewMaintenaceList)
    RecyclerView recyclerViewMaintenance;

    @BindView(R.id.cvPreventive)
    CardView cvPreventive;

    @BindView(R.id.cvCorrective)
    CardView cvCorrective;

    @BindView(R.id.ivCorrective)
    ImageView ivCorrective;

    @BindView(R.id.ivPreventive)
    ImageView ivPreventive;

    @BindView(R.id.tvCorrective)
    TextView tvCorrective;

    @BindView(R.id.tvPreventive)
    TextView tvPreventive;

    @BindView(R.id.spinnerStatus)
    Spinner spinnerStatus;

    @BindView(R.id.tvPMCount)
    TextView tvPMCount;

    @BindView(R.id.tvCMCount)
    TextView tvCMCount;

    private String[] list;
    private ArrayAdapter spinnerArrAdaper;
    private ServiceOrderAdapter mAdapter;
    private List<PMServiceInfoModel> serviceInfoModelList = new ArrayList<>();
    private ApiInterface apiInterface;
    private SmartScrollListener mSmartScrollListener;
    private SharePreferenceHelper mSharePreference;
    private FilterModelBody filterModelBody;

    private int page = 1;
    private int totalPages = 0;
    private String enumValue;
    private String textValue;
    private String filterExpression;
    PMServiceListModel pmServiceListModel;
    List<PMServiceInfoModel> pmServiceInfoModels;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mSharePreference = new SharePreferenceHelper(getContext());
        apiInterface = ApiClient.getClient(this.getContext());

        ButterKnife.bind(this, view);
        cvCorrectiveMaintenance.setOnClickListener(this);
        cvPreventiveMainennance.setOnClickListener(this);
        cvCorrective.setOnClickListener(this);
        cvPreventive.setOnClickListener(this);

        mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
            @Override
            public void onListEndReach() {
                page++;
                if (page <= totalPages)
                    getServiceOrders();
            }
        });

        mAdapter = new ServiceOrderAdapter(this.getContext().getApplicationContext(), serviceInfoModelList, this);

        recyclerViewMaintenance.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewMaintenance.setLayoutManager(mLayoutManager);
        recyclerViewMaintenance.setAdapter(mAdapter);
        recyclerViewMaintenance.addOnScrollListener(mSmartScrollListener);
        recyclerViewMaintenance.setAdapter(mAdapter);

        if (mSharePreference.getUserId().equals("")) {
            getUserIdAndUserDisplayName();
        }

        setServiceOrderCount();
        return view;
    }

    private void getUserIdAndUserDisplayName() {
        Call<UserProfile> call1 = apiInterface.getUserProfile("Bearer " + mSharePreference.getToken());
        call1.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {

                if (response.isSuccessful()) {
                    Data userData = response.body().getData();
                    mSharePreference.setUserIdAndDisplayName(userData.getUserId(), userData.getDisplayName());
                  //  Toast.makeText(getContext().getApplicationContext(), mSharePreference.getUserId() + ", " + mSharePreference.getDisplayName(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), response.code() + ", ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(getContext().getApplicationContext(), "failure", Toast.LENGTH_SHORT).show();

            }
        });

    }


    @Override
    public boolean hasUserId() {
        return !(mSharePreference.getUserId().equals(""));
    }


    public  void update_ARRP_To_ACK(String date, String serviceOrderId, int position) {
        UpdateEventBody updateEventBody = new UpdateEventBody(mSharePreference.getUserName(), mSharePreference.getUserId(),
                date,
                serviceOrderId,ACK
        );

       // String
        update_APPR_To_ACK_UI(position);
        Call<ReturnStatus> call = apiInterface.updateStatusEvent("Bearer " + mSharePreference.getToken(), updateEventBody);
        call.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                if (response.isSuccessful()) {
                    ReturnStatus returnStatus = response.body();
                    Toast.makeText(getContext().getApplicationContext(), returnStatus.getStatus()+"APPRtoACK", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext().getApplicationContext(), response.code()+"APPRtoACK", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {

            }
        });
    }

    public void update_APPR_To_ACK_UI(int position){
        pmServiceInfoModels.get(position).setServiceOrderStatus(ACK);
        mAdapter.notifyItemChanged(position);
    }

    private void setServiceOrderCount() {
        filterModelBody = FilterModelBody.createFilterModel("ne", CM, JOBDONE, 1);
        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModelBody);
        call.enqueue(new Callback<PMServiceListModel>() {
            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    pmServiceListModel = response.body();
                    tvCMCount.setText(pmServiceListModel.getTotalElements()+"");
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
            }
        });

        filterModelBody = FilterModelBody.createFilterModel("ne", PM, JOBDONE, 1);
        call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModelBody);
        call.enqueue(new Callback<PMServiceListModel>() {
            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    pmServiceListModel = response.body();
                    tvPMCount.setText(pmServiceListModel.getTotalElements()+"");
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
            }
        });
    }

    private void getServiceOrders() {
        filterModelBody = FilterModelBody.createFilterModel(filterExpression, enumValue, textValue, page);

        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModelBody);

        call.enqueue(new Callback<PMServiceListModel>() {

            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    PMServiceListModel pmList = response.body();
                    totalPages = pmList.getTotalPages();
                 //   Toast.makeText(getContext(), "totalPages : " + totalPages + ", Current Page:" + page + "->" + pmList.getItems().size(),Toast.LENGTH_SHORT).show();
                    pmServiceInfoModels = pmList.getItems();
                    serviceInfoModelList.addAll(pmServiceInfoModels);
                    mAdapter.notifyDataSetChanged();
                }
                else if (response.code()==401){
                    Intent intent = new Intent(getContext(), LoadingActivity.class);
                 //   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    mSharePreference.logoutSharePreference();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
                Toast.makeText(getContext(), "connection failure", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClick(View view) {
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        page = 1;
        switch (view.getId()) {
            case R.id.cvCorrectiveMaintenance:
                layoutMaintenaceCV.setVisibility(View.GONE);
                layoutMaintenanceList.setVisibility(View.VISIBLE);
                showCorrectiveMaintenance();
                break;
            case R.id.cvPreventiveMaintenance:
                layoutMaintenaceCV.setVisibility(View.GONE);
                layoutMaintenanceList.setVisibility(View.VISIBLE);
                showPreventiveMaintenance();
                break;
            case R.id.cvCorrective:
                showCorrectiveMaintenance();
                break;
            case R.id.cvPreventive:
                showPreventiveMaintenance();
                break;
        }

        filterExpression = "ne"; textValue = JOBDONE;
        spinnerArrAdaper = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, list);
        spinnerArrAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerArrAdaper);
        spinnerStatus.setOnItemSelectedListener(this);

    }

    private void showCorrectiveMaintenance() {
        enumValue = CM;
        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvCorrective.setTextColor(getResources().getColor(R.color.colorBlack));

        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvPreventive.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        list = new String[]{ALL, APPR, ACK, INPRG};


    }

    private void showPreventiveMaintenance() {
        enumValue = PM;
        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvPreventive.setTextColor(getResources().getColor(R.color.colorBlack));

        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvCorrective.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        list = new String[]{ALL, WSCH, INPRG};
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        page = 1;
        switch (list[i]) {
            case ALL :
                filterExpression = "ne"; textValue = JOBDONE;
                break;
            case WSCH :
                filterExpression = "equalsIgnoreCase"; textValue = WSCH;
                break;
            case INPRG :
                filterExpression = "equalsIgnoreCase"; textValue = INPRG;
                break;
            case ACK :
                filterExpression = "equalsIgnoreCase"; textValue = ACK;
                break;
            case APPR:
                filterExpression = "equalsIgnoreCase"; textValue = APPR;
        }

        getServiceOrders();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onResume() {
        super.onResume();
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        page = 1;
        getServiceOrders();
    }
}
