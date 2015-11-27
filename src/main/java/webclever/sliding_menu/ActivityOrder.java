package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Fragment;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Activity;
import android.os.CountDownTimer;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;



import java.util.HashMap;
import java.util.Map;

import DataBase.DB_Ticket;
import Singleton.SingletonTempOrder;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;



public class ActivityOrder extends FragmentActivity {

    private Long aLongTimer = 900000l;
    private CountDownTimer countDownTimer;
    private Activity activity;
    private DB_Ticket db_ticket;

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
        db_ticket = new DB_Ticket(this,5);
        activity = this;
        startTimer();
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
        alertDialog.setMessage(getResources().getString(R.string.page_order_time_up_temp_order_dialog));
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public void showAlertDialogCancelOrder(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getResources().getString(R.string.page_order_attention));
        alertDialog.setMessage(getResources().getString(R.string.page_order_check_exit_temp_order_dialog));
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        cancelTempOrder();
                        dialog.cancel();
                    }
                });
        alertDialog.setNegativeButton(getResources().getString(R.string.page_order_cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void stopTimer(){
        if (countDownTimer != null){
            countDownTimer.cancel();
            aLongTimer = null;
        }
    }

    public void startTimer(){
        countDownTimer =  new CountDownTimer(aLongTimer,1000) {
            @Override
            public void onTick(long millis) {
                aLongTimer = millis;
            }

            @Override
            public void onFinish() {
                aLongTimer = null;
                showAlertDialog();
            }
        }.start();
    }

    public long getTimer(){
        return aLongTimer;
    }

    private void cancelTempOrder(){

        final String url = "http://tms.webclever.in.ua/api/deleteTempOrder";
        final String order_id = SingletonTempOrder.getInstance().getOrder_id();
        final String order_token = SingletonTempOrder.getInstance().getToken();

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                        if (s.equals("\"successfully deleted\"")){
                                SingletonTempOrder.getInstance().setExistenceOrder(false);
                                SingletonTempOrder.getInstance().setOrder_id(null);
                                SingletonTempOrder.getInstance().setToken(null);
                                activity.finish();
                            }else {
                                Toast.makeText(activity, getResources().getString(R.string.page_order_toast_error_delete_temp_order), Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStop (){
        super.onStop();
        cancelTempOrder();
    }

    public void deleteDB() {

        SQLiteDatabase db = db_ticket.getWritableDatabase();
        int rows = db.delete("Ticket_table", null, null);
        Log.i("id_ticket", "del rows" + String.valueOf(rows));

        rows = db.delete("Event_table", null, null);
        Log.i("id_ticket", "del rows" + String.valueOf(rows));

        db_ticket.close();
    }

}
