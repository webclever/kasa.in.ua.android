package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import Singleton.SingletonTempOrder;
import Singleton.UserProfileSingleton;
import Validator.Validator;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

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
    private EditText editTextMessage;
    private String url_pay = "http://kasa.tms.webclever.in.ua/event/pay?order_id=";

    private CountDownTimer countDownTimer;

    public FragmentUserDataKasa()    { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user_data_kasa, container, false);
        fragmentManager = getFragmentManager();

        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }

        userProfile = new UserProfileSingleton(this.getActivity());

        editTextName = (EditText) rootView.findViewById(R.id.editText11);
        editTextName.addTextChangedListener(new TextWatcherETicket(editTextName));
        sparseBooleanArray.put(editTextName.getId(), false);

        editTextLasName = (EditText) rootView.findViewById(R.id.editText12);
        editTextLasName.addTextChangedListener(new TextWatcherETicket(editTextLasName));
        sparseBooleanArray.put(editTextLasName.getId(), false);

        editTextPhone = (EditText) rootView.findViewById(R.id.editText13);
        editTextPhone.addTextChangedListener(new TextWatcherETicket(editTextPhone));
        sparseBooleanArray.put(editTextPhone.getId(), false);

        editTextEmail = (EditText) rootView.findViewById(R.id.editText14);
        editTextEmail.addTextChangedListener(new TextWatcherETicket(editTextEmail));
        sparseBooleanArray.put(editTextEmail.getId(), false);

        editTextMessage = (EditText) rootView.findViewById(R.id.editText7);

        if (userProfile.getStatus()){
            getUserDataProfile();
        }


        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);

        switch (paymentMethod){
            case 1:
                buttonConfirm.setText(getResources().getString(R.string.page_data_issue_order));
                break;
            case 2:
                buttonConfirm.setText(getResources().getString(R.string.page_data_pay_ticket));
                break;
        }


        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidUserData()){
                        saveOrderUser();
                }
            }
        });
        textViewTimer = (TextView) rootView.findViewById(R.id.textView98);
        startService();
        ((ActivityOrder)getActivity()).Trekking("Screen user data kasa ordering.");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        ((ActivityOrder)getActivity()).Trekking("Screen user data kasa ordering.");
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = new FragmentDeliveryOrder();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();

    }

    private void startService(){

        Long timer = ((ActivityOrder)getActivity()).getTimer();
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

    private void saveOrderUser() {
        final String url = "http://tms.net.ua/api/SaveOrder";
        final String order_id = SingletonTempOrder.getInstance().getOrder_id();
        final String order_token = SingletonTempOrder.getInstance().getToken();

        final JSONObject jsonObjectHeader = new JSONObject();
        if (userProfile.getStatus()){
            try {
                jsonObjectHeader.put("user_id",userProfile.getUserId());
                jsonObjectHeader.put("token",userProfile.getToken());
            }catch (JSONException e){
                e.printStackTrace();
            }
        }

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);

                                if (jsonObject.has("msg")) {
                                    if (!jsonObject.getString("msg").equals("places already sold OR event with this places not in sale")) {
                                        if (paymentMethod == 1) {

                                            Intent intent = new Intent(getActivity(), ActivitySuccessfulOrder.class);
                                            intent.putExtra("order_id", jsonObject.getString("order_id"));
                                            intent.putExtra("payment_method", paymentMethod);
                                            intent.putExtra("message", getResources().getString(R.string.page_success_order_number_ordering_kasa));
                                            startActivity(intent);


                                        }else if(paymentMethod == 2){
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                Fragment fragment = new FragmentPay();
                                                Bundle bundle = new Bundle();
                                                bundle.putString("order_id", jsonObject.getString("order_id"));
                                                bundle.putInt("payment_method",paymentMethod);
                                                fragment.setArguments(bundle);
                                                fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
                                            }else {
                                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_pay+jsonObject.getString("order_id")));
                                                startActivity(browserIntent);
                                            }
                                        }
                                        ((ActivityOrder) getActivity()).stopTimer();
                                        ((ActivityOrder) getActivity()).deleteDB();
                                    }else {
                                        JSONArray jsonArrayTicket = jsonObject.getJSONArray("place_ids");
                                        ((ActivityOrder)getActivity()).showAlertDialogPayTicket(jsonArrayTicket);
                                    }
                                }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                ((ActivityOrder)getActivity()).showAlertDialogPayTicket(null);
                if (volleyError.networkResponse != null) {
                    Log.d("Error Response code " , String.valueOf(volleyError.networkResponse.statusCode));
                }

                NetworkResponse response = volleyError.networkResponse;
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                   Log.i("Error Response code",response.toString());
                   Log.i("Error Response code", Arrays.toString(response.data));
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String string_json = jsonObjectHeader.toString();
                String header =  " " + Base64.encodeToString(string_json.getBytes(), Base64.NO_WRAP);
                params.put("tmssec", header);

                Log.i("Response_HeaderNoEncode", string_json);
                Log.i("Response_Header",params.toString());
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                params.put("temp_order",order_id);
                params.put("order_token",order_token);
                params.put("orderType","1");
                params.put("phone",editTextPhone.getText().toString());
                params.put("name",editTextName.getText().toString());
                params.put("surname",editTextLasName.getText().toString());
                params.put("email",editTextEmail.getText().toString());
                params.put("comment",editTextMessage.getText().toString());

                Log.i("Params",params.toString());
                return params;
            }
        };
        int socketTimeout = 30000; //30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringPostRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringPostRequest);
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

    private void getUserDataProfile() {

        final JSONObject jsonObjectHeader = new JSONObject();

        try {

            jsonObjectHeader.put("user_id", userProfile.getUserId());
            jsonObjectHeader.put("token", userProfile.getToken());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "http://tms.net.ua/api/getUserData";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response p", s);
                        try {

                            JSONObject jsonObjectUserData = new JSONObject(s);

                            editTextName.setText(jsonObjectUserData.getString("name"));
                            sparseBooleanArray.put(editTextName.getId(), validator.isNameValid(jsonObjectUserData.getString("name")));
                            editTextLasName.setText(jsonObjectUserData.getString("surname"));
                            sparseBooleanArray.put(editTextLasName.getId(), validator.isNameValid(jsonObjectUserData.getString("surname")));
                            editTextPhone.setText(jsonObjectUserData.getString("phone"));
                            sparseBooleanArray.put(editTextPhone.getId(), validator.isPhoneValid(jsonObjectUserData.getString("phone")));
                            editTextEmail.setText(jsonObjectUserData.getString("email"));
                            sparseBooleanArray.put(editTextEmail.getId(), validator.isEmailValid(jsonObjectUserData.getString("email")));

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
                Map<String, String> params = new HashMap<>();
                params.put("tmssec", jsonObjectHeader.toString());
                Log.i("Response_Header",params.get("tmssec"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                Log.i("Params",params.toString());
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);

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

}
