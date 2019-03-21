package ntw.jumo.smsfire;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;

import ntw.jumo.smsfire.db.Sms;
import ntw.jumo.smsfire.db.SmsRepository;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            String smsDate = "";
            boolean isSpam = false;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {


                    smsSender = smsMessage.getDisplayOriginatingAddress();
                    smsDate = String.valueOf(smsMessage.getTimestampMillis());
                    smsBody += smsMessage.getMessageBody();
                }
            } else {
                Bundle smsBundle = intent.getExtras();
                if (smsBundle != null) {
                    Object[] pdus = (Object[]) smsBundle.get("pdus");
                    if (pdus == null) {
                        // Display some error to the user
                        Log.e(TAG, "SmsBundle had no pdus key");
                        return;
                    }
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < messages.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        smsBody += messages[i].getMessageBody();
                    }
                    smsSender = messages[0].getOriginatingAddress();
                    smsDate = String.valueOf(messages[0].getTimestampMillis());
                }
            }
            Log.d(TAG, "onReceive() smsSender: " + smsSender);

            boolean isInContact = isInContact(context, smsSender);
            Log.d(TAG, "onReceive() isInContact: " + isInContact);

            // Call our api to classify the sms

            // Sms save in the db
            saveInDb(context, smsSender, smsBody, smsDate, isSpam);

        }

    }

    private boolean isInContact(Context context, String phoneNumber){
        boolean isInContact = false;

        if(phoneNumber == null && phoneNumber.isEmpty()) { return isInContact; }
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));

        ContentResolver contentResolver = context.getContentResolver();

        Cursor cursor = contentResolver.query(lookupUri, null,null,
                null, null);

        try {
            if (cursor != null && cursor.getCount() > 0) {
                // phone_number in contact
                cursor.moveToNext();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                Log.d(TAG, "isInContact() --> name: " + name);
                isInContact = true;

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return isInContact;
    }

    private void saveInDb(Context context, String address, String message, String date,
                          boolean isSpam){
        Log.d(TAG, "saveInDb() Saving received sms");
        Sms sms = new Sms();
        sms.address = address;
        sms.message = message;
        sms.date = date;
        sms.isSpam = isSpam;

        SmsRepository repo =  new SmsRepository(context);

        repo.insert(sms);
    }
}
