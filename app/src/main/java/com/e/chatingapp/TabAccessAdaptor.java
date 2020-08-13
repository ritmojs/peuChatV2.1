package com.e.chatingapp;

import android.app.DownloadManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAccessAdaptor  extends FragmentPagerAdapter
{


    public TabAccessAdaptor(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position)
        {
            case 0:
                ChatsFagment chatsFagment=new ChatsFagment();
                return chatsFagment;


            case 1:
                ContactsFragment contactsFragment =new ContactsFragment();
                return contactsFragment;
            case 2:
                RequestFragment requestFragment =new RequestFragment();
                return requestFragment;
            default:
                return null;

        }

    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position)
        {
            case 0:
                return "Chats";
            case 1:
                return "Contacts";
            case 2:
                return "Requests";
            default:
                return null;
        }
    }
}
