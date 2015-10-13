package webclever.sliding_menu;


import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.os.Bundle;



import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Format.DateFormat;
import Parallaxed.ParallaxListView;
import Singleton.DataEventSingelton;
import Singleton.SingletonCity;
import adapter.ObjectSpinnerAdapter;
import customlistviewadapter.CustomListAdapter;
import customlistviewapp.AppController;
import customlistviewmodel.Movie;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;


public class HomeFragment extends Fragment implements Spinner.OnItemSelectedListener, OnBackPressedListener {
    private static String TAG = MainActivity.class.getSimpleName();
    private String urlEvent;
    private static final String urlCity = "http://tms.webclever.in.ua/api/getCities?&token=3748563";
    private String urlSlideShow;
    private List<Movie> movieList = new ArrayList<Movie>();
    private DateFormat dateFormat = new DateFormat();
    private ParallaxListView listView;
    private CustomListAdapter adapter;

    //progressBAr
    ProgressBar progressBar;

    //spinner
    private List<SingletonCity> singletonCityList;
    private Spinner spinner;
    private ObjectSpinnerAdapter objectSpinnerAdapter;
    private Boolean checkSmoothScroll = false;

    private String nameCityy ;

    //private ProgressBar progressBar;
    private int limit = 10;
    private int start = 0;

    private Boolean checkDownload;

    /**Slider*/
    private HorizontalScrollView horizontalScrollView;
    private LinearLayout linearLayoutSlider;
    private List<Integer> listImgUrl;
    private ImageLoader imageLoader;


    private SharedPreferences sharedPreferencesNameCity;
    private SharedPreferences sharedPreferencesAutoLocation;

