package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

/**
 * Created by User on 13.08.2014.
 */
public class WhatsHotFragment extends Fragment implements OnBackPressedListener {
    public WhatsHotFragment(){setHasOptionsMenu(true);}

    //private TextView versinApp;
    private SQLiteDatabase db;
    private DB_Ticket db_ticket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_whatshot,container,false);
        ((MainActivity)getActivity()).setItemChecked(5,true);
        db_ticket = new DB_Ticket(getActivity(),5);
        Button buttonResponse = (Button) rootView.findViewById(R.id.buttonSend);
        buttonResponse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDB();
            }
        });

        return rootView;
    }
    private void deleteDB(){

            db = db_ticket.getWritableDatabase();
            int rows = db.delete("Ticket_table", null, null);
            Log.i("id_ticket", "del rows" + String.valueOf(rows));

            rows = db.delete("Event_table",null,null);
            Log.i("id_ticket","del rows" + String.valueOf(rows));

            db_ticket.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);
        MenuItem item = menu.findItem(R.id.menuCount);
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PhotosFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
        Toast.makeText(getActivity().getApplicationContext(), "From LocKasaFragment onBackPressed", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDestroyView ()
    {
        ((MainActivity)getActivity()).setItemChecked(5, false);
        super.onDestroyView();
    }

}
