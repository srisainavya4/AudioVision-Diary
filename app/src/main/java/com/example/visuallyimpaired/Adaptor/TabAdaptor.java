package com.example.visuallyimpaired.Adaptor;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.visuallyimpaired.Activities.CallActivity;
import com.example.visuallyimpaired.Activities.CallLogFragment;
import com.example.visuallyimpaired.Activities.ContactFragment;

public class TabAdaptor extends FragmentPagerAdapter {

    int totalTabs;
    private Context myContext;

    public TabAdaptor(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ContactFragment contactFragment = new ContactFragment();
                return contactFragment;
            case 1:
                CallLogFragment callLogFragment = new CallLogFragment();
                return callLogFragment;
            case 2:
                CallActivity callActivity = new CallActivity();
                return callActivity;
            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }

}
