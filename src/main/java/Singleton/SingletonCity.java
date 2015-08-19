package Singleton;

/**
 * Created by Zhenya on 12.06.2015.
 */
public class SingletonCity {
    private String nameCity;
    private Integer idCity;

    public SingletonCity() {
    }

    public void setNameCity(String nameCity)
    {
        this.nameCity = nameCity;
    }
    public String getNameCity()
    {
        return this.nameCity;
    }

    public void setIdCity(Integer idCity)
    {
        this.idCity = idCity;
    }
    public Integer getIdCity()
    {
        return this.idCity;
    }
}
