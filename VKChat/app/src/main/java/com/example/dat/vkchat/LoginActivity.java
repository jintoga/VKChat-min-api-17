package com.example.dat.vkchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.dat.vkchat.Fragments.FragmentContacts;
import com.example.dat.vkchat.Fragments.FragmentConversations;
import com.example.dat.vkchat.Fragments.LogoutFragment;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.Services.MainActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements OnClickListener {


    private Contact currentUser;
    public static final String[] sMyScope = new String[]{
            VKScope.FRIENDS,
            VKScope.WALL,
            VKScope.PHOTOS,
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.DOCS
    };

    private ImageView imageViewAvatar;
    private ProgressBar progressBarAvatarLoading;
    private TextView textViewName;
    private TextView textViewEmail;
    private Toolbar toolbar;
    // private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private LinearLayout menuItemHome, menuItemContacts, menuItemChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getIDs();
        setEvents();
        /*showLogin();*/
    }

    private void getIDs() {
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        //navigationView = (NavigationView) findViewById(R.id.navigation_view);
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        imageViewAvatar = (ImageView) findViewById(R.id.imageViewProfile);
        progressBarAvatarLoading = (ProgressBar) findViewById(R.id.progressBarAvatarLoading);
        progressBarAvatarLoading.setVisibility(View.VISIBLE);
        textViewName = (TextView) findViewById(R.id.textView_username);
        menuItemHome = (LinearLayout) findViewById(R.id.menuItemHome);
        menuItemHome.setBackgroundColor(getResources().getColor(R.color.ripple));
        menuItemChat = (LinearLayout) findViewById(R.id.menuItemChat);
        menuItemContacts = (LinearLayout) findViewById(R.id.menuItemContacts);
        fragmentContacts = (FragmentContacts) this.getSupportFragmentManager().findFragmentById(R.id.fragmentContactsInMain);
        fragmentChat = (FragmentConversations) this.getSupportFragmentManager().findFragmentById(R.id.fragmentTestInMain);

        fragmentManager.beginTransaction().hide(fragmentContacts).commit();
        fragmentManager.beginTransaction().hide(fragmentChat).commit();
    }


    private static FragmentContacts fragmentContacts = null;
    private static FragmentConversations fragmentChat = null;
    android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();


    private void setEvents() {
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                //toolbar.setTitle("Closed");
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                //toolbar.setTitle("Open");
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();

        menuItemHome.setOnClickListener(this);
        menuItemContacts.setOnClickListener(this);

        menuItemChat.setOnClickListener(this);


    }

    public void addFriendToConversations(Contact receiver) {

        fragmentChat.addFriendToChat(receiver);
        toolbar.setTitle("Conversations");

        menuItemHome.setBackgroundColor(getResources().getColor(android.R.color.white));
        menuItemContacts.setBackgroundColor(getResources().getColor(android.R.color.white));
        menuItemChat.setBackgroundColor(getResources().getColor(R.color.ripple));

        fragmentManager.beginTransaction().hide(fragmentContacts).commit();
        fragmentManager.beginTransaction().show(fragmentChat).commit();
    }


    private void showLogout() {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, new LogoutFragment())
                .commit();
    }

    @Override
    public void onClick(View v) {
        menuItemHome.setBackgroundColor(getResources().getColor(android.R.color.white));
        menuItemContacts.setBackgroundColor(getResources().getColor(android.R.color.white));
        menuItemChat.setBackgroundColor(getResources().getColor(android.R.color.white));
        switch (v.getId()) {
            case R.id.menuItemHome:
                fragmentManager.beginTransaction().hide(fragmentContacts).commit();
                fragmentManager.beginTransaction().hide(fragmentChat).commit();
                toolbar.setTitle("Home");
                fragmentChat.getFragmentChat().stopRefreshInAllFrgaments();
                menuItemHome.setBackgroundColor(getResources().getColor(R.color.ripple));
                break;
            case R.id.menuItemContacts:
                if (fragmentContacts.getContacts() == null) {
                    fragmentContacts.setContacts(getContacts());
                }
                fragmentManager.beginTransaction().show(fragmentContacts).commit();
                fragmentManager.beginTransaction().hide(fragmentChat).commit();
                toolbar.setTitle("Contacts");
                fragmentChat.getFragmentChat().stopRefreshInAllFrgaments();
                menuItemContacts.setBackgroundColor(getResources().getColor(R.color.ripple));
                break;
            case R.id.menuItemChat:
                fragmentManager.beginTransaction().hide(fragmentContacts).commit();
                fragmentManager.beginTransaction().show(fragmentChat).commit();
                toolbar.setTitle("Conversations");
                fragmentChat.getFragmentChat().startRefreshInAllFrgaments();
                menuItemChat.setBackgroundColor(getResources().getColor(R.color.ripple));
                break;
        }
        drawerLayout.closeDrawers();

    }


    public static int request_code = 10;

    public void gotoLoginActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivityForResult(intent, request_code);
    }


    public void clearUserOldData() {
        imageViewAvatar.setImageResource(R.drawable.boy);
        textViewName.setText("User");
        if (contacts != null) {
            contacts.clear();
            fragmentContacts.clearContactsList();
            fragmentChat.clearData();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == this.request_code && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            contacts = bundle.getParcelableArrayList("data");
            fragmentContacts.setContacts(contacts);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (VKSdk.isLoggedIn()) {
            setUserData();
            requestContacts();
            requestMessagesDialogs();
            showLogout();
            if (fragmentChat.isVisible()) {
                fragmentChat.getFragmentChat().startRefreshInAllFrgaments();
            }
        } else {
            clearUserOldData();
            gotoLoginActivity();
            //showLogin();

        }

    }


    private void setUserData() {
// User passed Authorization
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "first_name,photo_200"));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("Response", response.toString());

                try {
                    JSONArray jsonArray = response.json.getJSONArray("response");
                    //String name = jsonArray.get("first_name");
                    Log.d("jsonArray", jsonArray.toString());
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    Log.d("jsonObject", jsonObject.toString());
                    int cur_user_id = jsonObject.getInt("id");
                    String first_name = jsonObject.getString("first_name");
                    String last_name = jsonObject.getString("last_name");
                    String full_name = first_name + " " + last_name;
                    Log.d("Name", first_name);
                    String photo_url = "";
                    if (jsonObject.getString("photo_200") != null) {
                        photo_url = jsonObject.getString("photo_200");
                        Log.d("photo_url", photo_url);
                        Picasso.with(getApplicationContext()).load(photo_url).into(imageViewAvatar, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBarAvatarLoading.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBarAvatarLoading.setVisibility(View.GONE);
                                imageViewAvatar.setImageResource(R.drawable.vk_avatar);
                            }
                        });
                    } else {
                        imageViewAvatar.setImageResource(R.drawable.vk_avatar);
                        progressBarAvatarLoading.setVisibility(View.GONE);
                    }
                    textViewName.setText(first_name + " " + last_name);
                    Contact curUser = new Contact();
                    curUser.setName(full_name);
                    curUser.setAvatar_url(photo_url);
                    curUser.setIsOnline(1);
                    curUser.setUser_id(cur_user_id);
                    setCurrentUser(curUser);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);

            }
        });
    }

    private ArrayList<Contact> contacts = null;

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    private void requestContacts() {
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name,photo_200"));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("Response", response.toString());
                contacts = new ArrayList<Contact>();
                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    Log.d("jsonObject", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    Log.d("jsonArray", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject joContact = jsonArray.getJSONObject(i);
                        Log.d("joContact", joContact.toString());
                        int user_id = joContact.getInt("id");
                        String first_name = joContact.getString("first_name");
                        String last_name = joContact.getString("last_name");
                        String full_name = first_name + " " + last_name;
                        int status = joContact.getInt("online");
                        String avatar_url = "";
                        if (joContact.isNull("photo_200") == false) {
                            avatar_url = joContact.getString("photo_200");
                        }
                        Contact contact = new Contact(user_id, full_name, avatar_url, status);
                        contacts.add(contact);
                    }
                    Log.d("contacts size", contacts.size() + "");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
    }

    private void requestMessagesDialogs() {
        VKRequest request = VKApi.messages().getDialogs(VKParameters.from(VKApiConst.OUT, "1", VKApiConst.COUNT, "200"));

        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("Response", response.toString());
                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    Log.d("jsonObject", jsonObject.toString());


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Contact getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(Contact currentUser) {
        this.currentUser = currentUser;
    }

    /*@Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            //this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }*/

    @Override
    public void onBackPressed() {

        menuItemHome.setBackgroundColor(getResources().getColor(android.R.color.white));
        menuItemContacts.setBackgroundColor(getResources().getColor(android.R.color.white));
        menuItemChat.setBackgroundColor(getResources().getColor(android.R.color.white));
        if (!fragmentContacts.isHidden()) {
            fragmentManager.beginTransaction().hide(fragmentContacts).commit();
            fragmentManager.beginTransaction().hide(fragmentChat).commit();
            menuItemHome.setBackgroundColor(getResources().getColor(R.color.ripple));
            toolbar.setTitle("Home");
        } else {
            fragmentChat.getFragmentChat().stopRefreshInAllFrgaments();
            fragmentManager.beginTransaction().show(fragmentContacts).commit();
            fragmentManager.beginTransaction().hide(fragmentChat).commit();
            menuItemContacts.setBackgroundColor(getResources().getColor(R.color.ripple));
            toolbar.setTitle("Contacts");
        }
        drawerLayout.closeDrawers();
    }

}
