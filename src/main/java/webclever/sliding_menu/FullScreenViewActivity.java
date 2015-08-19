package webclever.sliding_menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;


import java.util.ArrayList;

import adapter.FullScreenPagerAdapter;

/**
 * Created by Admin on 13.10.2014.
 */
public class FullScreenViewActivity extends Activity {

    private ArrayList<String> img_url;
    private ViewPager viewPager;
    private FullScreenPagerAdapter adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscrinactivity);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        img_url = new ArrayList<String>();
        Intent i = getIntent();
        final int position = i.getIntExtra("position", 0);
        img_url = i.getStringArrayListExtra("img_url");

        Log.i("position",String.valueOf(position));
        Log.i("img_url",img_url.toString());

        adapter = new FullScreenPagerAdapter(FullScreenViewActivity.this,img_url);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);



    }

}
