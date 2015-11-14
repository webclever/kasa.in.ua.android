package webclever.sliding_menu;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;
import android.preference.DialogPreference;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.SingletonTempOrder;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.fragments_container;

public class ActivityOrder extends FragmentActivity {

    private Long aLongTimer = 900000l;
    private CountDownTimer countDownTimer;
    private ActionBar actionBar;
    private Activity activity;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                if (SingletonTempOrder.getInstance().getExistenceOrder()){
                    showAlertDialogCancelOrder();
                }else {
                    finish();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        activity = this;
        startTimer();
        actionBar = getActionBar();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragments_container);
        OnBackPressedListener onBackPressedListener = null;
        if(fragment instanceof OnBackPressedListener)
        {
            onBackPressedListener = (OnBackPressedListener) fragment;
        }

        if (onBackPressedListener != null)
        {
            onBackPressedListener.onBackPressed();
        }else {
            super.onBackPressed();
        }

    }

    public void showAlertDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("На жаль, відведений час на оформлення замовлення завершився і тимчасове замовлення було скасовано.");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new FragmentBasket();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(fragments_container, fragment).commit();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void showAlertDialogCancelOrder(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Увага!");
        alertDialog.setMessage("На жаль, відведений час на оформлення замовлення завершився і тимчасове замовлення було скасовано.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelTempOrder();
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }



    public void stopTimer(){
        if (countDownTimer != null){
            countDownTimer.onFinish();
            aLongTimer = null;
        }
    }

    public void startTimer(){
        countDownTimer =  new CountDownTimer(900000,1000) {

            @Override
            public void onTick(long millis) {
                aLongTimer = millis;
            }

            @Override
            public void onFinish() {
                aLongTimer = null;
            }
        }.start();

    }

    public long getTimer(){
        return aLongTimer;
    }

    private void cancelTempOrder(){

        final String url = "http://tms.webclever.in.ua/api/SaveOrder";
        final String order_id = SingletonTempOrder.getInstance().getOrder_id();
        final String order_token = SingletonTempOrder.getInstance().getToken();

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                            if (s.equals("successfully deleted")){
                                showAlertDialog();
                                activity.finish();
                            }else {
                                Toast.makeText(activity, "Помилка при видалені тимчасовго замовлення!", Toast.LENGTH_SHORT).show();
                            }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("Response_err", String.valueOf(volleyError.getMessage()));

            }
        }){

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                params.put("order_id",order_id);
                params.put("order_token",order_token);
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);
    }

}
