package com.freelance.solutionhub.mma.fragment;

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

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.adapter.MaintenanceAdapter;
import com.freelance.solutionhub.mma.model.MaintenanceInfoModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

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
    private MaintenanceAdapter mAdapter;
    private List<MaintenanceInfoModel> maintenanceList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        cvCorrectiveMaintenance.setOnClickListener(this);
        cvPreventiveMainennance.setOnClickListener(this);
        cvCorrective.setOnClickListener(this);
        cvPreventive.setOnClickListener(this);

        mAdapter = new MaintenanceAdapter(view.getContext(), maintenanceList);

        recyclerViewMaintenance.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(view.getContext());
        recyclerViewMaintenance.setLayoutManager(mLayoutManager);
        recyclerViewMaintenance.setAdapter(mAdapter);
        prepareMaintenaceList();
        return view;
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
        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvCorrective.setTextColor(getResources().getColor(R.color.colorBlack));

        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvPreventive.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        list = new String[]{"ALL", "New", "ACK", "INPRG"};

    }

    private void showPreventiveMaintenance() {
        ivPreventive.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.colorPrimary));
        tvPreventive.setTextColor(getResources().getColor(R.color.colorBlack));

        ivCorrective.setColorFilter(ContextCompat.getColor(this.getContext(), R.color.color_grey_stroke_dark));
        tvCorrective.setTextColor(getResources().getColor(R.color.color_grey_stroke_dark));
        list = new String[]{"ALL", "New", "INPRG"};
    }


    private void prepareMaintenaceList() {
        MaintenanceInfoModel obj = new MaintenanceInfoModel("xxxx", "APPR", "Faculty", "Rochr Do Ladier Go", "12/4/2020 7:03PM");
        maintenanceList.add(obj);
        maintenanceList.add(obj); maintenanceList.add(obj);
        maintenanceList.add(obj); maintenanceList.add(obj); maintenanceList.add(obj);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}