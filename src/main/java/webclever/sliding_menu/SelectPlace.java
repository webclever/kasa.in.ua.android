package webclever.sliding_menu;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import DataBase.DB_Ticket;
import Singleton.DataEventSingelton;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;
import static webclever.sliding_menu.R.id.name_event;

/**
 * Created by Admin on 23.01.2015.
 */
@SuppressLint("SetJavaScriptEnabled")
public class SelectPlace extends Fragment implements OnBackPressedListener{

    private FragmentActivity myContext;

    private Animation animationShake;
    private Animation animationBounce;

    private Button ConfirmButton;

    private DB_Ticket db_ticket;
    private SQLiteDatabase db;

    private String stringNameEvent;
    private int idEvent;
    private TextView textViewPriceall;
    private TextView textViewTicketall;
    private int totalTicket = 0;
    private int totalPrice = 0;

    private WebView webViewSchema;
    private ProgressBar mProgress;
    private TextView textViewStatus;

    //PriceColor
    private TextView PriceLabel;
    private LinearLayout Container;
    private boolean status = false;
    private final String url_price_color = "https://api.myjson.com/bins/5177x";
    private static String TAG = MainActivity.class.getSimpleName();
    private String fromFragment;

    private String id_place;
    private Integer serverIdPlace;

    private TextView textViewTicketCount;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator(20f);
    private HashMap<String,Boolean> containerTicket;

