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

import Singleton.UserProfileSingleton;
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

    private UserProfileSingleton userProfile;

    private SparseBooleanArray sparseBooleanArrayValidator;
    private Validator validator;

    private GoogleApiClient mGoogleApiClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_pages,container,false);
        ((MainActivity)getActivity()).setItemChecked(6,true);
        userProfile = new UserProfileSingleton(getActivity());
        sparseBooleanArrayValidator = new SparseBooleanArray();
        validator = new Validator();

        textViewSigIn = (TextView) rootView.findViewById(R.id.textViewSigIn);
        textViewSigIn.setText(userProfile.getNameSocial());

        editTextName = (EditText) rootView.findViewById(R.id.editText2);
        editTextName.setText(userProfile.getName());
        editTextName.addTextChangedListener(new MyTextWatcher(editTextName));
        sparseBooleanArrayValidator.put(editTextName.getId(), validator.isNameValid(userProfile.getName()));
        editTextLName = (EditText) rootView.findViewById(R.id.editText27);
        editTextLName.setText(userProfile.getLastName());
        editTextLName.addTextChangedListener(new MyTextWatcher(editTextLName));
        sparseBooleanArrayValidator.put(editTextLName.getId(), validator.isNameValid(userProfile.getLastName()));
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

        userProfile.setName(editTextName.getText().toString());
        userProfile.setLastName(editTextLName.getText().toString());
        userProfile.setPhone(editTextPhone.getText().toString());
        userProfile.setEmail(editTextEmail.getText().toString());
        userProfile.setAddress(editTextAddress.getText().toString());
        userProfile.setNewPost(editTextNPost.getText().toString());


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

    @Override
    public void onDestroyView () {

        ((MainActivity)getActivity()).setItemChecked(6,false);
        super.onDestroyView();
    }

}
