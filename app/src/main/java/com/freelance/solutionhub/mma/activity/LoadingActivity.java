package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.transition.CircularPropagation;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.model.PMServiceInfoDetailModel;
import com.freelance.solutionhub.mma.util.ApiClient;
import com.freelance.solutionhub.mma.util.ApiInterface;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.freelance.solutionhub.mma.util.AppConstant.token;

public class LoadingActivity extends AppCompatActivity {

    @BindView(R.id.circularProgressBar)
    CircularProgressBar progressBar;

    @BindView(R.id.tvProgressPercent)
    TextView tvPercent;

    private ApiInterface apiInterface;
    private SharePreferenceHelper mSharePrefrence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = ApiClient.getClient(this);
        mSharePrefrence = new SharePreferenceHelper(this);
        setContentView(R.layout.activity_loading);
        ButterKnife.bind(this);

        getServieOrderbyId(getIntent().getStringExtra("id"));

    }

    private void getServieOrderbyId(String id) {
        Call<PMServiceInfoDetailModel> call = apiInterface.getPMServiceOrderByID("Bearer " + mSharePrefrence.getToken() , id);
        call.enqueue(new Callback<PMServiceInfoDetailModel>() {
            @Override
            public void onResponse(Call<PMServiceInfoDetailModel> call, Response<PMServiceInfoDetailModel> response) {

                if (response.isSuccessful()) {
                    Intent intent = new Intent(LoadingActivity.this, CMActivity.class);
                    intent.putExtra("object", response.body());
                    startActivity(intent);
                    finish();
                } else {
                    Log.i("SERVICEORDER", response.code()+"");
                }
            }
            @Override
            public void onFailure(Call<PMServiceInfoDetailModel> call, Throwable t) {
                Log.i("SERVICEORDER", id+"failure");
            }
        });
    }
}
