package ntw.jumo.smsfire.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = {Sms.class}, version = 1)
public abstract class SmsDatabase extends RoomDatabase {
    public abstract SmsDao smsDao();

}
