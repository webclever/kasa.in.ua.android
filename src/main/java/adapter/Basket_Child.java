package adapter;

/**
 * Created by Admin on 24.03.2015.
 */
public class Basket_Child {
    private String name;
    private String row;
    private String place;
    private String price;
    private int id_ticket;
    private int id_event;
    private static Basket_Child ourInstance = new Basket_Child();

    public static Basket_Child getInstance() {
        return ourInstance;
    }

    public Basket_Child() {
    }
    public int getId_event()
    {
        return id_event;
    }
    public void setId_event(int id_event)
    {
        this.id_event = id_event;
    }
    public int getId_ticket()
    {
        return id_ticket;
    }
    public void setId_ticket(int id_ticket)
    {
        this.id_ticket = id_ticket;
    }
    public String getNameBasketChild()
    {
        return name;
    }
    public void setNameBasketChild(String name)
    {
        this.name = name;
    }

    public String getRowBasketChild()
    {
        return row;
    }
    public void setRowBasketChild(String row)
    {
        this.row = row;
    }

    public String getPlaceBasketChild()
    {
        return place;
    }
    public void setPlaceBasketChild(String place)
    {
        this.place = place;
    }

    public String getPriceBasketChild()
    {
        return price;
    }
    public void setPriceBasketChild(String price)
    {
        this.price = price;
    }

}
