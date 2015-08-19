package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

import webclever.sliding_menu.CreateAccount;
import webclever.sliding_menu.Login;
import webclever.sliding_menu.LoginActivity;


/**
 * Created by Женя on 03.08.2015.
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {

    public TabsPagerAdapter(FragmentManager fm){
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new Login();
            case 1:
                return new CreateAccount();
        }

        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
