package com.example.dat.vkchat.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.dat.vkchat.Fragments.FragmentContacts;
import com.example.dat.vkchat.Fragments.FragmentConversations;
import com.example.dat.vkchat.LoginActivity;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DAT on 8/26/2015.
 */
public class CustomContactsAdapter extends RecyclerView.Adapter<CustomContactsAdapter.ViewHolder> implements Filterable {

    private ArrayList<Contact> contacts;
    private ArrayList<Contact> contactsTempForSearching;
    private LayoutInflater inflater;
    private Context context;
    FragmentContacts fragmentContacts;

    public CustomContactsAdapter(Context context, ArrayList<Contact> listModels, FragmentContacts fragmentContacts) {
        inflater = LayoutInflater.from(context);
        this.contacts = listModels;
        this.context = context;
        this.fragmentContacts = fragmentContacts;
        this.contactsTempForSearching = listModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_item_contact, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Contact contact = contacts.get(position);
        if (contact.getAvatar_url() != "") {
            Picasso.with(context).load(contact.getAvatar_url())./*centerInside().*/resize(120, 120)/*.transform(new RoundedTransformation(10, 10))*/.into(holder.imageViewContactAvatar);
        } else {
            Picasso.with(context).load(R.drawable.vk_avatar)./*centerInside().*/resize(120, 120)/*.transform(new RoundedTransformation(10, 10))*/.into(holder.imageViewContactAvatar);
        }
        holder.textViewContactName.setText(contact.getName());
        if (contact.getIsOnline() == 1) {
            holder.textViewStatus.setText("online");
            holder.textViewStatus.setTextColor(context.getResources().getColor(R.color.accent));
            holder.floatingActionButtonChat.setImageResource(R.drawable.ic_chat_black_18dp);
        } else {
            holder.textViewStatus.setText("offline");
            holder.textViewStatus.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
            holder.floatingActionButtonChat.setImageResource(R.drawable.ic_email_black_18dp);
        }
        holder.floatingActionButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("CLICK", contacts.get(position).getName());
                ((LoginActivity) context).addFriendToConversations(contacts.get(position));
                //goToChat(contacts.get(position));
            }
        });

    }

    static FragmentConversations fragmentTest;

    private void goToChat(Contact receiver) {
        //Put receiver for chat item
        Bundle bundle = new Bundle();
        bundle.putSerializable("data", receiver);
        fragmentTest = getFragmentTest();
        fragmentTest.setArguments(bundle);
       /* android.support.v4.app.FragmentTransaction fragmentTransaction = ((LoginActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameContainer, fragmentTest);

        fragmentTransaction.commit();*/

    }

    @Override
    public int getItemCount() {
        if (contacts != null) {
            //Log.d("contacts size", contacts.size() + "");
            return contacts.size();
        } else {
            return 0;
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        TextView textViewContactName, textViewStatus;
        CircleImageView imageViewContactAvatar;
        FloatingActionButton floatingActionButtonChat;
        /*public MyViewHolderClicks mListener;*/

        public ViewHolder(View itemView) {
            super(itemView);
            textViewContactName = (TextView) itemView.findViewById(R.id.textViewContactName);
            textViewStatus = (TextView) itemView.findViewById(R.id.textViewStatus);
            imageViewContactAvatar = (CircleImageView) itemView.findViewById(R.id.imageViewContactAvatar);
            floatingActionButtonChat = (FloatingActionButton) itemView.findViewById(R.id.floatingActionButtonChat);
        }

    }

    public FragmentConversations getFragmentTest() {
        if (fragmentTest == null)
            return new FragmentConversations();
        else
            return fragmentTest;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (fragmentContacts.isSearchOn()) {
                    constraint = constraint.toString().toLowerCase();
                    if (constraint == null || constraint.length() == 0) {
                        filterResults.values = contactsTempForSearching;
                        filterResults.count = contactsTempForSearching.size();
                        fragmentContacts.setSearchOn(false);

                    } else {
                        ArrayList<Contact> filteredContacts = new ArrayList<Contact>();
                        for (int i = 0; i < contactsTempForSearching.size(); i++) {
                            String name = contactsTempForSearching.get(i).getName();
                            if (name.toLowerCase().contains(constraint.toString())) {
                                filteredContacts.add(contactsTempForSearching.get(i));
                            }
                        }
                        filterResults.count = filteredContacts.size();
                        filterResults.values = filteredContacts;
                    }
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contacts = (ArrayList<Contact>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }
}
