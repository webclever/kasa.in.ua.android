package webclever.sliding_menu;

public class NavDrawerItem {
    private String title;
    private String count = "0";
    private int icon;
    private boolean isCounterVisible = false;


    public NavDrawerItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return this.title;
    }

    public int getIcon() {
        return this.icon;
    }

    public String getCount() {
        return this.count;
    }

    public boolean getCounterVisibility() {
        return this.isCounterVisible;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setCount(String title)
    {
        this.count=count;
    }
    public void setgetCounterVisibility (boolean isCounterVisible)
    {
        this.isCounterVisible=isCounterVisible;
    }


}
