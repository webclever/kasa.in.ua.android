package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import DataBase.DB_Ticket;
import webclever.sliding_menu.FragmentBasket;
import webclever.sliding_menu.R;

/**
 * Created by Zhenya on 18.03.2015.
 */
public class ViewBasketAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Basket> basketList;
    private DB_Ticket db_ticket;
    private SQLiteDatabase db;


    public ViewBasketAdapter(Activity activity, List<Basket> basketList)
    {
        this.activity = activity;
        this.basketList = basketList;
    }

    @Override
    public int getCount() {
        return basketList.size();
    }

    @Override
    public Object getItem(int i) {
        return basketList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View view, final ViewGroup viewGroup) {

        if(inflater == null)
        {
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if(view == null)
        {
            view = inflater.inflate(R.layout.list_viewgroup_basket, viewGroup, false);
        }

        TextView textViewNameEvent = (TextView) view.findViewById(R.id.textViewNameEventBasket);
        TextView textViewDateEvent = (TextView) view.findViewById(R.id.textViewDateEventBasket);
        TextView textViewTimeEvent = (TextView) view.findViewById(R.id.textViewTimeEventBasket);
        TextView textViewCityEvent = (TextView) view.findViewById(R.id.textViewCityEventBasket);
        final Basket basket = basketList.get(position);
        textViewNameEvent.setText(basket.getNameBasket());
        textViewDateEvent.setText(basket.getDateBasket());
        textViewTimeEvent.setText(basket.getTimeBasket());
        textViewCityEvent.setText(basket.getCityBasket());
        final ArrayList<Basket_Child> arrayListBasketChild = basket.getBasket_childArrayList();
        final ViewGroup viewGroupTicketContainer = (ViewGroup) view.findViewById(R.id.container_basket);
        viewGroupTicketContainer.removeAllViews();
        for (int i = 0; i < arrayListBasketChild.size(); i++){
            final  Basket_Child basket_child = arrayListBasketChild.get(i);
            final ViewGroup viewGroupTicket = (ViewGroup) LayoutInflater.from(activity).inflate(R.layout.list_basket_ticket, viewGroupTicketContainer, false);
            TextView textViewSector = (TextView) viewGroupTicket.findViewById(R.id.sectorTicketBasket);
            TextView textViewNameRow = (TextView) viewGroupTicket.findViewById(R.id.rowdef);
            TextView textViewRow = (TextView) viewGroupTicket.findViewById(R.id.rowTicketBasket);
            TextView textViewPlace = (TextView) viewGroupTicket.findViewById(R.id.placeTicketBasket);
            TextView textViewPrice = (TextView) viewGroupTicket.findViewById(R.id.priceTicketBasket);
                textViewSector.setText(basket_child.getNameBasketChild());
                textViewNameRow.setText(basket_child.getName_row() + ": ");
                textViewRow.setText(String.valueOf(basket_child.getRowBasketChild()));
                textViewPlace.setText(basket_child.getPlaceBasketChild());
                textViewPrice.setText(basket_child.getPriceBasketChild() + " грн.");
            final View finalV = view;
            ImageView imageView = (ImageView) viewGroupTicket.findViewById(R.id.imageViewTicketBasket);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View viewOnClick) {
                    db_ticket = new DB_Ticket(activity, 5);
                    db = db_ticket.getWritableDatabase();

                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("Ви бажаєте видалити цей квиток?");
                    builder.setCancelable(false);
                    builder.setPositiveButton("Так", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int del_id_ticket = db.delete("Ticket_table", "id_ticket=" + String.valueOf(basket_child.getId_ticket()), null);
                            Log.i("id_ticket_del", String.valueOf(del_id_ticket));
                            //viewGroupTicketContainer.removeView(viewGroupTicket);
                            if (arrayListBasketChild.size() == 1) {

                                int del_id_event = db.delete("Event_table","id_event="+String.valueOf(basket_child.getId_event()),null);
                                Log.i("id_event_del",String.valueOf(del_id_event));
                                Log.i("Position", String.valueOf(position));
                                arrayListBasketChild.remove(basket_child);
                                viewGroupTicketContainer.removeView(viewGroupTicket);
                                basketList.remove(basket);
                                viewGroup.removeViewInLayout(finalV);
                                //deleteCell(finalV,viewGroup);

                            }else {
                                arrayListBasketChild.remove(basket_child);
                                deleteCell(viewGroupTicket, viewGroupTicketContainer);
                            }

                            FragmentBasket fragmentBasket = (FragmentBasket) activity.getFragmentManager().findFragmentById(R.id.frame_container);
                            fragmentBasket.Price(basket_child.getPriceBasketChild());
                            db_ticket.close();

                        }
                    });
                    builder.setNegativeButton("Ні", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            });

            viewGroupTicketContainer.addView(viewGroupTicket);
        }

        return view;
    }

    private void deleteCell(final View v, final ViewGroup viewGroup) {
        Animation.AnimationListener al = new Animation.AnimationListener() {
            @Override public void onAnimationEnd(Animation arg0) {
                viewGroup.removeViewInLayout(v);
            }
            @Override public void onAnimationRepeat(Animation animation) {}
            @Override public void onAnimationStart(Animation animation) {}
        };
        collapse(v, al);
    }

    private void collapse(final View v, Animation.AnimationListener al) {
        final int initialHeight = v.getMeasuredHeight();

        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if (interpolatedTime == 1) {
                    v.setVisibility(View.GONE);
                    Log.i("animation","1");
                }
                else {
                    Log.i("animation","2");
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };
        anim.setDuration(200);

        if (al != null) {
            anim.setAnimationListener(al);
        }
        v.startAnimation(anim);

    }



}
