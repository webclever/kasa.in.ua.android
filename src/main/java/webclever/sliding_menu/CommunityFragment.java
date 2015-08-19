package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Singleton.OrderingChild;
import Singleton.OrderingParent;
import Singleton.TicketChildOrdering;
import adapter.OrderingAdapter;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by User on 13.08.2014.
 */
public class CommunityFragment extends Fragment implements OnBackPressedListener {

    private static final String stringUrlOrdering = "https://api.myjson.com/bins/1myei";
    private OrderingAdapter orderingAdapter;

    public CommunityFragment(){setHasOptionsMenu(true);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner,Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_community,conteiner,false);
        ExpandableListView expandableListViewOrdering = (ExpandableListView) rootView.findViewById(R.id.expandableListViewOrdering);
        List<OrderingParent> arrayListOrderParent = new ArrayList<OrderingParent>();
        arrayListOrderParent = addOrdering(stringUrlOrdering);
        orderingAdapter = new OrderingAdapter(this.getActivity(), arrayListOrderParent);
        expandableListViewOrdering.setAdapter(orderingAdapter);
        return rootView;
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
        TextView textViewTicketCount = (TextView)relativeLayoutShopCart.getChildAt(1);
        textViewTicketCount.setText(((MainActivity) getActivity()).getCountTicket());

        super.onCreateOptionsMenu(menu, inflater);
    }

    private ArrayList<OrderingParent> addOrdering(String url)
    {
        final ArrayList<OrderingParent> orderingParentArrayList = new ArrayList<OrderingParent>();
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                try {
                    for (int i = 0; i < jsonArray.length();i++)
                        {

                        JSONObject jsonObjectParent = jsonArray.getJSONObject(i);
                        final OrderingParent orderingParent = new OrderingParent();
                        orderingParent.setNumberOrdering(jsonObjectParent.getString("number"));
                        orderingParent.setTotalCountTicket(jsonObjectParent.getString("total_count_ticket"));
                        orderingParent.setTotalPriceTicket(jsonObjectParent.getString("total_price_ticket"));
                        orderingParent.setDeliveryMethod(jsonObjectParent.getString("delivery_method"));
                        orderingParent.setPaymentMethod(jsonObjectParent.getString("payment_method"));
                        orderingParent.setStatusDelivery(jsonObjectParent.getString("status_delivery"));
                        orderingParent.setStatusPayment(jsonObjectParent.getString("status_payment"));
                        orderingParent.setCreateOrdering(jsonObjectParent.getString("create_ordering"));

                        orderingParent.setOrderingChildArrayList(new ArrayList<OrderingChild>());
                        JSONArray jsonArrayEvent = jsonObjectParent.getJSONArray("array_event");

                            for (int j=0; j < jsonArrayEvent.length(); j++)
                            {
                                JSONObject jsonObjectChild = jsonArrayEvent.getJSONObject(j);
                                final OrderingChild orderingChild = new OrderingChild();
                                orderingChild.setNameEventOrdering(jsonObjectChild.getString("name"));
                                orderingChild.setDataEventOrdering(jsonObjectChild.getString("date"));
                                orderingChild.setTimeEventOrdering(jsonObjectChild.getString("time"));
                                orderingChild.setCityEventOrdering(jsonObjectChild.getString("city"));
                                orderingChild.setTicketChildOrderingArrayList(new ArrayList<TicketChildOrdering>());

                                    JSONArray jsonArrayTicket = jsonObjectChild.getJSONArray("array_ticket");
                                    for (int k=0; k < jsonArrayTicket.length(); k++)
                                    {
                                        JSONObject jsonObjectTicket = jsonArrayTicket.getJSONObject(k);
                                        final TicketChildOrdering ticketChildOrdering = new TicketChildOrdering();
                                        ticketChildOrdering.setSectorOrdering(jsonObjectTicket.getString("sector"));
                                        ticketChildOrdering.setRowOrdering(jsonObjectTicket.getString("row"));
                                        ticketChildOrdering.setPlaceOrdering(jsonObjectTicket.getString("place"));
                                        ticketChildOrdering.setPriceOrdering(jsonObjectTicket.getString("price"));
                                        ticketChildOrdering.setStatusTicket(jsonObjectTicket.getBoolean("status"));
                                        orderingChild.getTicketChildOrderingArrayList().add(ticketChildOrdering);
                                    }
                                orderingParent.getOrderingChildArrayList().add(orderingChild);
                            }

                            orderingParentArrayList.add(orderingParent);
                        }

                        orderingAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonerror",e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                VolleyLog.d("Error: " ,volleyError.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return orderingParentArrayList;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container,fragment).commit();
    }

}
