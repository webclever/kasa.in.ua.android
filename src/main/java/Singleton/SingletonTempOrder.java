package Singleton;

/**
 * Created by Женя on 12.11.2015.
 */
public class SingletonTempOrder {
    private static SingletonTempOrder ourInstance = new SingletonTempOrder();

    private Boolean existenceOrder;
    private String order_id;
    private String token;


    public static SingletonTempOrder getInstance() {

        if (ourInstance == null)
        {
            ourInstance = new SingletonTempOrder();
        }

        return ourInstance;
    }

    public SingletonTempOrder() {

    }

    public Boolean getExistenceOrder(){
        return this.existenceOrder;
    }
    public void setExistenceOrder(Boolean existenceOrder){
        this.existenceOrder = existenceOrder;
    }

    public String getOrder_id(){
        return this.order_id;
    }
    public void setOrder_id(String order_id){
        this.order_id = order_id;
    }

    public String getToken(){
        return this.token;
    }
    public void setToken(String token){
        this.token = token;
    }



}