    public SelectPlace()
    {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        containerTicket = new HashMap<>();
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
        textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        totalTicket = Integer.parseInt(((MainActivity)getActivity()).getCountTicket());
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        myContext = (FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_selectplace, container, false);
        stringNameEvent = getArguments().getString("name_event");
        idEvent = getArguments().getInt("id");
        fromFragment = getArguments().getString("fromFragment");

        //Loader
        textViewStatus = (TextView) rootView.findViewById(R.id.textViewStatus);
        mProgress = (ProgressBar) rootView.findViewById(R.id.progressbarcircular);
        mProgress.setMax(100);
        mProgress.setVisibility(View.GONE);
        textViewStatus.setVisibility(View.GONE);

        webViewSchema = (WebView) rootView.findViewById(R.id.webviewSchema);
        webViewSchema.loadUrl("http://tms.webclever.in.ua/api/previewScheme?event_id=4&token=3748563");

        webViewSchema.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSchema.getSettings().setDomStorageEnabled(true);

        webViewSchema.getSettings().setSupportZoom(true);
        webViewSchema.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
        webViewSchema.getSettings().setDisplayZoomControls(true);
        webViewSchema.getSettings().setBuiltInZoomControls(true);

        webViewSchema.setPadding(0, 0, 0, 0);

        webViewSchema.getSettings().setAllowContentAccess(true);
        webViewSchema.getSettings().setAppCacheEnabled(true);

        webViewSchema.getSettings().setLoadWithOverviewMode(true);
        webViewSchema.getSettings().setUseWideViewPort(true);
        webViewSchema.setInitialScale(1);

        mProgress.setProgress(0);
        webViewSchema.setWebChromeClient(new MyWebViewChromeClient());
        webViewSchema.getSettings().setJavaScriptEnabled(true);
        JavaScriptInterface javaScriptInterface = new JavaScriptInterface(myContext);
        webViewSchema.addJavascriptInterface(javaScriptInterface,"JSInterface");

        /*textViewPlace = (TextView) rootView.findViewById(R.id.textView30);
        textViewSector = (TextView) rootView.findViewById(R.id.textView28);
        textViewRow = (TextView) rootView.findViewById(R.id.textView32);
        textViewPricePlace = (TextView) rootView.findViewById(R.id.textView33);*/

        db_ticket = new DB_Ticket(getActivity(),5);

        textViewPriceall = (TextView) rootView.findViewById(R.id.textView42);
        textViewTicketall = (TextView) rootView.findViewById(R.id.textView34);

        getActivity().getActionBar().setTitle(stringNameEvent);

        ConfirmButton = (Button) rootView.findViewById(R.id.confirm_button);

        animationShake = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
        animationShake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ConfirmButton.setText("ПРИБРАТИ З КОШИКА");
                ConfirmButton.getBackground().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
                ConfirmButton.setTag("1");
            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animationBounce = AnimationUtils.loadAnimation(getActivity(),R.anim.bounce_anim);
        animationBounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ConfirmButton.setText("ДОДАТИ ДО КОШИКА");
                ConfirmButton.getBackground().setColorFilter(null);
                ConfirmButton.setTag("0");
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ConfirmButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                textViewTicketCount.animate().setDuration(200);

                if (ConfirmButton.getTag() == "0") {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        Log.i("add_ticket", "yes");
                        webViewSchema.loadUrl("javascript:mobileCart(\'" + id_place + "\',1)");
                        addTicket();
                        textViewTicketCount.animate().setInterpolator(decelerateInterpolator).scaleX(.7f).scaleY(.7f);

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        textViewTicketCount.animate().setInterpolator(overshootInterpolator).scaleX(1f).scaleY(1f);
                    }
                }else if (ConfirmButton.getTag() == "1"){

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        webViewSchema.loadUrl("javascript:mobileCart(\'" + id_place + "\',0)");
                        delTicket();
                        containerTicket.put(id_place, false);
                        textViewTicketCount.animate().setInterpolator(decelerateInterpolator).scaleX(1.3f).scaleY(1.3f);

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        textViewTicketCount.animate().setInterpolator(overshootInterpolator).scaleX(1f).scaleY(1f);
                    }
                }
                return false;
            }
        });

        ImageView sold = (ImageView) rootView.findViewById(R.id.sold);
        sold.setColorFilter(Color.parseColor("#FF3300"), PorterDuff.Mode.MULTIPLY);
        ImageView imageView2 = (ImageView) rootView.findViewById(R.id.imageView4);
        imageView2.setColorFilter(Color.parseColor("#FFFF00"), PorterDuff.Mode.MULTIPLY);
        ImageView imageView3 = (ImageView) rootView.findViewById(R.id.imageView6);
        imageView3.setColorFilter(Color.parseColor("#0066FF"),PorterDuff.Mode.MULTIPLY);

        Container = (LinearLayout) rootView.findViewById(R.id.Container);
        Container.setVisibility(View.GONE);
        PriceLabel = (TextView) rootView.findViewById(R.id.PriceLabel);
        setPriceColorList();

        return rootView;
    }

    private void setPriceColorList() {
        JsonArrayRequest movieReq = new JsonArrayRequest(url_price_color,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {

                            for(int i = 0; i < jsonArray.length(); i++)
                            {
                                try {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    View view = getActivity().getLayoutInflater().inflate(R.layout.price_color, Container, false);
                                    TextView textView = (TextView) view.findViewById(R.id.PriceLabel);
                                    textView.setText(obj.getString("price"));
                                    textView.setBackgroundColor(Color.parseColor(obj.getString("color")));
                                    Container.addView(view);

                                }catch (JSONException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        PopUp();
                    }
                },new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }

        });
        AppController.getInstance().addToRequestQueue(movieReq);

    }
    public void PopUp() {
        PriceLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = null;
                if (status == false) {
                    Container.setVisibility(View.VISIBLE);
                    status = true;
                    anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.my_fade_in);
                } else if (status == true) {
                    Container.setVisibility(View.GONE);
                    status = false;
                    anim = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.my_fade_out);
                }
                Container.startAnimation(anim);
            }
        });
    }

    private void getPlaceInfo(String schemaIdPlace) {
        final String urlInfoPlace = "http://tms.webclever.in.ua/api/getPlaces?places=["+schemaIdPlace+"]&token=3748563";
        Log.i("url_place", urlInfoPlace);
        JsonArrayRequest jsonArrayRequestPlaceInfo = new JsonArrayRequest(urlInfoPlace,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                             try {
                                 for (int i=0; i < jsonArray.length(); i++){
                                     JSONObject jsonObjectInfoPlace = jsonArray.getJSONObject(i);
                                     JSONObject jsonObjectRow = jsonObjectInfoPlace.getJSONObject("row");
                                     JSONObject jsonObjectPlace = jsonObjectInfoPlace.getJSONObject("place");
                                     JSONObject jsonObjectSector = jsonObjectInfoPlace.getJSONObject("sector");

                                     /*textViewRow.setText(jsonObjectRow.getString("name"));
                                     textViewPlace.setText(jsonObjectPlace.getString("name"));
                                     textViewSector.setText(jsonObjectSector.getString("name"));
                                     textViewPricePlace.setText(jsonObjectInfoPlace.getString("price"));*/
                                 }

                             }catch (JSONException e){
                                 e.printStackTrace();
                             }
                    }
                },new Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        AppController.getInstance().addToRequestQueue(jsonArrayRequestPlaceInfo);
    }

    public class MyWebViewChromeClient extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //setValue(newProgress);
            super.onProgressChanged(view, newProgress);
        }

    }

    public void setValue(int progress) {
        mProgress.setProgress(progress);
        textViewStatus .setText(String.valueOf(progress) + " %");
        if (mProgress.getProgress() == 100)
        {
            mProgress.setVisibility(View.INVISIBLE);
            textViewStatus.setVisibility(View.INVISIBLE);

        }
    }

    public class JavaScriptInterface {
        Context mContext;

        JavaScriptInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void clickHandler(String id, String serverId) {
            Log.i("from",id + " " + serverId);
            Log.i("id_place_server: ",serverId);

            if (id != null)
            {
                id_place = id;
                serverIdPlace = Integer.parseInt(serverId);
                getPlaceInfo(serverId);
                Toast.makeText(getActivity(),id + "  " + serverId,Toast.LENGTH_SHORT).show();
                if (!containerTicket.isEmpty()){
                    if (containerTicket.containsKey(id)){
                        if (containerTicket.get(id)){
                            ConfirmButton.startAnimation(animationShake);
                        }else {
                            ConfirmButton.startAnimation(animationBounce);
                        }
                    }else {
                        ConfirmButton.startAnimation(animationBounce);
                    }
                }else {
                    ConfirmButton.startAnimation(animationBounce);
                }
                ConfirmButton.setEnabled(true);
            }else{
                id_place = null;
                ConfirmButton.setEnabled(false);
            }
        }
        @JavascriptInterface
        public void schemeLoadingListener()
        {
            getBasketTicket();
        }
    }

    private void getBasketTicket() {
        String str = "";
        db = db_ticket.getReadableDatabase();
        Cursor cursorSelectedPlace =  db.query("Ticket_table",new String[]{"id_place_schema"},"id_event="+String.valueOf(idEvent),null,null,null,null,null);
        if (cursorSelectedPlace != null){
            if (cursorSelectedPlace.getCount() > 0){
                cursorSelectedPlace.moveToFirst();
                for (int i=0; i < cursorSelectedPlace.getCount(); i++){
                    str = cursorSelectedPlace.getString(0);
                    containerTicket.put(str,true);
                    webViewSchema.loadUrl("javascript:mobileCart(\'" + str + "\',1)");
                    cursorSelectedPlace.moveToNext();
                }
            }
        }
        assert cursorSelectedPlace != null;
        cursorSelectedPlace.close();
        db_ticket.close();
    }

    private void delTicket(){
        db_ticket = new DB_Ticket(getActivity(),5);
        db = db_ticket.getWritableDatabase();
        int del_id_ticket = db.delete("Ticket_table", "id_ticket=" + String.valueOf(serverIdPlace), null);
        if (del_id_ticket != 0){
            textViewTicketCount.setText(String.valueOf(--totalTicket));
        }
        Log.i("id_ticket_del",String.valueOf(del_id_ticket));
        Cursor cursorDel = db.query("Ticket_table",new String[]{"id_event"},"id_event=" + String.valueOf(idEvent),null,null,null,null,null);
        if (cursorDel.getCount() == 0)
        {
            Log.i("getcountEvent",String.valueOf(cursorDel.getCount()));
            int del_id_event = db.delete("Event_table","id_event="+String.valueOf(idEvent),null);
            Log.i("id_event_del", String.valueOf(del_id_event));
        }
        cursorDel.close();
        db_ticket.close();
    }

    /**Add data to data base*/
    private void addTicket() {
        db = db_ticket.getWritableDatabase();

        int intIdEvent = DataEventSingelton.getInstance().getId_event();
        String stringNameEvent = DataEventSingelton.getInstance().getName_event();
        String stringDateEvent = DataEventSingelton.getInstance().getDate_event();
        String stringTimeEvent = DataEventSingelton.getInstance().getTime_event();
        String stringPlaceEvent = DataEventSingelton.getInstance().getPlace_event();
        String stringImgUrlEvent = DataEventSingelton.getInstance().getImg_url();
        Log.i("img_urlll", stringImgUrlEvent);

        Cursor cursorT = db.query("Ticket_table", new String[]{"id_ticket"}, "id_ticket=" + serverIdPlace, null, null, null, null, null);

        if (cursorT != null)
        {
            if (cursorT.getCount() > 0)
            {
                cursorT.moveToFirst();
                Log.i("id_ticket", cursorT.getString(0));

            }else
            {
                containerTicket.put(id_place,true);
                ContentValues contentValues = new ContentValues();
                contentValues.put("id_ticket",serverIdPlace);
                /*contentValues.put("zon_ticket",textViewSector.getText().toString());
                contentValues.put("row_ticket",textViewRow.getText().toString());
                contentValues.put("place_ticket",textViewPlace.getText().toString());
                contentValues.put("price_ticket",textViewPricePlace.getText().toString());*/
                contentValues.put("id_place_schema",id_place);
                contentValues.put("name_user","Zhenya");
                contentValues.put("last_name_user","White");
                contentValues.put("id_event", String.valueOf(intIdEvent));
                long id_ticket = db.insert("Ticket_table","id_ticket" + serverIdPlace,contentValues);
                Log.i("id_ticket_add", String.valueOf(id_ticket));
                totalTicket ++;
                //totalPrice = totalPrice + Integer.parseInt(textViewPricePlace.getText().toString());
                textViewTicketall.setText(String.valueOf(totalTicket));
                textViewPriceall.setText(String.valueOf(totalPrice));
                String count = textViewTicketCount.getText().toString();
                Integer integerCount = Integer.parseInt(count);
                textViewTicketCount.setText(String.valueOf(++integerCount));
            }
            cursorT.close();
        }

        Cursor cursorE = db.query("Event_table",new String[]{"id_event"},"id_event=" + String.valueOf(intIdEvent),null,null,null,null,null);
        if (cursorE != null)
        {
            if (cursorE.getCount() > 0)
            {
                cursorE.moveToFirst();
                Log.i("id_ticket",cursorE.getString(0));
            }else {
                ContentValues contentValuesEvent = new ContentValues();
                contentValuesEvent.put("id_event",intIdEvent);
                contentValuesEvent.put("name_event",stringNameEvent);
                contentValuesEvent.put("date_event",stringDateEvent);
                contentValuesEvent.put("time_event",stringTimeEvent);
                contentValuesEvent.put("place_event",stringPlaceEvent);
                contentValuesEvent.put("url_img",stringImgUrlEvent);
                long id_event = db.insert("Event_table","id_event" + String.valueOf(intIdEvent),contentValuesEvent);
                Log.i("id_ticket_addEvent",String.valueOf(id_event));
            }
            cursorE.close();
        }

        db_ticket.close();

    }

    private void readDB() {
        db = db_ticket.getWritableDatabase();
        Cursor cursor = db.query("Ticket_table", null, null, null, null, null, null);
        if (cursor.moveToFirst())
        {
            int id_ticket = cursor.getColumnIndex("id_ticket");
            int zon_ticket = cursor.getColumnIndex("zon_ticket");
            int row_ticket = cursor.getColumnIndex("row_ticket");
            int place_ticket = cursor.getColumnIndex("place_ticket");
            int price_ticket = cursor.getColumnIndex("price_ticket");
            int id_event = cursor.getColumnIndex("id_event");

            do{
                Log.i("id_ticket","id_ticket"+" "+ cursor.getInt(id_ticket) + ","+
                        "zon_ticket" + " " + cursor.getString(zon_ticket)+ ","+
                        "row_ticket" + " " + cursor.getInt(row_ticket)+ " "+
                        "place_ticket" + " " + cursor.getInt(place_ticket)+ ","+
                        "price_ticket" + " " + cursor.getInt(price_ticket)+ ","+
                        "id_event" + " " + cursor.getInt(id_event)+ ".");
            }while (cursor.moveToNext());

        }else {
            cursor.close();
        }

        Cursor cursorE = db.query("Event_table",null,null,null,null,null,null);

        if (cursorE.moveToFirst())
        {
            int id_event = cursorE.getColumnIndex("id_event");
            int name_event = cursorE.getColumnIndex("name_event");
            int date_event = cursorE.getColumnIndex("date_event");
            int time_event = cursorE.getColumnIndex("time_event");
            int place_event = cursorE.getColumnIndex("place_event");
            int img_url = cursorE.getColumnIndex("url_img");

            do {
                Log.i("dataintableticketEvent","table_event: "+
                        "id_event"+ " "+ cursorE.getInt(id_event)+ " "+ ","+
                        "name_event"+ " "+ cursorE.getString(name_event)+ " "+ ","+
                        "date_event"+ " "+ cursorE.getString(date_event)+ " "+ ","+
                        "time_event"+ " "+ cursorE.getString(time_event)+ " "+ ","+
                        "place_event"+ " "+ cursorE.getString(place_event)+" "+ ","+
                        "img_url"+ " "+ cursorE.getString(img_url)+ ".");



            }while (cursorE.moveToNext());

        }else{
            cursorE.close();
        }
        db_ticket.close();
    }

    public void deleteDB() {
        db = db_ticket.getWritableDatabase();
        int rows = db.delete("Ticket_table", null, null);
        Log.i("id_ticket","del rows" + String.valueOf(rows));

        rows = db.delete("Event_table",null,null);
        Log.i("id_ticket","del rows" + String.valueOf(rows));

        db_ticket.close();
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        //SingleIvent singleIvent = (SingleIvent)getFragmentManager().findFragmentById(R.id.frame_container);

            if (fromFragment.equals("eventList"))
            {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id", idEvent);
                myBundle.putString("name_event", String.valueOf(name_event));
                myBundle.putString("fromFragment","eventList");
                Fragment fragment = new SingleIvent();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }else
            {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id", idEvent);
                myBundle.putString("name_event", String.valueOf(name_event));
                myBundle.putString("fromFragment","searchFragment");
                Fragment fragment = new SingleIvent();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }

    }

}
