package webclever.sliding_menu;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.UserProfileSingleton;
import Validator.Validator;
import customlistviewapp.AppController;

public class RegistrationActivity extends FragmentActivity implements
        ResultCallback<People.LoadPeopleResult>  {

    private SparseBooleanArray sparseBooleanArrayValidator;
    private EditText editTextUserName;
    private EditText editTextUserLastNAme;
    private EditText editTextUserPhone;
    private EditText editTextUserEmail;
    private Validator validator = new Validator();

    private String stringSocial;
    private Integer integerSocialID;
    private String stringUserID;
    private String stringUserName;
    private String stringLUserName;
    private String stringUserPhone;
    private String stringUserEMail;
    private final String urlUserRegistration = "http://tms.net.ua/api/register";

    //private GoogleApiClient mGoogleApiClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        if (intent != null){
        stringSocial = intent.getStringExtra("SOCIAL");
        integerSocialID = intent.getIntExtra("SOCIAL_ID", 0);
        stringUserID = intent.getStringExtra("USER_ID");
        stringUserName = intent.getStringExtra("USER_NAME");
        stringLUserName = intent.getStringExtra("USER_LNAME");
        stringUserPhone = intent.getStringExtra("USER_PHONE");
        stringUserEMail = intent.getStringExtra("USER_EMAIL");}

        TextView textViewSocialLogin = (TextView) findViewById(R.id.textView82);
        textViewSocialLogin.setText(stringSocial);
        sparseBooleanArrayValidator = new SparseBooleanArray();
        editTextUserName = (EditText) findViewById(R.id.textPersonName);
        editTextUserName.addTextChangedListener(new myTextWatcher(editTextUserName));
        editTextUserName.setText(stringUserName);
        sparseBooleanArrayValidator.put(editTextUserName.getId(), validator.isNameValid(stringUserName));
        editTextUserLastNAme = (EditText) findViewById(R.id.editText26);
        editTextUserLastNAme.addTextChangedListener(new myTextWatcher(editTextUserLastNAme));
        editTextUserLastNAme.setText(stringLUserName);
        sparseBooleanArrayValidator.put(editTextUserLastNAme.getId(), validator.isLastNameValid(stringLUserName));
        editTextUserPhone = (EditText) findViewById(R.id.editTextPhone);
        editTextUserPhone.addTextChangedListener(new myTextWatcher(editTextUserPhone));
        editTextUserPhone.setText(stringUserPhone);
        sparseBooleanArrayValidator.put(editTextUserPhone.getId(), validator.isPhoneValid("+380"));
        editTextUserEmail = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextUserEmail.addTextChangedListener(new myTextWatcher(editTextUserEmail));
        editTextUserEmail.setText(stringUserEMail);
        sparseBooleanArrayValidator.put(editTextUserEmail.getId(), validator.isEmailValid(stringUserEMail));
        Button buttonRegistration = (Button) findViewById(R.id.buttonReg);
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    RegistrationUser();
                }
            }
        });
        VKSdk.wakeUpSession(this, new VKCallback<VKSdk.LoginState>() {
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

        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen confirm or change user data from social.");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    private void RegistrationUser(){

        String url = "http://tms.net.ua/api/register";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject jsonObjectParams = new JSONObject();
        try {

            jsonObject.put("user_id",stringUserID);
            jsonObject.put("service",stringSocial);


            jsonObjectParams.put("name",editTextUserName.getText().toString());
            jsonObjectParams.put("last_name",editTextUserLastNAme.getText().toString());
            jsonObjectParams.put("email",editTextUserEmail.getText().toString());
            jsonObjectParams.put("phone",editTextUserPhone.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                        try {

                            JSONObject jsonObjectUser = new JSONObject(s);
                            UserProfileSingleton userProfileSingleton = new UserProfileSingleton(RegistrationActivity.this);
                            userProfileSingleton.setNameSocial(stringSocial);
                            userProfileSingleton.setSocialId(integerSocialID);
                            userProfileSingleton.setStatus(true);
                            userProfileSingleton.setUserId(jsonObjectUser.getString("user_id"));
                            userProfileSingleton.setToken(jsonObjectUser.getLong("token"));
                            Toast.makeText(RegistrationActivity.this, getResources().getString(R.string.page_registration_success), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getApplicationContext(), ActivitySuccessRegistration.class);
                            startActivity(intent);

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
                params.put("tmssec", jsonObject.toString());
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

    @Override
    public void onResult(People.LoadPeopleResult loadPeopleResult) {

    }

    private class myTextWatcher implements TextWatcher{

        private View view;
        myTextWatcher(View view){
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
                case R.id.textPersonName:
                    sparseBooleanArrayValidator.put(R.id.textPersonName,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText26:
                    sparseBooleanArrayValidator.put(R.id.editText26,validator.isLastNameValid(editable.toString()));
                    break;
                case R.id.editTextPhone:
                    sparseBooleanArrayValidator.put(R.id.editTextPhone,validator.isPhoneValid(editable.toString()));
                    break;
                case R.id.editTextEmailAddress:
                    sparseBooleanArrayValidator.put(R.id.editTextEmailAddress,validator.isEmailValid(editable.toString()));
                    break;
            }

        }
    }
    private boolean validation() {
        Boolean valid = true;
        for (int i=0; i<sparseBooleanArrayValidator.size(); i++){
            EditText editText = (EditText) findViewById(sparseBooleanArrayValidator.keyAt(i));
            if (!sparseBooleanArrayValidator.valueAt(i)){
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                valid = false;
            }else {
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }
        }
        return valid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                    LogOut(integerSocialID);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy (){
        super.onDestroy();
    }

    private void LogOut(Integer soc_id){
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
            /*case 3:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    Log.i("User", "logout G++ !");
                } else {
                    Log.i("User", "no logout G++ !");
                }
                break;*/
        }
    }

    @Override
    public void onBackPressed (){
        LogOut(integerSocialID);
        finish();
    }

}
