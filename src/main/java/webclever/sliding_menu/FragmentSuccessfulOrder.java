package webclever.sliding_menu;


import android.app.FragmentManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import DataBase.DB_Ticket;
import interfaces.OnBackPressedListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSuccessfulOrder extends Fragment implements OnBackPressedListener {

    private DB_Ticket db_ticket;

    public FragmentSuccessfulOrder() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_successful_order, container, false);
        // Inflate the layout for this fragment
        Button button = (Button) rootView.findViewById(R.id.buttonConfirmOrder);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
        });
        db_ticket = new DB_Ticket(getActivity(),5);
        deleteDB();

        return rootView;
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
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
