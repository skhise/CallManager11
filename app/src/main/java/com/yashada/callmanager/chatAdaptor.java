        package com.yashada.callmanager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Shekhar on 4/21/2018.
 */

public class chatAdaptor extends BaseAdapter {

    Context context;
    List  <ChatMessage> chatMessageList=null;


    public chatAdaptor(List<ChatMessage> list, Context context){
        this.context = context;
        this.chatMessageList = new ArrayList<ChatMessage>();
        this.chatMessageList=list;

    }
    @Override
    public int getCount() {
        return this.chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.chatMessageList.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        
        Integer side= chatMessageList.get(position).getSide();
        String date= chatMessageList.get(position).getDate_time();
        String msg = chatMessageList.get(position).getMessage();
        TextView chatText;
        TextView DateText;
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Log.e("side",""+side);
        if (side==2) {
            row = inflater.inflate(R.layout.other_user_msg, parent, false);
        }else{
            row = inflater.inflate(R.layout.loged_user_msg, parent, false);
        }
        chatText = (TextView) row.findViewById(R.id.msgr);
        DateText = (TextView) row.findViewById(R.id.str_time);
        chatText.setText(msg);
        DateText.setText(date);
        return row;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
