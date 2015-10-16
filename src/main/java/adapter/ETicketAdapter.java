package adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Singleton.ticket_name;
import Validator.Validator;
import webclever.sliding_menu.R;

/**
 * Created by White on 06.07.2015.
 */
public class ETicketAdapter extends BaseAdapter  {

    private Context context;
    private Activity activity;
    private LayoutInflater layoutInflater;
    private List<ticket_name> ticketNameList = new ArrayList<ticket_name>();
    private int lastFocussedPosition = -1;
    private Handler handler = new Handler();


    public ETicketAdapter(Context context,Activity activity, List<ticket_name> ticketNameList)
    {
        this.context = context;
        this.activity = activity;
        this.ticketNameList = ticketNameList;
    }

    @Override
    public int getCount() {
        return ticketNameList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.list_ticket_data,null);
        }

        TextView textViewNameEvent = (TextView) convertView.findViewById(R.id.textViewNameEvent);
        TextView textViewSectorEvent = (TextView) convertView.findViewById(R.id.textViewSectorEvent);
        TextView textViewRowPlace = (TextView) convertView.findViewById(R.id.textViewRowPlace);
        TextView textViewPriceTicket = (TextView) convertView.findViewById(R.id.textViewPriceTicket);
        final EditText editTextUserLastName = (EditText) convertView.findViewById(R.id.editTextNameUser);
        EditText editTextUserName = (EditText) convertView.findViewById(R.id.editTextLastName);

        editTextUserLastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (lastFocussedPosition == -1 || lastFocussedPosition == position) {
                                lastFocussedPosition = position;
                                editTextUserLastName.requestFocus();
                            }
                        }
                    }, 200);

                } else {
                    lastFocussedPosition = -1;
                }
            }
        });

        final ticket_name ticketName = ticketNameList.get(position);

        textViewNameEvent.setText(ticketName.getName_event());
        textViewSectorEvent.setText(ticketName.getSector());
        String str = "p.:" + String.valueOf(ticketName.getRow()) + ", м.:" + String.valueOf(ticketName.getPlace());
        textViewRowPlace.setText(str);
        textViewPriceTicket.setText(ticketName.getPrice() + "₴");

        return convertView;
    }



}
