package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by Admin on 13.05.2015.
 */
public class fragment_NumberOrder extends Fragment {

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup conteiner,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_result, conteiner, false);
        /**return to home fragment*/
        ((Button)rootView.findViewById(R.id.Main)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id",0);
                Fragment fragment = new HomeFragment();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
            }
        });

        ((Button)rootView.findViewById(R.id.Locations)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id",0);
                Fragment fragment = new LocKasaFragment();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
            }
        });




        return rootView;
    }


}
