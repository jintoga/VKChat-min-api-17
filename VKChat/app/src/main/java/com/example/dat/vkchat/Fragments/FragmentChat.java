package com.example.dat.vkchat.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dat.vkchat.Adapters.ViewPagerAdapter;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.R;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by DAT on 8/29/2015.
 */
public class FragmentChat extends Fragment implements ViewPagerAdapter.IRemoveContactFromChat {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;


    Contact receiver = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        getIDs(view);
        setEvents();

        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    private void getIDs(View view) {
        viewPager = (ViewPager) view.findViewById(R.id.my_viewpager);
        adapter = getAdapter();
        viewPager.setAdapter(adapter);


        tabLayout = (TabLayout) view.findViewById(R.id.my_tab_layout);


    }

    private void setEvents() {
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager) {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                super.onTabSelected(tab);
                viewPager.setCurrentItem(tab.getPosition());

                Log.d("Selected", "Selected " + tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                super.onTabUnselected(tab);
                Log.d("Unselected", "Unselected " + tab.getPosition());


            }
        });
    }

    public void setupTabLayout() {
        if (tabLayout.getTabCount() <= 0) {
            tabLayout.removeAllTabs();
        } else {
            for (int i = 0; i < tabLayout.getTabCount(); i++) {
                tabLayout.getTabAt(i).setCustomView(adapter.getTabView(i));
            }
        }
    }

    public void addFragmentToViewPager(Contact receiver) {

        Bundle bundle = new Bundle();
        bundle.putSerializable("data", receiver);
        FragmentChatItem fci = new FragmentChatItem();
        fci.setArguments(bundle);
        if (adapter == null) {
            adapter = getAdapter();
            viewPager.setAdapter(adapter);
        }
        adapter.addFrag(fci, receiver);
        //adapter.notifyDataSetChanged();
        if (adapter.getCount() > 0) {
            tabLayout.setupWithViewPager(viewPager);
        }

        viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();
    }

    /*public void removeFragmentFromViewPager(int position) {
        adapter.removeFrag(position);
        *//*viewPager.setCurrentItem(adapter.getCount() - 1);
        setupTabLayout();*//*
    }*/


    public ViewPagerAdapter getAdapter() {
        if (adapter == null)
            return new ViewPagerAdapter(getFragmentManager(), getActivity(), this);
        else
            return adapter;
    }


    public void stopRefreshInAllFrgaments() {
        if (adapter != null) {
            for (Fragment fragment : adapter.getmFragmentList()) {
                ((FragmentChatItem) fragment).setRefreshRunner(false);
            }
        }
    }

    public void startRefreshInAllFrgaments() {
        if (adapter != null) {
            for (Fragment fragment : adapter.getmFragmentList()) {
                ((FragmentChatItem) fragment).setRefreshRunner(true);
                ((FragmentChatItem) fragment).refreshToDetectIncomingMsg();
            }
        }
    }

    public void clearData() {
        if (adapter != null) {
            adapter.clearAll();
            adapter = null;
        }
        if (tabLayout != null) {
            tabLayout.removeAllTabs();
            //tabLayout = null;
        }
    }

    @Override
    public void removeFromChat(int position) {
        if (tabLayout.getTabCount() <= 1) {
            clearData();
            setupTabLayout();
        } else {
            tabLayout.removeTabAt(position);
            if (adapter.getCount() > 0)
                viewPager.setCurrentItem(adapter.getCount() - 1);
        }
    }
}
