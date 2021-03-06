package com.digisoft.mma.activity;

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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.fragment.First_Step_PM_Fragment;
import com.digisoft.mma.fragment.Second_Step_PM_Fragment;
import com.digisoft.mma.model.PMServiceInfoDetailModel;
import com.digisoft.mma.util.SharePreferenceHelper;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class PMActivity extends AppCompatActivity {

    @BindView(R.id.progress_bar)
    RelativeLayout progressBar;

    @BindView(R.id.firstText)
    TextView firstText;

    @BindView(R.id.secondText)
    TextView secondText;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private First_Step_PM_Fragment first_step_pm_fragment;
    private Second_Step_PM_Fragment second_step_pm_fragment;
    private PMServiceInfoDetailModel pmServiceInfoDetailModel;

    private Runnable r;
    private Handler handler;
    private boolean startHandler = true;
    private SharePreferenceHelper sharePreferenceHelper;
    private InitializeDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_p_m);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        dbHelper = InitializeDatabase.getInstance(this);
        sharePreferenceHelper.setLock(false);
        sharePreferenceHelper.userClickPMStepOne(false);

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
        bundle.putString("start_time", getIntent().getStringExtra("start_time"));
        first_step_pm_fragment.setArguments(bundle);
        second_step_pm_fragment.setArguments(bundle);

        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(PMActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(PMActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };

    }


    public void showProgressBar(Boolean isVerify) {
        if (isVerify) {
            firstText.setText("Verifying...");
            secondText.setVisibility(View.GONE);
        } else {
            firstText.setText("Saving update data.");
            secondText.setVisibility(View.VISIBLE);
        }
        progressBar.setVisibility(View.VISIBLE);
    }
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive();
        if (isScreenOn)
            stopHandler();
        sharePreferenceHelper.setLock(true);
    }

    @Override
    public void onUserInteraction() {
        // TODO Auto-generated method stub
        super.onUserInteraction();
      //  Toast.makeText(this, "UserInteraction", Toast.LENGTH_SHORT).show();
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
        if (sharePreferenceHelper.getLock()) {
            Intent intent = new Intent(PMActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
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
                Intent intent = new Intent(PMActivity.this, NFCReadingActivity.class);
                intent.putExtra("id", pmServiceInfoDetailModel.getId());
                intent.putExtra("TAG_OUT", 1);
                startActivity(intent);
                return true;
            case android.R.id.home:
                sharePreferenceHelper.setLock(false);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        sharePreferenceHelper.setLock(false);
    }
}