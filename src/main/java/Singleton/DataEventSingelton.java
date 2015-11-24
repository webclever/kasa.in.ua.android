package Singleton;

import android.util.Log;

/**
 * Created by Admin on 15.04.2015.
 */
public class DataEventSingelton {
    private static DataEventSingelton ourInstance = new DataEventSingelton();
    private int id_event;
    private String name_event;
    private String date_event;
    private String time_event;
    private String place_event;
    private String img_url;
    public static DataEventSingelton getInstance() {
        if (ourInstance == null)
        {
            ourInstance = new DataEventSingelton();
        }

        return ourInstance;
    }

    public DataEventSingelton() {
    }
    public int  getId_event()
    {

        return id_event;
    }
    public void setId_event(int id_event)
    {
        this.id_event = id_event;

    }
    public void setName_event(String name_event)
    {
        this.name_event = name_event;
    }
    public String getName_event()
    {
        return name_event;
    }

    public void setDate_event(String date_event)
    {
        this.date_event = date_event;
    }
    public String getDate_event()
    {
        return date_event;
    }

    public void setTime_event(String time_event)
    {
        this.time_event = time_event;
    }
    public String getTime_event()
    {
        return time_event;
    }

    public void setPlace_event(String place_event)
    {
        this.place_event = place_event;
    }
    public String getPlace_event()
    {
        return place_event;
    }

}
