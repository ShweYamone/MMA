package com.freelance.solutionhub.mma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.fragment.First_Step_PM_Fragment;
import com.freelance.solutionhub.mma.fragment.Second_Step_PM_Fragment;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class PMActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private First_Step_PM_Fragment first_step_pm_fragment;
    private Second_Step_PM_Fragment second_step_pm_fragment;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_m);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        first_step_pm_fragment = new First_Step_PM_Fragment();
        second_step_pm_fragment = new Second_Step_PM_Fragment();

        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getIntent().getSerializableExtra("object"));

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        viewPager = findViewById(R.id.viewpager);
        addTabs(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        ///pass id(data) from activity to fragments
        Bundle bundle = new Bundle();
        bundle.putSerializable("object",  pmServiceInfoDetailModel);
        first_step_pm_fragment.setArguments(bundle);
        second_step_pm_fragment.setArguments(bundle);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.pm_cm_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_tagout:
                Intent intent = new Intent(PMActivity.this, NFCReadingActivity.class);
                intent.putExtra("id", pmServiceInfoDetailModel.getId());
                intent.putExtra("TAG_OUT", true);
                startActivity(intent);
                finish();
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    // Add steps' fragment to view pager
    private void addTabs(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(first_step_pm_fragment, "STEP 1");
        adapter.addFrag(second_step_pm_fragment, "STEP 2");
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