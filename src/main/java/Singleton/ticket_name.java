package Singleton;

/**
 * Created by Женя on 06.07.2015.
 */
public class ticket_name  {

    private String name_event;
    private String sector;
    private String name_row;
    private String row;
    private String place;
    private String price;
    private int id_event;
    private int id_ticket;

    public ticket_name()
    {

    }

    public int getId_event()
    {
        return this.id_event;
    }
    public void setId_event(int id_event)
    {
        this.id_event = id_event;
    }

    public int getId_ticket()
    {
        return this.id_ticket;
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

    public String getName_event()
    {
        return this.name_event;
    }
    public void setName_event(String name_event)
    {
        this.name_event = name_event;
    }

    public String getSector()
    {
        return this.sector;
    }
    public void setSector(String sector)
    {
        this.sector = sector;
    }

    public String getRow()
    {
        return this.row;
    }
    public void setRow(String row)
    {
        this.row = row;
    }

    public String getPlace()
    {
        return this.place;
    }
    public void setPlace(String place)
    {
        this.place = place;
    }

    public String getPrice()
    {
        return this.price;
    }
    public void setPrice(String price)
    {
        this.price = price;
    }

}
