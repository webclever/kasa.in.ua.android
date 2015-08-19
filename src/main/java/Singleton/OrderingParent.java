package Singleton;

import java.util.ArrayList;

/**
 * Created by Admin on 08.05.2015.
 */
public class OrderingParent {
    private static OrderingParent ourInstance = new OrderingParent();
    private String numberOrdering;
    private String totalCountTicket;
    private String totalPriceTicket;
    private String deliveryMethod;
    private String paymentMethod;
    private String statusDelivery;
    private String statusPayment;
    private String createOrdering;

    private ArrayList<OrderingChild> orderingChildArrayList;

    public static OrderingParent getInstance() {
        if (ourInstance == null) {
            ourInstance = new OrderingParent();
        }
        return ourInstance;
    }

    public OrderingParent() {
    }

    public String getNumberOrdering() {
        return this.numberOrdering;
    }

    public void setNumberOrdering(String numberOrdering) {
        this.numberOrdering = numberOrdering;
    }

    public String getTotalCountTicket()
    {
        return this.totalCountTicket;
    }
    public void setTotalCountTicket(String totalCountTicket) {
        this.totalCountTicket = totalCountTicket;
    }

    public String getTotalPriceTicket()
    {
        return this.totalPriceTicket;
    }
    public void setTotalPriceTicket (String totalPriceTicket) {
        this.totalPriceTicket = totalPriceTicket;
    }

    public String getDeliveryMethod()
    {
        return this.deliveryMethod;
    }
    public void setDeliveryMethod(String deliveryMethod){
        this.deliveryMethod = deliveryMethod;
    }

    public String getPaymentMethod(){
        return this.paymentMethod;
    }
    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    public String getStatusDelivery() {
        return this.statusDelivery;
    }
    public void setStatusDelivery(String statusDelivery){
        this.statusDelivery = statusDelivery;
    }

    public String getStatusPayment() {
        return this.statusPayment;
    }
    public void setStatusPayment(String statusPayment){
        this.statusPayment = statusPayment;
    }

    public String getCreateOrdering (){
        return this.createOrdering;
    }
    public void setCreateOrdering(String createOrdering){
        this.createOrdering = createOrdering;
    }



    public ArrayList<OrderingChild> getOrderingChildArrayList() {
        return this.orderingChildArrayList;
    }
    public void setOrderingChildArrayList(ArrayList<OrderingChild> orderingChildArrayList) {
        this.orderingChildArrayList = orderingChildArrayList;
    }







}
