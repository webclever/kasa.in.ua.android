package adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import customlistviewapp.AppController;
import webclever.sliding_menu.R;

/**
 * Created by Admin on 13.10.2014.
 */
public class FullScreenPagerAdapter extends PagerAdapter {

    private Activity _activity;
    private ArrayList<String> _img_str;
    private LayoutInflater inflater;
    private ImageLoader imageLoader = AppController.getInstance().getImageLoader();


    public FullScreenPagerAdapter(Activity activity,ArrayList<String> img_str)
    {
        this._activity = activity;
        this._img_str = img_str;
    }

    @Override
    public int getCount()
    {
        return _img_str.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object)
    {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position)
    {
        NetworkImageView networkImageView;

        inflater = (LayoutInflater) _activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View viewLayot = inflater.inflate(R.layout.list_gridview,container,false);

        if(imageLoader == null)
        {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        networkImageView = (NetworkImageView) viewLayot.findViewById(R.id.volleyimg);
        networkImageView.setImageUrl(_img_str.get(position),imageLoader);

        ((ViewPager) container).addView(viewLayot);

        return viewLayot;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }


}
