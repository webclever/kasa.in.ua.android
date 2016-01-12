package webclever.sliding_menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import customlistviewapp.AppController;


public class ActivitySuccessRegistration extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_registration);
        Button button = (Button) findViewById(R.id.buttonSuccessContinue);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen success registration.");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }
}
