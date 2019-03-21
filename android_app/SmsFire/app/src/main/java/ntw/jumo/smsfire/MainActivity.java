package ntw.jumo.smsfire;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.lang.ref.WeakReference;

import ntw.jumo.smsfire.db.SmsRepository;


public class MainActivity extends AppCompatActivity {
    private static final int SMS_PERMISSION_CODE = 0;
    private static final int CONTACT_PERMISSION_CODE = 0;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasReadSmsPermission()) {
            showRequestPermissionsInfoAlertDialog();
        }

        if(!hasReadContactPermission()){
            requestReadContactPermission();
        }


        new countSmsAsyncTask(this).execute();

    }

    private static class countSmsAsyncTask extends AsyncTask<Void, Void, Void> {
        private WeakReference<Activity> weakActivity;

        countSmsAsyncTask(Activity activity) {
            weakActivity = new WeakReference<>(activity);;
        }

        @Override
        protected Void doInBackground(final Void... params) {

            Activity activity = weakActivity.get();
            if(activity == null) {
                return null;
            }

            SmsRepository repo =  new SmsRepository(activity.getApplication());
            int smsCount = repo.countByIsSpam(false);
            int spamCount = repo.countByIsSpam(true);

            TextView smsCountView = activity.findViewById(R.id.sms_count_text_view);
            TextView spamCountView = activity.findViewById(R.id.spam_count_text_view);


            smsCountView.setText(String.valueOf(smsCount));
            spamCountView.setText(String.valueOf(spamCount));
            return null;
        }
    }


    /**
     * Runtime permission shenanigans
     */
    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadContactPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED ;
    }

    private void requestReadContactPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_CONTACTS)){
            Log.d(TAG, "requestReadContactPermission(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_CONTACTS},
                CONTACT_PERMISSION_CODE);
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS)) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS},
                SMS_PERMISSION_CODE);
    }

    /**
     * Displays an AlertDialog explaining the user why the SMS permission is going to be requests
     *
     *  makeSystemRequest if set to true the system permission will be shown when the dialog is dismissed.
     */
    public void showRequestPermissionsInfoAlertDialog() {
        showRequestPermissionsInfoAlertDialog(true);
    }

    public void showRequestPermissionsInfoAlertDialog(final boolean makeSystemRequest) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title); // Your own title
        builder.setMessage(R.string.permission_dialog_message); // Your own message

        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // Display system runtime permission request?
                if (makeSystemRequest) {
                    requestReadAndSendSmsPermission();
                }
            }
        });

        builder.setCancelable(false);
        builder.show();
    }

    public void listSms(View view){
        Intent intent = new Intent(this, SmsActivity.class);
        startActivity(intent);
    }

    public void listSpamSms(View view) {
        Intent intent = new Intent(this, SpamActivity.class);
        startActivity(intent);
    }

}
