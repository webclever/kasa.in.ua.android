package customlistviewmodel;

/**
 * Created by Web on 05.09.2014.
 */
public class Movie {
    private String name;
    private String thumbnailUrl;
    private String data,time;
    private String place, city;
    private String price;
    private int id_ivent;

    public Movie()
    {

    }

    public String getName()
    {
        return name;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public String getThumbnailUrl()
    {
        return thumbnailUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl)
    {
        this.thumbnailUrl=thumbnailUrl;
    }

    public int getId_ivent()
    {
        return id_ivent;
    }
    public void setId_ivent(int id_ivent)
    {
        this.id_ivent = id_ivent;
    }

    public String getData()
    {
        return data;
    }
    public void setData(String data)
    {
        this.data=data;
    }


    public String getTime()
    {
        return time;
    }
    public void setTime(String time)
    {
        this.time=time;
    }


    public String getCity(){ return city;}
    public void setCity(String city)
    {
        this.city = city;
    }
}
