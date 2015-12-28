package adapter;

/**
 * Created by Admin on 24.03.2015.
 */
public class Basket_Child {
    private String name_row;
    private String name;
    private String row;
    private String place;
    private String price;
    private String name_place;
    private int id_ticket;
    private int id_event;
    private Integer typePlace;
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

    public String getName_row(){
        return name_row;
    }
    public void setName_row(String name_row){
        this.name_row = name_row;
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

    public Integer getTypePlace(){
        return this.typePlace;
    }
    public void setTypePlace(Integer typePlace){
        this.typePlace = typePlace;
    }

    public String getName_place() {
        return name_place;
    }

    public void setName_place(String name_place) {
        this.name_place = name_place;
    }
}
