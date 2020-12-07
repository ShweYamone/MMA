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
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

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

    private String[] list;
    private ArrayAdapter spinnerArrAdaper;
    private ServiceOrderAdapter mAdapter;
    private List<PMServiceInfoModel> serviceInfoModelList = new ArrayList<>();
    private ApiInterface apiInterface;
    private SmartScrollListener mSmartScrollListener;
    private int page = 1;
    private int totalPages = 0;
    private String maintenace;
    private String CM = "CM"; private String PM = "PM";

    private SharePreferenceHelper mSharePreference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        mSharePreference = new SharePreferenceHelper(getContext());

        ButterKnife.bind(this, view);
        cvCorrectiveMaintenance.setOnClickListener(this);
        cvPreventiveMainennance.setOnClickListener(this);
        cvCorrective.setOnClickListener(this);
        cvPreventive.setOnClickListener(this);

        apiInterface = ApiClient.getClient(this.getContext());

        mSmartScrollListener = new SmartScrollListener(new SmartScrollListener.OnSmartScrollListener() {
            @Override
            public void onListEndReach() {
                page++;
               // if (page <= totalPages)
                    //getServiceOrders("ne", "JOBDONE");
            }
        });

        mAdapter = new ServiceOrderAdapter(this.getContext().getApplicationContext(), serviceInfoModelList);

        recyclerViewMaintenance.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewMaintenance.setLayoutManager(mLayoutManager);
        recyclerViewMaintenance.setAdapter(mAdapter);
        recyclerViewMaintenance.addOnScrollListener(mSmartScrollListener);
        recyclerViewMaintenance.setAdapter(mAdapter);

        return view;
    }

    private FilterModel createFilterModel(String filterExpression, String textValue) {
        List<FilterParam> filterParamList = new ArrayList<>();
        filterParamList.add(new FilterParam("equals", "enum", "serviceOrderType", 0, new EnumValue("PREVENTATIVE")));
        filterParamList.add(new FilterParam(filterExpression, "text", "serviceOrderStatus", 0, new TextValue(textValue)));

        Filter filter = new Filter("FILTER_LOGIC_AND", filterParamList);
        PaginationParam paginationParam = new PaginationParam(1);
        List<SortingParam> sortingParamList = new ArrayList<>();
        sortingParamList.add(new SortingParam("creationDate", 0, "desc"));
        FilterModel filterModel = new FilterModel(filter, paginationParam, sortingParamList );

        return filterModel;
    }

    private void getServiceOrders(String filterExpression, String textValue) {
        FilterModel filterModel = createFilterModel(filterExpression, textValue);

        Call<PMServiceListModel> call = apiInterface.getPMServiceOrders("Bearer " + mSharePreference.getToken(), filterModel);

        call.enqueue(new Callback<PMServiceListModel>() {

            @Override
            public void onResponse(Call<PMServiceListModel> call, Response<PMServiceListModel> response) {

                if (response.isSuccessful()) {
                    PMServiceListModel pmList = response.body();
                    totalPages = pmList.getTotalPages();
                    Toast.makeText(getContext(), "totalPages : " + totalPages + ", " + pmList.getItems().size(),Toast.LENGTH_SHORT).show();
                    serviceInfoModelList.addAll(pmList.getItems());
                    mAdapter.notifyDataSetChanged();
                }
                else {
                    Toast.makeText(getContext(), response.code()+"f", Toast.LENGTH_SHORT).show();
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

        spinnerArrAdaper = new ArrayAdapter(this.getContext(), android.R.layout.simple_spinner_item, list);
        spinnerArrAdaper.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(spinnerArrAdaper);
        spinnerStatus.setOnItemSelectedListener(this);

    }

    private void showCorrectiveMaintenance() {
        maintenace = "CM";
        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvCorrective.setTextColor(getResources().getColor(R.color.colorBlack));

        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvPreventive.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        list = new String[]{"ALL", "New", "ACK", "INPRG"};


    }

    private void showPreventiveMaintenance() {
        maintenace = "PM";
        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvPreventive.setTextColor(getResources().getColor(R.color.colorBlack));

        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvCorrective.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        list = new String[]{"ALL", "New", "INPRG"};
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        serviceInfoModelList.clear();
        mAdapter.notifyDataSetChanged();
        switch (list[i]) {
            case "ALL" : getServiceOrders("ne", "JOBDONE");break;
            case "New" : getServiceOrders("equalsIgnoreCase", "APPR");break;
            case "INPRG" : getServiceOrders("equalsIgnoreCase", "INPRG");break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
