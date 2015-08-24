package webclever.sliding_menu;


import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import DataBase.DB_Ticket;
import Singleton.UserProfileSingleton;
import adapter.NavDrawerListAdapter;
import interfaces.OnBackPressedListener;
import location.AppLocationService;



import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;


import io.fabric.sdk.android.Fabric;



public class MainActivity extends FragmentActivity  implements ActionBar.OnNavigationListener {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private String[] navMenuTitles;
    private TypedArray navMenuIcons;

    private ArrayList<NavDrawerItem> navDrawerItems;
    private NavDrawerListAdapter adapter;
    private static String TAG = MainActivity.class.getSimpleName();

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    private double latitude;
    private double longitude;
    protected LocationManager locationManager;

    private List<Address> addresses;
    private Address fetchedAddress;
    public static final String APP_PREFERENCES = "user_profile";
    private SharedPreferences sPref;
    private String STATUS_USER = "status_user";
    private AppLocationService appLocationService;

    private DB_Ticket db_ticket;

    private static final String TWITTER_KEY = "NtcdkYkfnL4hRjN8jg8yNZbsH";
    private static final String TWITTER_SECRET = "gahp8a6Ro2M15sKW2aAuW1vJtitKTkLVgYJor7w2TQAQQ70vsI";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db_ticket = new DB_Ticket(this,5);
        addresses = new ArrayList<Address>();
        appLocationService = new AppLocationService(MainActivity.this);
        sPref = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        //Getting GPS status
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //Getting network status
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        getMyLocationAddress();
        //Toast.makeText(this,getCity,Toast.LENGTH_LONG).show();

        mTitle = mDrawerTitle = getTitle();

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList=(ListView) findViewById(R.id.list_slidermenu);
        mDrawerList.setOnItemClickListener( new SlideMenuClickListener());

        navDrawerItems = new ArrayList<NavDrawerItem>();

