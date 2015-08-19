package Singleton;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Admin on 21.04.2015.
 */
public class UserProfileSingleton {
    private Activity activity;
    private SharedPreferences sharedPreferencesUserData;
    private static final String APP_PREFERENCES = "user_profile";
    private Boolean status = false;
    private String name;
    private String lName;
    private String phone;
    private String email;
    private String country;
    private String oblast;
    private String city;
    private String address;
    private String newPost;



    public UserProfileSingleton(Activity activity) {
        this.activity = activity;
        sharedPreferencesUserData = this.activity.getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
    }
    public Boolean getStatus()
    {
        return status;
    }

    public String getName()
    {
        name = sharedPreferencesUserData.getString("user_name", "");
        return name;
    }
    public String getLastName(){
        lName = sharedPreferencesUserData.getString("user_last_name", "");
        return lName;
    }

    public String getPhone() {
        phone = sharedPreferencesUserData.getString("user_phone", "");
        return phone;
    }

    public String getEmail() {
        email = sharedPreferencesUserData.getString("user_email", "");
        return email;
    }

    public String getCountry(){
        country = sharedPreferencesUserData.getString("user_country", "");
        return country;
    }

    public String getOblast(){
        oblast = sharedPreferencesUserData.getString("user_oblast", "");
        return oblast;
    }

    public String getCity(){
        city = sharedPreferencesUserData.getString("user_city", "");
        return city;
    }

    public String getAddress(){
        address = sharedPreferencesUserData.getString("address","");
        return address;
    }

    public String getNewPost(){
        newPost = sharedPreferencesUserData.getString("number_post","");
        return newPost;
    }
}
