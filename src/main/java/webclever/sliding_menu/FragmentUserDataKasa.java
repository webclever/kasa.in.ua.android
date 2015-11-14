package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.SingletonTempOrder;
import Singleton.UserProfileSingleton;
import Validator.Validator;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.editText;
import static webclever.sliding_menu.R.id.editTextName;
import static webclever.sliding_menu.R.id.fragments_container;
import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by Zhenya on 03.07.2015.
 */
public class FragmentUserDataKasa extends Fragment implements OnBackPressedListener {

    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private Validator validator = new Validator();
    private UserProfileSingleton userProfile;
    private TextView textViewTimer;
    private Integer paymentMethod;
    private FragmentManager fragmentManager;
    private Bundle bundle;

    private EditText editTextName;
    private EditText editTextLasName;
    private EditText editTextPhone;
    private EditText editTextEmail;

    public FragmentUserDataKasa()    { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user_data_kasa, container, false);
        fragmentManager = getFragmentManager();

        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }


        Toast.makeText(getActivity().getApplicationContext(),getArguments().getString("type"),Toast.LENGTH_SHORT).show();
        userProfile = new UserProfileSingleton(this.getActivity());
        editTextName = (EditText) rootView.findViewById(R.id.editText11);
        editTextName.setText(userProfile.getName());
        editTextName.addTextChangedListener(new TextWatcherETicket(editTextName));
        sparseBooleanArray.put(editTextName.getId(), validator.isNameValid(userProfile.getName()));

        editTextLasName = (EditText) rootView.findViewById(R.id.editText12);
        editTextLasName.setText(userProfile.getLastName());
        editTextLasName.addTextChangedListener(new TextWatcherETicket(editTextLasName));
        sparseBooleanArray.put(editTextLasName.getId(), validator.isLastNameValid(userProfile.getLastName()));

        editTextPhone = (EditText) rootView.findViewById(R.id.editText13);
        if (!userProfile.getPhone().equals("")){
        editTextPhone.setText(userProfile.getPhone());
        }else {
            editTextPhone.setText("+38");
        }
        editTextPhone.addTextChangedListener(new TextWatcherETicket(editTextPhone));
        sparseBooleanArray.put(editTextPhone.getId(), validator.isPhoneValid(userProfile.getPhone()));

        editTextEmail = (EditText) rootView.findViewById(R.id.editText14);
        editTextEmail.setText(userProfile.getEmail());
        editTextEmail.addTextChangedListener(new TextWatcherETicket(editTextEmail));
        sparseBooleanArray.put(editTextEmail.getId(), validator.isEmailValid(userProfile.getEmail()));

        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);

        switch (paymentMethod){
            case 1:
                buttonConfirm.setText("оформити замовлення");
                break;
            case 2:
                buttonConfirm.setText("перейти до оплати");
                break;
        }


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidUserData()){

                    //Fragment fragment = new FragmentSuccessfulOrder();
                    //fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                }
            }
        });
        textViewTimer = (TextView) rootView.findViewById(R.id.textView98);
        startService();
        return rootView;
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = new FragmentDeliveryOrder();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();

    }

    private void startService(){

        long timer = ((ActivityOrder)getActivity()).getTimer();
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
                    ((ActivityOrder)getActivity()).showAlertDialog();
                }
            }.start();
        }
    }

    private Boolean getValidUserData() {
        Boolean valid = true;

        for(int i=0; i<sparseBooleanArray.size(); i++)
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

    private void saveOrder(){
        final String url = "http://tms.webclever.in.ua/api/SaveOrder";
        String order_id = SingletonTempOrder.getInstance().getOrder_id();
        String order_token = SingletonTempOrder.getInstance().getToken();

        final JSONObject jsonObjectHeader = new JSONObject();
        final JSONObject jsonObjectParams = new JSONObject();
        try {

            jsonObjectHeader.put("tempOrder",order_id);
            jsonObjectHeader.put("token",order_token);
            jsonObjectHeader.put("orderType",paymentMethod);
            jsonObjectHeader.put("name",editTextName.getText().toString());
            jsonObjectHeader.put("surname",editTextLasName.getText().toString());
            jsonObjectHeader.put("phone",editTextPhone.getText().toString());
            jsonObjectHeader.put("email",editTextEmail.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                        try {

                            JSONObject jsonObject = new JSONObject(s);

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
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tmssec", jsonObjectHeader.toString());
                Log.i("Response_Header",params.get("tmssec"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token","3748563");
                params.put("userInfo",jsonObjectParams.toString());
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);
    }

}
