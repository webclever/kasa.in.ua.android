package webclever.sliding_menu;

import android.app.FragmentManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class FragmentContacts extends Fragment {

    public FragmentContacts() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_fragment_contacts, container, false);

        TextView textViewMailKasa = (TextView) rootView.findViewById(R.id.textViewMailKasa);
                textViewMailKasa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"info@kasa.in.ua"});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "kasa.in.ua");
                        try {
                            startActivity(Intent.createChooser(intent, "Відправити повідомлення..."));
                        } catch (android.content.ActivityNotFoundException ex) {
                            Toast.makeText(getActivity(), "Не має додатків для відправлення повідомлення!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        TextView textViewWeb = (TextView) rootView.findViewById(R.id.textViewKasaInUa);
        textViewWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.kasa.in.ua"));
                startActivity(browserIntent);
            }
        });

        TextView textViewKasaPhone = (TextView) rootView.findViewById(R.id.textView33);
        textViewKasaPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:"+"+380930000754"));
                try{
                    startActivity(intent);
                }

                catch (android.content.ActivityNotFoundException ex){
                    Toast.makeText(getActivity().getApplicationContext(),"yourActivity is not founded",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button buttonShowAddressKassa = (Button) rootView.findViewById(R.id.button_kasa_adress);
        buttonShowAddressKassa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new LocKasaFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }
        });




        return rootView;
    }


}
