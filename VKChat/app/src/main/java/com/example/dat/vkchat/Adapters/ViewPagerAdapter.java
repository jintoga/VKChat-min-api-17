package com.example.dat.vkchat.Adapters;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DAT on 8/31/2015.
 */
public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    private final ArrayList<Fragment> mFragmentList = new ArrayList<>();
    private final ArrayList<Contact> contacts = new ArrayList<>();
    IRemoveContactFromChat iRemoveContactFromChat;
    private Activity activity;
    private Fragment mPrimaryItem;
    List<View> mlist;

    public ViewPagerAdapter(FragmentManager fm, Activity activity, IRemoveContactFromChat listener) {

        super(fm);
        this.activity = activity;
        Log.d("SAA", "A");
        this.iRemoveContactFromChat = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, Contact contact) {
        mFragmentList.add(fragment);
        contacts.add(contact);
        notifyDataSetChanged();
    }

    public void removeFrag(int position) {
        Fragment fragment = mFragmentList.get(position);
        Contact contact = contacts.get(position);
        mFragmentList.remove(fragment);
        contacts.remove(contact);
        notifyDataSetChanged();
    }

    @Override
    public int getItemPosition(Object object) {

        return POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        super.destroyItem(container, position, object);
        if (position <= getCount()) {
            FragmentManager manager = ((Fragment) object).getFragmentManager();
            FragmentTransaction trans = manager.beginTransaction();
            trans.remove((Fragment) object);
            trans.commit();
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return contacts.get(position).getName();
    }


    public View getTabView(final int position) {
        final View view = LayoutInflater.from(activity).inflate(R.layout.custom_item_tab, null);
        TextView tabItemName = (TextView) view.findViewById(R.id.textViewTabItemName);
        CircleImageView tabItemAvatar = (CircleImageView) view.findViewById(R.id.imageViewTabItemAvatar);


        tabItemName.setText(contacts.get(position).getName());
        tabItemName.setTextColor(activity.getResources().getColor(android.R.color.background_light));
        if (!contacts.get(position).getAvatar_url().equals("")) {
            Picasso.with(activity).load(contacts.get(position).getAvatar_url()).resize(50, 50).into(tabItemAvatar);
        } else {
            Picasso.with(activity).load(R.drawable.vk_avatar).resize(50, 50).into(tabItemAvatar);
        }
        ImageButton imageButtonRemove = (ImageButton) view.findViewById(R.id.imageButtonRemoveFromChat);
        imageButtonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFragmentList.size() == 1) {
                    Toast.makeText(activity, contacts.get(0).getName(), Toast.LENGTH_SHORT).show();
                    removeFrag(0);
                    iRemoveContactFromChat.removeFromChat(0);
                } else {
                    Toast.makeText(activity, contacts.get(position).getName(), Toast.LENGTH_SHORT).show();
                    removeFrag(position);
                    iRemoveContactFromChat.removeFromChat(position);
                }

            }
        });
        return view;
    }

    public ArrayList<Fragment> getmFragmentList() {
        return mFragmentList;
    }

    public void clearAll() {
        mFragmentList.clear();
        contacts.clear();
        notifyDataSetChanged();
    }

    public interface IRemoveContactFromChat {
        void removeFromChat(int position);
    }

}
