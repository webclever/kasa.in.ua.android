package adapter;

import java.util.ArrayList;

/**
 * Created by Admin on 18.03.2015.
 */
public class Basket {

    private String name;
    private String date;
    private String time;
    private String city;
    private int id_event;
    private ArrayList<Basket_Child> basket_childArrayList;

    public Basket() {
    }

    public int getId_event()
    {
        return id_event;
    }
    public void setId_event(int id_event)
    {
        this.id_event = id_event;
    }

    public String getNameBasket()
    {
        return name;
    }
    public void setNameBasket (String name)
    {
        this.name = name;
    }

    public String getDateBasket()
    {
        return  date;
    }
    public void setDate(String date)
    {
        this.date = date;
    }

    public String getTimeBasket()
    {
        return time;
    }
    public void setTimeBasket(String time)
    {
        this.time = time;
    }

    public String getCityBasket()
    {
        return city;
    }
    public void setCityBasket(String city)
    {
        this.city = city;
    }

    public ArrayList<Basket_Child> getBasket_childArrayList()
    {
        return basket_childArrayList;
    }
    public void setBasket_childArrayList(ArrayList<Basket_Child> basket_childArrayList){
        this.basket_childArrayList = basket_childArrayList;
    }
}
