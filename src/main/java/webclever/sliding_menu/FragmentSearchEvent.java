package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Format.DateFormat;
import customlistviewadapter.CustomListAdapter;
import customlistviewapp.AppController;
import customlistviewmodel.Movie;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;


public class FragmentSearchEvent extends Fragment implements OnBackPressedListener {


    private List<Movie> movieList = new ArrayList<>();
    private DateFormat dateFormat = new DateFormat();
    private CustomListAdapter adapter;
    private static String TAG = "Response";
    private ProgressBar progressBar;
    private int limit = 10;
    private int start = 0;
    private Boolean checkDownload;
    private StringRequest movieReq = null;

    public FragmentSearchEvent(){
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_search_event,conteiner,false);
        ((MainActivity)getActivity()).setItemChecked(1, true);
        if (getActivity().getActionBar() != null){
        getActivity().getActionBar().setTitle(getResources().getString(R.string.page_search_event_title));}
        ListView listView = (ListView) rootView.findViewById(R.id.listView);
        adapter = new CustomListAdapter(getActivity(),getActivity(),movieList,"searchEvent");
        listView.setAdapter(adapter);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar2);


        listView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {

                Bundle myBundle = new Bundle();
                int id_ivent = movieList.get(arg2).getId_ivent();
                myBundle.putInt("id", id_ivent);
                myBundle.putString("fromFragment", "searchFragment");
                /*Toast.makeText(getActivity(), String.valueOf(id_ivent), Toast.LENGTH_SHORT).show();*/
                Fragment fragment = new FragmentEventPage();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();

            }
        });


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                /*int threshold = 1;
                int count = listView.getCount();
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (listView.getLastVisiblePosition() >= count
                            - threshold) {
                        // Execute LoadMoreDataTask AsyncTask
                        //new LoadMoreDataTask().execute();
                        if (checkDownload) {
                            limit += 10;
                            start += 10;
                            loadEvent(url);
                            Log.i("Scroll end", "yes");
                        }
                    }
                }*/

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        ((MainActivity)getActivity()).Trekking("Screen search event.");
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        // Tracking the screen view
        ((MainActivity)getActivity()).Trekking("Screen search event.");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        /** Inflating the current activity's menu with res/menu/items.xml */
        getActivity().getMenuInflater().inflate(R.menu.serch_event, menu);
        MenuItem item = menu.findItem(R.id.menuCount);
        if (((MainActivity) getActivity()).getCountTicket().equals("0")) {
            menu.getItem(1).setVisible(false);
        }
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentBasket();
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(frame_container, fragment).commit();
            }
        });
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        android.widget.SearchView searchView = (android.widget.SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.onActionViewExpanded();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*Toast.makeText(getActivity(), query, Toast.LENGTH_SHORT).show();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                movieList.clear();
                adapter.notifyDataSetChanged();
                AppController.getInstance().getRequestQueue().cancelAll("search_req");
                if(movieReq != null){
                    movieReq.cancel();
                }
                loadEvent(newText);

                //adapter.getFilter().filter(newText);
                Log.i("SerchView", newText);

                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
    }

    @Override
    public void onDestroyView () {
        ((MainActivity)getActivity()).setItemChecked(1,false);
        super.onDestroyView();
    }

    private void loadEvent(final String name_event) {
        final String search_event_url = "http://tms.net.ua/api/getEventList";
        checkDownload = false;
        progressBar.setVisibility(View.VISIBLE);
        movieReq = new StringRequest(Request.Method.POST, search_event_url,
        new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                        Log.d(TAG, s);
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                        //if (limit < jsonArray.length()){
                            for(int i = 0; i < jsonArray.length(); i++)
                            {

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
                                    }else {
                                        movie.setThumbnailUrl("null");
                                    }
                                    movieList.add(movie);
                            }
                        }catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                        checkDownload = true;
                        //}
                          /*else {  for(int i = start; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    Movie movie = new Movie();
                                    movie.setId_ivent(obj.getInt("id"));
                                    movie.setName(obj.getString("name"));
                                    movie.setData(obj.getString("date"));
                                    movie.setTime(obj.getString("time"));
                                    JSONObject city = obj.getJSONObject("city");
                                    movie.setCity(city.getString("name"));
                                    movie.setThumbnailUrl(obj.getString("img"));

                                    movieList.add(movie);

                                }catch (JSONException e)
                                {
                                    e.printStackTrace();

                                }
                            }
                            checkDownload = false;
                        }*/
                        progressBar.setVisibility(View.INVISIBLE);
                        adapter.notifyDataSetChanged();

                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }

        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                params.put("name", name_event);
                params.put("city_id","0");
                Log.i("Params",params.toString());
                return params;
            }
        };
        movieReq.setTag("search_req");
        movieReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(movieReq);
    }

}
