package adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Format.EncodingTicketCount;
import Singleton.OrderingChild;
import Singleton.OrderingParent;
import Singleton.TicketChildOrdering;
import webclever.sliding_menu.R;


public class OrderingAdapter extends BaseExpandableListAdapter {
    private Activity activity;
    private LayoutInflater layoutInflater;
    private LayoutInflater layoutInflaterChild;
    private List<OrderingParent> orderingParents;
    private EncodingTicketCount encodingTicketCount;

    public OrderingAdapter(Activity activity, List<OrderingParent> orderingParents)
    {   encodingTicketCount = new EncodingTicketCount();
        this.activity = activity;
        this.orderingParents = orderingParents;
    }

    @Override
    public int getGroupCount() {
        return orderingParents.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        int size=0;
        if(orderingParents.get(groupPosition).getOrderingChildArrayList() != null)
        {
            size = orderingParents.get(groupPosition).getOrderingChildArrayList().size();
        }

        return size;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return orderingParents.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return orderingParents.get(groupPosition).getOrderingChildArrayList().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        final OrderingParent orderingParent = orderingParents.get(groupPosition);
        if (layoutInflater == null)
        {
            layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.list_ordering_header,null);
        }


        TextView textViewNumber = (TextView) convertView.findViewById(R.id.textViewNumberOrder);
        TextView textViewStatus = (TextView) convertView.findViewById(R.id.textViewStatusOrder);
        RelativeLayout relativeLayoutHeader = (RelativeLayout) convertView.findViewById(R.id.relLayoutOrderingHeader);

        textViewNumber.setText("# " + orderingParent.getNumberOrdering());
        String countTicket = orderingParent.getTotalCountTicket() + " " + encodingTicketCount.getNumEnding(orderingParent.getTotalCountTicket()) + " " + orderingParent.getTotalPriceTicket() + " грн.";
        textViewStatus.setText(countTicket);

        if (isExpanded)
        {
            relativeLayoutHeader.setBackgroundColor(activity.getResources().getColor(R.color.list_divider1));
        }else {
            relativeLayoutHeader.setBackgroundColor(activity.getResources().getColor(android.R.color.white));
        }


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final OrderingParent orderingParent = orderingParents.get(groupPosition);
        final OrderingChild orderingChild = orderingParent.getOrderingChildArrayList().get(childPosition);
        final ArrayList<TicketChildOrdering> ticketChildOrderingArrayList = orderingChild.getTicketChildOrderingArrayList();

        if (layoutInflaterChild == null)
        {
            layoutInflaterChild = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null)
        {
            convertView = layoutInflaterChild.inflate(R.layout.list_event_ordering,null);
        }
        RelativeLayout relativeLayoutStatus = (RelativeLayout) convertView.findViewById(R.id.relative_layout_status);
        TextView textViewNameEvent = (TextView) convertView.findViewById(R.id.textViewNameEventOrdering);
        TextView textViewDataEvent = (TextView) convertView.findViewById(R.id.textViewDateEventOrdering);
        TextView textViewTimeEvent = (TextView) convertView.findViewById(R.id.textViewTimeEventOrdering);
        TextView textViewCityEvent = (TextView) convertView.findViewById(R.id.textViewCityEventOrdering);
        textViewCityEvent.setAllCaps(true);
        TextView textViewCreate = (TextView) convertView.findViewById(R.id.textViewCreate);
        TextView textViewDeliveryMethod = (TextView) convertView.findViewById(R.id.textViewDeliveryMethod);
        TextView textViewMethodPayment = (TextView) convertView.findViewById(R.id.textViewMethodPayment);
        TextView textViewStatusDelivery = (TextView) convertView.findViewById(R.id.textViewStatusDelivery);
        TextView textViewStatusPayment = (TextView) convertView.findViewById(R.id.textViewStatusPayment);
        ViewGroup viewGroupTicketContainer = (ViewGroup) convertView.findViewById(R.id.linearLayoutContainerTicketOrdering);
        viewGroupTicketContainer.removeAllViews();

        textViewNameEvent.setText(orderingChild.getNameEventOrdering());
        textViewDataEvent.setText(orderingChild.getDataEventOrdering());
        textViewTimeEvent.setText(orderingChild.getTimeEventOrdering());
        textViewCityEvent.setText(orderingChild.getCityEventOrdering());

        for (int i=0; i < ticketChildOrderingArrayList.size(); i++)
        {
            TicketChildOrdering ticketChildOrdering = ticketChildOrderingArrayList.get(i);
            ViewGroup viewGroupTicket = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.list_item_example, viewGroupTicketContainer, false);
            TextView textViewSectorTicket = (TextView) viewGroupTicket.findViewById(R.id.sectorTicketOrdering);
            textViewSectorTicket.setAllCaps(true);
            TextView textViewRowTicket = (TextView) viewGroupTicket.findViewById(R.id.rowTicketOrdering);
            TextView textViewPlaceTicket = (TextView) viewGroupTicket.findViewById(R.id.placeTicketOrdering);
            TextView textViewPriceTicket = (TextView) viewGroupTicket.findViewById(R.id.priceTicketOrdering);
            TextView textViewStatusTicket = (TextView) viewGroupTicket.findViewById(R.id.textViewStatusTicket);

            textViewSectorTicket.setText(ticketChildOrdering.getSectorOrdering());
            textViewRowTicket.setText(ticketChildOrdering.getRowOrdering());
            textViewPlaceTicket.setText(ticketChildOrdering.getPlaceOrdering());
            textViewPriceTicket.setText(ticketChildOrdering.getPriceOrdering() + "₴");
            if (!ticketChildOrdering.getStatusTicket().equals("1")){
                textViewStatusTicket.setVisibility(View.VISIBLE);
            }
            viewGroupTicketContainer.addView(viewGroupTicket,0);
            Log.i("Count_view",String.valueOf(viewGroupTicketContainer.getChildCount()));
        }
        Log.i("expantable_childPositio",String.valueOf(childPosition));
        Log.i("expantable_orderingPare",String.valueOf(orderingParent.getOrderingChildArrayList().size()));
        if (childPosition == 0)
        {
            Log.i("expantable", String.valueOf(childPosition));
            textViewDeliveryMethod.setText(orderingParent.getDeliveryMethod());
            textViewMethodPayment.setText(orderingParent.getPaymentMethod());
            textViewStatusDelivery.setText(orderingParent.getStatusDelivery());
            textViewStatusPayment.setText(orderingParent.getStatusPayment());
            textViewCreate.setText("Створено: " + orderingParent.getCreateOrdering());
            relativeLayoutStatus.setVisibility(View.VISIBLE);

        }else {

            relativeLayoutStatus.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
