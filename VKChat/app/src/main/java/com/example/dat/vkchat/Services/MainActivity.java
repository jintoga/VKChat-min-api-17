package com.example.dat.vkchat.Services;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.dat.vkchat.Fragments.LoginFragment;
import com.example.dat.vkchat.LoginActivity;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.R;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
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

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private ArrayList<Contact> contacts = null;

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
                    Bundle bundle = new Bundle();
                    bundle.putParcelableArrayList("data", contacts);
                    Intent intent = new Intent();
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    finish();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                if (res != null) {
                    requestContacts();

                } else {

                }
            }

            @Override
            public void onError(VKError error) {
                // User didn't pass Authorization
                Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }
}
