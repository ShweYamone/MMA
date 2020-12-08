package com.freelance.solutionhub.mma.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.freelance.solutionhub.mma.adapter.ServiceOrderAdapter;
import com.freelance.solutionhub.mma.common.SmartScrollListener;
import com.freelance.solutionhub.mma.model.EnumValue;
import com.freelance.solutionhub.mma.model.Filter;
import com.freelance.solutionhub.mma.model.FilterModel;
import com.freelance.solutionhub.mma.model.FilterParam;
import com.freelance.solutionhub.mma.model.PMServiceInfoModel;
import com.freelance.solutionhub.mma.model.PMServiceListModel;
import com.freelance.solutionhub.mma.model.PaginationParam;
import com.freelance.solutionhub.mma.model.SortingParam;
import com.freelance.solutionhub.mma.model.TextValue;
import com.freelance.solutionhub.mma.model.UserModel;
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

import static com.freelance.solutionhub.mma.util.AppConstant.token;

public class HomeFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

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
    private TokenManager tokenManager;
    private SharePreferenceHelper mSharePreference;
    private FilterModel filterModel;

    private int page = 1;
    private int totalPages = 0;
    private String enumValue;
    private String textValue;
    private String filterExpression;

    private static final String PM = "PREVENTATIVE"; private static final String CM = "CORRECTIVE";
    private static final String ALL = "ALL";
    private static final String WSCH = "WSCH";
    private static final String INPRG = "INPRG";
    private static final String ACK = "ACK";
    private static final String APPR = "APPR";
    private static final String JOBDONE = "JOBDONE";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mSharePreference = new SharePreferenceHelper(getContext());
        apiInterface = ApiClient.getClient(this.getContext());
        tokenManager = new TokenManager(this.getContext(), apiInterface, mSharePreference);

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

        mAdapter = new ServiceOrderAdapter(this.getContext().getApplicationContext(), serviceInfoModelList);

        recyclerViewMaintenance.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewMaintenance.setLayoutManager(mLayoutManager);
        recyclerViewMaintenance.setAdapter(mAdapter);
        recyclerViewMaintenance.addOnScrollListener(mSmartScrollListener);
        recyclerViewMaintenance.setAdapter(mAdapter);
        setServiceOrderCount();
        return view;
    }

    private void setServiceOrderCount() {
        filterModel = FilterModel.createFilterModel("ne", CM, JOBDONE, 1);
        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModel);
        call.enqueue(new Callback<PMServiceListModel>() {
            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    PMServiceListModel pmServiceListModel = response.body();
                    tvCMCount.setText(pmServiceListModel.getTotalElements()+"");
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
            }
        });

        filterModel = FilterModel.createFilterModel("ne", PM, JOBDONE, 1);
        call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModel);
        call.enqueue(new Callback<PMServiceListModel>() {
            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    PMServiceListModel pmServiceListModel = response.body();
                    tvPMCount.setText(pmServiceListModel.getTotalElements()+"");
                }
            }

            @Override
            public void onFailure(Call<PMServiceListModel> call, Throwable t) {
            }
        });
    }

    private void getServiceOrders() {
        filterModel = FilterModel.createFilterModel(filterExpression, enumValue, textValue, page);

        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModel);

        call.enqueue(new Callback<PMServiceListModel>() {

            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    PMServiceListModel pmList = response.body();
                    totalPages = pmList.getTotalPages();
                    Toast.makeText(getContext(), "totalPages : " + totalPages + ", Current Page:" + page + "->" + pmList.getItems().size(),Toast.LENGTH_SHORT).show();
                    serviceInfoModelList.addAll(pmList.getItems());
                    mAdapter.notifyDataSetChanged();
                }
                else if (response.code()==401){
                    Toast.makeText(getContext(), "Set Token Again", Toast.LENGTH_SHORT).show();
                    tokenManager.setTokenAgain();
                    getServiceOrders();
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
}
