package com.yashada.callmanager;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by ABC1 on 04-Nov-17.
 */

public class ListAdaptor extends BaseAdapter implements Filterable{

  Context context;
  List  <Contact> contactList=null;
  ArrayList<Contact> arraylist;



  public ListAdaptor(List<Contact> contact_List,Context context){
    this.context = context;
    this.contactList = new ArrayList<Contact>();
    this.contactList = contact_List;
    this.arraylist = new ArrayList<Contact>();
    this.arraylist.addAll(contactList);
  }
  @Override
  public Filter getFilter() {
    return  new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        final FilterResults oReturn = new FilterResults();
        final ArrayList<Contact> results = new ArrayList<Contact>();
        if (arraylist == null)
          arraylist.addAll(contactList);
        if (constraint != null) {
          if (arraylist != null && arraylist.size() > 0) {
            for (final Contact g : arraylist) {
              if (g.getCustomer_name().toLowerCase()
                      .contains(constraint.toString()))
                results.add(g);
            }
          }
          oReturn.values = results;
        }
        return oReturn;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        contactList = (ArrayList<Contact>) results.values;
        notifyDataSetChanged();
      }
    };
  }
  public void notifyDataSetChanged() {
    super.notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return this.contactList.size();
  }

  @Override
  public Object getItem(int i) {
    return this.contactList.get(i);
  }

  @Override
  public long getItemId(int i) {
    return i;
  }

  @Override
  public View getView(int i, View view, ViewGroup viewGroup) {
    ViewItem viewItem =null;
    if(view==null){
      viewItem = new ViewItem();
      LayoutInflater layoutInfiater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
      //LayoutInflater layoutInfiater = LayoutInflater.from(context);
      view = layoutInfiater.inflate(R.layout.list_custom_view, null);
      viewItem.customer_name = (TextView)view.findViewById(R.id.call_customer_name);
      viewItem.date_time = (TextView)view.findViewById(R.id.call_date_time);
      viewItem.call_id = (TextView)view.findViewById(R.id.call_Id);
      viewItem.callAlive = (TextView)view.findViewById(R.id.call_alive);
      viewItem.system_call_id = (TextView)view.findViewById(R.id.system_call_id);
      viewItem.UnreadMessages = (TextView)view.findViewById(R.id.new_action_any);
      view.setTag(viewItem);
    }
    else
    {
      viewItem = (ViewItem) view.getTag();
    }
    viewItem.customer_name.setText(contactList.get(i).getCustomer_name());
    viewItem.date_time.setText(contactList.get(i).getDate_time());
    viewItem.call_id.setText(" "+contactList.get(i).getCall_id());
    viewItem.callAlive.setText(" "+contactList.get(i).getCallAlive());
    viewItem.system_call_id.setText(contactList.get(i).getSystem_call_id());
    Integer new_action_any = contactList.get(i).getUnreadMessages();
    if(new_action_any>0){
      viewItem.UnreadMessages.setVisibility(View.VISIBLE);
    } else {
      viewItem.UnreadMessages.setVisibility(View.GONE);
    }
    return view;
  }
  class ViewItem {
    TextView customer_name;
    TextView date_time;
    TextView call_id;
    TextView callAlive;
    TextView system_call_id;
    TextView UnreadMessages;
  }

  public void filter(String charText) {
    charText = charText.toLowerCase(Locale.getDefault());
    if(arraylist.size()==0){
      arraylist.addAll(contactList);
    }
      contactList.clear();
    if (charText.length() == 0 || charText.equals("") || charText.equals(null)) {
      Log.e("array list size",""+arraylist.size());
      contactList.addAll(arraylist);
    }
    else
    {
      for (Contact wp : arraylist)
      {
        if (wp.getCustomer_name().toLowerCase(Locale.getDefault()).contains(charText) || wp.getCall_id().toLowerCase(Locale.getDefault()).contains(charText))
        {
          contactList.add(wp);
        }
      }
    }
    notifyDataSetChanged();
  }

}

