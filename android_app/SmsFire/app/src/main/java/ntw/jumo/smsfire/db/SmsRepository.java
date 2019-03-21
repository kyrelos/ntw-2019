package ntw.jumo.smsfire.db;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class SmsRepository {
    private SmsDao mSmsDao;

    public SmsRepository(Context context) {
        SmsDatabase db = SmsDatabaseSingleton.getInstance(context);
        mSmsDao = db.smsDao();

    }

    public void insert (Sms sms) {
        new insertAsyncTask(mSmsDao).execute(sms);
    }

    public List<Sms> getSmsByIsSpam(boolean isSpam){
        return mSmsDao.findByIsSpam(isSpam);
    }

    public int countByIsSpam(boolean isSpam){
        return mSmsDao.countSmsByIsSpam(isSpam);
    }

    private static class insertAsyncTask extends AsyncTask<Sms, Void, Void> {

        private SmsDao mAsyncTaskDao;

        insertAsyncTask(SmsDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Sms... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
