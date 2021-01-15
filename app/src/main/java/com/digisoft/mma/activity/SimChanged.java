package com.digisoft.mma.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.model.PhotoFilePathModel;
import com.digisoft.mma.util.SharePreferenceHelper;

import java.io.File;
import java.util.List;

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
        sharePreferenceHelper.removeCurrentMSOID();
        sharePreferenceHelper.deletePinCode();
        File file;
        for(PhotoFilePathModel e : getPhotoFilePaths()){
            file = new File(e.getFilePath());
            if(file.exists())
                file.delete();
        }

        dbHelper.checkListDescDAO().deleteAll();
        dbHelper.eventDAO().deleteAll();
        dbHelper.updateEventBodyDAO().deleteAll();
        dbHelper.uploadPhotoDAO().deleteAll();
        dbHelper.photoFilePathDAO().deleteAll();

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

                        startActivity(new Intent(SimChanged.this, PasscodeRegisterActivity.class));
                        finishAffinity();
                    }

                })
                .show();

    }

    @Override
    public void onBackPressed() {
        //To disable user back press.............
    }

    /**
     * Get Photo File Paths from DB
     */
    private List<PhotoFilePathModel> getPhotoFilePaths() {
        return dbHelper.photoFilePathDAO().getPhotoFilePaths();
    }
}