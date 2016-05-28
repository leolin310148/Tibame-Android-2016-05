package com.tibame.shopping;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity {

    DataSnapshot dataSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        String itemId = getIntent().getStringExtra("itemId");


        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference().child("items").child(itemId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        ImageView imageView = (ImageView) findViewById(R.id.imageViewItemPicture);
                        TextView textViewItemName = (TextView) findViewById(R.id.textViewItemName);
                        TextView textViewItemPrice = (TextView) findViewById(R.id.textViewItemPrice);
                        TextView textViewItemCreatedAt = (TextView) findViewById(R.id.textViewItemCreatedAt);

                        Long createdAtTimestamp = dataSnapshot.child("createdAt").getValue(Long.class);
                        Date date = new Date(createdAtTimestamp);

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");


                        textViewItemName.setText(dataSnapshot.child("itemName").getValue().toString());
                        textViewItemPrice.setText(dataSnapshot.child("itemPrice").getValue().toString());
                        textViewItemCreatedAt.setText(sdf.format(date));


                        String url = dataSnapshot.child("url").getValue().toString();
                        Glide.with(ItemDetailActivity.this).load(url).into(imageView);

                        ItemDetailActivity.this.dataSnapshot = dataSnapshot;

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        database.getReference().child("orders").orderByChild("itemId").equalTo(itemId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        TextView textViewItemSoldCount = (TextView) findViewById(R.id.textViewItemSoldCount);
                        textViewItemSoldCount.setText(String.valueOf(dataSnapshot.getChildrenCount()));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }

    public void doOrderItem(View view) {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        final FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        final String itemId = getIntent().getStringExtra("itemId");

        new AlertDialog.Builder(this)
                .setTitle("訂購")
                .setMessage("確認要訂購嗎？")
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance();

                        Map<String, Object> orderMap = new HashMap<String, Object>();
                        orderMap.put("itemId", itemId);
                        orderMap.put("buyerId", currentUser.getUid());
                        String sellerId = dataSnapshot.child("userId").getValue().toString();
                        orderMap.put("sellerId", sellerId);

                        final ProgressDialog progressDialog = ProgressDialog.show(ItemDetailActivity.this, "處理中", "請稍候");

                        DatabaseReference order = database.getReference()
                                .child("orders")
                                .push();


                        order.setValue(orderMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        progressDialog.dismiss();
                                        Toast.makeText(ItemDetailActivity.this, "訂購成功", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });

                        database.getReference().child("ordersNotify")
                                .child(sellerId)
                                .child(order.getKey())
                                .setValue(false);
                    }
                })
                .show();
    }
}
