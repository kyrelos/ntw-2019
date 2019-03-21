package ntw.jumo.smsfire.db;


import android.arch.persistence.room.Room;
import android.content.Context;

public class SmsDatabaseSingleton
{
    private volatile static SmsDatabase instance;

    private SmsDatabaseSingleton() {}

    public static SmsDatabase getInstance(final Context context)
    {
        if (instance == null)
        {
            // To make thread safe
            synchronized (SmsDatabaseSingleton.class)
            {
                // check again as multiple threads
                // can reach above step
                if (instance==null)
                    instance = Room.databaseBuilder(context,
                            SmsDatabase.class, "sms_database").build();

            }
        }
        return instance;
    }
}
