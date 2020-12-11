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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.fragment.First_Step_CM_Fragment;
import com.freelance.solutionhub.mma.fragment.Second_Step_CM_Fragment;
import com.freelance.solutionhub.mma.fragment.Third_Step_CM_Fragment;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.util.AppConstant;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CMActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private First_Step_CM_Fragment first_step_cm_fragment;
    private Second_Step_CM_Fragment second_step_cm_fragment;
    private Third_Step_CM_Fragment third_step_cm_fragment;

    private PMServiceInfoDetailModel pmServiceInfoDetailModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getIntent().getSerializableExtra("object"));
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
        adapter.addFrag(third_step_cm_fragment, "STEP 3");
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
                Intent intent = new Intent(CMActivity.this, NFCReadingActivity.class);
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

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                second_step_cm_fragment.updateQRCode(result.getContents());
                Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();

            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}