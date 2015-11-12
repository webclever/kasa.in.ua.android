package Singleton;

/**
 * Created by Admin on 07.05.2015.
 */
public class TicketChildOrdering {
    private static TicketChildOrdering ourInstance = new TicketChildOrdering();
    private String sectorOrdering;
    private String rowOrdering;
    private String placeOrdering;
    private String priceOrdering;
    private String statusTicket;

    public static TicketChildOrdering getInstance() {
        if (ourInstance ==null)
        {
            ourInstance = new TicketChildOrdering();
        }
        return ourInstance;
    }

    public TicketChildOrdering() {
    }

    public String getSectorOrdering()
    {
        return this.sectorOrdering;
    }
    public void setSectorOrdering(String sectorOrdering)
    {
        this.sectorOrdering = sectorOrdering;
    }

    public String getRowOrdering()
    {
        return this.rowOrdering;
    }
    public void setRowOrdering(String rowOrdering)
    {
        this.rowOrdering = rowOrdering;
    }

    public String getPlaceOrdering()
    {
        return this.placeOrdering;
    }
    public void setPlaceOrdering(String placeOrdering)
    {
        this.placeOrdering = placeOrdering;
    }

    public String getPriceOrdering()
    {
        return this.priceOrdering;
    }
    public void setPriceOrdering(String priceOrdering)
    {
        this.priceOrdering = priceOrdering;
    }

    public String getStatusTicket()
    {
        return this.statusTicket;
    }
    public void setStatusTicket(String statusTicket)
    {
        this.statusTicket = statusTicket;
    }
}
