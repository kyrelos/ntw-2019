package ntw.jumo.smsfire.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;


@Entity
public class Sms {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "address")
    public String address;

    @ColumnInfo(name = "message")
    public String message;

    @ColumnInfo(name = "is_spam")
    public boolean isSpam;

    @ColumnInfo(name = "date")
    public String date;
}
