package adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;

import webclever.sliding_menu.FragmentMap;
import webclever.sliding_menu.HomeFragment;
import webclever.sliding_menu.LocKasaFragment;
import webclever.sliding_menu.R;

/**
 * Created by Admin on 18.02.2015.
 */
public class LocKasaAdapter extends BaseAdapter  {

    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<Loc_Kasa_Singelton> Kasa_Singelton;
    private Context mContext;
    private String nameCity;


    public LocKasaAdapter(Context context, Activity activity, List<Loc_Kasa_Singelton> Kasa_Singelton)
    {
        this.activity = activity;
        this.mContext = context;
        this.Kasa_Singelton = Kasa_Singelton;

    }

    @Override
    public int getCount ()
    {
        return Kasa_Singelton.size();

    }

    @Override
    public Object getItem(int position)
    {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if(layoutInflater == null)
        {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.list_kasa_adress, null);
        }

        TextView address = (TextView) convertView.findViewById(R.id.name_kasa);
        TextView nameKasa = (TextView) convertView.findViewById(R.id.address_txt_name);
        TextView description = (TextView) convertView.findViewById(R.id.description_kasa);
        TextView time_work = (TextView) convertView.findViewById(R.id.time_work_kasa);
        TextView advertisement = (TextView) convertView.findViewById(R.id.textViewadvertisement);
        LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.MyylinearLeyout);

        final Loc_Kasa_Singelton loc_kasa_singelton = Kasa_Singelton.get(position);

        address.setText(loc_kasa_singelton.getAddress());
        nameKasa.setText(loc_kasa_singelton.getName());
        description.setText(loc_kasa_singelton.getDescription());
        time_work.setText(Html.fromHtml(loc_kasa_singelton.getTime_work()));
        advertisement.setText(loc_kasa_singelton.getAdvertisement());

        linearLayout.setClickable(true);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("click","position:"+String.valueOf(position));
                /**Inizialization Fragment Class*/
                //LocKasaFragment locKasaFragment = (LocKasaFragment)activity.getFragmentManager().findFragmentById(R.id.frame_container);
                //LocKasaFragment fragmentt = (LocKasaFragment) activity.getFragmentManager().findFragmentById(R.id.frame_container);
                //nameCity = fragmentt.getNameCity();
                //Log.i("nameCity: adapter", nameCity);
                Bundle myBundle = new Bundle();
                //myBundle.putString("nameCity",nameCity);
                myBundle.putString("name",loc_kasa_singelton.getName());
                myBundle.putString("address",loc_kasa_singelton.getAddress());
                myBundle.putString("description",loc_kasa_singelton.getDescription());
                myBundle.putString("time_work",loc_kasa_singelton.getTime_work());
                myBundle.putString("notification",loc_kasa_singelton.getNotification());
                myBundle.putDouble("latitude",loc_kasa_singelton.getLatitude());
                myBundle.putDouble("longitude",loc_kasa_singelton.getLongitude());

                Fragment fragment = new FragmentMap();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = activity.getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
            }
        });

        return convertView;

    }

}
