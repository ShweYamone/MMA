package com.digisoft.mma.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.digisoft.mma.DB.InitializeDatabase;
import com.digisoft.mma.R;
import com.digisoft.mma.model.Event;
import com.digisoft.mma.model.ReturnStatus;
import com.digisoft.mma.model.UpdateEventBody;
import com.digisoft.mma.nfc.NdefMessageParser;
import com.digisoft.mma.nfc.ParsedNdefRecord;
import com.digisoft.mma.util.ApiClient;
import com.digisoft.mma.util.ApiInterface;
import com.digisoft.mma.util.Network;
import com.digisoft.mma.util.SharePreferenceHelper;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.digisoft.mma.util.AppConstant.BEARER;
import static com.digisoft.mma.util.AppConstant.CM_Step_THREE;
import static com.digisoft.mma.util.AppConstant.DATE_FORMAT;
import static com.digisoft.mma.util.AppConstant.FAILURE;
import static com.digisoft.mma.util.AppConstant.IOEXCEPTION;
import static com.digisoft.mma.util.AppConstant.NO;
import static com.digisoft.mma.util.AppConstant.PM_Step_TWO;
import static com.digisoft.mma.util.AppConstant.RESPONSE_UNSUCCESS;
import static com.digisoft.mma.util.AppConstant.TIME_SERVER;
import static com.digisoft.mma.util.AppConstant.CM;
import static com.digisoft.mma.util.AppConstant.PM;
import static com.digisoft.mma.util.AppConstant.UPLOAD_ERROR;
import static com.digisoft.mma.util.AppConstant.user_inactivity_time;

public class NFCReadingActivity extends AppCompatActivity {

    @BindView(R.id.btnCancel)
    Button btnCancel;

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private String serviceOrderId;
    private String pId;
    private SharePreferenceHelper mSharedPreference;
    private Network network;
    private InitializeDatabase dbHelper;
    private ApiInterface apiInterface;
    private Date date;
    boolean tag;
    String toPage = "";
    private final int NFC_PERMISSION_CODE = 1002;
    List<Event> events = new ArrayList<>();
    String currentDateTime;

