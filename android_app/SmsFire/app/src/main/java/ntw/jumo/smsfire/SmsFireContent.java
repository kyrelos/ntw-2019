package ntw.jumo.smsfire;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import java.util.ArrayList;
import java.util.List;


public class SmsFireContent {
    private static final String TAG = "SmsFireContent";
    public  List<SmsFireMessage> SMS = new ArrayList<SmsFireMessage>();

    public SmsFireContent(){
    }

    public void fetchInboxMessage(Context context){
        Cursor cursor = context.getContentResolver().query(
                Uri.parse("content://sms/inbox"), new String[]{"_id", "address", "body", "date"},
                null, null, "_id desc");

        int id_idx = cursor.getColumnIndex("_id");
        int address_idx = cursor.getColumnIndex("address");
        int body_idx = cursor.getColumnIndex("body");
        int date_idx = cursor.getColumnIndex("date");

        if (cursor.moveToFirst()) { // must check the result to prevent exception
            do {
                SmsFireMessage sms = createSmsFireMessage(
                        cursor.getString(id_idx),
                        cursor.getString(body_idx),
                        cursor.getString(address_idx),
                        cursor.getString(date_idx));
                addItem(sms);
            } while (cursor.moveToNext());
        } else {
            // empty box, no SMS
        }
    }



    private void addItem(SmsFireMessage message) {
        SMS.add(message);
    }

    public static SmsFireMessage createSmsFireMessage(String id, String message, String address,
                                                 String date) {
        return new SmsFireMessage(id, message, address, date);
    }

    public static class SmsFireMessage {
        public final String id;
        public final String message;
        public final String address;
        public final String date;

        public SmsFireMessage(String id, String message, String address, String date) {
            this.id = id;
            this.message = message;
            this.address = address;
            this.date = date;
        }

        @Override
        public String toString() {
            return message;
        }
    }
}
