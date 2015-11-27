package webclever.sliding_menu;

import android.app.FragmentManager;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import DataBase.DB_Ticket;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;

public class FragmentContacts extends Fragment implements View.OnClickListener , OnBackPressedListener {

    private final String[] phoneNumberKasaInUA = {"+380930000754","+380660000754","+380970000754"};
    private DB_Ticket db_ticket;
    public FragmentContacts() {
            setHasOptionsMenu(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        if (!((MainActivity) getActivity()).getCountTicket().equals("0")){
        getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);
        MenuItem item = menu.findItem(R.id.menuCount);
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentBasket();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());
        super.onCreateOptionsMenu(menu, inflater);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setItemChecked(5,true);
        db_ticket = new DB_Ticket(getActivity(),5);
        View rootView = inflater.inflate(R.layout.fragment_fragment_contacts, container, false);
        TextView textViewKasaPhone1 = (TextView) rootView.findViewById(R.id.textView46);
        textViewKasaPhone1.setOnClickListener(this);
        TextView textViewKasaPhone2 = (TextView) rootView.findViewById(R.id.textView97);
        textViewKasaPhone2.setOnClickListener(this);
        TextView textViewKasaPhone3 = (TextView) rootView.findViewById(R.id.textView33);
        textViewKasaPhone3.setOnClickListener(this);
        TextView textViewMailKasa = (TextView) rootView.findViewById(R.id.textViewMailKasa);
        textViewMailKasa.setOnClickListener(this);
        TextView textViewWeb = (TextView) rootView.findViewById(R.id.textViewKasaInUa);
        textViewWeb.setOnClickListener(this);
        Button buttonShowAddressKassa = (Button) rootView.findViewById(R.id.button_kasa_adress);
        buttonShowAddressKassa.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.textView46:
                makeCall(0);
                break;
            case R.id.textView97:
                makeCall(1);
                break;
            case R.id.textView33:
                makeCall(2);
                break;
            case R.id.textViewMailKasa:
                sendEMail();
                break;
            case R.id.textViewKasaInUa:
                //deleteDB();
                openWebKasaInUA();
                break;
            case R.id.button_kasa_adress:
                locationKasaInUa();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
    }

    private void makeCall(Integer id) {
        Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+phoneNumberKasaInUA[id]));
        try{
            startActivity(intent);
        }catch (android.content.ActivityNotFoundException ex){
            //Toast.makeText(getActivity().getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
        }
    }

    private void sendEMail() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@kasa.in.ua"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "kasa.in.ua");
        try {
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.page_contacts_send_message)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(), getResources().getString(R.string.page_contacts_not_have_app_for_send_message), Toast.LENGTH_SHORT).show();
        }
    }

    private void openWebKasaInUA() {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kasa.in.ua"));
        startActivity(browserIntent);
    }

    private void locationKasaInUa() {
        Fragment fragment = new LocKasaFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    private void deleteDB(){

        SQLiteDatabase db = db_ticket.getWritableDatabase();
            int rows = db.delete("Ticket_table", null, null);
            Log.i("id_ticket", "del rows" + String.valueOf(rows));

            rows = db.delete("Event_table", null, null);
            Log.i("id_ticket","del rows" + String.valueOf(rows));

            db_ticket.close();
    }
}
