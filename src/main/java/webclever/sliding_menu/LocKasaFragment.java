package webclever.sliding_menu;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Singleton.SingletonCity;
import adapter.LocKasaAdapter;
import adapter.Loc_Kasa_Singelton;
import adapter.ObjectSpinnerAdapter;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;
//import static webclever.sliding_menu.R.id.loginText;

/**
 * Created by Admin on 17.02.2015.
 */
public class LocKasaFragment extends Fragment implements AdapterView.OnItemSelectedListener , OnBackPressedListener {

    private static String TAG = MainActivity.class.getSimpleName();
    private static final String url="http://location.webclever.in.ua/api/getCities";
    private static final String url_kasa_city="http://location.webclever.in.ua/api/getPointsByCity?id=";

    private List<Loc_Kasa_Singelton> kasaList = new ArrayList<Loc_Kasa_Singelton>();
    private ListView listView;
    private LocKasaAdapter locKasaAdapter;
    private Spinner spinner;
    private List<SingletonCity> singletonCityList;
    private ObjectSpinnerAdapter objectSpinnerAdapter;
    private String nameCity;
    private SharedPreferences sharedPreferencesNameCity;
    private Integer idCity;
    private SharedPreferences sharedPreferencesAutoLoc;
    float distance;
    int position = 0;
    int positionUser = 0;
    public LocKasaFragment()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_adress_kasa, conteiner, false);
        sharedPreferencesNameCity = getActivity().getSharedPreferences("name_city_map", Context.MODE_PRIVATE);
        sharedPreferencesAutoLoc = getActivity().getSharedPreferences("auto_loc_map",Context.MODE_PRIVATE);

        spinner = (Spinner) rootView.findViewById(R.id.select_city_spinner);
        spinner.setOnItemSelectedListener(this);
        singletonCityList = new ArrayList<SingletonCity>();
        objectSpinnerAdapter = new ObjectSpinnerAdapter(getActivity(),singletonCityList);
        spinner.setAdapter(objectSpinnerAdapter);
        listView = (ListView) rootView.findViewById(R.id.list_address);
        listView.setVerticalFadingEdgeEnabled(true);
        listView.setVerticalFadingEdgeEnabled(true);
        listView.setFadingEdgeLength(50);
        locKasaAdapter = new LocKasaAdapter(getActivity(),getActivity(),kasaList);
        listView.setAdapter(locKasaAdapter);
        getJSonCity(url);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);

        MenuItem item = menu.findItem(R.id.menuCount);
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new PhotosFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void getJSonCity(String url)
    {
        JsonArrayRequest request = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                            try {

                                for (int i=0; i < response.length(); i++) {
                                    SingletonCity singletonCity = new SingletonCity();
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    singletonCity.setIdCity(jsonObject.getInt("id"));
                                    singletonCity.setNameCity(jsonObject.getString("name"));
                                    if (getNameCity() != -1){
                                        if (getNameCity() == (jsonObject.getInt("id"))){
                                            nameCity = jsonObject.getString("name");
                                            positionUser = i;
                                        }
                                    }
                                    if (!jsonObject.getString("lat").equals("null")  && !jsonObject.getString("lng").equals("null"))
                                    {
                                        float latitude = Float.parseFloat(jsonObject.getString("lat"));
                                        float longitude = Float.parseFloat(jsonObject.getString("lng"));
                                        Location location = ((MainActivity) getActivity()).getLocation();
                                        Location locationCity = new Location("loc_city");
                                        locationCity.setLatitude(latitude);
                                        locationCity.setLongitude(longitude);
                                        float distance2 = location.distanceTo(locationCity);
                                        if (i == 1){ distance = distance2; position = i;}
                                        if (distance2 < distance){
                                            distance = distance2;
                                            position = i;
                                        }
                                    }


                                    singletonCityList.add(singletonCity);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(getActivity(),
                                        "Error: " + e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        setCity();
                        objectSpinnerAdapter.notifyDataSetChanged();
                        getCityAutoLoc();

                    }
                } , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        AppController.getInstance().addToRequestQueue(request);
    }

    private void getJSonKasa(String url) {
        kasaList.clear();
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        // Parsing json
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    Loc_Kasa_Singelton loc_kasa_singelton = new Loc_Kasa_Singelton();
                                    loc_kasa_singelton.setAddress(jsonObject.getString("address"));
                                    loc_kasa_singelton.setName(jsonObject.getString("name"));

                                    String str = jsonObject.getString("description");
                                    if (str.equals("")) {

                                        loc_kasa_singelton.setDescription(null);
                                    } else {
                                        loc_kasa_singelton.setDescription(str);
                                    }

                                    String advertisement = jsonObject.getString("advertisement");
                                    if (advertisement.equals("")) {

                                        loc_kasa_singelton.setAdvertisement(null);
                                    } else {
                                        loc_kasa_singelton.setAdvertisement(advertisement);
                                    }

                                    String notification = jsonObject.getString("notification");
                                    if (notification.equals("")) {

                                        loc_kasa_singelton.setNotification(null);
                                    } else {
                                        loc_kasa_singelton.setNotification(notification);
                                    }
                                    loc_kasa_singelton.setTime_work(jsonObject.getString("work_time"));
                                    loc_kasa_singelton.setLatitude(jsonObject.getDouble("latitude"));
                                    loc_kasa_singelton.setLongitude(jsonObject.getDouble("longitude"));

                                    kasaList.add(loc_kasa_singelton);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        locKasaAdapter.notifyDataSetChanged();
                        listView.smoothScrollToPosition(0);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());


            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);

    }

    private void saveNameCity() {

        SharedPreferences.Editor editor = sharedPreferencesNameCity.edit();
        editor.putInt("city_id",singletonCityList.get(spinner.getSelectedItemPosition()).getIdCity());
        editor.putString("City", nameCity);
        editor.apply();
    }

    private Integer getNameCity() {
        return sharedPreferencesNameCity.getInt("city_id", -1);
    }

    @Override
    public void onDestroyView () {
        saveNameCity();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

            Toast.makeText(getActivity(),String.valueOf(pos),Toast.LENGTH_SHORT).show();

            kasaList.clear();
            getJSonKasa(url_kasa_city+String.valueOf(singletonCityList.get(pos).getIdCity()));
            nameCity = singletonCityList.get(pos).getNameCity();
            Log.i("spinnerlockasa",String.valueOf(singletonCityList.get(pos).getIdCity()));
            saveNameCity();

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
        Toast.makeText(getActivity().getApplicationContext(), "From LocKasaFragment onBackPressed", Toast.LENGTH_SHORT).show();
    }

    private void getCityAutoLoc(){
        Integer location = sharedPreferencesAutoLoc.getInt("city_id", -1);
        Integer spinnerLoc = singletonCityList.get(spinner.getSelectedItemPosition()).getIdCity();
        if (location != -1){
            if (!location.equals(singletonCityList.get(position).getIdCity()) && location.equals(spinnerLoc)) {
                showDialog();
            }
        }
        SharedPreferences.Editor editor = sharedPreferencesAutoLoc.edit();
        editor.putInt("city_id",singletonCityList.get(position).getIdCity());
        editor.putString("City", singletonCityList.get(position).getNameCity());
        editor.apply();
        Log.i("City", "AutoSaveLocation" + singletonCityList.get(position).getNameCity());

    }

    private void setCity() {
        if (getNameCity() != -1)
        {
            spinner.setSelection(positionUser);
        }else {
            spinner.setSelection(position);
            Log.i("Arguments","Null");
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Показати каси продажу у місті " + singletonCityList.get(position).getNameCity() + "?")
                .setPositiveButton("Oк", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        spinner.setSelection(position);
                        saveNameCity();
                    }
                })
                .setNegativeButton("Відміна", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        builder.create();
        builder.show();
    }

}
