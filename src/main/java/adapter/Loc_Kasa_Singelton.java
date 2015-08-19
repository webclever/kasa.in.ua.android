package adapter;

/**
 * Created by Admin on 18.02.2015.
 */
public class Loc_Kasa_Singelton {

    private String address,description,time_work, advertisement, notification , name;
    private Double latitude,longitude;
    public Loc_Kasa_Singelton() {
    }

    public String getAddress()
    {
        return address;
    }
    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getTime_work()
    {
        return time_work;
    }
    public void setTime_work(String time_work)
    {
        this.time_work = time_work;
    }

    public Double getLatitude()
    {
        return this.latitude;
    }
    public void setLatitude(Double latitude)
    {
        this.latitude = latitude;
    }

    public Double getLongitude()
    {
        return this.longitude;
    }
    public void setLongitude(Double longitude)
    {
        this.longitude = longitude;
    }

    public String getAdvertisement()
    {
        return  this.advertisement;
    }
    public void setAdvertisement(String advertisement)
    {
        this.advertisement = advertisement;
    }

    public String getNotification()
    {
        return this.notification;
    }
    public void setNotification(String notification)
    {
        this.notification = notification;
    }
    public String getName()
    {
        return this.name;
    }
    public void setName(String name)
    {
        this.name = name;
    }

}
