package com.example.dat.vkchat.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dat.vkchat.LoginActivity;
import com.example.dat.vkchat.R;
import com.vk.sdk.VKSdk;

/**
 * Created by DAT on 10/14/2015.
 */
public class LogoutFragment extends Fragment {

    public LogoutFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_logout, container, false);


        v.findViewById(R.id.button_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Sign-out from Application?")
                        .setPositiveButton("Sign-out", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                VKSdk.logout();
                                if (!VKSdk.isLoggedIn()) {
                                    //((LoginActivity) getActivity()).showLogin();
                                    ((LoginActivity) getActivity()).gotoLoginActivity();
                                    ((LoginActivity) getActivity()).clearUserOldData();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                builder.create();
                builder.show();
            }
        });
        return v;
    }


}
