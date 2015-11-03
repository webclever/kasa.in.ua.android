package webclever.sliding_menu;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import DataBase.DB_Ticket;
import Singleton.UserProfileSingleton;
import Singleton.ticket_name;
import Validator.Validator;
import interfaces.OnBackPressedListener;


public class UserDataETicket extends Fragment implements OnBackPressedListener {

    private List<ticket_name> ticket_nameList;
    private DB_Ticket db_ticket;
    private SQLiteDatabase db;
    private Validator validator = new Validator();
    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private UserProfileSingleton userProfile;
    private LinearLayout linearLayoutContainer;
    private LayoutInflater layoutInflater;
    private ViewGroup viewGroupTicketContainer;
    private TextView textViewTimer;
    private FragmentManager fragmentManager;
    private Bundle bundle;

    private Integer paymentMethod;

    public UserDataETicket() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user_data_eticket, container, false);

        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }
        fragmentManager = getFragmentManager();
        userProfile = new UserProfileSingleton(this.getActivity());
        db_ticket = new DB_Ticket(getActivity(),5);
        ticket_nameList = new ArrayList<>();
        viewGroupTicketContainer = (ViewGroup) rootView.findViewById(R.id.containerETicket);
        layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        linearLayoutContainer = (LinearLayout) layoutInflater.inflate(R.layout.list_layout_container, viewGroupTicketContainer, false);
        addTicket();
        viewGroupTicketContainer.addView(linearLayoutContainer, 0);
        textViewTimer = (TextView) rootView.findViewById(R.id.textView102);


        EditText editTextName = (EditText) rootView.findViewById(R.id.editText11);
        editTextName.setText(userProfile.getName());
        editTextName.addTextChangedListener(new TextWatcherETicket(editTextName));
        sparseBooleanArray.put(editTextName.getId(), validator.isNameValid(userProfile.getName()));

        EditText editTextLasName = (EditText) rootView.findViewById(R.id.editText12);
        editTextLasName.setText(userProfile.getLastName());
        editTextLasName.addTextChangedListener(new TextWatcherETicket(editTextLasName));
        sparseBooleanArray.put(editTextLasName.getId(), validator.isLastNameValid(userProfile.getLastName()));

        EditText editTextPhone = (EditText) rootView.findViewById(R.id.editText13);
        if (!userProfile.getPhone().equals("")){
            editTextPhone.setText(userProfile.getPhone());
        }else {
            editTextPhone.setText("+38");
        }
        editTextPhone.addTextChangedListener(new TextWatcherETicket(editTextPhone));
        sparseBooleanArray.put(editTextPhone.getId(), validator.isPhoneValid(userProfile.getPhone()));

        EditText editTextEMail = (EditText) rootView.findViewById(R.id.editText14);
        editTextEMail.setText(userProfile.getEmail());
        editTextEMail.addTextChangedListener(new TextWatcherETicket(editTextEMail));
        sparseBooleanArray.put(editTextEMail.getId(),validator.isEmailValid(userProfile.getEmail()));

        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);

        switch (paymentMethod) {
            case 7:
                buttonConfirm.setText("перейти до оплати");
                break;
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (getValidUserData() && getValidUserDataTicket()){
                    Fragment fragment = new FragmentSuccessfulOrder();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                }


            }
        });
        startService();
        return rootView;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new FragmentDeliveryOrder();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    private void addTicket() {
        db = db_ticket.getWritableDatabase();
        Cursor cursorEvent = db.query("Event_table", null, null, null, null, null, null);
        if (cursorEvent != null)
        {
            if (cursorEvent.moveToFirst())
            {
                int id_event = cursorEvent.getColumnIndex("id_event");
                int name_event = cursorEvent.getColumnIndex("name_event");

                do {
                    Log.i("id_event_basket", String.valueOf(id_event));


                    int id_event_basket = cursorEvent.getInt(id_event);

                    Cursor cursorTicket = db.query("Ticket_table",new String[]{"id_ticket","zon_ticket","name_row_ticket","row_ticket","place_ticket","price_ticket","id_event"},"id_event="+String.valueOf(id_event_basket),null,null,null,null,null);
                    if (cursorTicket != null)
                    {
                        if (cursorTicket.getCount() > 0)
                        {
                            if (cursorTicket.moveToFirst())
                            {
                                do {
                                    ticket_name tickets = new ticket_name();
                                    tickets.setId_event(id_event);
                                    tickets.setName_event(cursorEvent.getString(name_event));
                                    String id_ticket = cursorTicket.getString(0);
                                    String zon_ticket = cursorTicket.getString(1);
                                    String name_row_ticket = cursorTicket.getString(2);
                                    String row_ticket = cursorTicket.getString(3);
                                    String place_ticket = cursorTicket.getString(4);
                                    String price_ticket = cursorTicket.getString(5);
                                    String id_eventt = cursorTicket.getString(6);

                                    tickets.setId_event(Integer.parseInt(id_eventt));
                                    tickets.setId_ticket(Integer.parseInt(id_ticket));
                                    tickets.setSector(zon_ticket);
                                    tickets.setName_row(name_row_ticket);
                                    tickets.setRow(row_ticket);
                                    tickets.setPlace(place_ticket);
                                    tickets.setPrice(price_ticket);
                                    addTicketContainer(cursorEvent.getString(name_event),zon_ticket,
                                            name_row_ticket,row_ticket,place_ticket,price_ticket);
                                    ticket_nameList.add(tickets);

                                }while (cursorTicket.moveToNext());
                            }
                        }
                        cursorTicket.close();
                    }


                }while (cursorEvent.moveToNext());

            }
            cursorEvent.close();
        }
        db_ticket.close();

    }

    private void addTicketContainer(String name_event,String zon_ticket,String name_row_ticket,String row_ticket,String place_ticket,String price_ticket){
        final ViewGroup viewGroupTicket = (ViewGroup) LayoutInflater.from(this.getActivity()).inflate(R.layout.list_ticket_data,viewGroupTicketContainer,false);
        String str = name_row_ticket+": " + row_ticket + ", м.:" + String.valueOf(place_ticket);
        TextView textViewNameEvent = (TextView) viewGroupTicket.findViewById(R.id.textViewNameEvent);
        TextView textViewSectorEvent = (TextView) viewGroupTicket.findViewById(R.id.textViewSectorEvent);
        TextView textViewRowPlace = (TextView) viewGroupTicket.findViewById(R.id.textViewRowPlace);
        TextView textViewPriceTicket = (TextView) viewGroupTicket.findViewById(R.id.textViewPriceTicket);

        textViewNameEvent.setText(name_event);
        textViewSectorEvent.setText(zon_ticket);
        textViewRowPlace.setText(str);
        textViewPriceTicket.setText(price_ticket + "грн.");

        viewGroupTicketContainer.addView(viewGroupTicket,0);

    }

    private class TextWatcherETicket implements TextWatcher {

        private View view;
        public TextWatcherETicket(View view)
        {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            switch (view.getId())
            {
                case R.id.editText11:
                    sparseBooleanArray.put(R.id.editText11,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText12:
                    sparseBooleanArray.put(R.id.editText12,validator.isLastNameValid(editable.toString()));
                    break;
                case R.id.editText13:
                    sparseBooleanArray.put(R.id.editText13,validator.isPhoneValid(editable.toString()));
                    break;
                case R.id.editText14:
                    sparseBooleanArray.put(R.id.editText14,validator.isEmailValid(editable.toString()));
                    break;
            }

        }
    }

    private Boolean getValidUserData() {
        Boolean valid = true;

        for(int i=0; i < sparseBooleanArray.size(); i++)
        {
            if (sparseBooleanArray.valueAt(i))
            {
                EditText editText = (EditText)getActivity().findViewById(sparseBooleanArray.keyAt(i));
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }else {
                valid = false;
                EditText editText = (EditText)getActivity().findViewById(sparseBooleanArray.keyAt(i));
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            }
        }

        return valid;
    }

    private Boolean getValidUserDataTicket() {
        Boolean valid = true;
        for(int i=1; i < viewGroupTicketContainer.getChildCount(); i++)
        {
            View view1 = viewGroupTicketContainer.getChildAt(i);
            EditText editTextNameUserTicket = (EditText) view1.findViewById(R.id.editTextNameUser);
            EditText editTextLasNameUserTicket = (EditText) view1.findViewById(R.id.editTextLastName);
            String strUserName = editTextNameUserTicket.getText().toString();
            String strLastUserName = editTextLasNameUserTicket.getText().toString();
            if (!validator.isNameValid(strUserName)){
                valid = false;
                editTextNameUserTicket.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            }else{
                editTextNameUserTicket.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }
            if (!validator.isLastNameValid(strLastUserName)){
                editTextLasNameUserTicket.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                valid = false;
            }else{
                editTextLasNameUserTicket.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }
        }
        return valid;
    }

    private void startService() {

        long timer = ((MainActivity)getActivity()).getTimer();
        if (timer != 0){
            new CountDownTimer(timer,1000) {

                @Override
                public void onTick(long millis) {
                    int seconds = (int) (millis / 1000) % 60 ;
                    int minutes = (int) ((millis / (1000*60)) % 60);

                    String text = String.format("%02d : %02d",minutes,seconds);
                    textViewTimer.setText(text);

                }

                @Override
                public void onFinish() {
                    textViewTimer.setText("Бронювання скасоване !");
                    showAlertDialog();
                }
            }.start();
        }
    }

    private void showAlertDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("На жаль, відведений час на оформлення замовлення завершився і тимчасове замовлення було скасовано.");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new FragmentBasket();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, "1").commit();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

}
