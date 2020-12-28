package com.freelance.solutionhub.mma.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.fragment.First_Step_CM_Fragment;
import com.freelance.solutionhub.mma.fragment.Second_Step_CM_Fragment;
import com.freelance.solutionhub.mma.fragment.Third_Step_CM_Fragment;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.model.ThirdPartyModel;
import com.freelance.solutionhub.mma.util.AppConstant;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.google.android.material.tabs.TabLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.freelance.solutionhub.mma.util.AppConstant.user_inactivity_time;

public class CMActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    RelativeLayout progressBar;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private First_Step_CM_Fragment first_step_cm_fragment;
    private Second_Step_CM_Fragment second_step_cm_fragment;
    private Third_Step_CM_Fragment third_step_cm_fragment;

    private PMServiceInfoDetailModel pmServiceInfoDetailModel;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private boolean lockScreen = false;
    private SharePreferenceHelper mSharedPreference;
    private InitializeDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pmServiceInfoDetailModel = (PMServiceInfoDetailModel)(getIntent().getSerializableExtra("object"));

        setContentView(R.layout.activity_c_m);
        ButterKnife.bind(this);
        dbHelper = InitializeDatabase.getInstance(this);
        mSharedPreference = new SharePreferenceHelper(this);
        mSharedPreference.setLock(false);
        mSharedPreference.userClickCMStepOne(false);
        mSharedPreference.userClickCMStepTwo(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        first_step_cm_fragment = new First_Step_CM_Fragment();
        second_step_cm_fragment = new Second_Step_CM_Fragment();
        third_step_cm_fragment = new Third_Step_CM_Fragment();

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(2);
        addTabs(viewPager);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //////pass id(data) from activity to fragment
        Bundle bundle = new Bundle();
        bundle.putSerializable("object", pmServiceInfoDetailModel);
        bundle.putString("start_time", getIntent().getStringExtra("start_time"));
        if (getIntent().hasExtra("telco"))
            bundle.putSerializable("telco" , getIntent().getSerializableExtra("telco"));
        else
            bundle.putSerializable("telco", new ThirdPartyModel());

        if (getIntent().hasExtra("powerGrid"))
            bundle.putSerializable("powerGrid" , getIntent().getSerializableExtra("powerGrid"));
        else
            bundle.putSerializable("powerGrid", new ThirdPartyModel());

        if (getIntent().hasExtra("other"))
            bundle.putSerializable("other" , getIntent().getSerializableExtra("other"));
        else
            bundle.putSerializable("other", new ThirdPartyModel());


        first_step_cm_fragment.setArguments(bundle);
        second_step_cm_fragment.setArguments(bundle);
        third_step_cm_fragment.setArguments(bundle);

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(CMActivity.this, "user is inactive from last 5 minute",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(CMActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };
    }

    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Tracing......", "Stop: ");
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        mSharedPreference.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
        //   Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
        stopHandler();//stop first and then start
        if (startHandler)
            startHandler();
    }
    public void stopHandler() {
        handler.removeCallbacks(r);
    }
    public void startHandler() {
        handler.postDelayed(r, user_inactivity_time); //for 3 minutes
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Tracing......", "onResume: " + mSharedPreference.getLock());
        if (mSharedPreference.getLock()) {
            Intent intent = new Intent(CMActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreference.setLock(false);
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
                intent.putExtra("TAG_OUT", 1);
                startActivity(intent);
                finish();
                break;
            case android.R.id.home:
                mSharedPreference.setLock(false);
                finish();
                return true;
        }


        return super.onOptionsItemSelected(item);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mSharedPreference.setLock(false);
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