package webclever.sliding_menu;


import android.content.Intent;
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
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.UserProfileSingleton;
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
    private SparseBooleanArray sparseBooleanArrayValidator;
    private Validator validator = new Validator();

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

        ImageView btnSignIn = (ImageView) rootView.findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);

        loginButton = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
        loginButton.setText("");
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                ((LoginActivity)getActivity()).checkUserSigInKasa(String.valueOf(result.data.getUserId()),result.data.getUserName(),"","","Twitter",6);
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
        Button buttonCreateAccount = (Button) rootView.findViewById(R.id.buttonCreateAccount);
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

                        final JSONObject jsonObjectHeader = new JSONObject();
                        final JSONObject jsonObjectBody = new JSONObject();

                        try {
                            jsonObjectHeader.put("email",editTextEMail.getText().toString());
                            jsonObjectHeader.put("password",editTextPassword.getText().toString());
                            jsonObjectBody.put("name",editTextName.getText().toString());
                            jsonObjectBody.put("last_name",editTextLName.getText().toString());
                            jsonObjectBody.put("phone",editTextPhone.getText().toString());

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String url = "http://tms.webclever.in.ua/api/register";
                        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                                new Response.Listener<String>()
                                {
                                    @Override
                                    public void onResponse(String s) {
                                        Log.i("Response", s);
                                        try {
                                            JSONObject jsonObjectUserData = new JSONObject(s);
                                            UserProfileSingleton userProfileSingleton = new UserProfileSingleton(getActivity());
                                            if (!jsonObjectUserData.has("user")){
                                                userProfileSingleton.setUserId(jsonObjectUserData.getString("user_id"));
                                                userProfileSingleton.setToken(jsonObjectUserData.getInt("token"));
                                                userProfileSingleton.setStatus(true);
                                                userProfileSingleton.setName(jsonObjectUserData.getString("name"));
                                                userProfileSingleton.setLastName(jsonObjectUserData.getString("last_name"));
                                                userProfileSingleton.setPhone(jsonObjectUserData.getString("phone"));
                                                userProfileSingleton.setEmail(jsonObjectUserData.getString("email"));
                                                Intent intent = new Intent(getActivity(),ActivitySuccessRegistration.class);
                                                startActivity(intent);

                                            }else {
                                                Toast.makeText(getActivity(),jsonObjectUserData.getString("user"),Toast.LENGTH_SHORT).show();
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
                                params.put("userInfo", jsonObjectBody.toString());
                                Log.i("Params",params.toString());
                                return params;
                            }
                        };

                        AppController.getInstance().addToRequestQueue(stringPostRequest);

                    }
                break;
        }

    }

    private Boolean isUserDataValid() {
        Boolean isValid = true;

        for (int i=0; i < sparseBooleanArrayValidator.size(); i++){
            EditText editText = (EditText) getActivity().findViewById(sparseBooleanArrayValidator.keyAt(i));
            if (!sparseBooleanArrayValidator.valueAt(i)){
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                isValid = false;
            }else
            {
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }
        }
        if (!editTextPassword.getText().toString().equals(editTextCPassword.getText().toString())){
            isValid = false;
            editTextPassword.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            editTextCPassword.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            Toast.makeText(this.getActivity(),"Паролі не співпадають",Toast.LENGTH_SHORT).show();
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
                    //sparseBooleanArrayValidator.put(R.id.editTextEMailCreateAccount,validator.isEmailValid(s.toString()));
                    sparseBooleanArrayValidator.put(R.id.editTextEMailCreateAccount,validator.emailValidator(s.toString()));
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
