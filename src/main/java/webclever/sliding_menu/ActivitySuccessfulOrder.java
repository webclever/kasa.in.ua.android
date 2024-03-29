package webclever.sliding_menu;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import customlistviewapp.AppController;

public class ActivitySuccessfulOrder extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_order);

        Intent intent = getIntent();

        TextView textViewOrder = (TextView) findViewById(R.id.textView93);
        TextView textViewDescriptionOrder = (TextView) findViewById(R.id.textView95);

        TextView textViewNumberOrder = (TextView) findViewById(R.id.textNumberOrder);
        String orderID = "# " + intent.getStringExtra("order_id");
        textViewNumberOrder.setText(orderID);

        Button button = (Button) findViewById(R.id.buttonConfirmOrder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
        if (intent.hasExtra("message")) {
            textViewDescriptionOrder.setText(intent.getStringExtra("message"));
        }
        Log.i("pay_method",String.valueOf(intent.getIntExtra("payment_method", 0)));
        switch (intent.getIntExtra("payment_method", 0)){
            case 1:
                textViewOrder.setText(R.string.page_success_order_description_order1);
                break;
            case 2:
                textViewOrder.setText(R.string.page_success_description_order);
                textViewDescriptionOrder.setText(R.string.page_success_order_number_ordering_text);
                break;
            case 3:
                textViewOrder.setText(R.string.page_success_order_description_order1);
                break;
            case 4:
                textViewOrder.setText(R.string.page_success_description_order);
                textViewDescriptionOrder.setText(R.string.page_success_order_description_order2);
                break;
            case 5:
                textViewOrder.setText(R.string.page_success_order_description_order1);
                break;
            case 6:
                textViewOrder.setText(R.string.page_success_description_order);
                textViewDescriptionOrder.setText(R.string.page_success_order_description_courier);
                break;
            case 7:
                textViewOrder.setText(R.string.page_success_order_description_order1);
                break;
            case 8:
                textViewOrder.setText(R.string.page_success_description_order);
                textViewDescriptionOrder.setText(R.string.page_success_order_description_eticket);
                break;
        }

        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen successful order.");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    private void startMainActivity(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {

    }

}
