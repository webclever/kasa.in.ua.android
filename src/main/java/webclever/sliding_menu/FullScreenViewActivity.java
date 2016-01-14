package webclever.sliding_menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import java.util.ArrayList;
import adapter.FullScreenPagerAdapter;

public class FullScreenViewActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscrinactivity);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        Intent i = getIntent();
        final int position = i.getIntExtra("position", 0);
        ArrayList<String> img_url = i.getStringArrayListExtra("img_url");

        Log.i("position",String.valueOf(position));
        Log.i("img_url", img_url.toString());

        FullScreenPagerAdapter adapter = new FullScreenPagerAdapter(FullScreenViewActivity.this, img_url);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);



    }

}
