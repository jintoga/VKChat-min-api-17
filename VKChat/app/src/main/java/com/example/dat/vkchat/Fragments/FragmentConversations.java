package com.example.dat.vkchat.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.R;

import java.util.List;

/**
 * Created by DAT on 9/4/2015.
 */
public class FragmentConversations extends Fragment {
    FragmentChat fragmentChat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversations, container, false);
        getIDs(view);
        return view;
    }

    public void addFriendToChat(Contact receiver) {
        fragmentChat.addFragmentToViewPager(receiver);
    }

    private void getIDs(View view) {
        fragmentChat = (FragmentChat) getChildFragmentManager().findFragmentById(R.id.fragmentChatInsideConversations);
    }

    public FragmentChat getFragmentChat() {
        return fragmentChat;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        Log.d("NESTED", "NESTED");
        List<Fragment> fragments = getChildFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
