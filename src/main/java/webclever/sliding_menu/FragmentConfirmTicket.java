package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import interfaces.OnBackPressedListener;

/**
 * Created by Admin on 01.02.2015.
 */
public class FragmentConfirmTicket extends Fragment implements OnBackPressedListener {

    private final String TAG = "EditBox";

    private Button buttonDeliverTicket;

    public FragmentConfirmTicket(){}
    private RadioGroup mRadioGroup;
    private RadioGroup mRadioGroup2;

    private EditText editTextName;
    private EditText editTextPhone;
    private EditText editTextMail;

    private Boolean aBooleanCheckEditName = false;
    private Boolean aBooleanCheckEditPhone = false;
    private Boolean aBooleanCheckEditMail = false;
    private Boolean aBooleanRadioButoonGroup = false;
    private Boolean aBooleanRadioButoonGroup1 = false;
    private Boolean aBooleanAlleditBox = false;

    private ImageView imageViewDelivery;
    private ImageView imageViewPersoneData;
    private ImageView imageViewPayment;


    private CharSequence phone_number = "+3";

    @Override
    public View onCreateView (LayoutInflater inflater,ViewGroup conteiner,Bundle savedInstanceState)
    {
        View rootView=inflater.inflate(R.layout.fragment_confirm_ticket,conteiner,false);

        imageViewDelivery = (ImageView) rootView.findViewById(R.id.imageViewDelivery);
        imageViewPersoneData = (ImageView) rootView.findViewById(R.id.imageViewPersonData);
        imageViewPayment = (ImageView) rootView.findViewById(R.id.imageViewPayment);

        editTextName = (EditText) rootView.findViewById(R.id.editTextName);
        editTextPhone = (EditText) rootView.findViewById(R.id.editTextphone);
        editTextPhone.setText("+38");
        editTextMail = (EditText) rootView.findViewById(R.id.editTextmail);


        mRadioGroup = (RadioGroup) rootView.findViewById(R.id.radiogroup);
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                imageViewDelivery.setImageResource(R.mipmap.ic_launcher_tick);
                imageViewDelivery.setBackgroundResource(R.drawable.circle_bacground1);
                aBooleanRadioButoonGroup = true;
                checkAll();

                Log.i("checked","mRadioGroup");
            }
        });

        mRadioGroup2 = (RadioGroup) rootView.findViewById(R.id.radiogroup2);
        mRadioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                imageViewPayment.setImageResource(R.mipmap.ic_launcher_tick);
                imageViewPayment.setBackgroundResource(R.drawable.circle_bacground1);
                aBooleanRadioButoonGroup1 = true;
                checkAll();
            }
        });

        editTextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {

                    aBooleanCheckEditName = true;
                    checkEditBox();
                } else {
                    aBooleanCheckEditName = false;
                    checkEditBox();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        editTextPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                phone_number = charSequence;
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if (phone_number.length() > 0 & phone_number.length() == 13) {
                    aBooleanCheckEditPhone = true;
                    checkEditBox();
                }

            }
        });

        editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    if (phone_number.length() > 0 & phone_number.length() == 13) {
                        aBooleanCheckEditPhone = true;
                        checkEditBox();
                    } else {
                        aBooleanCheckEditPhone = false;
                        checkEditBox();
                    }
                }else {
                    if (phone_number.length() > 0 & phone_number.length() == 13) {
                        aBooleanCheckEditPhone = true;
                        checkEditBox();
                    }
                }
            }
        });



        editTextMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (isEmailValid(charSequence.toString()))
                {
                    aBooleanCheckEditMail = true;
                    isEmailValid(charSequence.toString());
                    checkEditBox();
                }else {
                    aBooleanCheckEditMail = false;
                    checkEditBox();
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        buttonDeliverTicket  = (Button)rootView.findViewById(R.id.confirm);
        buttonDeliverTicket.setEnabled(false);
        buttonDeliverTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id", 0);
                Fragment fragment = new FragmentNumberOrder();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
        });
        return  rootView;
    }

    private void checkEditBox()
    {
        if (aBooleanCheckEditName & aBooleanCheckEditPhone & aBooleanCheckEditMail)
        {   imageViewPersoneData.setImageResource(R.mipmap.ic_launcher_tick);
            imageViewPersoneData.setBackgroundResource(R.drawable.circle_bacground1);
            aBooleanAlleditBox = true;
            checkAll();
            Log.i(TAG,"all_true");
        }else {
            imageViewPersoneData.setImageResource(R.drawable.ic_list_remove);
            imageViewPersoneData.setBackgroundResource(R.drawable.circle_bacground);
            aBooleanAlleditBox = false;
            checkAll();
            Log.i(TAG, "not_all_true");
        }

        if (!aBooleanCheckEditPhone)
        {
            Toast.makeText(getActivity().getApplicationContext(),getResources().getString(R.string.page_confirm_ticket_toast_not_valid_phone),Toast.LENGTH_SHORT).show();
            editTextPhone.setBackgroundColor(getResources().getColor(R.color.red));
        }else
        {
            editTextPhone.setBackgroundColor(getResources().getColor(R.color.white));
        }

    }
    private void checkAll()
    {
        if (aBooleanRadioButoonGroup & aBooleanRadioButoonGroup1 & aBooleanAlleditBox)
        {
            buttonDeliverTicket.setEnabled(true);
        }else {
            buttonDeliverTicket.setEnabled(false);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new FragmentBasket();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();

    }

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
            Log.i("check_email","InValidate");
            return false;
        }
    }


}
