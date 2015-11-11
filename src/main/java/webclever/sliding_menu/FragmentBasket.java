package webclever.sliding_menu;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import DataBase.DB_Ticket;
import Format.EncodingTicketCount;
import adapter.Basket;
import adapter.Basket_Child;
import adapter.ViewBasketAdapter;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by User on 13.08.2014.
 */
public class FragmentBasket extends Fragment implements OnBackPressedListener {

    public FragmentBasket(){setHasOptionsMenu(true);}
    private ListView listViewBasketTicket;
    private ArrayList<Basket> basketArrayList = new ArrayList<Basket>();
    private ViewBasketAdapter viewBasketAdapter;
    private DB_Ticket db_ticket;
    private SQLiteDatabase db;
    private TextView textViewTicket;
    private TextView textViewPrice;
    private TextView textViewEmptyCart;
    private TextView textViewCountTicket;
    private Button mButton;
    private EncodingTicketCount ticketCount;
    private int tickets = 0, price = 0;
    private JSONArray jsonArray;

    private static final String APP_PREFERENCES_DIALOG = "dialog_show";
    private SharedPreferences spShowDialog;


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup conteiner,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_basket,conteiner,false);
        ((MainActivity)getActivity()).setItemChecked(2,true);

        spShowDialog = getActivity().getSharedPreferences(APP_PREFERENCES_DIALOG, Context.MODE_PRIVATE);
        listViewBasketTicket = (ListView) rootView.findViewById(R.id.listViewTicketBasket);
        mButton = (Button) rootView.findViewById(R.id.buttonBasket);
        textViewEmptyCart = (TextView) rootView.findViewById(R.id.textViewEmptyCart);
        mButton.setEnabled(false);
        mButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        if (!((MainActivity)getActivity()).getCountTicket().equals("0"))
        {
            mButton.setEnabled(true);
            mButton.getBackground().setColorFilter(null);
            textViewEmptyCart.setVisibility(View.GONE);
        }
        db_ticket = new DB_Ticket(getActivity(),5);
        textViewTicket = (TextView) rootView.findViewById(R.id.textView36);
        textViewPrice = (TextView) rootView.findViewById(R.id.textView39);
        textViewCountTicket = (TextView) rootView.findViewById(R.id.textView37);
        ticketCount = new EncodingTicketCount();
        basketArrayList = addTicket();
        loadHosts(basketArrayList);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((MainActivity)getActivity()).getStatusUser()) {
                    checkFreeTickets();
                } else {
                    if (spShowDialog.getBoolean("show_dialog",true)){
                        showDialog();
                    } else {
                        checkFreeTickets();
                    }
                }
            }
        });

        return  rootView;
    }

    private void checkFreeTickets() {

        jsonArray = new JSONArray();
        Log.i("id_ticket_mas", jsonArray.toString());
        jsonArray = getIdTickets();
        final String url = "http://tms.webclever.in.ua/api/createTempOrder";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Log.i("Response_ticket", s);

                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("msg")){

                                JSONArray jsonArrayTicket = jsonObject.getJSONArray("place_ids");
                                showDialogSoldTicket(jsonArrayTicket);

                            }else {

                                Fragment fragment = new FragmentDeliveryOrder();
                                FragmentManager fragmentManager = getFragmentManager();
                                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
                                startService();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                if (volleyError.networkResponse != null) {
                    Log.d("Error Response code " , String.valueOf(volleyError.networkResponse.statusCode));
                }

                NetworkResponse response = volleyError.networkResponse;
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    switch(response.statusCode){
                        case 400:
                            try {
                                String json = new String(response.data);
                                JSONObject jsonObject = new JSONObject(json);
                                JSONArray jsonArrayTicket = jsonObject.getJSONArray("place_ids");
                                showDialogSoldTicket(jsonArrayTicket);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                params.put("places", jsonArray.toString());
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);
    }

    private ArrayList<Basket> addTicket() {
        final ArrayList<Basket> basketsParent = new ArrayList<>();
        tickets = 0;
        price = 0;
        db = db_ticket.getWritableDatabase();
        Cursor cursorEvent = db.query("Event_table",null,null,null,null,null,null);
        if (cursorEvent != null)
        {
            if (cursorEvent.moveToFirst())
            {
                int id_event = cursorEvent.getColumnIndex("id_event");
                int name_event = cursorEvent.getColumnIndex("name_event");
                int date_event = cursorEvent.getColumnIndex("date_event");
                int time_event = cursorEvent.getColumnIndex("time_event");
                int place_event = cursorEvent.getColumnIndex("place_event");

                do {

                    Log.i("id_event_basket",String.valueOf(id_event));

                    final Basket basket = new Basket();
                    int id_event_basket = cursorEvent.getInt(id_event);
                    basket.setId_event(id_event);
                    basket.setNameBasket(cursorEvent.getString(name_event));
                    basket.setCityBasket(cursorEvent.getString(place_event));
                    basket.setDate(cursorEvent.getString(date_event));
                    basket.setTimeBasket(cursorEvent.getString(time_event));
                    basket.setBasket_childArrayList(new ArrayList<Basket_Child>());
                        Cursor cursorTicket = db.query("Ticket_table",new String[]{"id_ticket","zon_ticket","name_row_ticket","row_ticket","place_ticket","price_ticket","id_event"},"id_event="+String.valueOf(id_event_basket),null,null,null,null,null);
                        if (cursorTicket != null)
                        {
                            if (cursorTicket.getCount() > 0)
                            {
                                if (cursorTicket.moveToFirst())
                                {
                                  do {
                                      String id_ticket = cursorTicket.getString(0);
                                      String zon_ticket = cursorTicket.getString(1);
                                      String name_row_ticket = cursorTicket.getString(2);
                                      String row_ticket = cursorTicket.getString(3);
                                      String place_ticket = cursorTicket.getString(4);
                                      String price_ticket = cursorTicket.getString(5);
                                      String id_eventt = cursorTicket.getString(6);
                                      final Basket_Child basket_child = new Basket_Child();
                                      tickets ++;
                                      if (price_ticket != null) {
                                          price = price + Integer.parseInt(price_ticket);
                                          Log.i("price_ticket", String.valueOf(tickets));
                                      }
                                      basket_child.setId_event(Integer.parseInt(id_eventt));
                                      basket_child.setId_ticket(Integer.parseInt(id_ticket));
                                      basket_child.setNameBasketChild(zon_ticket);
                                      basket_child.setName_row(name_row_ticket);
                                      basket_child.setRowBasketChild(row_ticket);
                                      basket_child.setPlaceBasketChild(place_ticket);
                                      basket_child.setPriceBasketChild(price_ticket);
                                      basket.getBasket_childArrayList().add(basket_child);

                                  }while (cursorTicket.moveToNext());
                                }
                            }
                            cursorTicket.close();
                        }
                    basketsParent.add(basket);

                }while (cursorEvent.moveToNext());
            }
            cursorEvent.close();
        }
        db_ticket.close();
        setPrice();
        return basketsParent;
    }

    private JSONArray getIdTickets() {

        JSONArray jsonArray = new JSONArray();
        db = db_ticket.getWritableDatabase();
        Cursor cursorIdTickets = db.query("Ticket_table",new String[]{"id_ticket"},null,null,null,null,null,null);
        if (cursorIdTickets != null && cursorIdTickets.getCount() > 0){
            cursorIdTickets.moveToFirst();
            do {
                jsonArray.put(cursorIdTickets.getString(0));
            }while (cursorIdTickets.moveToNext());
            cursorIdTickets.close();
        }
        db.close();
        return jsonArray;
    }

    private void showDialogSoldTicket(final JSONArray jsonArrays){

        final AlertDialog.Builder alBuilder = new AlertDialog.Builder(this.getActivity());
        alBuilder.setTitle("Увага!");
        alBuilder.setMessage("Деякі з обраних вами квитків вже зайняті!");
        alBuilder.setCancelable(false);
        alBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteSoldTickets(jsonArrays);
                if (((MainActivity)getActivity()).getCountTicket().equals("0")){
                    closeFragment();
                }
                dialog.cancel();
            }
        });
        alBuilder.show();
    }

    private void deleteSoldTickets(JSONArray jsonArraySoldTickets){
        try {
            for (int i=0; i < jsonArraySoldTickets.length(); i++){
                db_ticket = new DB_Ticket(getActivity(),5);
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

                basketArrayList.clear();
                basketArrayList = addTicket();
                loadHosts(basketArrayList);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadHosts(final ArrayList<Basket> newBasket) {
        viewBasketAdapter = new ViewBasketAdapter(this.getActivity(),newBasket);
        listViewBasketTicket.setAdapter(viewBasketAdapter);
        viewBasketAdapter.notifyDataSetChanged();
    }

    private void setPrice() {
            textViewCountTicket.setText(ticketCount.getNumEnding(String.valueOf(tickets)));
            textViewPrice.setText(String.valueOf(price));
            textViewTicket.setText(String.valueOf(tickets));
    }

    public void Price(String price) {
        this.tickets --;
        this.price = this.price - Integer.parseInt(price);
        textViewCountTicket.setText(ticketCount.getNumEnding(String.valueOf(tickets)));
        textViewTicket.setText(String.valueOf(tickets));
        textViewPrice.setText(String.valueOf(this.price));
        if (tickets == 0){
            closeFragment();
        }
    }

    private void closeFragment(){
        ((MainActivity)getActivity()).showAlertDialog();
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    private void showDialog() {

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View viewLayout = layoutInflater.inflate(R.layout.fragment_dialog, null);
        alBuilder.setTitle("Увійдіть або зареєструйтесь.");
        alBuilder.setView(viewLayout);
        final Dialog alertDialog = alBuilder.create();
        Button buttonLogin = (Button)viewLayout.findViewById(R.id.button3);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "login", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                startLoginActivity(0);
            }
        });
        Button buttonCreateAccount = (Button)viewLayout.findViewById(R.id.button4);
        buttonCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "CreateLogin", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
                startLoginActivity(1);
            }
        });
        TextView textViewHideDialog = (TextView)viewLayout.findViewById(R.id.textView4);
        textViewHideDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Value from checkBox
                CheckBox checkBox = (CheckBox) viewLayout.findViewById(R.id.checkBoxDialog);
                checkShowDialog(checkBox.isChecked());

                alertDialog.dismiss();
                checkFreeTickets();
            }
        });

        alertDialog.show();
    }

    private void startLoginActivity(int position){
        Intent i = new Intent(getActivity(), LoginActivity.class);
        i.putExtra("POSITION", position);
        startActivity(i);
    }

    private void checkShowDialog(Boolean statusBox){
        if (statusBox){
            SharedPreferences.Editor editor = spShowDialog.edit();
            editor.putBoolean("show_dialog",false);
            editor.apply();
        }
    }

    private void startService(){
        ((MainActivity)getActivity()).startTimer();
    }

    @Override
    public void onDestroyView () {
        ((MainActivity) getActivity()).setItemChecked(2,false);
        super.onDestroyView();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        Toast.makeText(getActivity().getApplicationContext(), "From LocKasaFragment onBackPressed", Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Basket> addDataBasket() {
        final ArrayList<Basket> basketsParent = new ArrayList<Basket>();
        final Basket basket = new Basket();
        //basket.setUrl_img("http://kasa.in.ua/images/event/1282_l.jpg");
        basket.setNameBasket("Basta");
        basket.setCityBasket("lviv");
        basket.setDate("24.03.2015");
        basket.setTimeBasket("20:00");
        basket.setBasket_childArrayList(new ArrayList<Basket_Child>());


        final Basket_Child basket_child = new Basket_Child();
        basket_child.setNameBasketChild("parter");
        basket_child.setRowBasketChild("2");
        basket_child.setPlaceBasketChild("12");
        basket_child.setPriceBasketChild("200");
        basket.getBasket_childArrayList().add(basket_child);

        final Basket_Child basket_child1 = new Basket_Child();
        basket_child1.setNameBasketChild("parter1");
        basket_child1.setRowBasketChild("1");
        basket_child1.setPlaceBasketChild("12");
        basket_child1.setPriceBasketChild("200");
        basket.getBasket_childArrayList().add(basket_child1);
        basketsParent.add(basket);

        final Basket baskett = new Basket();
        //baskett.setUrl_img("http://kasa.in.ua/images/event/1282_l.jpg");
        baskett.setNameBasket("Basta1");
        baskett.setCityBasket("lviv");
        baskett.setDate("24.03.2015");
        baskett.setTimeBasket("20:00");
        baskett.setBasket_childArrayList(new ArrayList<Basket_Child>());

        final Basket_Child basket_child2 = new Basket_Child();
        basket_child2.setNameBasketChild("parter2");
        basket_child2.setRowBasketChild("2");
        basket_child2.setPlaceBasketChild("12");
        basket_child2.setPriceBasketChild("200");
        baskett.getBasket_childArrayList().add(basket_child2);

        final Basket_Child basket_child3 = new Basket_Child();
        basket_child3.setNameBasketChild("parter2");
        basket_child3.setRowBasketChild("1");
        basket_child3.setPlaceBasketChild("12");
        basket_child3.setPriceBasketChild("200");
        baskett.getBasket_childArrayList().add(basket_child3);
        basketsParent.add(baskett);

        return basketsParent;
    }

}
