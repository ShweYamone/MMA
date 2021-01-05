package com.digisoft.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.util.SharePreferenceHelper;

public class SimChanged extends AppCompatActivity {

    InitializeDatabase dbHelper;
    SharePreferenceHelper sharePreferenceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_changed);
        dbHelper = InitializeDatabase.getInstance(this);
        sharePreferenceHelper = new SharePreferenceHelper(this);
        sharePreferenceHelper.logoutSharePreference();
        sharePreferenceHelper.deletePinCode();

        dbHelper.checkListDescDAO().deleteAll();
        dbHelper.eventDAO().deleteAll();
        dbHelper.updateEventBodyDAO().deleteAll();
        dbHelper.uploadPhotoDAO().deleteAll();



        // TODO We must to ask deleting database after OK button click or Not
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("SIM Card Changed!")
                .setMessage("MMA application data has been deleted.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO delete your database and passcode

                        finishAffinity();
                    }

                })
                .show();

    }

    @Override
    public void onBackPressed() {

    }
}