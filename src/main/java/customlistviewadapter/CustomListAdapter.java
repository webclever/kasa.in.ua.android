package customlistviewadapter;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import customlistviewapp.AppController;
import customlistviewmodel.Movie;

import webclever.sliding_menu.MyNetworkImageView;
import webclever.sliding_menu.R;

public class CustomListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Movie> movieItems;
    //private EventFilter eventFilter;


    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter (Context context, Activity activity, List<Movie> movieItems, String fromFragment)
    {
        this.activity = activity;
        this.movieItems = movieItems;


    }

    @Override
    public int getCount ()
    {
        return movieItems.size();
    }

    @Override
    public Object getItem(int position)
    {
        return movieItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return movieItems.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if(inflater == null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(imageLoader == null)
        {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        if(convertView == null)        {
            convertView = inflater.inflate(R.layout.list_row,null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.data = (TextView) convertView.findViewById(R.id.data);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.city = (TextView) convertView.findViewById(R.id.place);
            holder.thumbNail = (NetworkImageView) convertView.findViewById(R.id.thumbnail);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Movie m = movieItems.get(position);
        holder.name.setText(Html.fromHtml(m.getName()));
        holder.data.setText(m.getData());
        holder.time.setText(m.getTime());
        holder.city.setText(m.getCity());

        if (!m.getThumbnailUrl().equals("null")) {
            holder.thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
        }else {
            holder.thumbNail.setDefaultImageResId(R.mipmap.ic_default_poster);
        }

        return convertView;
    }

    static class ViewHolder {
        TextView name;
        TextView data;
        TextView time;
        TextView city;
        NetworkImageView thumbNail;
    }



}
