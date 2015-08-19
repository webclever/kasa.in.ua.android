package Singleton;

/**
 * Created by Zhenya on 15.06.2015.
 */
public class SingletonPriceColor {

    public SingletonPriceColor()
    {

    }

    private String strPrice;
    private String strPriceColor;

    public void setStrPrice(String strPrice)
    {
        this.strPrice = strPrice;
    }
    public String getStrPrice()
    {
        return this.strPrice;
    }

    public void setStrPriceColor(String strPriceColor)
    {
        this.strPriceColor = strPriceColor;
    }
    public String getStrPriceColor(String strPriceColor)
    {
        return this.strPriceColor;
    }

}
