package webclever.sliding_menu;


import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;



import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
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
    private static final String urlSlideShow = "https://api.myjson.com/bins/tb8p";
    private List<Movie> movieList = new ArrayList<Movie>();
    private DateFormat dateFormat = new DateFormat();
    private ParallaxListView listView;
    private CustomListAdapter adapter;
    private String nameGetLocationCity;

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



    private int i=0;

    public HomeFragment()
    {
        setHasOptionsMenu(true);
    }



    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_home,container,false);
        if (getNameCity() != null)
        {
            nameCityy = getNameCity();
            Log.i("Arguments",nameCityy);
        }else {
            nameCityy = ((MainActivity)getActivity()).getNameCity();
            Log.i("Arguments","Null");
        }

        horizontalScrollView = new HorizontalScrollView(getActivity());
        horizontalScrollView.setHorizontalScrollBarEnabled(false);
        linearLayoutSlider = new LinearLayout(getActivity());
        imageLoader = AppController.getInstance().getImageLoader();
        linearLayoutSlider.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        linearLayoutSlider.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutSlider.setGravity(Gravity.CENTER);
        linearLayoutSlider.setBackgroundColor(getResources().getColor(R.color.year));
        horizontalScrollView.addView(linearLayoutSlider);

        JsonParsingImageSlider(urlSlideShow);
        listView = (ParallaxListView) rootView.findViewById(R.id.list_view);
        adapter = new CustomListAdapter(getActivity(),getActivity(),movieList,"eventList");
        listView.addParallaxedHeaderView(horizontalScrollView);
        listView.setAdapter(adapter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar3);

        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

                Bundle myBundle = new Bundle();
                int id_ivent = movieList.get(arg2 - 1).getId_ivent();
                myBundle.putInt("id", id_ivent);
                myBundle.putString("fromFragment", "eventList");
                myBundle.putString("city",nameCityy);
                Toast.makeText(getActivity(), String.valueOf(id_ivent), Toast.LENGTH_SHORT).show();
                Fragment fragment = new SingleIvent();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
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
                        listImgUrl.add(Integer.parseInt(jsonObject.getString("id_event")));
                        addImageSlider(jsonObject.getString("url_img")); }


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
                Toast.makeText(getActivity(), String.valueOf(linearLayoutSlider.indexOfChild(newViewGroup)), Toast.LENGTH_SHORT).show();
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
                                    movie.setThumbnailUrl(poster.getString("l"));
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

        /*SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //mDemoSlider.setVisibility(View.INVISIBLE);
                adapter.getFilter().filter(newText);
                Log.i("SerchView", newText);

                return true;
            }

        });*/

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

            limit = 5;
            start = 0;
            movieList.clear();
            urlEvent = "http://tms.webclever.in.ua/api/getEventList?&limit="+ String.valueOf(limit) +"&offset=" + String.valueOf(start) + "&token=3748563&city_id=" + String.valueOf(singletonCityList.get(pos).getIdCity());
            nameCityy = singletonCityList.get(pos).getNameCity();
            JsonParsingEvent(urlEvent);
            checkSmoothScroll = true;

    }


    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

    private void saveNameCity()
    {
        sharedPreferencesNameCity = getActivity().getSharedPreferences("name_city", Context.MODE_PRIVATE);
        Editor editor = sharedPreferencesNameCity.edit();
        editor.putString("City", nameCityy);
        editor.apply();
    }

    private String getNameCity()
    {
        sharedPreferencesNameCity = getActivity().getSharedPreferences("name_city", Context.MODE_PRIVATE);
        return sharedPreferencesNameCity.getString("City",null);
    }

    @Override
    public void onDestroyView ()
    {
        saveNameCity();
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
                            for (int i = 0; i < response.length(); i++) {

                                SingletonCity singletonCity = new SingletonCity();
                                JSONObject object = response.getJSONObject(i);
                                singletonCity.setNameCity(object.getString("name"));
                                nameGetLocationCity = object.getString("name");
                                singletonCity.setIdCity(object.getInt("id"));

                                if (nameCityy != null){
                                if(nameCityy.equals(nameGetLocationCity))
                                {
                                    spinner.setSelection(i);
                                }}
                                singletonCityList.add(singletonCity);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                        objectSpinnerAdapter.notifyDataSetChanged();
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

    @Override
    public void onBackPressed() {

    }
}
