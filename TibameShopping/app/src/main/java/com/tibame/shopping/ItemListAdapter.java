package com.tibame.shopping;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author leolin
 */
public class ItemListAdapter extends BaseAdapter {

    Activity activity;

    public ItemListAdapter(Activity activity) {
        this.activity = activity;
    }

    List<DataSnapshot> items = new ArrayList<>();

    public void setItems(List<DataSnapshot> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.listitem, null);
        }

        DataSnapshot dataSnapshot = items.get(position);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewItemPicture);
        TextView textViewItemName = (TextView) convertView.findViewById(R.id.textViewItemName);
        TextView textViewItemPrice = (TextView) convertView.findViewById(R.id.textViewItemPrice);
        TextView textViewItemCreatedAt = (TextView) convertView.findViewById(R.id.textViewItemCreatedAt);

        Long createdAtTimestamp = dataSnapshot.child("createdAt").getValue(Long.class);
        Date date = new Date(createdAtTimestamp);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");


        textViewItemName.setText(dataSnapshot.child("itemName").getValue().toString());
        textViewItemPrice.setText(dataSnapshot.child("itemPrice").getValue().toString());
        textViewItemCreatedAt.setText(sdf.format(date));


        String url = dataSnapshot.child("url").getValue().toString();
        Glide.with(activity).load(url).into(imageView);

        return convertView;
    }
}
