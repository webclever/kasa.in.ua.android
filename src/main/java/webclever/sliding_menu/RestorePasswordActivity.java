package webclever.sliding_menu;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Admin on 22.10.2014.
 */
public class RestorePasswordActivity extends FragmentActivity {
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_password);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        final Intent intent = NavUtils.getParentActivityIntent(this);

        Button button = (Button) findViewById(R.id.buttonRestorePassword);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Ваш пароль було надіслано!",Toast.LENGTH_LONG).show();
                thread.start();
            }
        });
        thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                    RestorePasswordActivity.this.finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}
