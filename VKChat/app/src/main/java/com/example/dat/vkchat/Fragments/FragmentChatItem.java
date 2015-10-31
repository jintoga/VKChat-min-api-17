package com.example.dat.vkchat.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v4.app.Fragment;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dat.vkchat.Adapters.CustomChatAdapter;
import com.example.dat.vkchat.LoginActivity;
import com.example.dat.vkchat.Model.Attachment;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.Model.Message;
import com.example.dat.vkchat.R;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.httpClient.VKImageOperation;
import com.vk.sdk.api.model.VKApiLink;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKAttachments;
import com.vk.sdk.api.model.VKPhotoArray;
import com.vk.sdk.api.photo.VKImageParameters;
import com.vk.sdk.api.photo.VKUploadImage;
import com.vk.sdk.api.photo.VKUploadWallPhotoRequest;
import com.vk.sdk.dialogs.VKShareDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by DAT on 8/31/2015.
 */
public class FragmentChatItem extends Fragment {
    private ArrayList<Message> listMsg = new ArrayList<>();
    private CustomChatAdapter customChatAdapter;
    private ImageButton buttonSend;
    private EditText editTextMsg;
    private ListView listViewChat;
    private Contact receiver = null;
    private ProgressBar progressBarLoadChat;
    private ImageButton imageButtonAttach;
    private TextView textViewFileName;
    private ProgressBar progressBarUploadAttachment;
    private boolean refreshRunner = false;
    private int msg_counter;

    private String attachment_url_604 = "";
    private String attachment_id = "";
    private String attachment_owner_id = "";

    private SwipeRefreshLayout swipeRefreshLayoutListViewChat;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        receiver = (Contact) bundle.getSerializable("data");
        View view = inflater.inflate(R.layout.fragment_chat_item, container, false);
        getIDs(view);
        setEvents();
        msg_counter = getCurrentMsgNumber();
        refreshRunner = true;

