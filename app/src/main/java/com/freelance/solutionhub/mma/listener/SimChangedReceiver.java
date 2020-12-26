package com.freelance.solutionhub.mma.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.SimChanged;

public class SimChangedReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d("SimChangedReceiver", "--> SIM state changed <--");
        Intent i=new Intent(context.getApplicationContext(), SimChanged.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        // Most likely, checking if the SIM changed could be limited to
        // events where the intent's extras contains a key "ss" with value "LOADED".
        // But it is more secure to just always check if there was a change.
    }
}
