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
    private  SharedPreferences.Editor editor;
    private static final String APP_PREFERENCES = "user_profile";
    public UserProfileSingleton(Activity activity) {
        this.activity = activity;
        sharedPreferencesUserData = this.activity.getSharedPreferences(APP_PREFERENCES,Context.MODE_PRIVATE);
        editor = sharedPreferencesUserData.edit();
    }
    public void setStatus(Boolean statusUser){
        editor.putBoolean("user_status",statusUser);
        editor.commit();
    }
    public Boolean getStatus()
    {
        return sharedPreferencesUserData.getBoolean("user_status", false);
    }

    public void setUserId(String userID){
        editor.putString("user_id",userID);
        editor.commit();
    }

    public String getUserId(){
        return sharedPreferencesUserData.getString("user_id","");
    }

    public void setToken(Long token){
        editor.putLong("user_token", token);
        editor.commit();
    }

    public Long getToken(){
        return sharedPreferencesUserData.getLong("user_token", -1);
    }


    public Integer getSocialId(){
        return sharedPreferencesUserData.getInt("social_id", -1);
    }

    public void setSocialId(Integer socialId){
        editor.putInt("social_id", socialId);
        editor.commit();
    }

    public String getNameSocial(){
        return sharedPreferencesUserData.getString("social_name","");
    }

    public void setNameSocial(String social_name){
        editor.putString("social_name", social_name);
        editor.commit();
    }

    public void deleteUserData(){
        editor.clear();
        editor.apply();
    }


}
