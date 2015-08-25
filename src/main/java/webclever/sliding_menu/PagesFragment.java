package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import Validator.Validator;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by User on 13.08.2014.
 */
public class PagesFragment extends Fragment implements OnBackPressedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public PagesFragment(){setHasOptionsMenu(true);}

    private TextView textViewSigIn;
    private EditText editTextName;
    private EditText editTextLName;
    private EditText editTextPhone;
    private EditText editTextEmail;
    private EditText editTextAddress;
    private EditText editTextNPost;
    private Spinner spinnerCountry;
    private Spinner spinnerOblast;
    private Spinner spinnerCity;

    private SharedPreferences sharedPreferencesUserData;
    private static final String APP_PREFERENCES = "user_profile";

    private SparseBooleanArray sparseBooleanArrayValidator;
    private Validator validator;

    private GoogleApiClient mGoogleApiClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_pages,container,false);
        sparseBooleanArrayValidator = new SparseBooleanArray();
        validator = new Validator();
        sharedPreferencesUserData = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        textViewSigIn = (TextView) rootView.findViewById(R.id.textViewSigIn);
        textViewSigIn.setText(sharedPreferencesUserData.getString("social",""));
        validator = new Validator();
        editTextName = (EditText) rootView.findViewById(R.id.editText2);
        editTextName.setText(sharedPreferencesUserData.getString("user_name", ""));
        editTextName.addTextChangedListener(new MyTextWatcher(editTextName));
        sparseBooleanArrayValidator.put(editTextName.getId(), validator.isNameValid(sharedPreferencesUserData.getString("user_name", "")));
        editTextLName = (EditText) rootView.findViewById(R.id.editText27);
        editTextLName.setText(sharedPreferencesUserData.getString("user_last_name", ""));
        editTextLName.addTextChangedListener(new MyTextWatcher(editTextLName));
        sparseBooleanArrayValidator.put(editTextLName.getId(), validator.isNameValid(sharedPreferencesUserData.getString("user_last_name", "")));
        editTextPhone = (EditText) rootView.findViewById(R.id.editText3);
        editTextPhone.setText(sharedPreferencesUserData.getString("user_phone", ""));
        editTextPhone.addTextChangedListener(new MyTextWatcher(editTextPhone));
        sparseBooleanArrayValidator.put(editTextPhone.getId(), validator.isPhoneValid(sharedPreferencesUserData.getString("user_phone", "")));
        editTextEmail = (EditText) rootView.findViewById(R.id.editText4);
        editTextEmail.setText(sharedPreferencesUserData.getString("user_email", ""));
        editTextEmail.addTextChangedListener(new MyTextWatcher(editTextEmail));
        sparseBooleanArrayValidator.put(editTextEmail.getId(), validator.isEmailValid(sharedPreferencesUserData.getString("user_email", "")));

        editTextAddress = (EditText)rootView.findViewById(R.id.editText8);
        editTextAddress.setText(sharedPreferencesUserData.getString("address",""));
        editTextNPost = (EditText)rootView.findViewById(R.id.editText9);
        editTextNPost.setText(sharedPreferencesUserData.getString("number_post", ""));

        //editTextAddress.addTextChangedListener(new MyTextWatcher(editTextName));

        Button mButtonSaveChange = (Button) rootView.findViewById(R.id.savechangebutton);
        mButtonSaveChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkValid()) {
                    if (SaveUserData()) {
                        Toast.makeText(getActivity(), "Дані успішно збережено!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        Button buttonExitAccount = (Button) rootView.findViewById(R.id.buttonExitAccount);
        buttonExitAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOutSocial(sharedPreferencesUserData.getInt("social_id", 0));
                SharedPreferences.Editor editor = sharedPreferencesUserData.edit();
                editor.clear();
                editor.apply();
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
        if (sharedPreferencesUserData.getInt("social_id", 0) == 0){
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

        mGoogleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();
        mGoogleApiClient.connect();

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);

        MenuItem item = menu.findItem(R.id.menuCount);
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PhotosFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());

        super.onCreateOptionsMenu(menu, inflater);
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
    public void onConnected(Bundle bundle) {
        Log.i("User","onConnected with GooglePlus");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
            }

        }
    }

    private Boolean checkValid(){
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

    private Boolean SaveUserData(){
        SharedPreferences.Editor editor = sharedPreferencesUserData.edit();
        editor.putString("social",textViewSigIn.getText().toString());
        editor.putString("user_name",editTextName.getText().toString());
        editor.putString("user_last_name",editTextLName.getText().toString());
        editor.putString("user_phone",editTextPhone.getText().toString());
        editor.putString("user_email",editTextEmail.getText().toString());
        editor.putString("address",editTextAddress.getText().toString());
        editor.putString("number_post",editTextNPost.getText().toString());
        editor.apply();
        return true;
    }

    private void logOutSocial(Integer soc_id){
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
            case 3:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    Log.i("User", "is logout G+ !");
                }else {
                    Log.i("User", "no logout G+ !");
                }
                break;
        }
    }

}
