package webclever.sliding_menu;


import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;

import Singleton.UserProfileSingleton;
import adapter.TabsPagerAdapter;
import customlistviewapp.AppController;

import android.content.Intent;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends FragmentActivity implements ActionBar.TabListener ,View.OnClickListener,GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener  {
    private ViewPager viewPager;
    private ActionBar actionBar;


    public static final int RC_SIGN_IN = 0;
    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;
    private boolean statusUserGoogle;
    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;
    private GoogleApiClient mGoogleApiClient;

    private static String[] sMyScope = new String[]{VKScope.FRIENDS, VKScope.WALL, VKScope.PHOTOS, VKScope.NOHTTPS,"email"};
    // Tab titles
    private String[] tabs;
    private CallbackManager callbackManager;
    private Intent intent;
    private Tracker mTracker;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tabs = new String[]{getResources().getString(R.string.page_login_tabs_enter), getResources().getString(R.string.page_login_tabs_register)};
        //intent = getIntent();

        callbackManager = CallbackManager.Factory.create();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .addScope(Plus.SCOPE_PLUS_PROFILE)
                .build();

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

        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        intent = getIntent();

        actionBar = getActionBar();
        //actionBar.setHomeButtonEnabled(false);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Adding Tabs
        for (String tab_name : tabs) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }
        if (intent != null){
            actionBar.setSelectedNavigationItem(intent.getIntExtra("POSITION",0));
            viewPager.setCurrentItem(intent.getIntExtra("POSITION",0));}
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        mTracker = application.getDefaultTracker();
    }

    public void Trekking(String nameScreen){
        mTracker.setScreenName(nameScreen);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TW
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.pager);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }

        //VK
        VKCallback<VKAccessToken> callback = new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                // User passed Authorization
                getUserDataVK(res.email);
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
                Log.i("User", "User login VK error!");
            }
        };

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, callback) ) {
            super.onActivityResult(requestCode, resultCode, data);
        }
        //Facebook
        callbackManager.onActivityResult(requestCode, resultCode, data);

        //Google+
        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further errors.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        }

    }

    private void getUserDataVK(final String mail) {
        Log.i("VK", "User login VK!");
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "id,first_name,last_name,email"));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray jsonArrayUserInfo = response.json.getJSONArray("response");
                    JSONObject jsonObjectUserInfo = jsonArrayUserInfo.getJSONObject(0);
                    checkUserSigInKasa(jsonObjectUserInfo.getString("id"),jsonObjectUserInfo.getString("first_name"),jsonObjectUserInfo.getString("last_name"),mail,"vkontakte", 2);

                    Log.i("User", "VK " + response.json.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void sigInVK() {
        if (VKSdk.isLoggedIn()) {
            VKSdk.logout();
        }else {
            VKSdk.login(this, sMyScope);
        }
    }

    public boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        if (accessToken != null) {
            Log.i("User", "is login FaceBook!");
            return true;
        }else {
            Log.i("User", "is NotLogin FaceBook!");
            return false;
        }
    }

    public void sigInFacebook() {
        Log.i("user", "OnSigInFacebook");
        isLoggedIn();

        if (!isLoggedIn()) {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile, email, user_birthday"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    // App code
                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(
                                        JSONObject object,
                                        GraphResponse response) {
                                    // Application code
                                    try {
                                        JSONObject jsonObjectUserInfo = response.getJSONObject();
                                        checkUserSigInKasa(jsonObjectUserInfo.getString("id"),
                                                jsonObjectUserInfo.getString("first_name"),
                                                jsonObjectUserInfo.getString("last_name"),
                                                jsonObjectUserInfo.getString("email"),
                                                "facebook", 1);
                                        Log.i("Response_data_Facebook", jsonObjectUserInfo.toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,last_name,first_name,email,gender, birthday");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    // App code
                }

                @Override
                public void onError(FacebookException exception) {
                    // App code
                }
            });
        }else {
            LoginManager.getInstance().logOut();
        }
        isLoggedIn();
    }

    @Override
    public void onStart () {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.i("position",String.valueOf(intent.getIntExtra("position",-1)));
                setResult(1,intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onConnected(Bundle bundle) {

        /*Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();*/
        mShouldResolve = false;
        // Get user's information
        getProfileInformation();
        // Update the UI after signin
        updateUI(false);
    }

    private void getProfileInformation() {
        try {
            if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
                Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
                String personName = currentPerson.getDisplayName();
                String user_id = currentPerson.getId();

                String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
                String[] userName = personName.split(" ");
                checkUserSigInKasa(user_id, userName[0], userName[1].trim(), email, "google_oauth", 3);
                Log.e("User", "Name: " + userName[0] + userName[1] + ", email: " + email);

            } else {
                /*Toast.makeText(getApplicationContext(),
                        "Person information is null", Toast.LENGTH_LONG).show();*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUI(boolean isSignedIn) {
        statusUserGoogle = isSignedIn;
    }

    @Override
    public void onConnectionSuspended(int i) {
        //mGoogleApiClient.connect();
        updateUI(false);
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            updateUI(false);
        }
    }

    private void showErrorDialog(ConnectionResult connectionResult) {
        int errorCode = connectionResult.getErrorCode();

        if (GooglePlayServicesUtil.isUserRecoverableError(errorCode)) {
            // Show the default Google Play services error dialog which may still start an intent
            // on our behalf if the user can resolve the issue.
            GooglePlayServicesUtil.getErrorDialog(errorCode, this, RC_SIGN_IN,
                    new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            mShouldResolve = false;
                            updateUI(false);
                        }
                    }).show();
        } else {
            mShouldResolve = false;
            updateUI(false);
        }
    }

    public void closeActivity() {
        LoginActivity.this.finish();
    }

    private void sigOutGooglePlus(){
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mShouldResolve = false;
            Log.i("User", "logout G+ !");
        }
    }

    public void sigInGooglePlus(){
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            mShouldResolve = false;
            Log.i("User", "logout G+ !");
        } else {
            mGoogleApiClient.connect();
            Log.i("User", "sigin with google+");
            mShouldResolve = true;
        }

    }

    public void checkUserSigInKasa(final String user_id, final String user_name, final String user_last_name, final String user_email, final String social_name, final Integer soc_id){

        String url = "http://tms.net.ua/api/checkAppUser";
        final JSONObject jsonObject = new JSONObject();
        final JSONObject jsonObjectParams = new JSONObject();
        try {
            jsonObject.put("user_id",user_id);
            jsonObject.put("service",social_name);
            jsonObject.put("name",user_name);

            jsonObjectParams.put("token","3748563");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response user data", s);
                        try {
                            JSONObject jsonObjectUser = new JSONObject(s);

                            if (jsonObjectUser.has("user_id")){
                            UserProfileSingleton userProfileSingleton = new UserProfileSingleton(LoginActivity.this);
                            userProfileSingleton.setStatus(true);
                            userProfileSingleton.setUserId(jsonObjectUser.getString("user_id"));
                            userProfileSingleton.setToken(jsonObjectUser.getLong("token"));
                            userProfileSingleton.setNameSocial(social_name);
                            userProfileSingleton.setSocialId(soc_id);
                            sigOutGooglePlus();
                            closeActivity();
                            }else {
                                startRegistrationActivity(user_id, user_name, user_last_name, user_email, social_name, soc_id);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                        Log.i("Response_err", String.valueOf(volleyError.getMessage()));
                        startRegistrationActivity(user_id, user_name, user_last_name, user_email, social_name, soc_id);

            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String string_json = jsonObject.toString();
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
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);

    }

    public void startRegistrationActivity(String user_id,String user_name, String user_last_name, String user_email, String social_name, Integer social_id) {

        Intent intent = new Intent(this,RegistrationActivity.class);
        intent.putExtra("SOCIAL",social_name);
        intent.putExtra("SOCIAL_ID",social_id);
        intent.putExtra("USER_ID",user_id);
        intent.putExtra("USER_NAME",user_name);
        intent.putExtra("USER_LNAME",user_last_name);
        intent.putExtra("USER_EMAIL",user_email);
        startActivity(intent);
        sigOutGooglePlus();
        closeActivity();

    }

}
