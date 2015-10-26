package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;


//Created by User on 13.08.2014.

public class WhatsHotFragment extends Fragment implements OnBackPressedListener {
    public WhatsHotFragment(){setHasOptionsMenu(true);}

    //private DB_Ticket db_ticket;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_whatshot,container,false);
        ((MainActivity)getActivity()).setItemChecked(6,true);
        //db_ticket = new DB_Ticket(getActivity(),5);
        Calendar calendar = Calendar.getInstance();

        TextView textViewRightsReserved = (TextView) rootView.findViewById(R.id.prava);
        textViewRightsReserved.setText("Всі права захищеноі. kassa.in.ua © " + String.valueOf(calendar.get(Calendar.YEAR)));
        TextView version_App = (TextView) rootView.findViewById(R.id.version_program);
        PackageInfo pInfo = null;
        try {
            pInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert pInfo != null;
        String version = "Версія " + pInfo.versionName;
        version_App.setText(version);

        return rootView;
    }

    /*private void deleteDB(){

        SQLiteDatabase db = db_ticket.getWritableDatabase();
            int rows = db.delete("Ticket_table", null, null);
            Log.i("id_ticket", "del rows" + String.valueOf(rows));

            rows = db.delete("Event_table", null, null);
            Log.i("id_ticket","del rows" + String.valueOf(rows));

            db_ticket.close();
    }*/

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
                Fragment fragment = new PhotosFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());
        super.onCreateOptionsMenu(menu, inflater);}
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
        ((MainActivity)getActivity()).setItemChecked(6, false);
        super.onDestroyView();
    }

}
