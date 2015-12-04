package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Singleton.SingletonCity;
import Singleton.SingletonTempOrder;
import Singleton.UserProfileSingleton;
import Validator.Validator;
import adapter.AdapterSelectCity;
import adapter.ObjectSpinnerAdapter;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;


public class UserDataPost extends Fragment implements OnBackPressedListener {

    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private Validator validator = new Validator();
    private UserProfileSingleton userProfile;
    private Spinner spinnerCountry;
    private List<SingletonCity> listCountries;
    private ObjectSpinnerAdapter objectSpinnerAdapter;
    private Integer idSelectedCountry = -1;
    private TextView textViewTimer;
    private FragmentManager fragmentManager;
    private Integer paymentMethod;

    private AdapterSelectCity adapterSelectCity;
    private ArrayList<SingletonCity> singletonCityArrayList;
    private Integer cityID;

    private EditText editTextCity;

    private Bundle bundle;

    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextSurname;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextNDepartment;
    private EditText editTextDescription;

    private Integer spinnerPosCountry = 0;

    private String url_pay = "http://kasa.tms.webclever.in.ua/event/pay?order_id=";

    private CountDownTimer countDownTimer;


    public UserDataPost() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_userdata_post, container, false);

        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }

        userProfile = new UserProfileSingleton(this.getActivity());

        textViewTimer = (TextView) rootView.findViewById(R.id.textView104);

        editTextName = (EditText) rootView.findViewById(R.id.editText15);
        editTextName.addTextChangedListener(new TextWatcherETicket(editTextName));
        sparseBooleanArray.put(editTextName.getId(), false);

        editTextLastName = (EditText) rootView.findViewById(R.id.editText16);
        editTextLastName.addTextChangedListener(new TextWatcherETicket(editTextLastName));
        sparseBooleanArray.put(editTextLastName.getId(), false);

        editTextSurname = (EditText) rootView.findViewById(R.id.editText29);
        editTextSurname.addTextChangedListener(new TextWatcherETicket(editTextSurname));
        sparseBooleanArray.put(editTextSurname.getId(), false);

        editTextPhone = (EditText) rootView.findViewById(R.id.editText17);
        editTextPhone.addTextChangedListener(new TextWatcherETicket(editTextPhone));
        sparseBooleanArray.put(editTextPhone.getId(), false);

        editTextEmail = (EditText) rootView.findViewById(R.id.editText18);
        editTextEmail.addTextChangedListener(new TextWatcherETicket(editTextEmail));
        sparseBooleanArray.put(editTextEmail.getId(), false);

        editTextNDepartment = (EditText) rootView.findViewById(R.id.editText21);
        editTextNDepartment.addTextChangedListener(new TextWatcherETicket(editTextNDepartment));
        sparseBooleanArray.put(editTextNDepartment.getId(), false);

        editTextDescription = (EditText) rootView.findViewById(R.id.editText6);



        spinnerCountry = (Spinner) rootView.findViewById(R.id.spinner);
        spinnerCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.i("Post", "Country id: " + String.valueOf(position));
                SingletonCity singletonCity = listCountries.get(position);
                idSelectedCountry = singletonCity.getIdCity();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        editTextCity = (EditText) rootView.findViewById(R.id.editText19);
        editTextCity.addTextChangedListener(new TextWatcherETicket(editTextCity));
        sparseBooleanArray.put(editTextCity.getId(),false);
        editTextCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCityDialog(idSelectedCountry);
            }
        });

        if (userProfile.getStatus()){
            getUserDataProfile();
        }else {
            getCountries(0);
        }

        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);

        switch (paymentMethod){
            case 3:
                buttonConfirm.setText(getResources().getString(R.string.page_data_issue_order));
                break;
            case 4:
                buttonConfirm.setText(getResources().getString(R.string.page_data_pay_ticket));
                break;
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidUserData()) {
                        saveOrderUser();
                }
            }
        });
        startService();

        fragmentManager = getActivity().getFragmentManager();
        return rootView;
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = new FragmentDeliveryOrder();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
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

    private void startService() {

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

    private void getCountries(final Integer id_country) {

        listCountries = new ArrayList<>();

        final String url = "http://tms.webclever.in.ua/api/GetCountries";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for(int i=0; i<jsonArray.length();i++){
                                SingletonCity singletonCity = new SingletonCity();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (jsonObject.getInt("id") == id_country){
                                    spinnerPosCountry = i;
                                }
                                singletonCity.setIdCity(jsonObject.getInt("id"));
                                singletonCity.setNameCity(jsonObject.getString("name"));

                                listCountries.add(singletonCity);
                            }
                            objectSpinnerAdapter.notifyDataSetChanged();
                            spinnerCountry.setSelection(spinnerPosCountry);
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
                Log.i("Params",params.toString());
                return params;
            }
        };

        objectSpinnerAdapter = new ObjectSpinnerAdapter(getActivity(),listCountries,false);
        spinnerCountry.setAdapter(objectSpinnerAdapter);

        AppController.getInstance().addToRequestQueue(stringPostRequest);
    }

    private void showSelectCityDialog(final Integer id_country) {

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View viewLayout = layoutInflater.inflate(R.layout.list_dialog_select_city, null);
        alBuilder.setTitle(getResources().getString(R.string.page_user_profile_select_city));
        alBuilder.setView(viewLayout);
        final Dialog alertDialog = alBuilder.create();
        singletonCityArrayList = new ArrayList<>();

        adapterSelectCity = new AdapterSelectCity(getActivity(),singletonCityArrayList);
        ListView listView = (ListView) viewLayout.findViewById(R.id.listView2);


        listView.setAdapter(adapterSelectCity);
        EditText editText = (EditText) viewLayout.findViewById(R.id.editText20);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 1){
                    singletonCityArrayList.clear();
                    singletonCityArrayList = getListCity(id_country, s.toString());
                    Log.i("dialog",s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                editTextCity.setText(singletonCityArrayList.get(position).getNameCity());
                cityID = singletonCityArrayList.get(position).getIdCity();
                alertDialog.dismiss();
            }
        });


        alertDialog.show();
    }

    private ArrayList<SingletonCity> getListCity(final Integer id_country, final String text) {

        final String url = "http://tms.webclever.in.ua/api/searchCity";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {

                            JSONArray jsonArray = new JSONArray(s);
                            for(int i=0; i < jsonArray.length(); i++){
                                SingletonCity singletonCity = new SingletonCity();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                singletonCity.setIdCity(jsonObject.getInt("id"));
                                singletonCity.setNameCity(jsonObject.getString("text"));
                                singletonCityArrayList.add(singletonCity);
                            }

                            adapterSelectCity.notifyDataSetChanged();

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
                params.put("token", "3748563");
                params.put("country_id", String.valueOf(id_country));
                params.put("name",text);
                params.put("all","1");
                Log.i("Params", params.toString());
                return params;
            }
        };
        AppController.getInstance().addToRequestQueue(stringPostRequest);
        return singletonCityArrayList;
    }

    private void saveOrderUser() {
        final String url = "http://tms.webclever.in.ua/api/SaveOrder";
        final String order_id = SingletonTempOrder.getInstance().getOrder_id();
        final String order_token = SingletonTempOrder.getInstance().getToken();

        final JSONObject jsonObjectHeader = new JSONObject();
        if (userProfile.getStatus()){
        try {
            jsonObjectHeader.put("user_id",userProfile.getUserId());
            jsonObjectHeader.put("token",userProfile.getToken());
        }catch (JSONException e){
            e.printStackTrace();
        }}

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            if (jsonObject.has("msg") && !jsonObject.getString("msg").equals("places already sold OR event with this places not in sale")) {
                            if (paymentMethod == 3) {

                                    Intent intent = new Intent(getActivity(), ActivitySuccessfulOrder.class);
                                    intent.putExtra("order_id", jsonObject.getString("order_id"));
                                    intent.putExtra("payment_method", paymentMethod);
                                    intent.putExtra("message", getResources().getString(R.string.page_success_order_description_order2));
                                    startActivity(intent);

                            }else if (paymentMethod == 4){
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    Fragment fragment = new FragmentPay();
                                    Bundle bundle = new Bundle();
                                    bundle.putString("order_id", jsonObject.getString("order_id"));
                                    bundle.putInt("payment_method", paymentMethod);
                                    fragment.setArguments(bundle);
                                    fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
                                }else {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url_pay + jsonObject.getString("order_id")));
                                    startActivity(browserIntent);
                                }
                            }
                                ((ActivityOrder) getActivity()).stopTimer();
                                ((ActivityOrder) getActivity()).deleteDB();
                            }else {
                                JSONArray jsonArrayTicket = jsonObject.getJSONArray("place_ids");
                                ((ActivityOrder)getActivity()).showAlertDialogPayTicket(jsonArrayTicket);
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
                params.put("tmssec", jsonObjectHeader.toString());
                Log.i("Response_Header",params.get("tmssec"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                params.put("temp_order",order_id);
                params.put("order_token",order_token);
                params.put("orderType",String.valueOf(paymentMethod));
                params.put("phone",editTextPhone.getText().toString());
                params.put("name",editTextName.getText().toString());
                params.put("surname",editTextLastName.getText().toString());
                params.put("patr_name",editTextSurname.getText().toString());
                params.put("email",editTextEmail.getText().toString());
                params.put("np",editTextNDepartment.getText().toString());
                params.put("country_id",String.valueOf(idSelectedCountry));
                params.put("city_id",String.valueOf(cityID));
                params.put("comment",editTextDescription.getText().toString());
                Log.i("Params",params.toString());
                return params;
            }
        };
        int socketTimeout = 30000; //30 seconds
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringPostRequest.setRetryPolicy(policy);
        AppController.getInstance().addToRequestQueue(stringPostRequest);
    }

    private void getUserDataProfile() {

        final JSONObject jsonObjectHeader = new JSONObject();

        try {

            jsonObjectHeader.put("user_id", userProfile.getUserId());
            jsonObjectHeader.put("token", userProfile.getToken());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String url = "http://tms.webclever.in.ua/api/getUserData";
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
                            editTextLastName.setText(jsonObjectUserData.getString("surname"));
                            sparseBooleanArray.put(editTextLastName.getId(), validator.isNameValid(jsonObjectUserData.getString("surname")));
                            if (jsonObjectUserData.has("patr_name")){
                                editTextSurname.setText(jsonObjectUserData.getString("patr_name"));
                                sparseBooleanArray.put(editTextLastName.getId(), validator.isNameValid(jsonObjectUserData.getString("patr_name")));}
                            editTextPhone.setText(jsonObjectUserData.getString("phone"));
                            sparseBooleanArray.put(editTextPhone.getId(), validator.isPhoneValid(jsonObjectUserData.getString("phone")));
                            editTextEmail.setText(jsonObjectUserData.getString("email"));
                            sparseBooleanArray.put(editTextEmail.getId(), validator.isEmailValid(jsonObjectUserData.getString("email")));
                            if (jsonObjectUserData.has("country_id")){
                                getCountries(jsonObjectUserData.getInt("country_id"));}
                            if (!jsonObjectUserData.getString("np_id").equals("0")){
                                editTextNDepartment.setText(jsonObjectUserData.getString("np_id"));
                                sparseBooleanArray.put(editTextNDepartment.getId(), validator.isNumberValid(jsonObjectUserData.getString("np_id")));}
                            if(jsonObjectUserData.has("city_id")){
                                cityID = jsonObjectUserData.getInt("city_id");
                                getCity(cityID);
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

    private void getCity(final Integer city_id) {
        final String url = "http://tms.webclever.in.ua/api/getCityById";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response p", s);
                        try {
                            JSONObject jsonObjectUserData = new JSONObject(s);
                            cityID = jsonObjectUserData.getInt("id");
                            editTextCity.setText(jsonObjectUserData.getString("name"));
                            sparseBooleanArray.put(editTextCity.getId(), validator.isNameValid(jsonObjectUserData.getString("name")));

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
                params.put("id",String.valueOf(city_id));
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
                case R.id.editText15:
                    sparseBooleanArray.put(R.id.editText15,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText16:
                    sparseBooleanArray.put(R.id.editText16,validator.isLastNameValid(editable.toString()));
                    break;
                case R.id.editText29:
                    sparseBooleanArray.put(R.id.editText29, validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText17:
                    sparseBooleanArray.put(R.id.editText17,validator.isPhoneValid(editable.toString()));
                    break;
                case R.id.editText18:
                    sparseBooleanArray.put(R.id.editText18,validator.isEmailValid(editable.toString()));
                    break;
                case R.id.editText21:
                    sparseBooleanArray.put(R.id.editText21,validator.isNumberValid(editable.toString()));
                    break;
                case R.id.editText19:
                    sparseBooleanArray.put(R.id.editText19,validator.isAddressValid(editable.toString()));
            }

        }
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
