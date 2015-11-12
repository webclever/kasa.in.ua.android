package Singleton;

/**
 * Created by Женя on 12.11.2015.
 */
public class SingletonTempOrder {
    private static SingletonTempOrder ourInstance = new SingletonTempOrder();

    private Integer order_id;
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

    public Integer getOrder_id(){
        return order_id;
    }
    public void setOrder_id(Integer order_id){
        this.order_id = order_id;
    }

    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }



}
