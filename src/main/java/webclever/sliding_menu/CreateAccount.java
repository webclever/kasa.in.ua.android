package webclever.sliding_menu;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Validator.Validator;
import customlistviewapp.AppController;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateAccount extends Fragment implements View.OnClickListener {
    private TwitterLoginButton loginButton;
    private EditText editTextName;
    private EditText editTextLName;
    private EditText editTextPhone;
    private EditText editTextEMail;
    private EditText editTextPassword;
    private EditText editTextCPassword;
    private Button buttonCreateAccount;
    private SparseBooleanArray sparseBooleanArrayValidator;
    private Validator validator = new Validator();
    private SharedPreferences sharedPreferencesUserData;
    private static final String APP_PREFERENCES = "user_profile";
    private String url="tms.webclever.in.ua/api/register?token=3748563";
    public CreateAccount() {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_account, container, false);
        // Inflate the layout for this fragment
        sharedPreferencesUserData = getActivity().getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        ImageView btnSignIn = (ImageView) rootView.findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);

        loginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
        loginButton.setText("");
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                ((LoginActivity) getActivity()).startRegistrationActivity("Twitter",5, result.data.getUserName(), "", "");
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.i("LogTW", "userCreateAccountException");
            }
        });

        ImageView imageViewFacebook = (ImageView)rootView.findViewById(R.id.imageViewFacebook);
        imageViewFacebook.setOnClickListener(this);

        ImageView imageViewVK = (ImageView) rootView.findViewById(R.id.imageViewVK);
        imageViewVK.setOnClickListener(this);
        sparseBooleanArrayValidator = new SparseBooleanArray();
        editTextName = (EditText) rootView.findViewById(R.id.editTextNameCreateAccount);
        editTextName.addTextChangedListener(new myTextWatcher(editTextName));
        sparseBooleanArrayValidator.put(editTextName.getId(), false);
        editTextLName = (EditText) rootView.findViewById(R.id.editTextLastNameCreateAccount);
        editTextLName.addTextChangedListener(new myTextWatcher(editTextLName));
        sparseBooleanArrayValidator.put(editTextLName.getId(), false);
        editTextPhone = (EditText) rootView.findViewById(R.id.editTextPhoneCreateAccount);
        editTextPhone.addTextChangedListener(new myTextWatcher(editTextPhone));
        sparseBooleanArrayValidator.put(editTextPhone.getId(), false);
        editTextEMail = (EditText) rootView.findViewById(R.id.editTextEMailCreateAccount);
        editTextEMail.addTextChangedListener(new myTextWatcher(editTextEMail));
        sparseBooleanArrayValidator.put(editTextEMail.getId(), false);
        editTextPassword = (EditText) rootView.findViewById(R.id.editTextPasswordCreateAccount);
        editTextPassword.addTextChangedListener(new myTextWatcher(editTextPassword));
        sparseBooleanArrayValidator.put(editTextPassword.getId(), false);
        editTextCPassword = (EditText) rootView.findViewById(R.id.editTextConfirmPasswordCreateAccount);
        editTextCPassword.addTextChangedListener(new myTextWatcher(editTextCPassword));
        sparseBooleanArrayValidator.put(editTextCPassword.getId(), false);
        buttonCreateAccount = (Button) rootView.findViewById(R.id.buttonCreateAccount);
        buttonCreateAccount.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onStart () {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }
    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.sign_in_button:
                ((LoginActivity)getActivity()).sigInGooglePlus();
                break;
            case R.id.imageViewVK:
                ((LoginActivity)getActivity()).sigInVK();
                break;
            case R.id.imageViewFacebook:
                ((LoginActivity)getActivity()).sigInFacebook();
                break;
            case R.id.buttonCreateAccount:
                    if (isUserDataValid()){

                        final JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("email","11555166");
                            jsonObject.put("password","vkontakte");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        StringRequest stringPostRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        Log.i("Response", s);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.i("Response_err", volleyError.getMessage());
                            }
                        }){
                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("tmssec", jsonObject.toString());
                                Log.i("Response_Header",params.get("tmssec"));
                                return params;
                            }
                        };

                        AppController.getInstance().addToRequestQueue(stringPostRequest);
                        //saveUserData();

                    }
                break;
        }

    }
    private void saveUserData()
    {
        Toast.makeText(this.getActivity(),"User login!",Toast.LENGTH_SHORT).show();
        SharedPreferences.Editor editor = sharedPreferencesUserData.edit();
        editor.putInt("social_id", 0);
        editor.putBoolean("user_status",true);
        editor.putString("social","Kasa.in.ua");
        editor.putString("user_name",editTextName.getText().toString());
        editor.putString("user_last_name",editTextLName.getText().toString());
        editor.putString("user_phone",editTextPhone.getText().toString());
        editor.putString("user_email",editTextEMail.getText().toString());
        editor.apply();
        editor.commit();
        Intent intent = new Intent(this.getActivity(),ActivitySuccessRegistration.class);
        startActivity(intent);
    }

    private Boolean isUserDataValid() {
        Boolean isValid = true;

        for (int i=0; i<sparseBooleanArrayValidator.size(); i++){
            EditText editText = (EditText) getActivity().findViewById(sparseBooleanArrayValidator.keyAt(i));
            if (!sparseBooleanArrayValidator.valueAt(i)){
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                isValid = false;
            }else
            {
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }
        }
        if (!editTextPassword.getText().toString().equals(editTextCPassword.getText().toString()) && editTextPassword.getText() != null){
            isValid = false;
            editTextPassword.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            editTextCPassword.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            Toast.makeText(this.getActivity(),"Паролі не співпадають",Toast.LENGTH_SHORT).show();
        }else {
            editTextPassword.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            editTextCPassword.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            Log.i("User_validate",isValid.toString());
        }

        return isValid;
    }

    private class myTextWatcher implements TextWatcher{
        private View view;
        public myTextWatcher(View view){
            this.view = view;
        }
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            switch (view.getId()){
                case R.id.editTextNameCreateAccount:
                    sparseBooleanArrayValidator.put(R.id.editTextNameCreateAccount,validator.isNameValid(s.toString()));
                    break;
                case R.id.editTextLastNameCreateAccount:
                    sparseBooleanArrayValidator.put(R.id.editTextLastNameCreateAccount,validator.isLastNameValid(s.toString()));
                    break;
                case R.id.editTextPhoneCreateAccount:
                    sparseBooleanArrayValidator.put(R.id.editTextPhoneCreateAccount,validator.isPhoneValid(s.toString()));
                    break;
                case R.id.editTextEMailCreateAccount:
                    sparseBooleanArrayValidator.put(R.id.editTextEMailCreateAccount,validator.isEmailValid(s.toString()));
                    break;
                case R.id.editTextPasswordCreateAccount:
                    sparseBooleanArrayValidator.put(R.id.editTextPasswordCreateAccount,validator.isPasswordValid(s.toString()));
                    break;
                case R.id.editTextConfirmPasswordCreateAccount:
                    sparseBooleanArrayValidator.put(R.id.editTextConfirmPasswordCreateAccount,validator.isPasswordValid(s.toString()));
                    break;
            }

        }
    }



}
