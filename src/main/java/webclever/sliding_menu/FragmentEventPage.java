package webclever.sliding_menu;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ShareActionProvider;

import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import Format.DateFormat;
import Singleton.DataEventSingelton;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;



public class FragmentEventPage extends Fragment implements OnBackPressedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private ProgressDialog progressDialog;
    private ImageLoader imageLoader;
    private TextView textViewTimeIvent;
    private TextView textViewDateIvent;
    private TextView textViewPriceIvent;
    private TextView textViewLocationIvent;
    private TextView textViewDescriptionIvent;
    private TextView textViewNameEvent;
    private TextView textViewEventAddress;

    private NetworkImageView networkImageView;

    private int id_ivent;
    private String fromFragment;


    private ViewGroup mViewGroupImage;
    //private ViewGroup mViewGroupVideo;

    //private String videoID = "99LOtY-unkA";
    private ArrayList<String> img_url;

    private DateFormat dateFormat = new DateFormat();

    public FragmentEventPage() {
        setHasOptionsMenu(true);
    }
    @Override
    public void onAttach(Activity activity) {
               super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_event_page, conteiner, false);
        id_ivent = getArguments().getInt("id");
        fromFragment = getArguments().getString("fromFragment");

        Log.i("id_event", String.valueOf(id_ivent));
        getActivity().getActionBar().setTitle("Подія");

        img_url = new ArrayList<>();
        //str_event += id_ivent;
        //str_url = new ArrayList<String>();

        imageLoader = AppController.getInstance().getImageLoader();
        networkImageView = (NetworkImageView) rootView.findViewById(R.id.iventimage);

        Button buy_ticket = (Button) rootView.findViewById(R.id.buy_ticket);

        textViewTimeIvent = (TextView) rootView.findViewById(R.id.textIventTime);
        textViewDateIvent = (TextView) rootView.findViewById(R.id.iventDate);
        textViewPriceIvent = (TextView) rootView.findViewById(R.id.iventPrice);
        textViewLocationIvent = (TextView) rootView.findViewById(R.id.iventLocation);
        textViewEventAddress = (TextView) rootView.findViewById(R.id.event_address);
        textViewDescriptionIvent = (TextView) rootView.findViewById(R.id.iventDescription);
        textViewNameEvent = (TextView) rootView.findViewById(R.id.name_event);

        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.RLayout);
        //imgSchema.setImageUrl("http://s018.radikal.ru/i509/1506/19/99a431f65a0e.png",imageLoader);

        mViewGroupImage = (ViewGroup) rootView.findViewById(R.id.gallery_container);
        //mViewGroupVideo = (ViewGroup) rootView.findViewById(R.id.video_container);
        imageLoader = AppController.getInstance().getImageLoader();


        /*addVideo("http://kasa.in.ua/images/event/1018_l.jpg");
        addVideo("http://kasa.in.ua/images/event/813_l.jpg");*/

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Loading...");
        progressDialog.show();



        /*StringRequest strReq = new StringRequest(Request.Method.POST,
                str_event, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject jsonObject = new JSONObject(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("token", "3748563");
                params.put("id",String.valueOf(id_ivent));

                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq);
*/

        String str_event = "http://tms.webclever.in.ua/api/getEvent";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                str_event, new Response.Listener<String>() {
            @Override
            public void onResponse(String response_string) {
                Log.d("Tag", response_string);
                hidePDialog();
                try {
                                    JSONObject response = new JSONObject(response_string);
                                    JSONObject jsonObjectPoster = response.getJSONObject("poster");
                                    networkImageView.setImageUrl(jsonObjectPoster.getString("l"), imageLoader);
                                    textViewNameEvent.setText(response.getString("name"));
                                    textViewTimeIvent.setText(response.getString("start_time"));
                                    JSONObject jsonObjectLocEvent = response.getJSONObject("location");
                                    textViewEventAddress.setText(jsonObjectLocEvent.getString("name"));
                                    if (dateFormat.getData(response.getString("start_time")) != null)
                                    {
                                        textViewDateIvent.setText(dateFormat.getData(response.getString("start_time")));
                                        DataEventSingelton.getInstance().setDate_event(dateFormat.getData(response.getString("start_time")));
                                    }
                                    if (dateFormat.getTime(response.getString("start_time")) != null)
                                    {
                                        textViewTimeIvent.setText(dateFormat.getTime(response.getString("start_time")));
                                        DataEventSingelton.getInstance().setTime_event(dateFormat.getTime(response.getString("start_time")));
                                    }

                                    JSONObject price = response.getJSONObject("minMaxPrice");
                                    if (price.getString("min")!= null & price.getString("max") != null)
                                    {
                                        String string = price.getString("min") + " - " + price.getString("max") + " UAH";
                                        textViewPriceIvent.setText(string);
                                    }else
                                    {
                                        textViewPriceIvent.setVisibility(View.GONE);
                                    }

                                    JSONObject city = jsonObjectLocEvent.getJSONObject("city");
                                    textViewLocationIvent.setText(city.getString("name"));
                                    if (!response.getString("description").equals("")){
                                        RelativeLayout relativeLayout = (RelativeLayout) rootView.findViewById(R.id.RLayout);
                                        relativeLayout.setVisibility(View.VISIBLE);
                                        textViewDescriptionIvent.setText(Html.fromHtml(response.getString("description")));

                                    }

                                    DataEventSingelton.getInstance().setId_event(id_ivent);
                                    DataEventSingelton.getInstance().setName_event(response.getString("name"));
                                    DataEventSingelton.getInstance().setPlace_event(city.getString("name"));
                                    DataEventSingelton.getInstance().setImg_url(jsonObjectPoster.getString("l"));
                                        JSONArray arrImgEvent = response.getJSONArray("images");
                                        for (int j=0; j < arrImgEvent.length(); j++)
                                        {
                                            JSONObject jsonObjectImage = arrImgEvent.getJSONObject(j);
                                            addImage(jsonObjectImage.getString("l"));
                                        }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
            }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
            })
                {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();
                    params.put("token", "3748563");
                    params.put("id",String.valueOf(id_ivent));
                    Log.i("Params",params.toString());
                    return params;
                }};

            AppController.getInstance().addToRequestQueue(strReq);

        buy_ticket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectPlace();
            }
        });


        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityDescription.class);
                String str = String.valueOf(textViewDescriptionIvent.getText());
                intent.putExtra("description", str);
                startActivity(intent);
            }
        });

        return rootView;
    }

    public void addImage(String url_img)
    {
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_image, mViewGroupImage, false);
        img_url.add(url_img);
        ((NetworkImageView) newView.findViewById(R.id.gallery_image)).setImageUrl(url_img, imageLoader);
        newView.findViewById(R.id.gallery_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getBaseContext(), String.valueOf(mViewGroupImage.indexOfChild(newView)), Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getActivity(), FullScreenViewActivity.class);
                i.putExtra("position", mViewGroupImage.indexOfChild(newView));
                i.putStringArrayListExtra("img_url", img_url);

                getActivity().startActivity(i);
                Log.i("position", String.valueOf(mViewGroupImage.indexOfChild(newView)));

            }
        });
        mViewGroupImage.addView(newView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here

        getActivity().getMenuInflater().inflate(R.menu.single_menu, menu);
        MenuItem item = menu.findItem(R.id.menuCount);
        if (((MainActivity) getActivity()).getCountTicket().equals("0")) {
            menu.getItem(0).setVisible(false);
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
            TextView textViewTicketCount = (TextView) relativeLayoutShopCart.getChildAt(1);
            textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());

        /** Getting the actionprovider associated with the menu item whose id is share */
        ShareActionProvider mShareActionProvider = (ShareActionProvider) menu.findItem(R.id.share).getActionProvider();

        /** Setting a share intent */
        mShareActionProvider.setShareIntent(getDefaultShareIntent());
        super.onCreateOptionsMenu(menu, inflater);
    }

    void SelectPlace()
    {
        Bundle myBundle = new Bundle();
        myBundle.putInt("id", id_ivent);
        myBundle.putString("name_event", String.valueOf(textViewNameEvent.getText()));
        myBundle.putString("fromFragment", fromFragment);
        Fragment fragment = new FragmentSelectPlace();
        fragment.setArguments(myBundle);
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }



    /** Add video gallery */
    /*public void addVideo(String video_code)
    {
        final ViewGroup newView = (ViewGroup) LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.list_video, mViewGroupVideo, false);
        ((NetworkImageView) newView.findViewById(R.id.gallery_video)).setImageUrl(video_code, imageLoader);
        newView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID)));
            }
        });
        mViewGroupVideo.addView(newView);
    }
*/

    /** Returns a share intent */
    private Intent getDefaultShareIntent() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_SUBJECT, "Title Of The Post");
        share.putExtra(Intent.EXTRA_TEXT, "http://kasa.in.ua/ua/events/view-vnochi-kuiv-dodatkovuj-koncert.html");
        return share;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();

        Log.i("OnDestroy", "SingleEvent");
    }

    private void hidePDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    public void onBackPressed() {

        if (fromFragment.equals("eventList")) {
            Fragment fragment = new HomeFragment();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            Toast.makeText(getActivity().getApplicationContext(), "From SingleEvent onBackPressed", Toast.LENGTH_SHORT).show();
        }else
        {
            Fragment fragment = new FragmentSearchEvent();
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            Toast.makeText(getActivity().getApplicationContext(), "From SingleEvent onBackPressed", Toast.LENGTH_SHORT).show();
        }

    }

}
