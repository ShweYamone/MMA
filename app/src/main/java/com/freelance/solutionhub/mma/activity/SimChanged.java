package com.freelance.solutionhub.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;

import com.freelance.solutionhub.mma.DB.InitializeDatabase;
import com.freelance.solutionhub.mma.R;

public class SimChanged extends AppCompatActivity {

    InitializeDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_changed);
        dbHelper = InitializeDatabase.getInstance(this);
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