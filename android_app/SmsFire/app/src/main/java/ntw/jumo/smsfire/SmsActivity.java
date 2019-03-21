package ntw.jumo.smsfire;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SmsActivity extends AppCompatActivity implements SmsFragment.OnListFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);

        if (savedInstanceState == null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            SmsFragment fragment = SmsFragment.newInstance(1, false);
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
    }

    public  void onListFragmentInteraction(SmsFireContent.SmsFireMessage item){

    }
}
