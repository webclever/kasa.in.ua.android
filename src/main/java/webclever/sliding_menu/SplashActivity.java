package webclever.sliding_menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Admin on 22.10.2014.
 */
public class SplashActivity extends Activity {

    private static int SPLASH_TIME_OUT = 2000;
    private static final String APP_PREFERENCES = "user_profile";
    private SharedPreferences sPref;
    private String STATUS_USER = "status_user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity


               /* if (sPref.contains(STATUS_USER))
                {
                    if (sPref.getBoolean(STATUS_USER, false))
                    {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                }else {
                        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                        startActivity(i);
                    }
                }else {

                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                }*/
                /*Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);*/

                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
}
