package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

import DataBase.DB_Ticket;
import Singleton.UserProfileSingleton;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;


//Created by Zhenya on 17.02.2015.

public class FragmentDeliveryOrder extends Fragment implements OnBackPressedListener {

    private RadioGroup radioGroupDeliveryMethod;

    private RadioGroup radioGroupKasa;
    private RadioGroup radioGroupNewPost;
    private RadioGroup radioGroupCourier;
    private RadioGroup radioGroupE_ticket;
    private RadioGroup radioGroup;

    private RadioButton radioButton;

    private Button buttonContinue;
    private TextView textViewDeliveryMethod;
    private TextView textViewTimer;

    private int lastIdRadioGroup  = -1;
    private Animation anim;

    private DB_Ticket db_ticket;
    private SQLiteDatabase db;

    private SparseArray<Fragment> fragmentSparseArray;

    private CountDownTimer countDownTimer;

    private FragmentManager fragmentManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_delivery_order, conteiner, false);
        db_ticket = new DB_Ticket(getActivity(),5);

        fragmentManager = getActivity().getFragmentManager();
        radioGroupKasa = (RadioGroup) rootView.findViewById(R.id.radioGroupKasa);
        radioGroupNewPost = (RadioGroup) rootView.findViewById(R.id.radioGroupNewPost);
        radioGroupCourier = (RadioGroup) rootView.findViewById(R.id.radioGroupCourier);
        radioGroupE_ticket = (RadioGroup) rootView.findViewById(R.id.radioGroupE_ticket);
        fragmentSparseArray = new SparseArray<>();
        fragmentSparseArray.put(radioGroupKasa.getId(), new FragmentUserDataKasa());
        fragmentSparseArray.put(radioGroupNewPost.getId(), new UserDataPost());
        fragmentSparseArray.put(radioGroupCourier.getId(), new UserDataCourier());
        fragmentSparseArray.put(radioGroupE_ticket.getId(), new UserDataETicket());
        textViewDeliveryMethod = (TextView) rootView.findViewById(R.id.textViewDeliveryMethod);
        textViewTimer = (TextView) rootView.findViewById(R.id.textView63);
        buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);
        radioGroupDeliveryMethod = (RadioGroup) rootView.findViewById(R.id.radioGroupDeliveryMethod);
        getPaymentMethod();
        if (getArguments() != null) {
            setCheckedDeliveryOrder(getArguments());
        }
        radioGroupDeliveryMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (textViewDeliveryMethod.getVisibility() == View.GONE) {
                    textViewDeliveryMethod.setVisibility(View.VISIBLE);
                    Animation anim2 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                    textViewDeliveryMethod.setAnimation(anim2);
                }

                if (buttonContinue.getVisibility() == View.GONE) {
                    buttonContinue.setVisibility(View.VISIBLE);
                }

                if (lastIdRadioGroup != -1) {
                    radioGroup = (RadioGroup) rootView.findViewById(lastIdRadioGroup);
                    Log.i("visible", String.valueOf(radioGroupKasa.getVisibility()));
                    radioGroup.clearCheck();
                    radioGroup.setVisibility(View.GONE);
                    Log.i("visible", String.valueOf(radioGroupKasa.getVisibility()));
                    Animation anim1 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_out);
                    radioGroup.setAnimation(anim1);
                }

                switch (i) {
                    case R.id.radioButtonKasa:

                        radioGroupKasa.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupKasa.setAnimation(anim);
                        lastIdRadioGroup = radioGroupKasa.getId();

                        break;


                    case R.id.radioButtonNewPost:

                        radioGroupNewPost.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupNewPost.setAnimation(anim);
                        lastIdRadioGroup = radioGroupNewPost.getId();

                        break;


                    case R.id.radioButtonCourier:

                        radioGroupCourier.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupCourier.setAnimation(anim);
                        lastIdRadioGroup = radioGroupCourier.getId();

                        break;

                    case R.id.radioButtonE_ticket:

                        radioGroupE_ticket.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupE_ticket.setAnimation(anim);
                        lastIdRadioGroup = radioGroupE_ticket.getId();

                        break;
                }
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lastIdRadioGroup != -1) {
                    radioGroup = (RadioGroup) rootView.findViewById(lastIdRadioGroup);

                    if (radioGroup.getCheckedRadioButtonId() != -1) {

                        radioButton = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());
                        Fragment fragment = fragmentSparseArray.get(radioGroup.getId());
                        Bundle bundleType = new Bundle();
                        bundleType.putString("type", String.valueOf(radioButton.getText()));
                        bundleType.putInt("delivery_method", radioGroupDeliveryMethod.getCheckedRadioButtonId());
                        bundleType.putInt("payment_button_id",radioButton.getId());
                        bundleType.putInt("payment_method", Integer.parseInt(radioButton.getTag().toString()));
                        fragment.setArguments(bundleType);
                        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
                        //Toast.makeText(getActivity().getApplicationContext(), String.valueOf(radioButton.getText()), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.page_delivery_chose_delivery_method), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

        startService();

        return rootView;
    }

    @Override
    public void onBackPressed() {

        ((ActivityOrder)getActivity()).showAlertDialogCancelOrder();

    }

    private void startService(){

        long timer = ((ActivityOrder)getActivity()).getTimer();
        if (timer != 0){
            countDownTimer = new CountDownTimer(timer,1000) {

            @Override
            public void onTick(long millis) {
                int seconds = (int) (millis / 1000) % 60 ;
                int minutes = (int) ((millis / (1000*60)) % 60);

                String text = String.format("%02d : %02d",minutes,seconds);
                textViewTimer.setText(text);
            }

            @Override
            public void onFinish() {
                textViewTimer.setText(getResources().getString(R.string.page_delivery_order_canceled));
            }
        }.start();
        }
    }

    private void setCheckedDeliveryOrder(Bundle bundle){

        radioGroupDeliveryMethod.check(bundle.getInt("delivery_method"));
        textViewDeliveryMethod.setVisibility(View.VISIBLE);
        buttonContinue.setVisibility(View.VISIBLE);
        switch (bundle.getInt("delivery_method")){
            case R.id.radioButtonKasa:

                radioGroupKasa.setVisibility(View.VISIBLE);
                radioGroupKasa.check(bundle.getInt("payment_button_id"));
                lastIdRadioGroup = radioGroupKasa.getId();

                break;


            case R.id.radioButtonNewPost:

                radioGroupNewPost.setVisibility(View.VISIBLE);
                radioGroupNewPost.check(bundle.getInt("payment_button_id"));
                lastIdRadioGroup = radioGroupNewPost.getId();

                break;


            case R.id.radioButtonCourier:

                radioGroupCourier.setVisibility(View.VISIBLE);
                radioGroupCourier.check(bundle.getInt("payment_button_id"));
                lastIdRadioGroup = radioGroupCourier.getId();

                break;

            case R.id.radioButtonE_ticket:

                radioGroupE_ticket.setVisibility(View.VISIBLE);
                radioGroupE_ticket.check(bundle.getInt("payment_button_id"));
                lastIdRadioGroup = radioGroupE_ticket.getId();

                break;
        }

    }

    private void getPaymentMethod(){
        String url = "http://tms.webclever.in.ua/api/getPaymentMethod";
        final String [] id_events = getIDEvents();
        Log.i("events", Arrays.toString(id_events));
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            Log.i("Response", s);
                            JSONArray jsonArrayPaymentsMethod = new JSONArray(s);
                            for(int i=0; i< jsonArrayPaymentsMethod.length(); i++){
                                setPaymentMethod(jsonArrayPaymentsMethod.getInt(i));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("Response_err", String.valueOf(volleyError.getMessage()));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                params.put("event_id", Arrays.toString(id_events));
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);

    }

    private void setPaymentMethod (Integer paymentMethod){
        RadioButton deliveryMethodKasa = (RadioButton) radioGroupDeliveryMethod.getChildAt(0);
        RadioButton deliveryMethodPost = (RadioButton) radioGroupDeliveryMethod.getChildAt(1);
        RadioButton deliveryMethodCourier = (RadioButton) radioGroupDeliveryMethod.getChildAt(2);
        RadioButton deliveryMethodTicketOnLine = (RadioButton) radioGroupDeliveryMethod.getChildAt(3);
        switch (paymentMethod){
            case 1:
                if (deliveryMethodKasa.getVisibility() == View.GONE){
                    deliveryMethodKasa.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodKasaPay = (RadioButton) radioGroupKasa.getChildAt(0);
                if (paymentMethodKasaPay.getVisibility() == View.GONE){
                    paymentMethodKasaPay.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (deliveryMethodKasa.getVisibility() == View.GONE){
                    deliveryMethodKasa.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodKasaOnLine = (RadioButton) radioGroupKasa.getChildAt(1);
                if (paymentMethodKasaOnLine.getVisibility() == View.GONE){
                    paymentMethodKasaOnLine.setVisibility(View.VISIBLE);
                }

                break;
            case 3:
                if (deliveryMethodPost.getVisibility() == View.GONE){
                    deliveryMethodPost.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodNewPostPay = (RadioButton) radioGroupNewPost.getChildAt(0);
                if (paymentMethodNewPostPay.getVisibility() == View.GONE){
                    paymentMethodNewPostPay.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                if (deliveryMethodPost.getVisibility() == View.GONE){
                    deliveryMethodPost.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodNewPostOnLine = (RadioButton) radioGroupNewPost.getChildAt(1);
                if (paymentMethodNewPostOnLine.getVisibility() == View.GONE){
                    paymentMethodNewPostOnLine.setVisibility(View.VISIBLE);
                }
                break;
            case 5:
                if (deliveryMethodCourier.getVisibility() == View.GONE){
                    deliveryMethodCourier.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodCourierPay = (RadioButton) radioGroupCourier.getChildAt(0);
                if (paymentMethodCourierPay.getVisibility() == View.GONE){
                    paymentMethodCourierPay.setVisibility(View.VISIBLE);
                }
                break;
            case 6:
                if (deliveryMethodCourier.getVisibility() == View.GONE){
                    deliveryMethodCourier.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodCourierOnLine = (RadioButton) radioGroupCourier.getChildAt(1);
                if (paymentMethodCourierOnLine.getVisibility() == View.GONE){
                    paymentMethodCourierOnLine.setVisibility(View.VISIBLE);
                }
                break;
            case 8:
                if (deliveryMethodTicketOnLine.getVisibility() == View.GONE){
                    deliveryMethodTicketOnLine.setVisibility(View.VISIBLE);
                }
                RadioButton paymentMethodTicketOnline = (RadioButton) radioGroupE_ticket.getChildAt(0);
                if (paymentMethodTicketOnline.getVisibility() == View.GONE){
                    paymentMethodTicketOnline.setVisibility(View.VISIBLE);
                }
                break;
        }

    }

    private String[] getIDEvents(){
        String[] masIdEvents = null;
        db = db_ticket.getReadableDatabase();
        Cursor cursorSelectedPlace =  db.query("Event_table",new String[]{"id_event"},null,null,null,null,null,null);
        if (cursorSelectedPlace != null){
            if (cursorSelectedPlace.getCount() > 0){
                cursorSelectedPlace.moveToFirst();
                masIdEvents = new String[cursorSelectedPlace.getCount()];
                for (int i=0; i < cursorSelectedPlace.getCount(); i++){
                    masIdEvents[i] = cursorSelectedPlace.getString(0);
                    Log.i("event_id",cursorSelectedPlace.getString(0));
                    cursorSelectedPlace.moveToNext();
                }
            }
        }
        assert cursorSelectedPlace != null;
        cursorSelectedPlace.close();
        db_ticket.close();
        return masIdEvents;
    }

    @Override
    public void onDestroy (){
        super.onDestroy();
        stopTimer();
    }

    public void stopTimer(){
        if (countDownTimer != null){
            countDownTimer.cancel();
        }
    }

}
