package com.freelance.solutionhub.mma.listener;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.freelance.solutionhub.mma.R;
import com.freelance.solutionhub.mma.activity.SimChanged;
import com.freelance.solutionhub.mma.util.SharePreferenceHelper;

public class SimChangedReceiver extends BroadcastReceiver {

    private static final String ACTION_SIM_STATE_CHANGED = "android.intent.action.SIM_STATE_CHANGED";

    private static final String EXTRA_SIM_STATE = "ss";
    private static final String SIM_STATE_LOADED = "LOADED";


    @Override
    public void onReceive(final Context context, final Intent intent) {

        SharePreferenceHelper mSharePreference = new SharePreferenceHelper(context);
        Log.d("SimChangedReceiver", "--> SIM state changed <--");
        String action = intent.getAction();
        if (ACTION_SIM_STATE_CHANGED.equals(action)) {
            Bundle extras = intent.getExtras();
            printExtras(extras);
            String state = extras.getString(EXTRA_SIM_STATE);
            Log.w("TAG", "SIM Action : " + action + " / State : " + state);
            // Test phoneName = GSM ?
            if (SIM_STATE_LOADED.equals(state)) {
                // Read Phone number
                String phoneNumber = getSystemPhoneNumber(context);
                if (mSharePreference.getPhoneNumber().isEmpty()) {
                    Log.e("TAG", "EventSpy : No phone number Readable in TelephonyManager");
                } else {
                    if (!mSharePreference.getPhoneNumber().equals(phoneNumber)) {
                        Log.w("TAG", "EventSpy SIM Change for new Phone Number : " + phoneNumber);
                        // Send message
                        sendNotifSms(context);
                        // Save as New Phone
                        // NO savePrefsPhoneNumber(prefs, phoneNumber);
                    }
                }
            }

        }
    }


    private void sendNotifSms(Context context) {
        Intent i = new Intent(context.getApplicationContext(), SimChanged.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    private void printExtras(Bundle extras) {
        if (extras != null) {
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d("TAG", "EventSpy SIM extras : " + key + " = " + value);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private static String getSystemPhoneNumber(Context context) {
        // Read Phone number
        TelephonyManager telephoneMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String phoneNumber = telephoneMgr.getLine1Number();
        Log.d("TAG", "EventSpy SIM PhoneNumber : " + phoneNumber); // Code IMEI
        return phoneNumber;
    }

}
