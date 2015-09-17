package customlistviewadapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;



import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import customlistviewapp.AppController;
import customlistviewmodel.Movie;

import webclever.sliding_menu.PhotosFragment;
import webclever.sliding_menu.R;
import webclever.sliding_menu.SingleIvent;

import static android.support.v4.app.ActivityCompat.startActivity;

/**
 * Created by Web on 05.09.2014.
 */
public class CustomListAdapter extends BaseAdapter implements Filterable{

    private Activity activity;
    private LayoutInflater inflater;
    private List<Movie> movieItems;
    private List<Movie> originalMovieItems;
    private EventFilter eventFilter;
    private Context mContext;
    private String fromFragment;
    private ArrayList<Movie> arrayList;

    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter (Context context, Activity activity, List<Movie> movieItems, String fromFragment)
    {

        this.activity = activity;
        this.movieItems = movieItems;
        this.originalMovieItems = movieItems;
        this.mContext = context;
        this.arrayList = new ArrayList<Movie>();
        this.arrayList.addAll(movieItems);
        this.fromFragment = fromFragment;

    }

    public class ViewHolder
    {
        NetworkImageView thumbNail;
        TextView name;
        TextView data;
        TextView time;
        TextView place;
        TextView price;
        TextView city;

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
        if(inflater == null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.list_row,null);
        }
        if(imageLoader == null)
        {
            imageLoader = AppController.getInstance().getImageLoader();
        }

        NetworkImageView thumbNail=(NetworkImageView) convertView.findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView data = (TextView) convertView.findViewById(R.id.data);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        TextView city = (TextView) convertView.findViewById(R.id.place);

        Movie m = movieItems.get(position);

        thumbNail.setImageUrl(m.getThumbnailUrl(),imageLoader);
        name.setText(m.getName());
        data.setText(m.getData());
        time.setText(m.getTime());
        city.setText(m.getCity());
        return convertView;

    }

    public void resetData()
    {
        movieItems = originalMovieItems;
    }

    @Override
    public Filter getFilter() {

        if (eventFilter == null)
            eventFilter = new EventFilter();
        Log.i("serchtext","getFilter");
        return eventFilter;
    }

    private class EventFilter extends Filter
    {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint == null || constraint.length() == 0) {
                // No filter implemented we return all the list
                results.values = originalMovieItems;
                results.count = originalMovieItems.size();
            }
            else {
                // We perform filtering operation
                List<Movie> nPlanetList = new ArrayList<Movie>();

                for (Movie p : movieItems) {
                    if (p.getName().toUpperCase().startsWith(constraint.toString().toUpperCase()))
                        nPlanetList.add(p);
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // Now we have to inform the adapter about the new list filtered
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                movieItems = (List<Movie>) results.values;
                notifyDataSetChanged();
            }


        }

    }

}
