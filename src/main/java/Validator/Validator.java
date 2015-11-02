package Validator;

import android.text.TextUtils;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by White on 06.07.2015.
 */
public class Validator {

    public Validator(){}

    public boolean isEmailValid(String email)
    {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.i("check_email","Validate");
            return true;
        }else{
            Log.i("check_email", "InValidate");
            return false;
        }
    }

    public boolean emailValidator(String email){
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isNameValid(String name)
    {
        String regExpn = "^[-АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЬЮЯабвгґдеєжзиіїйклмнопрстуфхцчшщьюяа-яА-Яa-zA-Z]{3,15}$";

        CharSequence inputStr = name;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.i("check_Name","Validate");
            return true;
        }else{
            Log.i("check_Name", "InValidate");
            return false;
        }
    }

    public boolean isPhoneValid(String phone)
    {
        String regExpn = "^[+0-9]{10,13}$";

        CharSequence inputStr = phone;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.i("check_Phone","Validate");
            return true;
        }else{
            Log.i("check_Phone", "InValidate");
            return false;
        }
    }

    public boolean isLastNameValid(String lastName)
    {
        String regExpn = "^[а-яА-Яa-zA-Z]{3,15}$";

        CharSequence inputStr = lastName;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.i("check_lastName","Validate");
            return true;
        }else{
            Log.i("check_lastName", "InValidate");
            return false;
        }
    }

    public boolean isNumberValid(String lastName)
    {
        String regExpn = "^[0-9]{1,4}$";

        CharSequence inputStr = lastName;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.i("check_lastName","Validate");
            return true;
        }else{
            Log.i("check_lastName", "InValidate");
            return false;
        }
    }

    public boolean isAddressValid(String address)
    {

        if(address.length() > 3) {
            Log.i("check_lastName","Validate");
            return true;
        }else{
            Log.i("check_lastName", "InValidate");
            return false;
        }
    }

    public boolean isPasswordValid(String password){

        String regExpn = "((?=.*[a-zA-Z])(?=.*[0-9]).{8,20})";

            CharSequence inputStr = password;

            Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(inputStr);

        if(matcher.matches()) {
            Log.i("check_Password","Validate");
            return true;
        }else {
            Log.i("check_Password", "InValidate");
            return false;
        }
    }
}
