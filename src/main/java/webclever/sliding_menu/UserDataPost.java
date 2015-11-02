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


public class UserDataPost extends Fragment implements OnBackPressedListener {

    private SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
    private Validator validator = new Validator();
    private UserProfileSingleton userProfile;

    private TextView textViewTimer;
    private CountDownTimer countDownTimer;
    private FragmentManager fragmentManager;

    private Integer paymentMethod;

    public UserDataPost() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_userdata_post, container, false);
        Toast.makeText(getActivity().getApplicationContext(), getArguments().getString("type"), Toast.LENGTH_SHORT).show();

        Bundle bundle = getArguments();
        if(bundle != null){
            paymentMethod = bundle.getInt("payment_method");
        }

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

        EditText editTextOblast = (EditText) rootView.findViewById(R.id.editText19);
        editTextOblast.setText(userProfile.getRegion());
        editTextOblast.addTextChangedListener(new TextWatcherETicket(editTextOblast));
        sparseBooleanArray.put(editTextOblast.getId(), validator.isNameValid(userProfile.getRegion()));

        EditText editTextCity = (EditText) rootView.findViewById(R.id.editText20);
        editTextCity.setText(userProfile.getCity());
        editTextCity.addTextChangedListener(new TextWatcherETicket(editTextCity));
        sparseBooleanArray.put(editTextCity.getId(), validator.isLastNameValid(userProfile.getCity()));

        EditText editTextNDepartament = (EditText) rootView.findViewById(R.id.editText21);
        editTextNDepartament.setText(userProfile.getNewPost());
        editTextNDepartament.addTextChangedListener(new TextWatcherETicket(editTextNDepartament));
        sparseBooleanArray.put(editTextNDepartament.getId(), validator.isNumberValid(userProfile.getNewPost()));

        textViewTimer = (TextView) rootView.findViewById(R.id.textView104);

        Button buttonConfirm = (Button) rootView.findViewById(R.id.button2);

        switch (paymentMethod){
            case 3:
                buttonConfirm.setText("оформити замовлення");
                break;
            case 4:
                buttonConfirm.setText("перейти до оплати");
                break;
        }

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getValidUserData()) {
                    Fragment fragment = new FragmentSuccessfulOrder();
                    FragmentManager fragmentManager = getFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                }
            }
        });
        startService();

        fragmentManager = getActivity().getFragmentManager();
        return rootView;
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = new FragmentDeliveryOrder();
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
                case R.id.editText19:
                    sparseBooleanArray.put(R.id.editText19, validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText20:
                    sparseBooleanArray.put(R.id.editText20,validator.isNameValid(editable.toString()));
                    break;
                case R.id.editText21:
                    sparseBooleanArray.put(R.id.editText21,validator.isNumberValid(editable.toString()));
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

    private void startService(){

        long timer = ((MainActivity)getActivity()).getTimer();
        if (timer != 0){
        countDownTimer = new CountDownTimer(timer,1000) {

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
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment, "1").commit();
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

}
