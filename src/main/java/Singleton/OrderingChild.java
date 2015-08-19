package Singleton;

import java.util.ArrayList;

/**
 * Created by Admin on 08.05.2015.
 */
public class OrderingChild {
    private static OrderingChild ourInstance = new OrderingChild();
    private String nameEventOrdering;
    private String dataEventOrdering;
    private String timeEventOrdering;
    private String cityEventOrdering;
    private ArrayList<TicketChildOrdering> ticketChildOrderingArrayList;
    public OrderingChild() {
    }
    public static OrderingChild getInstance() {
        if (ourInstance == null){
            ourInstance = new OrderingChild();
        }
        return ourInstance;
    }

    public String getNameEventOrdering()
    {
        return this.nameEventOrdering;
    }
    public void setNameEventOrdering(String nameEventOrdering)
    {
        this.nameEventOrdering = nameEventOrdering;
    }

    public String getDataEventOrdering()
    {
        return this.dataEventOrdering;
    }
    public void setDataEventOrdering(String dataEventOrdering)
    {
        this.dataEventOrdering = dataEventOrdering;
    }

    public String getTimeEventOrdering()
    {
        return this.timeEventOrdering;
    }
    public void setTimeEventOrdering(String timeEventOrdering)
    {
        this.timeEventOrdering = timeEventOrdering;
    }

    public String getCityEventOrdering()
    {
        return this.cityEventOrdering;
    }
    public void setCityEventOrdering(String cityEventOrdering)
    {
        this.cityEventOrdering = cityEventOrdering;
    }

    public ArrayList<TicketChildOrdering> getTicketChildOrderingArrayList()
    {
        return this.ticketChildOrderingArrayList;
    }
    public void setTicketChildOrderingArrayList(ArrayList<TicketChildOrdering> ticketChildOrderingArrayList)
    {
        this.ticketChildOrderingArrayList = ticketChildOrderingArrayList;
    }







}
