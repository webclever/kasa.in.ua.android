package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by User on 13.08.2014.
 */
public class PagesFragment extends Fragment implements OnBackPressedListener {

    public PagesFragment(){setHasOptionsMenu(true);}

    private EditText editTextName;
    private EditText editTextLName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private EditText editTextNPost;
    private EditText editTextSurname;
    private Spinner spinnerCountry;
    private List<SingletonCity> listCountries;
    private ObjectSpinnerAdapter objectSpinnerAdapter;
    private Integer idSelectedCountry = -1;

    private AdapterSelectCity adapterSelectCity;
    private ArrayList<SingletonCity> singletonCityArrayList;
    private Integer cityID;
    private EditText editTextCity;


    private UserProfileSingleton userProfile;

    private SparseBooleanArray sparseBooleanArrayValidator;
    private Validator validator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {
        View rootView = inflater.inflate(R.layout.fragment_pages,container,false);
        ((MainActivity)getActivity()).setItemChecked(7,true);
        userProfile = new UserProfileSingleton(getActivity());
        sparseBooleanArrayValidator = new SparseBooleanArray();
        validator = new Validator();

        TextView textViewSigIn = (TextView) rootView.findViewById(R.id.textViewSigIn);
        textViewSigIn.setText(userProfile.getNameSocial());

        VKSdk.wakeUpSession(this.getActivity(), new VKCallback<VKSdk.LoginState>() {
            @Override
            public void onResult(VKSdk.LoginState res) {
                switch (res) {
                    case LoggedOut:
                        Log.i("VK", "user is logout");
                        break;
                    case LoggedIn:
                        Log.i("VK", "user is login!");
                        break;
                    case Pending:
                        break;
                    case Unknown:
                        break;
                }
            }

            @Override
            public void onError(VKError error) {

            }
        });

        editTextName = (EditText) rootView.findViewById(R.id.editText2);
        editTextName.setText(userProfile.getName());
        editTextName.addTextChangedListener(new MyTextWatcher(editTextName));
        sparseBooleanArrayValidator.put(editTextName.getId(), validator.isNameValid(userProfile.getName()));
        editTextLName = (EditText) rootView.findViewById(R.id.editText27);
        editTextLName.setText(userProfile.getLastName());
        editTextLName.addTextChangedListener(new MyTextWatcher(editTextLName));
        sparseBooleanArrayValidator.put(editTextLName.getId(), validator.isNameValid(userProfile.getLastName()));
        editTextSurname = (EditText) rootView.findViewById(R.id.editText22);
        editTextSurname.setText(userProfile.getSurname());
        sparseBooleanArrayValidator.put(editTextSurname.getId(), true);
        editTextPhone = (EditText) rootView.findViewById(R.id.editText3);
        editTextPhone.setText(userProfile.getPhone());
        editTextPhone.addTextChangedListener(new MyTextWatcher(editTextPhone));
        sparseBooleanArrayValidator.put(editTextPhone.getId(), validator.isPhoneValid(userProfile.getPhone()));
        editTextEmail = (EditText) rootView.findViewById(R.id.editText4);
        editTextEmail.setText(userProfile.getEmail());
        editTextEmail.addTextChangedListener(new MyTextWatcher(editTextEmail));
        sparseBooleanArrayValidator.put(editTextEmail.getId(), validator.isEmailValid(userProfile.getEmail()));

        editTextAddress = (EditText)rootView.findViewById(R.id.editText8);
        editTextAddress.setText(userProfile.getAddress());
        editTextNPost = (EditText)rootView.findViewById(R.id.editText9);
        editTextNPost.setText(userProfile.getNewPost());

        spinnerCountry = (Spinner) rootView.findViewById(R.id.spinnerCountry);
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

        editTextCity = (EditText) rootView.findViewById(R.id.editText31);
        editTextCity.setText(userProfile.getCity());
        editTextCity.addTextChangedListener(new MyTextWatcher(editTextCity));
        sparseBooleanArrayValidator.put(editTextCity.getId(), validator.isAddressValid(userProfile.getCity()));
        editTextCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectCityDialog(idSelectedCountry);
            }
        });

        Button mButtonSaveChange = (Button) rootView.findViewById(R.id.savechangebutton);
        mButtonSaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValid()) {
                    SaveUserData();
                }
            }
        });
        Button buttonExitAccount = (Button) rootView.findViewById(R.id.buttonExitAccount);
        buttonExitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutSocial(userProfile.getSocialId());
                userProfile.deleteUserData();
                ((MainActivity) getActivity()).changeMenuItems(false);
                Toast.makeText(getActivity(), "User is Logout !", Toast.LENGTH_SHORT).show();
                Fragment fragment = new HomeFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            }
        });
        Button buttonChangePassword = (Button) rootView.findViewById(R.id.buttonChangePassword);
        buttonChangePassword.setEnabled(false);
        buttonChangePassword.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        if (userProfile.getSocialId() == 0){
            buttonChangePassword.setEnabled(true);
            buttonChangePassword.getBackground().setColorFilter(null);
            buttonChangePassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ActivityChangePassword.class);
                    startActivity(intent);
                }
            });
        }

        return rootView;
    }



    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
        Toast.makeText(getActivity().getApplicationContext(), "From LocKasaFragment onBackPressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onDestroyView () {

        ((MainActivity)getActivity()).setItemChecked(7, false);
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        if (!((MainActivity) getActivity()).getCountTicket().equals("0")){
        getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);

        MenuItem item = menu.findItem(R.id.menuCount);
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentBasket();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());

        super.onCreateOptionsMenu(menu, inflater);}
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

    private Boolean checkValid() {
        Boolean valid = true;
        for (int i=0; i<sparseBooleanArrayValidator.size(); i++){
            EditText editText = (EditText) getActivity().findViewById(sparseBooleanArrayValidator.keyAt(i));
            if (!sparseBooleanArrayValidator.valueAt(i)){
                valid = false;
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            }else{
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }
        }
        return valid;
    }

    private void SaveUserData() {

        final JSONObject jsonObjectHeader = new JSONObject();

        try {

            jsonObjectHeader.put("user_id", userProfile.getUserId());
            jsonObjectHeader.put("token", userProfile.getToken());
            jsonObjectHeader.put("name", editTextName.getText().toString());
            jsonObjectHeader.put("surname", editTextLName.getText().toString());
            jsonObjectHeader.put("----", editTextSurname.getText().toString());
            jsonObjectHeader.put("phone", editTextPhone.getText().toString());
            jsonObjectHeader.put("country_id", idSelectedCountry);
            jsonObjectHeader.put("city_id", cityID);
            jsonObjectHeader.put("email", editTextEmail.getText().toString());
            jsonObjectHeader.put("address", editTextAddress.getText().toString());
            jsonObjectHeader.put("np_id", Integer.parseInt(editTextNPost.getText().toString()));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://tms.webclever.in.ua/api/changePassword";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response p", s);
                        try {
                            JSONObject jsonObjectUserData = new JSONObject(s);
                            if (jsonObjectUserData.getBoolean("change_user_data")){

                                Toast.makeText(getActivity().getApplicationContext(),"Дані збережено!",Toast.LENGTH_SHORT).show();

                                userProfile.setName(editTextName.getText().toString());
                                userProfile.setLastName(editTextLName.getText().toString());
                                userProfile.setSurname(editTextSurname.getText().toString());
                                userProfile.setPhone(editTextPhone.getText().toString());
                                userProfile.setEmail(editTextEmail.getText().toString());
                                userProfile.setCity(editTextCity.getText().toString());
                                userProfile.setAddress(editTextAddress.getText().toString());
                                userProfile.setNewPost(editTextNPost.getText().toString());

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

    private void logOutSocial(Integer soc_id) {
        switch (soc_id){
            case 1:
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                if (accessToken != null) {
                    LoginManager.getInstance().logOut();
                    Log.i("User", "is logout FaceBook !");}
                break;
            case 2:
                if (VKSdk.isLoggedIn()) {
                    VKSdk.logout();
                    Log.i("User", "is logout VK !");
                }
                break;
           /* case 3:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    Log.i("User", "is logout G+ !");
                }else {
                    Log.i("User", "no logout G+ !");
                }
                break;*/
        }
    }

    private class MyTextWatcher implements TextWatcher{
        private View view;
        public MyTextWatcher(View view){
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
            switch (view.getId()){
                case R.id.editText2:
                    sparseBooleanArrayValidator.put(R.id.editTextName,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText27:
                    sparseBooleanArrayValidator.put(R.id.editText27,validator.isLastNameValid(editable.toString()));
                    break;
                case R.id.editText3:
                    sparseBooleanArrayValidator.put(R.id.editText3,validator.isPhoneValid(editable.toString()));
                    break;
                case R.id.editText4:
                    sparseBooleanArrayValidator.put(R.id.editText4,validator.isEmailValid(editable.toString()));
                    break;
                case R.id.editText31:
                    sparseBooleanArrayValidator.put(R.id.editText31,validator.isAddressValid(editable.toString()));
                    break;
            }

        }
    }
}
