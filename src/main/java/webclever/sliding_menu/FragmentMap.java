package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import interfaces.OnBackPressedListener;

public class FragmentMap extends Fragment implements OnBackPressedListener, OnMapReadyCallback {

    private double longitude;
    private double latitude;
    private String nameCity;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        Bundle bundle = getArguments();
        TextView textViewName = (TextView) rootView.findViewById(R.id.name_kasam);
        textViewName.setAllCaps(true);
        textViewName.setText(bundle.getString("address"));

        TextView textViewNameKasa = (TextView) rootView.findViewById(R.id.textViewNameKasa);
        textViewNameKasa.setText(bundle.getString("name"));

        TextView textViewDescription = (TextView) rootView.findViewById(R.id.description_kasam);
        textViewDescription.setText(bundle.getString("description"));

        TextView textViewnotificationMap = (TextView) rootView.findViewById(R.id.textViewnotificationMap);
        textViewnotificationMap.setText(bundle.getString("notification"));

        TextView textViewTimeWork = (TextView) rootView.findViewById(R.id.time_work_kasam);
        textViewTimeWork.setText(Html.fromHtml(bundle.getString("time_work")));

        //nameCity = bundle.getString("nameCity");
        //Log.i("nameCity: ", nameCity);

        latitude = bundle.getDouble("latitude");
        longitude  = bundle.getDouble("longitude");

        Log.d("map", String.valueOf(latitude) + " " + String.valueOf(longitude));




        try {
            // Loading map
            FragmentManager fm ;

            String TAG = "map";
            Log.d(TAG, "sdk: " + Build.VERSION.SDK_INT);
            Log.d(TAG, "release: " + Build.VERSION.RELEASE);

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                Log.d(TAG, "using getFragmentManager");
                fm = getFragmentManager();
            } else {
                Log.d(TAG, "using getChildFragmentManager");
                fm = getChildFragmentManager();
            }

            MapFragment mapFragment = (MapFragment) fm.findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // latitude and longitude
        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude)).title("kasa.in.ua");
        map.addMarker(marker);
        LatLng latLng = marker.getPosition();
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).tilt(45).zoom(18).build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public void onDestroyView() {
        super.onDestroyView();

        Fragment f = getActivity()
                .getFragmentManager().findFragmentById(R.id.map);
        if (f != null) {
            getActivity().getFragmentManager()
                    .beginTransaction().remove(f).commit();
        }
    }

    @Override
    public void onBackPressed() {
        //Bundle myBundle = new Bundle();
        //myBundle.putString("nameCity",nameCity);
        Fragment fragment = new LocKasaFragment();
        //fragment.setArguments(myBundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
        /*Toast.makeText(getActivity().getApplicationContext(), "From LocKasaFragment onBackPressed", Toast.LENGTH_SHORT).show();*/
    }

}
