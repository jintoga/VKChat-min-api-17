package com.example.dat.vkchat.Services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.Model.Message;
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
import java.util.Collections;

/**
 * Created by DAT on 9/6/2015.
 */
public class MyService extends Service {
    private ArrayList<Message> listMsg = new ArrayList<>();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    boolean isRunning = true;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        while (isRunning == true) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Toast.makeText(MyService.this, "Service is running", Toast.LENGTH_SHORT).show();
                    Bundle bundle = intent.getExtras();
                    Contact receiver = (Contact) bundle.getSerializable("data");
                    responseUpdateChat(receiver);
                }
            }, 200);
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isRunning = false;
    }

    private void responseUpdateChat(Contact receiver) {
        if (receiver != null) {
            VKRequest request = VKApi.messages().myGetMsgHistoryMethod(VKParameters.from(VKApiConst.COUNT, "10", VKApiConst.USER_ID, receiver.getUser_id()));
            request.secure = false;
            request.useSystemLanguage = false;
            request.executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    Log.d("Response", response.toString());
                    try {
                        JSONObject jsonObject = response.json.getJSONObject("response");
                        //Log.d("jsonObject", jsonObject.toString());
                        JSONArray jsonArray = jsonObject.getJSONArray("items");
                        int msg_count = jsonObject.getInt("count");
                        Log.d("msg_count", msg_count + "");
                        listMsg.clear();
                        //Log.d("jsonArray", jsonArray.toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject joMsg = jsonArray.getJSONObject(i);
                            //Log.d("joContact", joMsg.toString());
                            String body = joMsg.getString("body");
                            int user_id = joMsg.getInt("user_id");
                            int from_id = joMsg.getInt("from_id");
                            Message message = new Message();
                            message.setUser_id(user_id);
                            message.setFrom_id(from_id);
                            message.setBody(body);
                            listMsg.add(message);
                        }
                    /*Collections.reverse(listMsg);
                    if (msg_count != msg_counter) {
                        customChatAdapter.notifyDataSetChanged();
                        msg_counter = msg_count;

                    }*/

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
    }
}
