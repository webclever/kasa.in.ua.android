package adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Singleton.SingletonCity;
import webclever.sliding_menu.R;

/**
 * Created by Zhenya on 10.06.2015.
 */
public class ObjectSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

    private List<SingletonCity> mItems = new ArrayList<>();
    private Activity activity;
    private LayoutInflater inflater;
    public ObjectSpinnerAdapter(Activity activity,List<SingletonCity> mItems)
    {
        this.activity = activity;
        this.mItems = mItems;
    }


    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
        if (convertView == null)
            convertView = inflater.inflate(R.layout.toolbar_spinner_item_actionbar, null);
        SingletonCity singletonCity = mItems.get(position);
        TextView textViewNameCity = (TextView) convertView.findViewById(R.id.namecity);
        textViewNameCity.setText(singletonCity.getNameCity());

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {

        if (inflater == null){
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);}
        if (convertView == null)
            convertView = inflater.inflate(R.layout.toolbar_spinner_item_dropdown, null);
        SingletonCity singletonCity = mItems.get(position);
        TextView textViewNameCity = (TextView) convertView.findViewById(R.id.namecity2);
        textViewNameCity.setText(singletonCity.getNameCity());

        return convertView;
    }

}
