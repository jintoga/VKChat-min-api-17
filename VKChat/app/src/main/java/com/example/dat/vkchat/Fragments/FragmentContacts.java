package com.example.dat.vkchat.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.dat.vkchat.Adapters.CustomContactsAdapter;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.R;

import java.util.ArrayList;

public class FragmentContacts extends Fragment {

    private static ArrayList<Contact> contacts;
    private RecyclerView recyclerViewContacts;
    private CustomContactsAdapter customContactsAdapter;

    private EditText editTextSearchContact;
    private boolean searchOn = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        getIDs(view);
        setEvents();
        return view;
    }


    private void getIDs(View view) {
        recyclerViewContacts = (RecyclerView) view.findViewById(R.id.recyclerView_contacts);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(layoutManager);

        editTextSearchContact = (EditText) view.findViewById(R.id.editTextSearchContact);

    }

    private void setEvents() {
        editTextSearchContact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence constraint, int start, int before, int count) {
                searchOn = true;
                FragmentContacts.this.customContactsAdapter.getFilter().filter(constraint);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void clearContactsList() {
        if (contacts != null)
            contacts.clear();
        if (customContactsAdapter != null)
            customContactsAdapter.notifyDataSetChanged();
        if (editTextSearchContact != null)
            editTextSearchContact.setText("");
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
        customContactsAdapter = new CustomContactsAdapter(getActivity(), contacts, this);
        recyclerViewContacts.setAdapter(customContactsAdapter);
        customContactsAdapter.notifyDataSetChanged();
    }

    public boolean isSearchOn() {
        return searchOn;
    }

    public void setSearchOn(boolean searchOn) {
        this.searchOn = searchOn;
    }
}
