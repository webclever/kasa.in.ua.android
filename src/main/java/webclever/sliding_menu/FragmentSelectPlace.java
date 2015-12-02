package webclever.sliding_menu;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
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
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import DataBase.DB_Ticket;
import Format.EncodingTicketCount;
import Singleton.DataEventSingelton;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;


import static webclever.sliding_menu.R.id.frame_container;
import static webclever.sliding_menu.R.id.name_event;
import static webclever.sliding_menu.R.id.webviewSchema;

/**
 * Created by Admin on 23.01.2015.
 */
@SuppressLint("SetJavaScriptEnabled")
public class FragmentSelectPlace extends Fragment implements OnBackPressedListener{

    private FragmentActivity myContext;

    private Animation animationShake;
    private Animation animationBounce;
    private ImageButton ConfirmButton;

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

    private static String TAG = MainActivity.class.getSimpleName();
    private String fromFragment;

    private Integer serverIdPlace;
    private String schemaIdPlace;
    private String Row;
    private String name_Row;
    private String Place;
    private String Price;
    private String Sector;
    private String Sector_type;

    private Toast toast = null;

    private TextView textViewTicketCount;
    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private OvershootInterpolator overshootInterpolator = new OvershootInterpolator(20f);
    private HashMap<String,Boolean> containerTicket;
    private EncodingTicketCount encodingTicketCount;
    private TextView textViewCountTicket;
    private String countTicket;

    private ImageButton imageButtonBack;

    private Menu menu;

