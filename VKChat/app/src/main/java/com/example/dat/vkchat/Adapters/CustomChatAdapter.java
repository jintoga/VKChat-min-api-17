package com.example.dat.vkchat.Adapters;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.dat.vkchat.LoginActivity;
import com.example.dat.vkchat.Model.Attachment;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.Model.Message;
import com.example.dat.vkchat.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by DAT on 8/12/2015.
 */
public class CustomChatAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Message> data;
    private Contact receiver;

    public CustomChatAdapter(Context context, ArrayList<Message> data, Contact receiver) {
        this.context = context;
        this.data = data;
        this.receiver = receiver;

    }

    @Override
    public int getCount() {
        if (data != null) {
            return data.size();
        } else
            return 0;
    }

    @Override
    public Message getItem(int position) {
        if (data != null) {
            return data.get(position);
        } else
            return null;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        Message msg = getItem(position);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.custom_item_chat, viewGroup, false);
            viewHolder = createViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Contact cur = ((LoginActivity) context).getCurrentUser();
        Message re = getItem(position);

       /* Log.d("1", ((LoginActivity) context).getCurrentUser().getUser_id() + "");
        Log.d("2", getItem(position).getUser_id() + "");*/
        if (getItem(position) != null && ((LoginActivity) context).getCurrentUser() != null) {
            if (((LoginActivity) context).getCurrentUser().getUser_id() == getItem(position).getFrom_id()) {
                Picasso.with(context).load(((LoginActivity) context).getCurrentUser().getAvatar_url()).into(viewHolder.avatar);
                viewHolder.msg_body.setText(getItem(position).getBody());
                int dayChecker = checkTodayYesterday(getItem(position).getUnix_time());
                if (dayChecker == 1) {
                    viewHolder.date_time.setText("Today " + getTime(getItem(position).getUnix_time()));
                } else if (dayChecker == 2) {
                    viewHolder.date_time.setText("Yesterday " + getTime(getItem(position).getUnix_time()));
                } else {
                    viewHolder.date_time.setText(getItem(position).getTime_date());
                }
                ArrayList<Attachment> attachments = getItem(position).getAttachments();

                if (attachments != null) {
                    viewHolder.progressBarLoadImage.setVisibility(View.VISIBLE);
                    for (Attachment attachment : attachments) {
                        if (attachment.getType().equals("photo")) {
                            Picasso.with(context).load(attachment.getImage_url()).into(viewHolder.img_body, new Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.progressBarLoadImage.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    viewHolder.progressBarLoadImage.setVisibility(View.GONE);
                                    viewHolder.img_body.setImageDrawable(null);
                                }
                            });

                        }
                    }
                } else {
                    viewHolder.img_body.setImageDrawable(null);     //THIS IS SUPER IMPORTANT*****
                    viewHolder.progressBarLoadImage.setVisibility(View.GONE);
                }
                viewHolder.contentBody.setBackgroundResource(R.drawable.bubright);

                RelativeLayout.LayoutParams layoutParamsContainer = (RelativeLayout.LayoutParams) viewHolder.container.getLayoutParams();
                layoutParamsContainer.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                viewHolder.container.setLayoutParams(layoutParamsContainer);

                LinearLayout.LayoutParams layoutParamsAvatarContainer = (LinearLayout.LayoutParams) viewHolder.containerAvatar.getLayoutParams();
                layoutParamsAvatarContainer.gravity = Gravity.RIGHT;
                viewHolder.containerAvatar.setLayoutParams(layoutParamsAvatarContainer);

                LinearLayout.LayoutParams layoutParamsMsg = (LinearLayout.LayoutParams) viewHolder.contentBody.getLayoutParams();
                layoutParamsMsg.gravity = Gravity.RIGHT;
                layoutParamsMsg.rightMargin = 20;
                viewHolder.contentBody.setLayoutParams(layoutParamsMsg);

                FrameLayout.LayoutParams layoutParamsAvatar = (FrameLayout.LayoutParams) viewHolder.avatar.getLayoutParams();
                layoutParamsAvatar.gravity = Gravity.RIGHT;
                viewHolder.avatar.setLayoutParams(layoutParamsAvatar);


            } else {
                Picasso.with(context).load(receiver.getAvatar_url()).into(viewHolder.avatar);
                viewHolder.msg_body.setText(getItem(position).getBody());
                int dayChecker = checkTodayYesterday(getItem(position).getUnix_time());
                if (dayChecker == 1) {
                    viewHolder.date_time.setText("Today " + getTime(getItem(position).getUnix_time()));
                } else if (dayChecker == 2) {
                    viewHolder.date_time.setText("Yesterday " + getTime(getItem(position).getUnix_time()));
                } else {
                    viewHolder.date_time.setText(getItem(position).getTime_date());
                }
                ArrayList<Attachment> attachments = getItem(position).getAttachments();

                if (attachments != null) {
                    viewHolder.progressBarLoadImage.setVisibility(View.VISIBLE);
                    for (Attachment attachment : attachments) {
                        if (attachment.getType().equals("photo")) {
                            Picasso.with(context).load(attachment.getImage_url()).into(viewHolder.img_body, new Callback() {
                                @Override
                                public void onSuccess() {
                                    viewHolder.progressBarLoadImage.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    viewHolder.progressBarLoadImage.setVisibility(View.GONE);
                                    viewHolder.img_body.setImageDrawable(null);
                                }
                            });

                        }
                    }
                } else {
                    viewHolder.img_body.setImageDrawable(null);     //THIS IS SUPER IMPORTANT*****
                    viewHolder.progressBarLoadImage.setVisibility(View.GONE);
                }
                viewHolder.contentBody.setBackgroundResource(R.drawable.bubleft);

                RelativeLayout.LayoutParams layoutParamsContainer = (RelativeLayout.LayoutParams) viewHolder.container.getLayoutParams();
                layoutParamsContainer.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                viewHolder.container.setLayoutParams(layoutParamsContainer);

                LinearLayout.LayoutParams layoutParamsAvatarContainer = (LinearLayout.LayoutParams) viewHolder.containerAvatar.getLayoutParams();
                layoutParamsAvatarContainer.gravity = Gravity.LEFT;
                viewHolder.containerAvatar.setLayoutParams(layoutParamsAvatarContainer);

                LinearLayout.LayoutParams layoutParamsMsg = (LinearLayout.LayoutParams) viewHolder.contentBody.getLayoutParams();
                layoutParamsMsg.gravity = Gravity.LEFT;
                layoutParamsMsg.leftMargin = 20;
                viewHolder.contentBody.setLayoutParams(layoutParamsMsg);

                FrameLayout.LayoutParams layoutParamsAvatar = (FrameLayout.LayoutParams) viewHolder.avatar.getLayoutParams();
                layoutParamsAvatar.gravity = Gravity.LEFT;
                viewHolder.avatar.setLayoutParams(layoutParamsAvatar);

            }
        }
        return convertView;
    }

    private String getTime(long epoch) {
        String time = new java.text.SimpleDateFormat("HH:mm:ss").format(new java.util.Date(epoch * 1000));

        return time;
    }

    private int checkTodayYesterday(long epoch) {
        Date date = new Date(epoch * 1000);
        int day = date.getDate();
        int month = date.getMonth();
        int year = date.getYear();
        Date today = new Date();
        int cur_day = today.getDate();
        int cur_month = today.getMonth();
        int cur_year = today.getYear();
        if (day == cur_day && month == cur_month && year == cur_year) {
            return 1;
        } else if (day + 1 == cur_day && month == cur_month && year == cur_year) {
            return 2;
        } else {
            return 0;
        }
    }


    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.msg_body = (TextView) view.findViewById(R.id.textViewMsg);
        holder.img_body = (ImageView) view.findViewById(R.id.imageViewImgBody);
        holder.avatar = (CircleImageView) view.findViewById(R.id.imageViewAvatar);
        holder.containerAvatar = (FrameLayout) view.findViewById(R.id.frameLayoutAvatar);
        holder.container = (LinearLayout) view.findViewById(R.id.linearLayoutContainer);
        holder.contentBody = (LinearLayout) view.findViewById(R.id.linearLayoutContentBody);
        holder.progressBarLoadImage = (ProgressBar) view.findViewById(R.id.progressBarLoadImage);
        holder.date_time = (TextView) view.findViewById(R.id.textViewDateTime);
        return holder;
    }

    private static class ViewHolder {
        CircleImageView avatar;
        TextView msg_body;
        ImageView img_body;
        LinearLayout container;
        LinearLayout contentBody;
        FrameLayout containerAvatar;
        ProgressBar progressBarLoadImage;
        TextView date_time;
    }


}
