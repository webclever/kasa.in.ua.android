package webclever.sliding_menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import customlistviewapp.AppController;

/**
 * Created by Женя on 06.08.2015.
 */
public class FragmentLogin extends Fragment implements View.OnClickListener {

    private TwitterLoginButton loginButtonTW;
    private TextView textViewRememberPassword;
    private Button buttonLogin;
    private EditText editTextUserLogin;
    private EditText editTextUserPassword;
    private final String url = "http://tms.webclever.in.ua/api/login";
    public FragmentLogin() {
        // Required empty public constructor
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login,container,false);
        ImageView btnSignIn = (ImageView) rootView.findViewById(R.id.sign_in_button);
        btnSignIn.setOnClickListener(this);

        loginButtonTW = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
        loginButtonTW.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls

                Log.i("LogTW", "userLogin");
                ((LoginActivity)getActivity()).checkUserSigInKasa(String.valueOf(result.data.getUserId()),result.data.getUserName(),"","","twitter",6);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.i("LogTW", "exception");
            }
        });

        editTextUserLogin = (EditText) rootView.findViewById(R.id.editTextEmailLogin);
        editTextUserPassword = (EditText) rootView.findViewById(R.id.editText25);

        textViewRememberPassword = (TextView) rootView.findViewById(R.id.textView73);
        textViewRememberPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), RestorePasswordActivity.class);
                startActivity(intent);
            }
        });
        ImageView imageViewFacebook = (ImageView)rootView.findViewById(R.id.imageViewFacebook);
        imageViewFacebook.setOnClickListener(this);

        ImageView imageViewVK = (ImageView) rootView.findViewById(R.id.imageViewVK);
        imageViewVK.setOnClickListener(this);

        buttonLogin = (Button) rootView.findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //callbackManager.onActivityResult(requestCode, resultCode, data);
        loginButtonTW.onActivityResult(requestCode, resultCode, data);
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
            case R.id.buttonLogin:
                if (editTextUserLogin.getText().length() > 0 && editTextUserPassword.getText().length() > 0){
                LogKasa();}
                break;
        }
    }

    private void LogKasa(){

        Log.i("UserLogin","UserLogin" + editTextUserLogin.getText().toString() + " UserPassword " + editTextUserPassword.getText().toString());
        final JSONObject jsonObjectHeader = new JSONObject();

        try {
            jsonObjectHeader.put("email",editTextUserLogin.getText().toString());
            jsonObjectHeader.put("password",editTextUserPassword.getText().toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response", s);
                        try {

                            JSONObject jsonObjectUserData = new JSONObject(s);
                            UserProfileSingleton userProfileSingleton = new UserProfileSingleton(getActivity());
                            userProfileSingleton.setNameSocial("Kasa.in.ua");
                            userProfileSingleton.setSocialId(0);
                            userProfileSingleton.setUserId(jsonObjectUserData.getString("user_id"));
                            userProfileSingleton.setToken(jsonObjectUserData.getLong("token"));
                            userProfileSingleton.setStatus(true);
                            Log.i("user_token_c", String.valueOf(userProfileSingleton.getToken()));
                            getActivity().finish();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("Response_err", String.valueOf(volleyError.getMessage()));
               /* Toast.makeText(getActivity(),"Неправильний логін або пароль!",Toast.LENGTH_SHORT).show();*/
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("tmssec", jsonObjectHeader.toString());
                Log.i("Response_Header",params.get("tmssec"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("token","3748563");
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);

    }
}
