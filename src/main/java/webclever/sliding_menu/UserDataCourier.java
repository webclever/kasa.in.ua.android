package webclever.sliding_menu;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import Singleton.UserProfileSingleton;
import Validator.Validator;
import interfaces.OnBackPressedListener;


public class UserDataCourier extends Fragment implements OnBackPressedListener {

    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private Validator validator = new Validator();
    private UserProfileSingleton userProfile;
    private TextView textViewTimer;
    private Integer paymentMethod;
    private FragmentManager fragmentManager;
    private Bundle bundle;
    public UserDataCourier() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_user_data_courier, container, false);
        fragmentManager  = getFragmentManager();

        if(getArguments() != null){
            bundle = getArguments();
            paymentMethod = bundle.getInt("payment_method");
        }

        Toast.makeText(getActivity().getApplicationContext(), getArguments().getString("type"), Toast.LENGTH_SHORT).show();
        userProfile = new UserProfileSingleton(this.getActivity());

        EditText editTextName = (EditText) rootView.findViewById(R.id.editText15);
        editTextName.setText(userProfile.getName());
        editTextName.addTextChangedListener(new TextWatcherETicket(editTextName));
        sparseBooleanArray.put(editTextName.getId(), validator.isNameValid(userProfile.getName()));

        EditText editTextLasName = (EditText) rootView.findViewById(R.id.editText16);
        editTextLasName.setText(userProfile.getLastName());
        editTextLasName.addTextChangedListener(new TextWatcherETicket(editTextLasName));
        sparseBooleanArray.put(editTextLasName.getId(), validator.isLastNameValid(userProfile.getLastName()));

        EditText editTextPhone = (EditText) rootView.findViewById(R.id.editText17);
        if (!userProfile.getPhone().equals("")){
            editTextPhone.setText(userProfile.getPhone());
        }else {
            editTextPhone.setText("+38");
        }
        editTextPhone.addTextChangedListener(new TextWatcherETicket(editTextPhone));
        sparseBooleanArray.put(editTextPhone.getId(), validator.isPhoneValid(userProfile.getPhone()));

        EditText editTextEmail = (EditText) rootView.findViewById(R.id.editText18);
        editTextEmail.setText(userProfile.getEmail());
        editTextEmail.addTextChangedListener(new TextWatcherETicket(editTextEmail));
        sparseBooleanArray.put(editTextEmail.getId(), validator.isEmailValid(userProfile.getEmail()));

        EditText editTextOblast = (EditText) rootView.findViewById(R.id.editText22);
        editTextOblast.setText(userProfile.getRegion());
        editTextOblast.addTextChangedListener(new TextWatcherETicket(editTextOblast));
        sparseBooleanArray.put(editTextOblast.getId(), validator.isNameValid(userProfile.getRegion()));

        EditText editTextCity = (EditText) rootView.findViewById(R.id.editText23);
        editTextCity.setText(userProfile.getCity());
        editTextCity.addTextChangedListener(new TextWatcherETicket(editTextCity));
        sparseBooleanArray.put(editTextCity.getId(), validator.isNameValid(userProfile.getCity()));

        EditText editTextAddress = (EditText) rootView.findViewById(R.id.editText24);
        editTextAddress.setText(userProfile.getAddress());
        editTextAddress.addTextChangedListener(new TextWatcherETicket(editTextAddress));
        sparseBooleanArray.put(editTextAddress.getId(), validator.isAddressValid(userProfile.getAddress()));

        textViewTimer = (TextView) rootView.findViewById(R.id.textView100);

        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);
        switch (paymentMethod){
            case 1:
                buttonConfirm.setText("оформити замовлення");
                break;
            case 2:
                buttonConfirm.setText("перейти до оплати");
                break;
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidUserData()){
                    Fragment fragment = new FragmentSuccessfulOrder();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                }
            }
        });
        startService();
        return rootView;
    }


    private void startService(){

        long timer = ((MainActivity)getActivity()).getTimer();
        if (timer != 0){
            new CountDownTimer(timer,1000) {

                @Override
                public void onTick(long millis) {
                    int seconds = (int) (millis / 1000) % 60 ;
                    int minutes = (int) ((millis / (1000*60)) % 60);

                    String text = String.format("%02d : %02d",minutes,seconds);
                    textViewTimer.setText(text);

                }

                @Override
                public void onFinish() {
                    textViewTimer.setText("Бронювання скасоване !");
                    showAlertDialog();
                }
            }.start();
        }
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("На жаль, відведений час на оформлення замовлення завершився і тимчасове замовлення було скасовано.");
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragment = new FragmentBasket();
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, "1").commit();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onBackPressed() {
        Fragment fragment = new FragmentDeliveryOrder();
        fragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    private class TextWatcherETicket implements TextWatcher {

        private View view;
        public TextWatcherETicket(View view)
        {
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

            switch (view.getId())
            {
                case R.id.editText15:
                    sparseBooleanArray.put(R.id.editText15,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText16:
                    sparseBooleanArray.put(R.id.editText16,validator.isLastNameValid(editable.toString()));
                    break;
                case R.id.editText17:
                    sparseBooleanArray.put(R.id.editText17,validator.isPhoneValid(editable.toString()));
                    break;
                case R.id.editText18:
                    sparseBooleanArray.put(R.id.editText18,validator.isEmailValid(editable.toString()));
                    break;
                case R.id.editText22:
                    sparseBooleanArray.put(R.id.editText22, validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText23:
                    sparseBooleanArray.put(R.id.editText23,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText24:
                    sparseBooleanArray.put(R.id.editText24,validator.isAddressValid(editable.toString()));
                    break;
            }

        }
    }

    private Boolean getValidUserData() {
        Boolean valid = true;

        for(int i=0; i < sparseBooleanArray.size(); i++)
        {
            if (sparseBooleanArray.valueAt(i))
            {
                EditText editText = (EditText)getActivity().findViewById(sparseBooleanArray.keyAt(i));
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_true));
            }else {
                valid = false;
                EditText editText = (EditText)getActivity().findViewById(sparseBooleanArray.keyAt(i));
                editText.setBackground(getResources().getDrawable(R.drawable.editbox_bacground_false));
            }
        }

        return valid;
    }


}