    public FragmentSelectPlace()
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
        this.menu = menu;
        getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);
        cheSowSopCart();
        MenuItem item = menu.findItem(R.id.menuCount);
        RelativeLayout relativeLayoutShopCart = (RelativeLayout) item.getActionView();
        relativeLayoutShopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment = new FragmentBasket();
                Bundle bundle = new Bundle();
                bundle.putInt("id_event",idEvent);
                bundle.putString("fragment", "select_place");
                bundle.putString("name_event", stringNameEvent);

                fragment.setArguments(bundle);
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
        View rootView = inflater.inflate(R.layout.fragment_select_place, container, false);
        countTicket = " " + getResources().getString(R.string.page_select_sum);
        stringNameEvent = getArguments().getString("name_event");
        idEvent = getArguments().getInt("id");
        Log.i("id_event",String.valueOf(idEvent));
        fromFragment = getArguments().getString("fromFragment");

        imageButtonBack = (ImageButton) rootView.findViewById(R.id.back_button_schema);

        //Loader
        textViewStatus = (TextView) rootView.findViewById(R.id.textViewStatus);
        mProgress = (ProgressBar) rootView.findViewById(R.id.progressbarcircular);
        mProgress.setMax(100);
        mProgress.setVisibility(View.GONE);
        textViewStatus.setVisibility(View.GONE);

        webViewSchema = (WebView) rootView.findViewById(R.id.webviewSchema);
        webViewSchema.loadUrl("http://tms.webclever.in.ua/api/previewScheme?event_id=" + String.valueOf(idEvent) + "&token=3748563");

        webViewSchema.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        webViewSchema.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webViewSchema.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webViewSchema.getSettings().setDomStorageEnabled(true);

        webViewSchema.getSettings().setSupportZoom(true);

        webViewSchema.getSettings().setDisplayZoomControls(true);
        webViewSchema.getSettings().setBuiltInZoomControls(true);

        mProgress.setProgress(0);
        webViewSchema.setWebChromeClient(new MyWebViewChromeClient());
        webViewSchema.getSettings().setJavaScriptEnabled(true);
        JavaScriptInterface javaScriptInterface = new JavaScriptInterface(myContext);
        webViewSchema.addJavascriptInterface(javaScriptInterface, "JSInterface");

        db_ticket = new DB_Ticket(getActivity(),5);

        textViewPriceall = (TextView) rootView.findViewById(R.id.textView42);
        totalPrice = Integer.parseInt(((MainActivity) getActivity()).getTotalPrice());
        textViewPriceall.setText(((MainActivity) getActivity()).getTotalPrice());
        textViewTicketall = (TextView) rootView.findViewById(R.id.textView34);
        textViewTicketall.setText(((MainActivity) getActivity()).getCountTicket());
        encodingTicketCount = new EncodingTicketCount();
        textViewCountTicket = (TextView) rootView.findViewById(R.id.textView41);
        textViewCountTicket.setText(encodingTicketCount.getNumEnding(((MainActivity) getActivity()).getCountTicket()) + countTicket);
        if (getActivity().getActionBar() != null){
        getActivity().getActionBar().setTitle(stringNameEvent);}

        ConfirmButton = (ImageButton) rootView.findViewById(R.id.confirm_button);

        animationShake = AnimationUtils.loadAnimation(getActivity(),R.anim.shake);
        animationShake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ConfirmButton.setImageResource(R.mipmap.ic_minus_button);
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
                ConfirmButton.setImageResource(R.mipmap.ic_plus_button);
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
                cheSowSopCart();
                if (ConfirmButton.getTag() == "0") {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {


                        webViewSchema.loadUrl("javascript:mobileCart(\'" + schemaIdPlace + "\',1)");
                        if (serverIdPlace != null) {
                            Log.i("add_ticket", schemaIdPlace);
                            addTicket();
                        }
                        textViewTicketCount.animate().setInterpolator(decelerateInterpolator).scaleX(.7f).scaleY(.7f);

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        textViewTicketCount.animate().setInterpolator(overshootInterpolator).scaleX(1f).scaleY(1f);
                    }
                } else if (ConfirmButton.getTag() == "1") {

                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                        webViewSchema.loadUrl("javascript:mobileCart(\'" + schemaIdPlace + "\',0)");
                        if (serverIdPlace != null) {
                            delTicket();
                        }
                        containerTicket.put(schemaIdPlace, false);
                        textViewTicketCount.animate().setInterpolator(decelerateInterpolator).scaleX(1.3f).scaleY(1.3f);

                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        textViewTicketCount.animate().setInterpolator(overshootInterpolator).scaleX(1f).scaleY(1f);
                    }
                }
                return false;

            }
        });

        imageButtonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webViewSchema.loadUrl("javascript:backToMacro()");
                imageButtonBack.setVisibility(View.GONE);
                getZoom();
            }
        });

        ImageView sold = (ImageView) rootView.findViewById(R.id.sold);
        sold.setColorFilter(Color.parseColor("#e5e6e6"), PorterDuff.Mode.MULTIPLY);
        ImageView imageView2 = (ImageView) rootView.findViewById(R.id.imageView4);
        imageView2.setColorFilter(Color.parseColor("#ffef00"), PorterDuff.Mode.MULTIPLY);
        ImageView imageView4 = (ImageView) rootView.findViewById(R.id.imageView5);
        imageView4.setColorFilter(Color.parseColor("#a5bfbd"),PorterDuff.Mode.MULTIPLY);
        ImageView imageView3 = (ImageView) rootView.findViewById(R.id.imageView6);
        imageView3.setColorFilter(Color.parseColor("#ff4214"),PorterDuff.Mode.MULTIPLY);

        return rootView;
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
        public void clickHandler(String id, String serverId, String placeType) {
            Log.i("from", id + " " + serverId + " " + placeType);
            Log.i("id_place_server: ", serverId);

            if (placeType.equals("2")){
                getPlaceInfoFun(id);

            }else if (placeType.equals("1")){

                getPlaceInfo(id,serverId);
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

            }else{
                serverIdPlace = null;
            }
        }
        @JavascriptInterface
        public void schemeLoadingListener() {

            Log.i("schemeLoadingListener", "finish");
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    Log.i("checkShowBackButton", "finish2");
                    getZoom();
                    getBasketTicket();
                }
            });

        }

        @JavascriptInterface
        public void showMacroBackButton(String checkShowBack) {
            final Boolean checkShowBackButton = new Boolean(checkShowBack);
            Log.i("checkShowBackButtonBool", String.valueOf(checkShowBackButton));
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    if (checkShowBackButton) {
                        imageButtonBack.setVisibility(View.VISIBLE);
                    } else {
                        imageButtonBack.setVisibility(View.GONE);
                    }
                }
            });
        }
    }

    private void getPlaceInfoFun(final String id_fun){
        final String[] arrIdTicketFunZone = getIdTicketFunZone(id_fun);
        final String url = "http://tms.webclever.in.ua/api/getPlaces";
        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        try {
                            JSONArray jsonArray = new JSONArray(s);
                            Log.i("REsponse", s);

                            for (int i=0; i<jsonArray.length(); i++){

                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                JSONObject jsonObjectSector = jsonObject.getJSONObject("sector");
                                String pricePlace = jsonObject.getString("price");
                                if (!pricePlace.equals("null")) {
                                    schemaIdPlace = id_fun;
                                    serverIdPlace = jsonObject.getInt("id");
                                    showPlaceInfo(jsonObject.getString("label"), jsonObjectSector.getString("name"), "", "", jsonObject.getString("price"), jsonObject.getString("type"));
                                    ConfirmButton.startAnimation(animationBounce);
                                }else {
                                    serverIdPlace = null;
                                }

                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i("Response_err", String.valueOf(volleyError.getMessage()));
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token", "3748563");
                params.put("sector_id", id_fun);
                params.put("event_id", String.valueOf(idEvent));
                params.put("places", Arrays.toString(arrIdTicketFunZone));

                Log.i("Params", params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);

    }

    private String[] getIdTicketFunZone(String id_fun_zone){
        readDB();
        db_ticket = new DB_Ticket(getActivity(),5);
        db = db_ticket.getReadableDatabase();
        String[] idTicketFunZone = new String[0];

        Cursor cursorSelectedPlace = db.query("Ticket_table", new String[]{"id_ticket"}, "id_place_schema = ? AND id_event = ?", new String[]{id_fun_zone, String.valueOf(idEvent)}, null, null, null, null);
        if (cursorSelectedPlace != null){
            idTicketFunZone = new String[cursorSelectedPlace.getCount()];
            if (cursorSelectedPlace.getCount() > 0){
                cursorSelectedPlace.moveToFirst();
                for (int i=0; i < cursorSelectedPlace.getCount(); i++){
                    idTicketFunZone[i] = cursorSelectedPlace.getString(0);
                    cursorSelectedPlace.moveToNext();
                }
                Log.i("id_ticket_fun", Arrays.toString(idTicketFunZone));
            }
        }
        assert cursorSelectedPlace != null;
        cursorSelectedPlace.close();
        db_ticket.close();

        return idTicketFunZone;
    }

    private void getPlaceInfo(final String schemaId, final String serverId) {
        final String urlInfoPlace = "http://tms.webclever.in.ua/api/getPlaces?places=["+serverId+"]&token=3748563";
        JsonArrayRequest jsonArrayRequestPlaceInfo = new JsonArrayRequest(urlInfoPlace,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            Log.i("Response",jsonArray.toString());
                            for (int i=0; i < jsonArray.length(); i++){


                                JSONObject jsonObjectInfoPlace = jsonArray.getJSONObject(i);

                                serverIdPlace = jsonObjectInfoPlace.getInt("id");
                                schemaIdPlace = schemaId;

                                JSONObject jsonObjectRow = jsonObjectInfoPlace.getJSONObject("row");
                                JSONObject jsonObjectPlace = jsonObjectInfoPlace.getJSONObject("place");
                                JSONObject jsonObjectSector = jsonObjectInfoPlace.getJSONObject("sector");

                                showPlaceInfo(jsonObjectSector.getString("name"),
                                        jsonObjectRow.getString("prefix"),
                                        jsonObjectRow.getString("name"),
                                        jsonObjectPlace.getString("name"),
                                        jsonObjectInfoPlace.getString("price"),
                                        jsonObjectInfoPlace.getString("type"));
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

    private void getBasketTicket() {

        String[] str;
        db = db_ticket.getReadableDatabase();
        Cursor cursorSelectedPlace =  db.query("Ticket_table",new String[]{"id_place_schema , type_sector"},"id_event="+String.valueOf(idEvent),null,null,null,null,null);
        if (cursorSelectedPlace != null){
            if (cursorSelectedPlace.getCount() > 0){
                cursorSelectedPlace.moveToFirst();
                str = new String[cursorSelectedPlace.getCount()];
                for (int i=0; i < cursorSelectedPlace.getCount(); i++){
                    if (cursorSelectedPlace.getString(1).equals("1")) {
                        str[i] = cursorSelectedPlace.getString(0);
                        containerTicket.put(cursorSelectedPlace.getString(0), true);
                    }
                    cursorSelectedPlace.moveToNext();
                }
                addTicketToSchema(str);
            }
        }
        assert cursorSelectedPlace != null;
        cursorSelectedPlace.close();
        db_ticket.close();

    }

    private void addTicketToSchema(final String[] masIDSchema) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                webViewSchema.post(new Runnable() {
                    @Override
                    public void run() {
                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {
                                    ValueCallback<String> resultCallback;
                                    resultCallback = new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String value) {
                                            Log.i("error", value);
                                        }
                                    };

                                    for (int i = 0; i < masIDSchema.length; i++) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                            webViewSchema.evaluateJavascript("javascript:mobileCart(\'" + masIDSchema[i] + "\',1);", resultCallback);
                                        }
                                    }
                                }
                            });
                    }
                });

            } else {
                for (int i = 0; i < masIDSchema.length; i++) {
                    webViewSchema.loadUrl("javascript:mobileCart(\'" + masIDSchema[i] + "\',1);");
                }
            }


    }

    private void delTicket() {
        db_ticket = new DB_Ticket(getActivity(),5);
        db = db_ticket.getWritableDatabase();
        int del_id_ticket = db.delete("Ticket_table", "id_ticket=" + String.valueOf(serverIdPlace), null);
        if (del_id_ticket != 0){
            textViewTicketCount.setText(String.valueOf(--totalTicket));
            textViewTicketall.setText(String.valueOf(totalTicket));
            textViewCountTicket.setText(encodingTicketCount.getNumEnding(String.valueOf(totalTicket)) + countTicket);
            String price = String.valueOf( Integer.parseInt(textViewPriceall.getText().toString()) - Integer.parseInt(Price));
            textViewPriceall.setText(price);
        }
        Log.i("id_ticket_del", String.valueOf(del_id_ticket));
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

    private void showPlaceInfo(String sector,String rowName,String row, String place, String price, String type_sector) {
        String placeInfo = rowName + ": " + row + " " + getResources().getString(R.string.page_basket_list_name_place) + " " + place + " | " + price +" " + getResources().getString(R.string.page_select_place_grn);
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  layoutToast = layoutInflater.inflate(R.layout.list_toast, null);
        TextView textViewToast = (TextView) layoutToast.findViewById(R.id.textViewToast);
        textViewToast.setText(placeInfo);

        if (toast != null){
            toast.cancel();
        }
        toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layoutToast);
        toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, -10, 130);
        toast.show();
        Sector = sector;
        Sector_type = type_sector;
        name_Row = rowName;
        Row = row;
        Place = place;
        Price = price;
    }

    /**Add data to data base*/
    private void addTicket() {

        if (Integer.parseInt(((MainActivity)getActivity()).getCountTicket()) < 50) {

            db = db_ticket.getWritableDatabase();
            int intIdEvent = DataEventSingelton.getInstance().getId_event();
            String stringNameEvent = DataEventSingelton.getInstance().getName_event();
            String stringDateEvent = DataEventSingelton.getInstance().getDate_event();
            String stringTimeEvent = DataEventSingelton.getInstance().getTime_event();
            String stringPlaceEvent = DataEventSingelton.getInstance().getPlace_event();

            Cursor cursorT = db.query("Ticket_table", new String[]{"id_ticket"}, "id_ticket=" + String.valueOf(serverIdPlace), null, null, null, null, null);

            if (cursorT != null) {
                if (cursorT.getCount() > 0) {
                    cursorT.moveToFirst();
                    Log.i("id_ticket", cursorT.getString(0));

                } else {

                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id_ticket", serverIdPlace);
                    contentValues.put("zon_ticket", Sector);
                    contentValues.put("name_row_ticket", name_Row);
                    contentValues.put("row_ticket", Row);
                    contentValues.put("place_ticket", Place);
                    contentValues.put("price_ticket", Price);
                    contentValues.put("id_place_schema", schemaIdPlace);
                    contentValues.put("id_event", String.valueOf(intIdEvent));
                    contentValues.put("type_sector", Sector_type);

                    long id_ticket = db.insert("Ticket_table", "id_ticket=" + serverIdPlace, contentValues);
                    Log.i("id_ticket_add", String.valueOf(id_ticket));
                    if (id_ticket != -1) {

                        containerTicket.put(schemaIdPlace, true);
                        totalTicket++;
                        totalPrice = totalPrice + Integer.parseInt(Price);
                        textViewTicketall.setText(String.valueOf(totalTicket));
                        textViewCountTicket.setText(encodingTicketCount.getNumEnding(String.valueOf(totalTicket)) + countTicket);
                        textViewPriceall.setText(String.valueOf(totalPrice));
                        String count = textViewTicketCount.getText().toString();
                        Integer integerCount = Integer.parseInt(count);
                        textViewTicketCount.setText(String.valueOf(++integerCount));

                    }

                }
                cursorT.close();
            }

            Cursor cursorE = db.query("Event_table", new String[]{"id_event"}, "id_event=" + String.valueOf(intIdEvent), null, null, null, null, null);
            if (cursorE != null) {
                if (cursorE.getCount() > 0) {
                    cursorE.moveToFirst();
                    Log.i("id_ticket", cursorE.getString(0));
                } else {
                    ContentValues contentValuesEvent = new ContentValues();
                    contentValuesEvent.put("id_event", intIdEvent);
                    contentValuesEvent.put("name_event", stringNameEvent);
                    contentValuesEvent.put("date_event", stringDateEvent);
                    contentValuesEvent.put("time_event", stringTimeEvent);
                    contentValuesEvent.put("place_event", stringPlaceEvent);
                    long id_event = db.insert("Event_table", "id_event" + String.valueOf(intIdEvent), contentValuesEvent);
                    Log.i("id_ticket_addEvent", String.valueOf(id_event));
                }
                cursorE.close();
            }

            db_ticket.close();
        }else {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
            alertDialog.setTitle(getResources().getString(R.string.page_order_attention));
            alertDialog.setMessage(getResources().getString(R.string.page_select_max_ticket));
            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
            alertDialog.show();
        }

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

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub

            if (fromFragment.equals("eventList"))
            {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id", idEvent);
                myBundle.putString("name_event", String.valueOf(name_event));
                myBundle.putString("fromFragment","eventList");
                Fragment fragment = new FragmentEventPage();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }else {
                Bundle myBundle = new Bundle();
                myBundle.putInt("id", idEvent);
                myBundle.putString("name_event", String.valueOf(name_event));
                myBundle.putString("fromFragment","searchFragment");
                Fragment fragment = new FragmentEventPage();
                fragment.setArguments(myBundle);
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
            }

    }

    private void cheSowSopCart() {
        if(((MainActivity)getActivity()).getCountTicket().equals("0")){
            menu.getItem(0).setVisible(false);
        }else {
            menu.getItem(0).setVisible(true);
        }
    }

    private void getZoom(){

        webViewSchema.setPadding(0, 0, 0, 0);

        webViewSchema.getSettings().setAllowContentAccess(true);
        webViewSchema.getSettings().setAppCacheEnabled(true);

        webViewSchema.getSettings().setLoadWithOverviewMode(true);
        webViewSchema.getSettings().setUseWideViewPort(true);

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR1){
            webViewSchema.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
            webViewSchema.setInitialScale(0);
        }else {
            webViewSchema.setInitialScale(1);
        }

        zoomOut();

    }

    private void zoomOut(){
        while (webViewSchema.zoomOut());
    }


}