    public HomeFragment()
    {
        setHasOptionsMenu(true);
    }
    float distance;
    int position = 0;
    int positionUser = 0;



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);

        sharedPreferencesNameCity = getActivity().getSharedPreferences("name_city", Context.MODE_PRIVATE);
        sharedPreferencesAutoLocation = getActivity().getSharedPreferences("auto_location", Context.MODE_PRIVATE);
        horizontalScrollView = new HorizontalScrollView(getActivity());
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        ((MainActivity)getActivity()).setItemChecked(0,true);

        linearLayoutSlider = new LinearLayout(getActivity());
        imageLoader = AppController.getInstance().getImageLoader();
        linearLayoutSlider.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutSlider.setGravity(Gravity.CENTER);
        horizontalScrollView.addView(linearLayoutSlider);
        horizontalScrollView.getChildAt(0).setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
        horizontalScrollView.setBackgroundColor(getResources().getColor(R.color.year));
        listView = (ParallaxListView) rootView.findViewById(R.id.list_view);
        adapter = new CustomListAdapter(getActivity(),getActivity(),movieList,"eventList");
        listView.addParallaxedHeaderView(horizontalScrollView);
        listView.setAdapter(adapter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar3);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
                startEventFragment(movieList.get(arg2 - 1).getId_ivent());
            }
        });

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                int threshold = 1;
                int count = listView.getCount();

                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= count
                            - threshold) {
                        if (checkDownload) {
                            limit = 5;
                            start += 5;
                            checkSmoothScroll = false;
                            urlEvent = "http://tms.webclever.in.ua/api/getEventList?&limit="+ String.valueOf(limit) +
                                    "&offset=" + String.valueOf(start) + "&token=3748563&city_id=" +
                                    String.valueOf(singletonCityList.get(spinner.getSelectedItemPosition()).getIdCity());
                            JsonParsingEvent(urlEvent);

                        }
                    }
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });
        return rootView;

    }
    private void JsonParsingImageSlider(String url) {
        listImgUrl = new ArrayList<Integer>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                    try {
                        for (int i = 0; i <jsonArray.length();i++)
                        {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        listImgUrl.add(Integer.parseInt(jsonObject.getString("id")));
                        JSONObject jsonObjectPoster = jsonObject.getJSONObject("poster");
                        addImageSlider(jsonObjectPoster.getString("m")); }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }
        },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }

        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
    }

    private void addImageSlider(String url) {
        final ViewGroup newViewGroup = (ViewGroup) LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_slider_image, linearLayoutSlider,false);
        final MyNetworkImageView networkImageView = (MyNetworkImageView) newViewGroup.findViewById(R.id.networkimageviewslider);
        networkImageView.setImageUrl(url,imageLoader);
        networkImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startEventFragment(listImgUrl.get(linearLayoutSlider.indexOfChild(newViewGroup)));
                horizontalScrollView.setSmoothScrollingEnabled(true);
            }
        });
        linearLayoutSlider.addView(newViewGroup);
    }

    private void JsonParsingEvent(String url) {

        progressBar.setVisibility(View.VISIBLE);
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        Log.d(TAG, jsonArray.toString());
                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                if (jsonArray.length() < limit){
                                    checkDownload = false;
                                }else {checkDownload = true; }
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    Movie movie = new Movie();
                                    movie.setId_ivent(obj.getInt("id"));
                                    movie.setName(obj.getString("name"));
                                    movie.setTime(obj.getString("start_time"));
                                    if (dateFormat.getData(obj.getString("start_time")) != null)
                                    {
                                        movie.setData(dateFormat.getData(obj.getString("start_time")));
                                    }
                                    if (dateFormat.getTime(obj.getString("start_time")) != null)
                                    {
                                        movie.setTime(dateFormat.getTime(obj.getString("start_time")));
                                    }
                                    JSONObject city = obj.getJSONObject("city");
                                    movie.setCity(city.getString("name"));
                                    JSONObject poster = obj.getJSONObject("poster");
                                    if(!poster.toString().equals("{}")){
                                        movie.setThumbnailUrl(poster.getString("l"));
                                    }
                                    movieList.add(movie);

                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }

                        adapter.notifyDataSetChanged();
                        if (checkSmoothScroll)
                        {
                            listView.smoothScrollToPosition(0);
                            Log.i("SmoothScroll","true");
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                progressBar.setVisibility(View.INVISIBLE);
            }

        });
        AppController.getInstance().addToRequestQueue(movieReq);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        /** Inflating the current activity's menu with res/menu/items.xml */
        getActivity().getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.menuSort);
        spinner = (Spinner) item.getActionView();
        singletonCityList = new ArrayList<>();
        setJsonRequestNameCity();
        objectSpinnerAdapter = new ObjectSpinnerAdapter(getActivity(),singletonCityList);
        spinner.setAdapter(objectSpinnerAdapter);
        spinner.setOnItemSelectedListener(this);

        MenuItem itemSearch = menu.findItem(R.id.search);
        itemSearch.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                Fragment fragment = new FindPeopleFragment();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
                return true;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (pos != 0){
                urlSlideShow = "http://tms.webclever.in.ua/api/GetSliderEvent?city_id=" + String.valueOf(singletonCityList.get(pos).getIdCity()) + "&token=3748563";
            }else {
                urlSlideShow = "http://tms.webclever.in.ua/api/GetSliderEvent?&token=3748563";
            }
            Log.i("city_id",String.valueOf(singletonCityList.get(pos).getIdCity()));
            limit = 5;
            start = 0;
            movieList.clear();
            urlEvent = "http://tms.webclever.in.ua/api/getEventList?&limit="+ String.valueOf(limit) +"&offset=" + String.valueOf(start) + "&token=3748563&city_id=" + String.valueOf(singletonCityList.get(pos).getIdCity());
            nameCityy = singletonCityList.get(pos).getNameCity();
            checkSmoothScroll = true;
            linearLayoutSlider.removeAllViews();
            JsonParsingImageSlider(urlSlideShow);
            JsonParsingEvent(urlEvent);
            saveNameCity();
    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void saveNameCity() {
        int id_city = spinner.getSelectedItemPosition();
        Editor editor = sharedPreferencesNameCity.edit();
        editor.putInt("city_id", singletonCityList.get(id_city).getIdCity());
        editor.putString("City", nameCityy);
        editor.apply();
    }
    private void saveAutoLocation() {

            Integer location = sharedPreferencesAutoLocation.getInt("city_id", -1);
            Integer spinnerLoc = singletonCityList.get(spinner.getSelectedItemPosition()).getIdCity();
            if (location != -1){
                if (!location.equals(singletonCityList.get(position).getIdCity()) && location.equals(spinnerLoc)) {
                    showDialog();
                }
            }
                Editor editor = sharedPreferencesAutoLocation.edit();
                editor.putInt("city_id",singletonCityList.get(position).getIdCity());
                editor.putString("City", singletonCityList.get(position).getNameCity());
                editor.apply();
                Log.i("City", "AutoSaveLocation" + singletonCityList.get(position).getNameCity());

    }

    private Integer getNameCity()
    {
        return sharedPreferencesNameCity.getInt("city_id", -1);
    }

    @Override
    public void onDestroyView ()
    {

        ((MainActivity)getActivity()).setItemChecked(0,false);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    private void setJsonRequestNameCity() {
        singletonCityList.clear();
        JsonArrayRequest req = new JsonArrayRequest(urlCity,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        try {
                            Location location = ((MainActivity) getActivity()).getLocation();
                            for (int i = 0; i < response.length(); i++) {

                                SingletonCity singletonCity = new SingletonCity();
                                JSONObject object = response.getJSONObject(i);
                                singletonCity.setNameCity(object.getString("name"));
                                singletonCity.setIdCity(object.getInt("id"));
                                singletonCityList.add(singletonCity);
                                if (getNameCity() != -1){
                                    if (getNameCity() == (object.getInt("id"))){
                                        nameCityy = object.getString("name");
                                        positionUser = i;
                                    }
                                }
                                if (!object.getString("lat").equals("null")  && !object.getString("lng").equals("null"))
                                {
                                    float latitude = Float.parseFloat(object.getString("lat"));
                                    float longitude = Float.parseFloat(object.getString("lng"));

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
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        setCity();
                        objectSpinnerAdapter.notifyDataSetChanged();
                        saveAutoLocation();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                /*Toast.makeText(getActivity(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();*/

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(req);
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

    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Показати події у місті " + singletonCityList.get(position).getNameCity())
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

    @Override
    public void onBackPressed() {

    }

    private void startEventFragment(Integer idEvent){
        Bundle myBundle = new Bundle();
        myBundle.putInt("id", idEvent);
        myBundle.putString("fromFragment", "eventList");
        myBundle.putString("city",nameCityy);
        Toast.makeText(getActivity(), String.valueOf(idEvent), Toast.LENGTH_SHORT).show();
        Fragment fragment = new SingleIvent();
        fragment.setArguments(myBundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }
}
