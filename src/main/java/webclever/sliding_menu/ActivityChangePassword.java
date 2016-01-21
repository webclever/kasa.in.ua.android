package webclever.sliding_menu;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Singleton.UserProfileSingleton;
import Validator.Validator;
import customlistviewapp.AppController;

public class ActivityChangePassword extends FragmentActivity {
    private EditText editTextOldPass;
    private EditText editTextNewPass;
    private EditText editTextCNewPass;
    private Validator validator;
    private UserProfileSingleton userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ActionBar actionBar = getActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        validator = new Validator();

        userProfile = new UserProfileSingleton(this);
        Log.i("user_token_c",String.valueOf(userProfile.getToken()));


        editTextOldPass = (EditText) findViewById(R.id.editTextOldPassword);
        editTextNewPass = (EditText) findViewById(R.id.editTextNewPassword);
        editTextCNewPass = (EditText) findViewById(R.id.editTextConfirmNewPassword);
        Button buttonSaveChanges = (Button) findViewById(R.id.buttonSaveChanges);
        buttonSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringNewPass = editTextNewPass.getText().toString();
                String stringCNewPass = editTextCNewPass.getText().toString();
                if (stringCNewPass.equals(stringNewPass) && validator.isPasswordValid(stringNewPass)) {

                    changePassword();


                } else {
                    editTextNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                    editTextCNewPass.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
                }

            }
        });
        // Obtain the shared Tracker instance.
        AppController application = (AppController) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen change password.");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private boolean changePassword() {

        final JSONObject jsonObjectHeader = new JSONObject();

        try {

            jsonObjectHeader.put("user_id",userProfile.getUserId());
            jsonObjectHeader.put("token",userProfile.getToken());
            jsonObjectHeader.put("old_password",editTextOldPass.getText().toString());
            jsonObjectHeader.put("new_password", editTextNewPass.getText().toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = "http://tms.net.ua/api/changePassword";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String s) {
                        Log.i("Response p", s);
                        try {
                            JSONObject jsonObjectUserData = new JSONObject(s);
                            if (jsonObjectUserData.getBoolean("change_password")){

                                Toast.makeText(getApplicationContext(),getResources().getString(R.string.page_user_change_password_successful),Toast.LENGTH_SHORT).show();
                                ActivityChangePassword.this.finish();
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
                String string_json = jsonObjectHeader.toString();
                String header =  " " + Base64.encodeToString(string_json.getBytes(), Base64.NO_WRAP);
                params.put("tmssec", header);

                Log.i("Response_HeaderNoEncode",string_json);
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

        return false;
    }


}
