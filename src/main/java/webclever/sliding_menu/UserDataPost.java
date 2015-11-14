package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
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

import com.android.volley.Request;
import com.android.volley.Response;
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

    public UserDataPost() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_userdata_post, container, false);
        Toast.makeText(getActivity().getApplicationContext(), getArguments().getString("type"), Toast.LENGTH_SHORT).show();


        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }

        userProfile = new UserProfileSingleton(this.getActivity());

        EditText editTextName = (EditText) rootView.findViewById(R.id.editText15);
        editTextName.setText(userProfile.getName());
        editTextName.addTextChangedListener(new TextWatcherETicket(editTextName));
        sparseBooleanArray.put(editTextName.getId(), validator.isNameValid(userProfile.getName()));

        EditText editTextLasName = (EditText) rootView.findViewById(R.id.editText16);
        editTextLasName.setText(userProfile.getLastName());
        editTextLasName.addTextChangedListener(new TextWatcherETicket(editTextLasName));
        sparseBooleanArray.put(editTextLasName.getId(), validator.isLastNameValid(userProfile.getLastName()));

        EditText editTextSurname = (EditText) rootView.findViewById(R.id.editText29);
        editTextSurname.setText(userProfile.getSurname());
        editTextSurname.addTextChangedListener(new TextWatcherETicket(editTextSurname));
        sparseBooleanArray.put(editTextSurname.getId(),validator.isNameValid(userProfile.getSurname()));

        EditText editTextPhone = (EditText) rootView.findViewById(R.id.editText17);
        if (!userProfile.getPhone().equals("")){
            editTextPhone.setText(userProfile.getPhone());
        }else {
            editTextPhone.setText("+38");
        }
        editTextPhone.addTextChangedListener(new TextWatcherETicket(editTextPhone));
        sparseBooleanArray.put(editTextPhone.getId(), validator.isPhoneValid(userProfile.getPhone()));

        EditText editTextEmail = (EditText) rootView.findViewById(R.id.editText18);
        editTextEmail.setText(userProfile.getEmail());
        editTextEmail.addTextChangedListener(new TextWatcherETicket(editTextEmail));
        sparseBooleanArray.put(editTextEmail.getId(), validator.isEmailValid(userProfile.getEmail()));

        EditText editTextNDepartament = (EditText) rootView.findViewById(R.id.editText21);
        editTextNDepartament.setText(userProfile.getNewPost());
        editTextNDepartament.addTextChangedListener(new TextWatcherETicket(editTextNDepartament));
        sparseBooleanArray.put(editTextNDepartament.getId(), validator.isNumberValid(userProfile.getNewPost()));

        textViewTimer = (TextView) rootView.findViewById(R.id.textView104);

        spinnerCountry = (Spinner) rootView.findViewById(R.id.spinner);
        listCountries = getCountries();
        objectSpinnerAdapter = new ObjectSpinnerAdapter(getActivity(),listCountries,false);
        spinnerCountry.setAdapter(objectSpinnerAdapter);
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
        editTextCity.setText(userProfile.getCity());
        editTextCity.addTextChangedListener(new TextWatcherETicket(editTextCity));
        sparseBooleanArray.put(editTextCity.getId(),validator.isAddressValid(userProfile.getCity()));
        editTextCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCityDialog(idSelectedCountry);
            }
        });

        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);

        switch (paymentMethod){
            case 3:
                buttonConfirm.setText("оформити замовлення");
                break;
            case 4:
                buttonConfirm.setText("перейти до оплати");
                break;
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidUserData()) {
                    Fragment fragment = new FragmentSuccessfulOrder();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragments_container, fragment).commit();
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

    private ArrayList<SingletonCity> getCountries(){
        final String url = "http://tms.webclever.in.ua/api/GetCountries";
        final ArrayList<SingletonCity> singletonCityArrayList = new ArrayList<>();
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            for(int i=0; i<jsonArray.length();i++){
                                SingletonCity singletonCity = new SingletonCity();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                singletonCity.setIdCity(jsonObject.getInt("id"));
                                singletonCity.setNameCity(jsonObject.getString("name"));

                                singletonCityArrayList.add(singletonCity);
                            }
                            objectSpinnerAdapter.notifyDataSetChanged();
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

        AppController.getInstance().addToRequestQueue(stringPostRequest);

        return singletonCityArrayList;
    }

    private void showSelectCityDialog(final Integer id_country){

        AlertDialog.Builder alBuilder = new AlertDialog.Builder(this.getActivity());
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        final View viewLayout = layoutInflater.inflate(R.layout.list_dialog_select_city, null);
        alBuilder.setTitle("Введіть назву міста");
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

    private ArrayList<SingletonCity> getListCity(final Integer id_country, final String text){

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
}
