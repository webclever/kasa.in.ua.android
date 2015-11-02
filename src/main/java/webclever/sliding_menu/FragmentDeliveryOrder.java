package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Timer;

import interfaces.OnBackPressedListener;
import my_services.TimerService;


//Created by Zhenya on 17.02.2015.

public class FragmentDeliveryOrder extends Fragment implements OnBackPressedListener {



    private RadioGroup radioGroupKasa;
    private RadioGroup radioGroupNewPost;
    private RadioGroup radioGroupCourier;
    private RadioGroup radioGroupE_ticket;
    private RadioGroup radioGroup;

    private RadioButton radioButton;

    private Button buttonContinue;
    private TextView textViewDeliveryMethod;
    private TextView textViewTimer;

    private int lastIdRadioGroup  = -1;
    private Animation anim;

    private SparseArray<Fragment> fragmentSparseArray;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_delivery_order, conteiner, false);

        radioGroupKasa = (RadioGroup) rootView.findViewById(R.id.radioGroupKasa);
        radioGroupNewPost = (RadioGroup) rootView.findViewById(R.id.radioGroupNewPost);
        radioGroupCourier = (RadioGroup) rootView.findViewById(R.id.radioGroupCourier);
        radioGroupE_ticket = (RadioGroup) rootView.findViewById(R.id.radioGroupE_ticket);
        fragmentSparseArray = new SparseArray<>();
        fragmentSparseArray.put(radioGroupKasa.getId(), new FragmentUserDataKasa());
        fragmentSparseArray.put(radioGroupNewPost.getId(), new UserDataPost());
        fragmentSparseArray.put(radioGroupCourier.getId(), new UserDataCourier());
        fragmentSparseArray.put(radioGroupE_ticket.getId(), new UserDataETicket());
        textViewDeliveryMethod = (TextView) rootView.findViewById(R.id.textViewDeliveryMethod);
        textViewTimer = (TextView) rootView.findViewById(R.id.textView63);
        buttonContinue = (Button) rootView.findViewById(R.id.buttonContinue);

        RadioGroup radioGroupDeliveryMethod = (RadioGroup) rootView.findViewById(R.id.radioGroupDeliveryMethod);
        radioGroupDeliveryMethod.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (textViewDeliveryMethod.getVisibility() == View.GONE) {
                    textViewDeliveryMethod.setVisibility(View.VISIBLE);
                    Animation anim2 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                    textViewDeliveryMethod.setAnimation(anim2);
                }

                if (buttonContinue.getVisibility() == View.GONE) {
                    buttonContinue.setVisibility(View.VISIBLE);
                }

                if (lastIdRadioGroup != -1) {
                    radioGroup = (RadioGroup) rootView.findViewById(lastIdRadioGroup);
                    Log.i("visible", String.valueOf(radioGroupKasa.getVisibility()));
                    radioGroup.clearCheck();
                    radioGroup.setVisibility(View.GONE);
                    Log.i("visible", String.valueOf(radioGroupKasa.getVisibility()));
                    Animation anim1 = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_out);
                    radioGroup.setAnimation(anim1);
                }

                switch (i) {
                    case R.id.radioButtonKasa:

                        radioGroupKasa.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupKasa.setAnimation(anim);
                        lastIdRadioGroup = radioGroupKasa.getId();

                        break;


                    case R.id.radioButtonNewPost:

                        radioGroupNewPost.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupNewPost.setAnimation(anim);
                        lastIdRadioGroup = radioGroupNewPost.getId();

                        break;


                    case R.id.radioButtonCourier:

                        radioGroupCourier.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupCourier.setAnimation(anim);
                        lastIdRadioGroup = radioGroupCourier.getId();

                        break;

                    case R.id.radioButtonE_ticket:

                        radioGroupE_ticket.setVisibility(View.VISIBLE);
                        anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.payment_in);
                        radioGroupE_ticket.setAnimation(anim);
                        lastIdRadioGroup = radioGroupE_ticket.getId();

                        break;
                }
            }
        });

        buttonContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lastIdRadioGroup != -1) {
                    radioGroup = (RadioGroup) rootView.findViewById(lastIdRadioGroup);

                    if (radioGroup.getCheckedRadioButtonId() != -1) {

                        radioButton = (RadioButton) rootView.findViewById(radioGroup.getCheckedRadioButtonId());
                        Fragment fragment = fragmentSparseArray.get(radioGroup.getId());
                        Bundle bundleType = new Bundle();
                        bundleType.putString("type", String.valueOf(radioButton.getText()));
                        fragment.setArguments(bundleType);
                        FragmentManager fragmentManager = getFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
                        Toast.makeText(getActivity().getApplicationContext(), String.valueOf(radioButton.getText()), Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Виберіть спосіб оплати!", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



        new CountDownTimer(900000,1000) {

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
            }
        }.start();


        startService();

        return rootView;
    }

    @Override
    public void onBackPressed() {

        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();

    }

    private void startService(){
        Intent intent = new Intent("webclever.sliding_menu");
        ServiceConnection sConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //TimerService myService  = ((TimerService.ALARM_SERVICE));
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };


    }
}
