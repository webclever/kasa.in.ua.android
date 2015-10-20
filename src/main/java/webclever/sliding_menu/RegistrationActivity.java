package webclever.sliding_menu;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import Validator.Validator;

/**
 * Created by Admin on 22.10.2014.
 */
public class RegistrationActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<People.LoadPeopleResult>  {

    private SparseBooleanArray sparseBooleanArrayValidator;
    private TextView textViewSocialLogin;
    private EditText editTextUserName;
    private EditText editTextUserLastNAme;
    private EditText editTextUserPhone;
    private EditText editTextUserEmail;
    private Button buttonRegistration;
    private Validator validator = new Validator();

    private String stringSocial;
    private Integer integerSocialID;
    private String stringUserName;
    private String stringLUserName;
    private String stringUserPhone;
    private String stringUserEMail;

    private SharedPreferences sharedPreferencesUserData;
    private static final String APP_PREFERENCES = "user_profile";

    private final String urlUserRegistration = "http://tms.webclever.in.ua/api/register";

    private GoogleApiClient mGoogleApiClient;

    private Boolean statusUserLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        Intent intent = getIntent();

        sharedPreferencesUserData = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        if (intent != null){
        stringSocial = intent.getStringExtra("SOCIAL");
        integerSocialID = intent.getIntExtra("SOCIAL_ID",0);
        stringUserName = intent.getStringExtra("USER_NAME");
        stringLUserName = intent.getStringExtra("USER_LNAME");
        stringUserPhone = intent.getStringExtra("USER_PHONE");
        stringUserEMail = intent.getStringExtra("USER_EMAIL");}

        textViewSocialLogin = (TextView)findViewById(R.id.textView82);
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
        sparseBooleanArrayValidator.put(editTextUserPhone.getId(), validator.isPhoneValid(stringUserPhone));
        editTextUserEmail = (EditText) findViewById(R.id.editTextEmailAddress);
        editTextUserEmail.addTextChangedListener(new myTextWatcher(editTextUserEmail));
        editTextUserEmail.setText(stringUserEMail);
        sparseBooleanArrayValidator.put(editTextUserEmail.getId(), validator.isEmailValid(stringUserEMail));
        buttonRegistration = (Button) findViewById(R.id.buttonReg);
        buttonRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    Toast.makeText(RegistrationActivity.this, "Ви успішно зареєструвались", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = sharedPreferencesUserData.edit();

                    editor.putBoolean("user_status", true);
                    editor.putString("social", stringSocial);
                    editor.putInt("social_id", integerSocialID);
                    editor.putString("user_name", editTextUserName.getText().toString());
                    editor.putString("user_last_name", editTextUserLastNAme.getText().toString());
                    editor.putString("user_phone", editTextUserPhone.getText().toString());
                    editor.putString("user_email", editTextUserEmail.getText().toString());
                    editor.apply();
                    statusUserLogin = true;
                    Intent intent = new Intent(getApplicationContext(), ActivitySuccessRegistration.class);
                    startActivity(intent);
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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .build();


    }

    @Override
    public void onConnected(Bundle bundle) {

        Plus.PeopleApi.loadVisible(mGoogleApiClient, null).setResultCallback(this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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
                if (!statusUserLogin){
                    LogOut(integerSocialID);}
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
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
            case 3:
                if (mGoogleApiClient.isConnected()) {
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    Log.i("User", "logout G++ !");
                } else {
                    Log.i("User", "no logout G++ !");
                }
                break;
        }
    }

    @Override
    public void onBackPressed (){
        LogOut(integerSocialID);
        finish();
    }


}