    private Handler handler;
    private Runnable r;
    private boolean startHandler = true;
    private String step = "";
    private final String TAG_OUT = "TAG_OUT";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreading);
        ButterKnife.bind(this);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mSharedPreference = new SharePreferenceHelper(this);
        mSharedPreference.setLock(false);


        apiInterface = ApiClient.getClient(this);
        network = new Network(this);
        dbHelper = InitializeDatabase.getInstance(this);
        serviceOrderId = getIntent().getStringExtra("id");


        /**
         after certain amount of user inactivity, asks for passcode
         */
        handler = new Handler();
        r = new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Toast.makeText(NFCReadingActivity.this, "user is inactive from last 5 minutes",Toast.LENGTH_SHORT).show();
                startHandler = false;
                Intent intent = new Intent(NFCReadingActivity.this, PasscodeActivity.class);
                intent.putExtra("workInMiddle", "work");
                startActivity(intent);
                stopHandler();
            }
        };

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSharedPreference.setLock(false);
                finish();
            }
        });
        ////////////////////////////////
        if (getIntent().hasExtra(TAG_OUT)) {
            tag = true;
            Log.i(TAG_OUT,getIntent().getIntExtra(TAG_OUT,0)+"");
            events.add(new Event(TAG_OUT, "tagOut", "tagOut"));
            pId = dbHelper.eventDAO().getEventValue("pid", "pid");
        } else {
            tag = false;
            events.add(new Event("TAG_IN", "tagIn", "tagIn"));
            pId = getIntent().getStringExtra("pid");
            Event tempEvent = new Event("pid", "pid", pId);
            tempEvent.setEvent_id("pidKey");
            tempEvent.setUpdateEventBodyKey("PID");
            dbHelper.eventDAO().insert(tempEvent);

        }
        if (getIntent().hasExtra("JOB_DONE")) {
            toPage = "COMPLETION";
        }
        if (serviceOrderId.startsWith(PM))
            step = PM_Step_TWO;
        else
            step = CM_Step_THREE;

        /****To Fix when NFC can read*********/
        /*********************/
        /////////////////////////////////////
        perFormTagEvent();

        if(nfcAdapter == null){
       //     Toast.makeText(this, "No NFC", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }else {
            if (!nfcAdapter.isEnabled()){
                showWirelessSettings();
            }
        }

        pendingIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, this.getClass())
                        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

    }

    private void performLocalTagOutEvent() {
        UpdateEventBody eventBody = dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(TAG_OUT);
        eventBody.setEvents(dbHelper.eventDAO().getEventsToUpload(TAG_OUT));
        Call<ReturnStatus> call = apiInterface.updateEvent(BEARER + mSharedPreference.getToken(), eventBody);
        call.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                if (response.isSuccessful()) {
               //     Toast.makeText(getApplicationContext(), "LOCAL_TAG_OUT", Toast.LENGTH_SHORT).show();
                    if (serviceOrderId.startsWith(PM)) {
                        performFinalStepEvent(PM);

                    } else {
                        performFinalStepEvent(CM);
                    }
                }
                else {
                    Log.e(UPLOAD_ERROR, RESPONSE_UNSUCCESS + "");
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {
                Log.e(UPLOAD_ERROR, FAILURE + "");
            }
        });
    }

    private void performFinalStepEvent(String pmOrcm) {
      //  dbHelper.updateEventBodyDAO().updateDateTime(date, step);

        Call<ReturnStatus> call = apiInterface.updateStatusEvent(BEARER + mSharedPreference.getToken(),
                dbHelper.updateEventBodyDAO().getUpdateEventBodyByID(step));
        call.enqueue(new Callback<ReturnStatus>() {
            @Override
            public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                if (response.isSuccessful()) {
                    Intent intent;
                    if (pmOrcm.equals(PM)) {
                        intent = new Intent(NFCReadingActivity.this, PMCompletionActivity.class);
                    } else {
                        intent = new Intent(NFCReadingActivity.this, CMCompletionActivity.class);
                    }
                    intent.putExtra("id", serviceOrderId);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);


                } else {
                //    Toast.makeText(getApplicationContext(), "response " + response.code(), Toast.LENGTH_LONG).show();
                    ResponseBody errorReturnBody = response.errorBody();
                    try {
                        Log.e(UPLOAD_ERROR, "" + errorReturnBody.string());

                    } catch (IOException e) {
                        Log.e(UPLOAD_ERROR, IOEXCEPTION + "");
                    }
                }
            }

            @Override
            public void onFailure(Call<ReturnStatus> call, Throwable t) {
                Log.e(UPLOAD_ERROR, FAILURE + "");
            }
        });
    }

    private void perFormTagEvent() {

        if (network.isNetworkAvailable()) {
            new getCurrentNetworkTime().execute();
        } else {//network unavailable, store data to local
            if (tag) {
                date = new Date();
                Timestamp timestamp = new Timestamp(date.getTime());
                currentDateTime = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
                UpdateEventBody eventBody;
                eventBody = new UpdateEventBody(
                        mSharedPreference.getUserName(),
                        mSharedPreference.getUserId(),
                        currentDateTime,
                        serviceOrderId
                );
                eventBody.setId(TAG_OUT);
                dbHelper.updateEventBodyDAO().insert(eventBody);
                for (Event event : events) {
                    event.setEvent_id(TAG_OUT);
                    event.setUpdateEventBodyKey(TAG_OUT);
                    event.setAlreadyUploaded(NO);
                }
                dbHelper.eventDAO().insertAll(events);
                Intent intent = new Intent(NFCReadingActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

        }
    }

    class getCurrentNetworkTime extends AsyncTask<String, Void, Boolean> {

        //TAG_EVENT
        private void updateEvent(String networkDateTime) {
            UpdateEventBody eventBody;
            eventBody = new UpdateEventBody(
                    mSharedPreference.getUserName(), mSharedPreference.getUserId(), networkDateTime, serviceOrderId, events
            );
            Call<ReturnStatus> call = apiInterface.updateEvent(BEARER + mSharedPreference.getToken(), eventBody);
            call.enqueue(new Callback<ReturnStatus>() {
                @Override
                public void onResponse(Call<ReturnStatus> call, Response<ReturnStatus> response) {
                    if (response.isSuccessful()) {

                   //     Toast.makeText(getApplicationContext(), networkDateTime + "TAG_" + tag +  response.body().getStatus(), Toast.LENGTH_SHORT).show();
                        if (tag) {
                            Event tempEvent = new Event("end_time", "end_time", networkDateTime);
                            tempEvent.setEvent_id("end_timeend_time");
                            dbHelper.eventDAO().insert(tempEvent);
                            if (toPage.equals("COMPLETION")) {
                                if (dbHelper.updateEventBodyDAO().getNumberOfUpdateEventsById(TAG_OUT) > 0) {
                                    performLocalTagOutEvent();
                                } else {
                                    if (serviceOrderId.startsWith(PM)) {
                                        performFinalStepEvent(PM);

                                    } else {
                                        performFinalStepEvent(CM);
                                    }

                                }

                            } else {
                                Intent intent = new Intent(NFCReadingActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                        } else {
                            Intent intent = new Intent(NFCReadingActivity.this, LoadingActivity.class);
                            intent.putExtra("id", serviceOrderId);
                            intent.putExtra("start_time", networkDateTime);
                            startActivity(intent);
                            finish();
                        }
                    } else {

                  //      ResponseBody errorReturnBody = response.errorBody();
                        Log.e(UPLOAD_ERROR, RESPONSE_UNSUCCESS + "");
                  /*      try {
                            Log.e("UPLOAD_ERROR", "onResponse: ");
                 //           Toast.makeText(getApplicationContext(), events.get(0).getKey() + "response " + errorReturnBody.string(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {

                        }*/
                    }
                }

                @Override
                public void onFailure(Call<ReturnStatus> call, Throwable t) {
                    Log.e(UPLOAD_ERROR, FAILURE + "");
                }
            });
        }

        protected Boolean doInBackground(String... urls) {
            boolean is_locale_date = false;
            try {
                NTPUDPClient timeClient = new NTPUDPClient();
                timeClient.open();
                timeClient.setDefaultTimeout(3000);
                InetAddress inetAddress = InetAddress.getByName(TIME_SERVER);
                TimeInfo timeInfo = timeClient.getTime(inetAddress);
                long localTime = timeInfo.getReturnTime();
                long serverTime = timeInfo.getMessage().getTransmitTimeStamp().getTime();
                Timestamp timestamp = new Timestamp(localTime);
                String localDateTime = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
                Log.i("Time__Local", "doInBackground: " + localTime + "--> " + localDateTime);
                timestamp = new Timestamp(serverTime);
                String actualDateTime = new SimpleDateFormat(DATE_FORMAT).format(timestamp);
                Log.i("Time__Server", "doInBackground:" + serverTime + "--> " + actualDateTime);
                //after getting network time, update event with the network time
                updateEvent(actualDateTime);
                if (new Date(localTime) != new Date(serverTime))
                    is_locale_date = true;

            } catch (UnknownHostException e) {
                Log.e("UnknownHostException: ", e.getMessage());
            } catch (IOException e) {
                Log.e("IOException: ", e.getMessage());
            }
            return is_locale_date;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
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
        Log.i("LOCKSCREEN", "onResume: " + mSharedPreference.getLock());
        if (mSharedPreference.getLock()) {
            Intent intent = new Intent(NFCReadingActivity.this, PasscodeActivity.class);
            intent.putExtra("workInMiddle", "work");
            startActivity(intent);
        } else {
            startHandler = true;
            startHandler();
        }
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, null);
        }
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
         setIntent(intent);
         resolveIntent(intent);
         mSharedPreference.setLock(false);
    }

    private void resolveIntent(Intent intent) {
        String action = intent.getAction();

        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)
                || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
            Parcelable[] rawMsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage[] msgs;

            if (rawMsgs != null) {
                msgs = new NdefMessage[rawMsgs.length];

                for (int i = 0; i < rawMsgs.length; i++) {
                    msgs[i] = (NdefMessage) rawMsgs[i];
                }

            } else {
                byte[] empty = new byte[0];
                byte[] id = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
                Tag tag = (Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
                byte[] payload = dumpTagData(tag).getBytes();
                NdefRecord record = new NdefRecord(NdefRecord.TNF_UNKNOWN, empty, id, payload);
                NdefMessage msg = new NdefMessage(new NdefRecord[] {record});
                msgs = new NdefMessage[] {msg};
            }

            displayMsgs(msgs);
        }
    }

    private void displayMsgs(NdefMessage[] msgs) {
        if (msgs == null || msgs.length == 0)
            return;

        StringBuilder builder = new StringBuilder();
        List<ParsedNdefRecord> records = NdefMessageParser.parse(msgs[0]);
        final int size = records.size();

        for (int i = 0; i < size; i++) {
            ParsedNdefRecord record = records.get(i);
            String str = record.str();
            builder.append(str).append("\n");
        }

        if(builder.toString().equals(pId)){
            perFormTagEvent();
        }else {
            perFormTagEvent();
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.warning)
                    .setTitle("PID Mismatch!")
                    .setMessage("Your panel id is wrong.")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }

                    })
                    .show();
        }
    }

    private void showWirelessSettings() {
        //Toast.makeText(this, "You need to enable NFC", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(Settings.ACTION_NFC_SETTINGS);
        startActivity(intent);
    }

    private String dumpTagData(Tag tag) {
        StringBuilder sb = new StringBuilder();
        byte[] id = tag.getId();
        sb.append("ID (hex): ").append(toHex(id)).append('\n');
        sb.append("ID (reversed hex): ").append(toReversedHex(id)).append('\n');
        sb.append("ID (dec): ").append(toDec(id)).append('\n');
        sb.append("ID (reversed dec): ").append(toReversedDec(id)).append('\n');

        String prefix = "android.nfc.tech.";
        sb.append("Technologies: ");
        for (String tech : tag.getTechList()) {
            sb.append(tech.substring(prefix.length()));
            sb.append(", ");
        }

        sb.delete(sb.length() - 2, sb.length());

        for (String tech : tag.getTechList()) {
            if (tech.equals(MifareClassic.class.getName())) {
                sb.append('\n');
                String type = "Unknown";

                try {
                    MifareClassic mifareTag = MifareClassic.get(tag);

                    switch (mifareTag.getType()) {
                        case MifareClassic.TYPE_CLASSIC:
                            type = "Classic";
                            break;
                        case MifareClassic.TYPE_PLUS:
                            type = "Plus";
                            break;
                        case MifareClassic.TYPE_PRO:
                            type = "Pro";
                            break;
                    }
                    sb.append("Mifare Classic type: ");
                    sb.append(type);
                    sb.append('\n');

                    sb.append("Mifare size: ");
                    sb.append(mifareTag.getSize() + " bytes");
                    sb.append('\n');

                    sb.append("Mifare sectors: ");
                    sb.append(mifareTag.getSectorCount());
                    sb.append('\n');

                    sb.append("Mifare blocks: ");
                    sb.append(mifareTag.getBlockCount());
                } catch (Exception e) {
                    sb.append("Mifare classic error: " + e.getMessage());
                }
            }

            if (tech.equals(MifareUltralight.class.getName())) {
                sb.append('\n');
                MifareUltralight mifareUlTag = MifareUltralight.get(tag);
                String type = "Unknown";
                switch (mifareUlTag.getType()) {
                    case MifareUltralight.TYPE_ULTRALIGHT:
                        type = "Ultralight";
                        break;
                    case MifareUltralight.TYPE_ULTRALIGHT_C:
                        type = "Ultralight C";
                        break;
                }
                sb.append("Mifare Ultralight type: ");
                sb.append(type);
            }
        }

        return sb.toString();
    }

    private String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = bytes.length - 1; i >= 0; --i) {
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
            if (i > 0) {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

    private String toReversedHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            int b = bytes[i] & 0xff;
            if (b < 0x10)
                sb.append('0');
            sb.append(Integer.toHexString(b));
        }
        return sb.toString();
    }

    private long toDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = 0; i < bytes.length; ++i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    private long toReversedDec(byte[] bytes) {
        long result = 0;
        long factor = 1;
        for (int i = bytes.length - 1; i >= 0; --i) {
            long value = bytes[i] & 0xffl;
            result += value * factor;
            factor *= 256l;
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSharedPreference.setLock(false);
    }
}