        navDrawerItems.add(new NavDrawerItem(navMenuTitles[0],navMenuIcons.getResourceId(0,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[1],navMenuIcons.getResourceId(1,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[2],navMenuIcons.getResourceId(2,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[3],navMenuIcons.getResourceId(3,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[4],navMenuIcons.getResourceId(5,-1)));
        navDrawerItems.add(new NavDrawerItem(navMenuTitles[5],navMenuIcons.getResourceId(6,-1)));

        if (getStatusUser()){
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[6],navMenuIcons.getResourceId(4,-1)));
        }else {
            navDrawerItems.add(new NavDrawerItem(navMenuTitles[7],navMenuIcons.getResourceId(7,-1)));
        }

        navMenuIcons.recycle();

        adapter = new NavDrawerListAdapter(getApplicationContext(),navDrawerItems);
        mDrawerList.setAdapter(adapter);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer1,R.string.app_name,R.string.app_name) {
            public void onDrawerClosed(View view)
            {
                getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu();
            }
            public void onDrawerOpened (View drawerView)
            {
                View viewKeyboard = getCurrentFocus();
                if (viewKeyboard != null) {
                    InputMethodManager imm = (InputMethodManager)getSystemService(
                            Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(viewKeyboard.getWindowToken(), 0);
                }
                getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu();
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if(savedInstanceState==null)
        {
            displayView(0);
        }
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_container);
        OnBackPressedListener onBackPressedListener = null;
        if(fragment instanceof OnBackPressedListener)
        {
            onBackPressedListener = (OnBackPressedListener) fragment;
        }

        if (onBackPressedListener != null)
        {
            onBackPressedListener.onBackPressed();
        }else {
            super.onBackPressed();
        }
        Toast.makeText(getApplicationContext(), "From Activity onBackPressed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        changeMenuItems(getStatusUser());
    }

    public void changeMenuItems(Boolean statusUser) {
        navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);
        if (statusUser) {
            navDrawerItems.set(6, new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(4, -1)));
    }   else {
            navDrawerItems.set(6, new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
        }
        adapter.notifyDataSetChanged();
    }

    public String getCountTicket() {
        SQLiteDatabase DB = db_ticket.getReadableDatabase();
        Cursor cursorDBTicket = DB.query("Ticket_table", null, null, null, null, null, null);
        String count = String.valueOf(cursorDBTicket.getCount());
        cursorDBTicket.close();
        DB.close();
        return count;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
       // int id = item.getItemId();
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        /*switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
            return super.onOptionsItemSelected(item);
        }*/

        return false;

    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public void setTitle (CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {

        return false;
    }

    private class SlideMenuClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id)
        {
            displayView(position);
            View viewKeyboard = getCurrentFocus();
            if (viewKeyboard != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

        }
    }

    private void displayView (int position) {
        Fragment fragment = null;
        switch (position)
        {
            case 0:
                fragment = new HomeFragment();
                break;
            case 1:
                fragment = new FindPeopleFragment();
                break;
            case 2:
                fragment = new PhotosFragment();
                break;
            case 3:
                fragment = new CommunityFragment();
                break;
            case 4:
                fragment = new LocKasaFragment();
                break;
            case 5:
                fragment = new WhatsHotFragment();
                break;
            case 6:
                if (getStatusUser()){
                    fragment = new PagesFragment();
                }else {
                    Intent intent = new Intent(this,LoginActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(mDrawerList);
                }
                break;
            default:
                break;

        }
        if(fragment != null)
        {
            FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
            mDrawerList.setItemChecked(position,true);
            mDrawerList.setSelection(position);
            setTitle(navMenuTitles[position]);
            mDrawerLayout.closeDrawer(mDrawerList);
        }
        else
        {
            Log.e("MainActivity","Error in creating fragment");
        }

    }

    public void onLocationChanged() {

        if (isGPSEnabled) {
            //here Gps
            Location gpsLocation = appLocationService
                    .getLocation(LocationManager.GPS_PROVIDER);

            if (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
                /*Toast.makeText(
                        getApplicationContext(),
                        "Mobile Location (GPS): \nLatitude: " + latitude
                                + "\nLongitude: " + longitude,
                        Toast.LENGTH_LONG).show();*/

            } else {
                showSettingsAlert("GPS");
            }


        } else if (isNetworkEnabled) {
            //here Network
            Location nwLocation = appLocationService
                    .getLocation(LocationManager.NETWORK_PROVIDER);

            if (nwLocation != null) {
                latitude = nwLocation.getLatitude();
                longitude = nwLocation.getLongitude();
                /*Toast.makeText(
                        getApplicationContext(),
                        "Mobile Location (NW): \nLatitude: " + latitude
                                + "\nLongitude: " + longitude,
                        Toast.LENGTH_LONG).show();
                Log.i("latitude", String.valueOf(latitude));
                Log.i("longitude", String.valueOf(longitude));*/



            } else {
                showSettingsAlert("NETWORK");
            }

        }

    }

    public Location getLocation(){
        onLocationChanged();

        Location location = new Location("myLocation");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        return location;
    }



    public void getMyLocationAddress() {

        onLocationChanged();

        try {

            Geocoder geocoder = new Geocoder(getApplicationContext());

            //Place your latitude and longitude
            addresses = geocoder.getFromLocation(latitude,longitude,1);

            if(!addresses.isEmpty()) {

                fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for(int i=0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                }

                //myAddress.setText("I am at: " +strAddress.toString());
                Toast.makeText(this,strAddress.toString(),Toast.LENGTH_LONG).show();

            }

            else {
                Toast.makeText(this, "No location found..!", Toast.LENGTH_LONG).show();
            }

        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
        }

    }

    public Boolean getStatusUser()
    {
        return sPref.getBoolean("user_status",false);
    }

    public String getNameCity() {
        try {
            return fetchedAddress.getAddressLine(1);
        }catch (Exception e)
        {
            //Log.e("error get address",e.getMessage());
            return null;
        }

    }

    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog
                .setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }
}
