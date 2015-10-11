package com.example.dat.vkchat.Model;

import java.util.ArrayList;

/**
 * Created by DAT on 8/27/2015.
 */
public class Message {
    private String body;
    private int id;
    private int user_id;
    private int from_id;
    private long unix_time;
    private String time_date;
    private ArrayList<Attachment> attachments;


    public Message() {
    }

    public Message(String body, int id, int user_id, int from_id) {
        this.body = body;
        this.id = id;
        this.user_id = user_id;
        this.from_id = from_id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public ArrayList<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(ArrayList<Attachment> attachments) {
        this.attachments = attachments;
    }

    public long getUnix_time() {
        return unix_time;
    }

    public void setUnix_time(long unix_time) {
        this.unix_time = unix_time;
    }

    public String getTime_date() {
        return time_date;
    }

    public void setTime_date(String time_date) {
        this.time_date = time_date;
    }
}
