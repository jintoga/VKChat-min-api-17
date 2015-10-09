package com.example.dat.vkchat.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.dat.vkchat.LoginActivity;
import com.example.dat.vkchat.Model.Attachment;
import com.example.dat.vkchat.Model.Contact;
import com.example.dat.vkchat.Model.Message;
import com.example.dat.vkchat.R;
import com.squareup.picasso.Picasso;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import java.util.ArrayList;

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
        ViewHolder viewHolder;
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
                ArrayList<Attachment> attachments = getItem(position).getAttachments();
                if (getItem(position).getAttachments() != null) {
                    for (Attachment attachment : getItem(position).getAttachments()) {
                        if (attachment.getType().equals("photo"))
                            Glide.with(context).load(attachment.getImage_url()).into(viewHolder.img_body);
                    }
                }
                viewHolder.contentBody.setBackgroundResource(R.drawable.bubright);

                RelativeLayout.LayoutParams layoutParamsContainer = (RelativeLayout.LayoutParams) viewHolder.container.getLayoutParams();
                layoutParamsContainer.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                viewHolder.container.setLayoutParams(layoutParamsContainer);

                LinearLayout.LayoutParams layoutParamsAvatar = (LinearLayout.LayoutParams) viewHolder.avatar.getLayoutParams();
                layoutParamsAvatar.gravity = Gravity.RIGHT;
                viewHolder.avatar.setLayoutParams(layoutParamsAvatar);

                LinearLayout.LayoutParams layoutParamsMsg = (LinearLayout.LayoutParams) viewHolder.contentBody.getLayoutParams();
                layoutParamsMsg.gravity = Gravity.RIGHT;
                layoutParamsMsg.rightMargin = 20;
                viewHolder.contentBody.setLayoutParams(layoutParamsMsg);
            } else {
                Picasso.with(context).load(receiver.getAvatar_url()).into(viewHolder.avatar);
                viewHolder.msg_body.setText(getItem(position).getBody());
                ArrayList<Attachment> attachments = getItem(position).getAttachments();
                if (getItem(position).getAttachments() != null) {
                    for (Attachment attachment : getItem(position).getAttachments()) {
                        if (attachment.getType().equals("photo"))
                            Glide.with(context).load(attachment.getImage_url()).into(viewHolder.img_body);
                    }
                }
                viewHolder.contentBody.setBackgroundResource(R.drawable.bubleft);

                RelativeLayout.LayoutParams layoutParamsContainer = (RelativeLayout.LayoutParams) viewHolder.container.getLayoutParams();
                layoutParamsContainer.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                viewHolder.container.setLayoutParams(layoutParamsContainer);

                LinearLayout.LayoutParams layoutParamsAvatar = (LinearLayout.LayoutParams) viewHolder.avatar.getLayoutParams();
                layoutParamsAvatar.gravity = Gravity.LEFT;
                viewHolder.avatar.setLayoutParams(layoutParamsAvatar);

                LinearLayout.LayoutParams layoutParamsMsg = (LinearLayout.LayoutParams) viewHolder.contentBody.getLayoutParams();
                layoutParamsMsg.gravity = Gravity.LEFT;
                layoutParamsMsg.leftMargin = 20;
                viewHolder.contentBody.setLayoutParams(layoutParamsMsg);
            }
        }
        return convertView;
    }

    private ViewHolder createViewHolder(View view) {
        ViewHolder holder = new ViewHolder();
        holder.msg_body = (TextView) view.findViewById(R.id.textViewMsg);
        holder.img_body = (ImageView) view.findViewById(R.id.imageViewImgBody);
        holder.avatar = (CircleImageView) view.findViewById(R.id.imageViewAvatar);
        holder.container = (LinearLayout) view.findViewById(R.id.linearLayoutContainer);
        holder.contentBody = (LinearLayout) view.findViewById(R.id.linearLayoutContentBody);
        return holder;
    }

    private static class ViewHolder {
        CircleImageView avatar;
        TextView msg_body;
        ImageView img_body;
        LinearLayout container;
        LinearLayout contentBody;
    }


}
