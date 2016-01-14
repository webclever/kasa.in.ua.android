package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

import DataBase.DB_Ticket;
import Singleton.SingletonTempOrder;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;



public class ActivityOrder extends FragmentActivity {

    private Long aLongTimer = 300000l;
    private CountDownTimer countDownTimer;
    private Activity activity;
    private DB_Ticket db_ticket;
    private SQLiteDatabase db;
    private Tracker mTracker;

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
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
        setContentView(R.layout.activity_order);
        db_ticket = new DB_Ticket(this,5);
        activity = this;
        startTimer();
    }

    public void Trekking(String nameScreen){
        mTracker.setScreenName(nameScreen);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
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

    public void showAlertDialogPayTicket(final JSONArray jsonArray){
        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this);
        alBuilder.setTitle(getResources().getString(R.string.page_order_attention));
        alBuilder.setMessage(getResources().getString(R.string.page_basket_ticket_already_sale_dialog));
        alBuilder.setCancelable(false);
        alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (jsonArray != null) {
                    deleteSoldTickets(jsonArray);
                }else {
                    deleteDB();
                }
                stopTimer();
                Intent intent = new Intent(activity,MainActivity.class);
                startActivity(intent);
                finish();
                dialog.cancel();
            }
        });
        alBuilder.show();
    }

    private void deleteSoldTickets(JSONArray jsonArraySoldTickets){
        try {
            for (int i=0; i < jsonArraySoldTickets.length(); i++){
                db_ticket = new DB_Ticket(this,5);
                db = db_ticket.getWritableDatabase();
                int del_id_ticket ;
                int id_event = 0;
                Cursor cursorDel = db.query("Ticket_table",new String[]{"id_event"},"id_ticket=" + jsonArraySoldTickets.getString(i),null,null,null,null,null);
                if (cursorDel != null ){
                    cursorDel.moveToFirst();
                    id_event = Integer.parseInt(cursorDel.getString(0));
                    cursorDel.close();
                }
                del_id_ticket = db.delete("Ticket_table", "id_ticket=" + jsonArraySoldTickets.getString(i), null);
                Log.i("id_ticket_del", String.valueOf(del_id_ticket));
                Cursor cursorDelEvent = db.query("Ticket_table",new String[]{"id_event"},"id_event=" + String.valueOf(id_event),null,null,null,null,null);
                if (cursorDelEvent.getCount() == 0)
                {
                    Log.i("getcountEvent",String.valueOf(cursorDelEvent.getCount()));
                    int del_id_event = db.delete("Event_table","id_event="+String.valueOf(id_event),null);
                    Log.i("id_event_del", String.valueOf(del_id_event));
                }
                cursorDelEvent.close();
                db_ticket.close();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

        final String url = "http://tms.net.ua/api/deleteTempOrder";
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
        stopTimer();
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
