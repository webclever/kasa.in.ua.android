package webclever.sliding_menu;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Format.DateFormat;
import Singleton.OrderingChild;
import Singleton.OrderingParent;
import Singleton.TicketChildOrdering;
import Singleton.UserProfileSingleton;
import adapter.OrderingAdapter;
import customlistviewapp.AppController;
import interfaces.OnBackPressedListener;

import static webclever.sliding_menu.R.id.frame_container;

/**
 * Created by User on 13.08.2014.
 */
public class FragmentHistoryOrdering extends Fragment implements OnBackPressedListener {

    private UserProfileSingleton userProfile;
    private OrderingAdapter orderingAdapter;
    private DateFormat dateFormat;
    private ProgressBar progressBar;

    public FragmentHistoryOrdering(){setHasOptionsMenu(true);}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup conteiner,Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_ordering,conteiner,false);
        ((MainActivity)getActivity()).setItemChecked(3,true);
        userProfile = new UserProfileSingleton(getActivity());
        dateFormat = new DateFormat();
        ExpandableListView expandableListViewOrdering = (ExpandableListView) rootView.findViewById(R.id.expandableListViewOrdering);
        List<OrderingParent> arrayListOrderParent = new ArrayList<OrderingParent>();

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBarOrdering);

        arrayListOrderParent = addOrdering1();
        orderingAdapter = new OrderingAdapter(this.getActivity(), arrayListOrderParent);
        expandableListViewOrdering.setAdapter(orderingAdapter);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        if (!((MainActivity) getActivity()).getCountTicket().equals("0")) {
            getActivity().getMenuInflater().inflate(R.menu.menu_select_place, menu);
            MenuItem item = menu.findItem(R.id.menuCount);
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

            super.onCreateOptionsMenu(menu, inflater);
        }
    }

    private ArrayList<OrderingParent> addOrdering1()
    {
        progressBar.setVisibility(View.VISIBLE);
        final ArrayList<OrderingParent> orderingParentArrayList = new ArrayList<OrderingParent>();
        final String url = "http://tms.webclever.in.ua/api/getOrders";

        final JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("user_id",userProfile.getUserId());
            jsonObject.put("token",userProfile.getToken());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringPostRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    try {
                        Log.i("REsponse",s);
                        JSONArray jsonArray = new JSONArray(s);
                        for (int i = 0; i < jsonArray.length();i++) {

                        JSONObject jsonObjectParent = jsonArray.getJSONObject(i);
                        final OrderingParent orderingParent = new OrderingParent();
                        orderingParent.setNumberOrdering(jsonObjectParent.getString("id"));
                        orderingParent.setTotalCountTicket(jsonObjectParent.getString("count"));
                        orderingParent.setTotalPriceTicket(jsonObjectParent.getString("total"));
                        orderingParent.setDeliveryMethod(jsonObjectParent.getString("delivery"));
                        orderingParent.setPaymentMethod(jsonObjectParent.getString("pay_type"));
                        orderingParent.setStatusDelivery(jsonObjectParent.getString("delivery_status"));
                        orderingParent.setStatusPayment(jsonObjectParent.getString("pay_status"));
                        orderingParent.setCreateOrdering(jsonObjectParent.getString("date_create"));

                        orderingParent.setOrderingChildArrayList(new ArrayList<OrderingChild>());
                        JSONArray jsonArrayEvent = jsonObjectParent.getJSONArray("tickets");

                        for (int j=0; j < jsonArrayEvent.length(); j++)
                        {
                            final OrderingChild orderingChild = new OrderingChild();
                            JSONObject jsonObjectChild = jsonArrayEvent.getJSONObject(j);
                            JSONObject jsonObjectEventInfo = jsonObjectChild.getJSONObject("event_info");
                            JSONObject jsonObjectCity = jsonObjectEventInfo.getJSONObject("city");

                            orderingChild.setNameEventOrdering(jsonObjectEventInfo.getString("name"));
                            String date = dateFormat.getData(jsonObjectEventInfo.getString("date_start"));
                            String time = dateFormat.getTime(jsonObjectEventInfo.getString("date_start"));

                            orderingChild.setDataEventOrdering(date);
                            orderingChild.setTimeEventOrdering(time);
                            orderingChild.setCityEventOrdering(jsonObjectCity.getString("name"));
                            orderingChild.setTicketChildOrderingArrayList(new ArrayList<TicketChildOrdering>());

                            JSONArray jsonArrayTicket = jsonObjectChild.getJSONArray("tickets");
                            for (int k=0; k < jsonArrayTicket.length(); k++)
                            {
                                JSONObject jsonObjectTicket = jsonArrayTicket.getJSONObject(k);
                                final TicketChildOrdering ticketChildOrdering = new TicketChildOrdering();

                                ticketChildOrdering.setSectorOrdering(jsonObjectTicket.getString("sector"));
                                ticketChildOrdering.setRowOrdering(jsonObjectTicket.getString("row"));
                                ticketChildOrdering.setPlaceOrdering(jsonObjectTicket.getString("place"));
                                ticketChildOrdering.setPriceOrdering(jsonObjectTicket.getString("price"));
                                ticketChildOrdering.setStatusTicket(jsonObjectTicket.getString("status"));
                                ticketChildOrdering.setTypeTicket(jsonObjectTicket.getString("type"));
                                orderingChild.getTicketChildOrderingArrayList().add(ticketChildOrdering);

                            }
                            orderingParent.getOrderingChildArrayList().add(orderingChild);
                        }

                        orderingParentArrayList.add(orderingParent);
                    }
                        progressBar.setVisibility(View.GONE);
                    orderingAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jsonerror",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    Log.d("Error Response code " , String.valueOf(volleyError.networkResponse.statusCode));
                }

                NetworkResponse response = volleyError.networkResponse;
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    switch(response.statusCode){
                        case 400:
                            try {
                                String json = new String(response.data);
                                JSONObject jsonObject = new JSONObject(json);
                                Log.i("Response_error",json);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            break;
                    }
                }
            }
        }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("tmssec", jsonObject.toString());
                Log.i("Response_Header",params.get("tmssec"));
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("token","3748563");
                Log.i("Params",params.toString());
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringPostRequest);
        return orderingParentArrayList;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = new HomeFragment();
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
    }

    @Override
    public void onDestroyView ()
    {
        ((MainActivity)getActivity()).setItemChecked(3,false);
        super.onDestroyView();
    }
}