        numb_of_receiving_msg = 10;
        refreshToDetectIncomingMsg();
        return view;
    }


    private void getIDs(View view) {
        progressBarLoadChat = (ProgressBar) view.findViewById(R.id.progressBarLoadChat);
        //progressBarLoadChat.setVisibility(View.INVISIBLE);

        buttonSend = (ImageButton) view.findViewById(R.id.imageButtonSend);
        editTextMsg = (EditText) view.findViewById(R.id.editTextMsg);
        editTextMsg.requestFocus();
        listViewChat = (ListView) view.findViewById(R.id.listViewChat);

        imageButtonAttach = (ImageButton) view.findViewById(R.id.imageButtonAttach);
        textViewFileName = (TextView) view.findViewById(R.id.textViewFileName);
        progressBarUploadAttachment = (ProgressBar) view.findViewById(R.id.progressBarUploadAttchment);
        progressBarUploadAttachment.setVisibility(View.INVISIBLE);

        swipeRefreshLayoutListViewChat = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayoutListViewChat);

    }

    private boolean flag_loading, isScrollingDown;

    private void setEvents() {
        customChatAdapter = new CustomChatAdapter(getActivity(), listMsg, receiver);
        SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(customChatAdapter);
        swingBottomInAnimationAdapter.setAbsListView(listViewChat);
        assert swingBottomInAnimationAdapter.getViewAnimator() != null;
        swingBottomInAnimationAdapter.getViewAnimator().setInitialDelayMillis(600);


        listViewChat.setAdapter(swingBottomInAnimationAdapter);

        listViewChat.setOnTouchListener(new View.OnTouchListener() {
            float initialY, finalY;


            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case (MotionEvent.ACTION_DOWN):
                        initialY = event.getY();
                        if (initialY < finalY) {
                            Log.d("UP", "Scrolling up: " + scroll_offset);
                            isScrollingDown = false;
                        } else if (initialY > finalY) {
                            Log.d("DOWN", "Scrolling down: " + scroll_offset);
                            isScrollingDown = true;
                        }
                    case (MotionEvent.ACTION_UP):
                        finalY = event.getY();
                    default:
                }
                return false;
            }
        });
        listViewChat.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (listIsAtBottom() == true && scroll_offset > 0) {
                    scroll_offset = scroll_offset - 2;
                    refreshRunner = false;
                    customChatAdapter.clear();
                    customChatAdapter.notifyDataSetChanged();
                    loadMoreItems();
                    Log.d("scroll_offset", scroll_offset + "");
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!isUploadingAttachment) {
                    CharBuffer cbuf = null;
                    try {
                        Charset charset = Charset.forName("UTF-8");
                        CharsetDecoder decoder = charset.newDecoder();
                        CharsetEncoder encoder = charset.newEncoder();
                        ByteBuffer bbuf = encoder.encode(CharBuffer.wrap(editTextMsg.getText() + ""));
                        cbuf = decoder.decode(bbuf);
                    } catch (CharacterCodingException e) {
                        e.printStackTrace();
                    }
                    String msg = cbuf.toString();
                    if (scroll_offset > 0)
                        scroll_offset = 0;
                    requestSendMsg(msg, String.valueOf(receiver.getUser_id()));
                    editTextMsg.setText("");
                    textViewFileName.setText("No Attachment");
                    textViewFileName.setTextColor(getResources().getColor(android.R.color.black));
                    attachment = "";
                    attachment_url_604 = "";
                } else {
                    Toast.makeText(getContext(), "Please wait for Attachment to be uploaded!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        imageButtonAttach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachImage();
            }
        });

        swipeRefreshLayoutListViewChat.setColorSchemeColors(getResources().getColor(R.color.primaryDark)
                , getResources().getColor(R.color.primary)
                , getResources().getColor(R.color.accent)
                , getResources().getColor(R.color.ripple));
        swipeRefreshLayoutListViewChat.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (flag_loading == false) {
                    if (numb_of_receiving_msg < 200)        //max amount of msg-s VK allows is 200
                        numb_of_receiving_msg = numb_of_receiving_msg + 2;      //increase number of receiving msg-s to 200
                    else {                                    //then use offset to get older msg-s
                        numb_of_receiving_msg = 200;          //if numb_of_receiving_msg >=200 => numb_of_receiving_msg = 200 to avoid error invalid parameter
                        scroll_offset = scroll_offset + 2;

                    }
                    Log.d("numb_of_receiving_msg", numb_of_receiving_msg + " offset" + scroll_offset);
                    refreshRunner = false;
                    customChatAdapter.clear();
                    customChatAdapter.notifyDataSetChanged();
                    loadMoreItems();
                }

            }
        });
    }

    private void loadMoreItems() {
        VKRequest request = VKApi.messages().myGetMsgHistoryMethod(VKParameters.from(VKApiConst.OFFSET, scroll_offset, VKApiConst.COUNT, numb_of_receiving_msg, VKApiConst.USER_ID, receiver.getUser_id()));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                //Log.d("Response", response.toString());
                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    //Log.d("jsonObject", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int msg_count = jsonObject.getInt("count");
                    //Log.d("msg_count", msg_count + "");
                    listMsg.clear();
                    //Log.d("jsonArray", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject joMsg = jsonArray.getJSONObject(i);
                        //Log.d("joContact", joMsg.toString());
                        String body = joMsg.getString("body");
                        int user_id = joMsg.getInt("user_id");
                        int from_id = joMsg.getInt("from_id");
                        long unix_date = joMsg.getLong("date");
                        Message message = new Message();
                        message.setUser_id(user_id);
                        message.setFrom_id(from_id);
                        message.setBody(body);
                        message.setUnix_time(unix_date);
                        message.setTime_date(unixDateConvert(unix_date));

                        try {
                            JSONArray jsonArrayAttachments;
                            if ((jsonArrayAttachments = joMsg.getJSONArray("attachments")) != null) {

                                ArrayList<Attachment> attachments = new ArrayList<Attachment>();
                                for (int j = 0; j < jsonArrayAttachments.length(); j++) {
                                    JSONObject joAttachment = jsonArrayAttachments.getJSONObject(j);
                                    Attachment attachment = new Attachment();
                                    attachment.setType(joAttachment.getString("type"));
                                    switch (attachment.getType()) {
                                        /*case "audio":
                                            JSONObject joAudio = joAttachment.getJSONObject("audio");
                                            attachment.setMusic_url(joAudio.getString("url"));
                                            break;*/
                                        case "photo":
                                            JSONObject joPhoto = joAttachment.getJSONObject("photo");
                                            attachment.setImage_url(joPhoto.getString("photo_604"));
                                            attachments.add(attachment);
                                            break;
                                        /*case "doc":
                                            JSONObject joDoc = joAttachment.getJSONObject("doc");
                                            attachment.setDoc_url(joDoc.getString("url"));
                                            break;
                                        case "video":
                                            JSONObject joVideo = joAttachment.getJSONObject("video");
                                            attachment.setVideo_img_url(joVideo.getString("photo_130"));
                                            attachments.add(attachment);
                                            break;*/
                                    }
                                    //TEMP SOLUTION
                                    //attachments.add(attachment);
                                }
                                if (attachments.size() > 0)
                                    message.setAttachments(attachments);
                            }
                        } catch (JSONException e) {
                            //Log.i("JSON ERROR", "No value for attachments");
                        }

                        listMsg.add(message);
                    }
                    Collections.reverse(listMsg);
                    customChatAdapter.notifyDataSetChanged();
                    if (isScrollingDown == true) {
                        listViewChat.setSelection(customChatAdapter.getCount() - 1);
                        Log.d("isScrollingDown", String.valueOf(isScrollingDown));
                    } else {
                        listViewChat.setSelection(1);
                        Log.d("isScrollingDown", String.valueOf(isScrollingDown));
                    }
                    flag_loading = false;
                    swipeRefreshLayoutListViewChat.setRefreshing(false);
                    refreshRunner = true;
                    refreshToDetectIncomingMsg();
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
                swipeRefreshLayoutListViewChat.setRefreshing(false);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                flag_loading = true;
            }
        });
    }


    private static int RESULT_LOAD_IMAGE = 502;
    private boolean waitingForActivityResult = false;

    private void attachImage() {

        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        getParentFragment().startActivityForResult(intent, RESULT_LOAD_IMAGE);
        waitingForActivityResult = true;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && data != null && waitingForActivityResult == true) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            //String fileName = picturePath.substring(picturePath.lastIndexOf("/") + 1);
            cursor.close();

            Log.d("picturePath:", picturePath);

            File file = new File(picturePath);
            textViewFileName.setText(file.getName());
            textViewFileName.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));
            uploadImage(file);
            waitingForActivityResult = false;
        }
    }

    private boolean isUploadingAttachment = false;

    private void uploadImage(File file) {
        textViewFileName.setVisibility(View.INVISIBLE);
        progressBarUploadAttachment.setVisibility(View.VISIBLE);

        VKRequest request = VKApi.uploadMessagesPhotoRequest(file);
        request.secure = false;
        request.useSystemLanguage = false;
        isUploadingAttachment = true;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                Log.d("Response", response.toString());
                parseJson(response.json);
                progressBarUploadAttachment.setVisibility(View.INVISIBLE);
                textViewFileName.setVisibility(View.VISIBLE);
                isUploadingAttachment = false;
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                progressBarUploadAttachment.setVisibility(View.INVISIBLE);
                textViewFileName.setText("Upload Error");
                textViewFileName.setTextColor(getResources().getColor(android.R.color.black));
                textViewFileName.setVisibility(View.VISIBLE);
                isUploadingAttachment = false;
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                progressBarUploadAttachment.setVisibility(View.INVISIBLE);
                textViewFileName.setText("Upload Error");
                textViewFileName.setTextColor(getResources().getColor(android.R.color.black));
                textViewFileName.setVisibility(View.VISIBLE);
                isUploadingAttachment = false;
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                progressBarUploadAttachment.setVisibility(View.VISIBLE);
                isUploadingAttachment = true;
            }
        });

    }

    private void parseJson(JSONObject json) {
        try {
            JSONArray jsonArray = json.getJSONArray("response");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject joPhoto = jsonArray.getJSONObject(i);
                attachment_url_604 = joPhoto.getString("photo_604");
                attachment_id = joPhoto.getString("id");
                attachment_owner_id = joPhoto.getString("owner_id");
                Log.d("attachment_url_604", attachment_url_604);
                Log.d("attachment_id", attachment_id);
                Log.d("attachment_owner_id", attachment_owner_id);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private int getCurrentMsgNumber() {
        final int[] count = {0};
        VKRequest request = VKApi.messages().myGetMsgHistoryMethod(VKParameters.from(VKApiConst.COUNT, "1", VKApiConst.USER_ID, receiver.getUser_id()));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                //Log.d("Response", response.toString());
                progressBarLoadChat.setVisibility(View.GONE);
                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    int msg_count = jsonObject.getInt("count");
                    //Log.d("msg_count", msg_count + "");
                    count[0] = msg_count;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                super.attemptFailed(request, attemptNumber, totalAttempts);
                progressBarLoadChat.setVisibility(View.GONE);
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                progressBarLoadChat.setVisibility(View.GONE);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
                progressBarLoadChat.setVisibility(View.VISIBLE);
            }


        });
        return count[0];
    }

    public void refreshToDetectIncomingMsg() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    while (refreshRunner) {
                        Thread.sleep(1000);      //VK Api allows no more than 3 requests per second so lets make 2 requests per second for safety
                        //Log.d("DoINBackGround", "On doInBackground...");
                        responseUpdateChat();
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Log.d("DoINBackGround", "On doInBackground...");
                responseUpdateChat();
                return null;
            }

            protected void onPreExecute() {
                Log.d("PreExceute", "On pre Exceute......");
            }
        }.execute();


    }

    private String attachment = "";

    private void requestSendMsg(final String msg, final String user_id) {


        VKRequest request = VKApi.messages().mySendingMsgMethod(VKParameters.from(VKApiConst.MESSAGE, msg, VKApiConst.USER_ID, user_id));
        if (!attachment_url_604.equals("")) {
            attachment = "photo" + attachment_owner_id + "_" + attachment_id;
            request.addExtraParameters(VKParameters.from(VKApiConst.ATTACHMENT, attachment));
            //Toast.makeText(getContext(), "With Attachment Sent", Toast.LENGTH_SHORT).show();
        }

        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                //Log.d("Response", response.toString());
                refreshToDetectIncomingMsg();
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
    public void onPause() {
        super.onPause();
        refreshRunner = false;
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            refreshRunner = true;
            refreshToDetectIncomingMsg();
        } else {
            Log.d("Hidden", "Hidden");
            refreshRunner = false;
        }
    }

    private int scroll_offset = 0;
    private int numb_of_receiving_msg;

    private void responseUpdateChat() {
        //Log.d("Receiver", receiver.getName() + " id:" + receiver.getUser_id());
        VKRequest request = VKApi.messages().myGetMsgHistoryMethod(VKParameters.from(VKApiConst.OFFSET, scroll_offset, VKApiConst.COUNT, numb_of_receiving_msg, VKApiConst.USER_ID, receiver.getUser_id()));
        request.secure = false;
        request.useSystemLanguage = false;
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                //Log.d("Response", response.toString());
                try {
                    JSONObject jsonObject = response.json.getJSONObject("response");
                    //Log.d("jsonObject", jsonObject.toString());
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    int msg_count = jsonObject.getInt("count");
                    //Log.d("msg_count", msg_count + "");
                    listMsg.clear();
                    //Log.d("jsonArray", jsonArray.toString());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject joMsg = jsonArray.getJSONObject(i);
                        //Log.d("joContact", joMsg.toString());
                        String body = joMsg.getString("body");
                        int user_id = joMsg.getInt("user_id");
                        int from_id = joMsg.getInt("from_id");
                        long unix_date = joMsg.getLong("date");
                        Message message = new Message();
                        message.setUser_id(user_id);
                        message.setFrom_id(from_id);
                        message.setBody(body);
                        message.setUnix_time(unix_date);
                        message.setTime_date(unixDateConvert(unix_date));

                        try {
                            JSONArray jsonArrayAttachments;
                            if ((jsonArrayAttachments = joMsg.getJSONArray("attachments")) != null) {

                                ArrayList<Attachment> attachments = new ArrayList<Attachment>();
                                for (int j = 0; j < jsonArrayAttachments.length(); j++) {
                                    JSONObject joAttachment = jsonArrayAttachments.getJSONObject(j);
                                    Attachment attachment = new Attachment();
                                    attachment.setType(joAttachment.getString("type"));
                                    switch (attachment.getType()) {
                                        /*case "audio":
                                            JSONObject joAudio = joAttachment.getJSONObject("audio");
                                            attachment.setMusic_url(joAudio.getString("url"));
                                            break;*/
                                        case "photo":
                                            JSONObject joPhoto = joAttachment.getJSONObject("photo");
                                            attachment.setImage_url(joPhoto.getString("photo_604"));
                                            attachments.add(attachment);
                                            break;
                                        /*case "doc":
                                            JSONObject joDoc = joAttachment.getJSONObject("doc");
                                            attachment.setDoc_url(joDoc.getString("url"));
                                            break;
                                        case "video":
                                            JSONObject joVideo = joAttachment.getJSONObject("video");
                                            attachment.setVideo_img_url(joVideo.getString("photo_130"));
                                            attachments.add(attachment);
                                            break;*/
                                    }
                                    //TEMP SOLUTION
                                    //attachments.add(attachment);
                                }
                                if (attachments.size() > 0)
                                    message.setAttachments(attachments);
                            }
                        } catch (JSONException e) {
                            //Log.i("JSON ERROR", "No value for attachments");
                        }

                        //Log.d("Msg", message.toString());
                        listMsg.add(message);
                    }
                    Collections.reverse(listMsg);
                    if (msg_count != msg_counter) {     //if there's new msg then notifyDataSetChanged
                        customChatAdapter.notifyDataSetChanged();
                        listViewChat.setSelection(customChatAdapter.getCount() - 1);
                        msg_counter = msg_count;

                    }

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

    private String unixDateConvert(long epoch) {
        String date_time = new java.text.SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new java.util.Date(epoch * 1000));
        Date date = new Date(epoch * 1000);

        return date_time;
    }

    public boolean isRefreshRunner() {
        return refreshRunner;
    }

    public void setRefreshRunner(boolean refreshRunner) {
        this.refreshRunner = refreshRunner;
    }

    private boolean listIsAtTop() {
        if (listViewChat.getChildCount() == 0)
            return true;
        return listViewChat.getChildAt(0).getTop() == 0;
    }

    private boolean listIsAtBottom() {

        return customChatAdapter.listIsAtBottom(listViewChat.getLastVisiblePosition());
    }

}
