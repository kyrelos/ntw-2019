package ntw.jumo.smsfire.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface SmsDao {


    @Query("SELECT * FROM sms WHERE is_spam == :isSpam" )
    List<Sms> findByIsSpam(boolean isSpam);

    @Insert
    void insert(Sms sms);


    @Query("SELECT count(*) FROM sms WHERE is_spam == :isSpam")
    int countSmsByIsSpam(boolean isSpam);

}
