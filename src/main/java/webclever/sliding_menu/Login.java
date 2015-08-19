package webclever.sliding_menu;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by Женя on 06.08.2015.
 */
public class Login extends Fragment implements View.OnClickListener {

    private TwitterLoginButton loginButtonTW;
    private TextView textViewRememberPassword;
    private Button buttonLogin;
    private Thread thread;
    public Login() {
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
                ((LoginActivity)getActivity()).startRegistrationActivity("Twitter",5, result.data.getUserName(), "", "");
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.i("LogTW", "exception");
            }
        });

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

        thread = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(3500); // As I am using LENGTH_LONG in Toast
                    ((LoginActivity)getActivity()).closeActivity();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

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
                Toast.makeText(this.getActivity(),"User login",Toast.LENGTH_LONG).show();
                thread.start();
                break;
        }
    }
}
