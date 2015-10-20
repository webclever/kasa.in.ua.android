package webclever.sliding_menu;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;



import java.util.ArrayList;

import DataBase.DB_Ticket;
import Format.EncodingTicketCount;
import adapter.Basket;
import adapter.Basket_Child;
import adapter.ViewBasketAdapter;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by User on 13.08.2014.
 */
public class PhotosFragment extends Fragment implements OnBackPressedListener {

    public PhotosFragment(){setHasOptionsMenu(true);}
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

    private static final String APP_PREFERENCES_DIALOG = "dialog_show";
    private SharedPreferences spShowDialog;



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup conteiner,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_photos,conteiner,false);
        ((MainActivity)getActivity()).setItemChecked(2,true);
        spShowDialog = getActivity().getSharedPreferences(APP_PREFERENCES_DIALOG, Context.MODE_PRIVATE);
        listViewBasketTicket = (ListView) rootView.findViewById(R.id.listViewTicketBasket);
        mButton = (Button) rootView.findViewById(R.id.buttonBasket);
        textViewEmptyCart = (TextView) rootView.findViewById(R.id.textViewEmptyCart);
        //mButton.setEnabled(false);
        //mButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
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
                    Fragment fragment = new Fragment_Setings();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(frame_container, fragment).commit();

                } else {
                    if (spShowDialog.getBoolean("show_dialog",true)){
                    showDialog();
                    }else {
                        Fragment fragment = new Fragment_Setings();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
                    }
                }
                //deleteDB();
            }
        });
        return  rootView;
    }

    public void deleteDB() {
        db = db_ticket.getWritableDatabase();
        int rows = db.delete("Ticket_table", null, null);
        Log.i("id_ticket","del rows" + String.valueOf(rows));

        rows = db.delete("Event_table", null, null);
        Log.i("id_ticket", "del rows" + String.valueOf(rows));

        db_ticket.close();
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

    private ArrayList<Basket> addTicket() {
        final ArrayList<Basket> basketsParent = new ArrayList<Basket>();

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
                                      textViewTicket.setText("125");
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

    private void loadHosts(final ArrayList<Basket> newBasket) {
        viewBasketAdapter = new ViewBasketAdapter(this.getActivity(),newBasket);
        listViewBasketTicket.setAdapter(viewBasketAdapter);
        viewBasketAdapter.notifyDataSetChanged();

    }

    private void setPrice() {
        if (tickets > 0 ){
        textViewCountTicket.setText(ticketCount.getNumEnding(String.valueOf(tickets)));
        textViewPrice.setText(String.valueOf(price));
        textViewTicket.setText(String.valueOf(tickets));
        }
    }

    public void Price(String price) {
        this.tickets --;
        this.price = this.price - Integer.parseInt(price);
        textViewCountTicket.setText(ticketCount.getNumEnding(String.valueOf(tickets)));
        textViewTicket.setText(String.valueOf(tickets));
        textViewPrice.setText(String.valueOf(this.price));
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
        Toast.makeText(getActivity().getApplicationContext(), "From LocKasaFragment onBackPressed", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getActivity(), "Login", Toast.LENGTH_SHORT).show();
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
                Fragment fragment = new Fragment_Setings();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
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

    @Override
    public void onDestroyView ()
    {
        ((MainActivity)getActivity()).setItemChecked(2,false);
        super.onDestroyView();
    }

}
