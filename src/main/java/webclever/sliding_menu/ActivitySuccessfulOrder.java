package webclever.sliding_menu;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivitySuccessfulOrder extends Activity {

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_order);

        this.intent = getIntent();

        TextView textViewOrder = (TextView) findViewById(R.id.textView93);
        TextView textViewDescriptionOrder = (TextView) findViewById(R.id.textView95);

        TextView textViewNumberOrder = (TextView) findViewById(R.id.textNumberOrder);
        textViewNumberOrder.setText("# " + intent.getStringExtra("order_id"));

        Button button = (Button) findViewById(R.id.buttonConfirmOrder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
        if (intent.hasExtra("message")) {
            textViewDescriptionOrder.setText(Html.fromHtml(intent.getStringExtra("message")));
        }else {
            textViewDescriptionOrder.setText("Ви можете здійснити оплату, перейшовши по посиланню, що надійшло Вам на пошту.");
        }
        Log.i("pay_method",String.valueOf(intent.getIntExtra("payment_method",0)));
        switch (intent.getIntExtra("payment_method",0)){
            case 1:
                textViewOrder.setText(R.string.description_order1);
                break;
            case 2:
                textViewOrder.setText(R.string.description_order);
                break;
            case 3:
                textViewOrder.setText(R.string.description_order1);
                break;
            case 4:
                textViewOrder.setText(R.string.description_order);
                break;
            case 5:
                textViewOrder.setText(R.string.description_order1);
                break;
            case 6:
                textViewOrder.setText(R.string.description_order);
                break;
            case 7:
                textViewOrder.setText(R.string.description_order1);
                break;
            case 8:
                textViewOrder.setText(R.string.description_order1);
                break;
        }
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
