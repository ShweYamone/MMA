package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.fragment.First_Step_CM_Fragment;
import com.freelance.solutionhub.mma.fragment.Second_Step_CM_Fragment;
import com.freelance.solutionhub.mma.fragment.Third_Step_CM_Fragment;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class CMActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private First_Step_CM_Fragment first_step_cm_fragment;
    private Second_Step_CM_Fragment second_step_cm_fragment;
    private Third_Step_CM_Fragment third_step_cm_fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PMServiceInfoDetailModel pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getIntent().getSerializableExtra("object"));
        setContentView(R.layout.activity_c_m);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        first_step_cm_fragment = new First_Step_CM_Fragment();
        second_step_cm_fragment = new Second_Step_CM_Fragment();
        third_step_cm_fragment = new Third_Step_CM_Fragment();

        viewPager = findViewById(R.id.viewpager);
        addTabs(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //////pass id(data) from activity to fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", pmServiceInfoDetailModel);

        first_step_cm_fragment.setArguments(bundle);
        second_step_cm_fragment.setArguments(bundle);
        third_step_cm_fragment.setArguments(bundle);
    }

    // Add steps' fragment to view pager
    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(first_step_cm_fragment, "STEP 1");
        adapter.addFrag(second_step_cm_fragment, "STEP 2");
        adapter.addFrag(new Third_Step_CM_Fragment(), "STEP 3");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}