package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import Singleton.SingletonCity;
import webclever.sliding_menu.R;

/**
 * Created by White on 09.11.2015.
 */
public class AdapterSelectCity extends BaseAdapter {

    private Activity activity;
    private ArrayList<SingletonCity> singletonCityArrayList;
    private LayoutInflater inflater;

    public AdapterSelectCity(Activity activity, ArrayList<SingletonCity> singletonCityArrayList){
        this.activity = activity;
        this.singletonCityArrayList = singletonCityArrayList;
    }

    @Override
    public int getCount() {
        return singletonCityArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return singletonCityArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, null);
        SingletonCity singletonCity = singletonCityArrayList.get(position);
        TextView textView = (TextView) convertView.findViewById(R.id.namecity2);
        textView.setText(singletonCity.getNameCity());
        return convertView;
    }
}
