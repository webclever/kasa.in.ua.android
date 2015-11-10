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
    public void setName(String name){
        editor.putString("user_name",name);
        editor.commit();
    }
    public String getName() {
        return sharedPreferencesUserData.getString("user_name", "");
    }
    public void setLastName(String userLastName){
        editor.putString("user_last_name",userLastName);
        editor.commit();
    }
    public String getLastName(){
        return sharedPreferencesUserData.getString("user_last_name", "");
    }
    public void setSurname(String surname){
        editor.putString("user_surname", surname);
        editor.commit();
    }

    public String getSurname(){
        return sharedPreferencesUserData.getString("user_surname","");
    }

    public void setPhone(String phone){
        editor.putString("user_phone",phone);
        editor.commit();
    }
    public String getPhone() {
        return sharedPreferencesUserData.getString("user_phone", "");
    }
    public void setEmail(String email){
        editor.putString("user_email",email);
        editor.commit();
    }
    public String getEmail() {
        return sharedPreferencesUserData.getString("user_email", "");
    }
    public void setCountry(String country){
        editor.putString("user_country",country);
        editor.commit();
    }
    public String getCountry(){
        return sharedPreferencesUserData.getString("user_country", "");
    }

    public void setRegion(String region){
        editor.putString("user_region",region);
        editor.commit();
    }
    public String getRegion(){
        return sharedPreferencesUserData.getString("user_region", "");
    }
    public void setCity(String city){
        editor.putString("user_city", city);
    }

    public String getCity(){
        return sharedPreferencesUserData.getString("user_city", "");
    }
    public void setAddress(String address){
        editor.putString("user_address",address);
    }

    public String getAddress(){
        return sharedPreferencesUserData.getString("user_address","");
    }
    public void setNewPost(String numberNewPost){
        editor.putString("number_post",numberNewPost);
        editor.commit();
    }

    public String getNewPost(){
        return sharedPreferencesUserData.getString("number_post","");
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
