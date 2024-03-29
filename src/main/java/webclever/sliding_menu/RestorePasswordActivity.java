package webclever.sliding_menu;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import customlistviewapp.AppController;


public class RestorePasswordActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);

        Button button = (Button) findViewById(R.id.buttonRestorePassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.page_restore_password_send),Toast.LENGTH_LONG).show();

            }
        });

        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen restore password.");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                    finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }
}
