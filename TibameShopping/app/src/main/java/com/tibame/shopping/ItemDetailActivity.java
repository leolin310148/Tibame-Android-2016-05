package com.tibame.shopping;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        final String itemId = getIntent().getStringExtra("itemId");

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("items").child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                String url = dataSnapshot.child("url").getValue().toString();
                final String itemName = dataSnapshot.child("itemName").getValue().toString();
                String itemPrice = dataSnapshot.child("itemPrice").getValue().toString();
                Long itemCreatedAt = dataSnapshot.child("createdAt").getValue(Long.class);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                ImageView imageView = (ImageView) findViewById(R.id.imageViewItemPicture);
                TextView textViewItemName = (TextView) findViewById(R.id.textViewItemName);
                TextView textViewItemPrice = (TextView) findViewById(R.id.textViewItemPrice);
                TextView textViewItemCreatedAt = (TextView) findViewById(R.id.textViewItemCreatedAt);

                textViewItemName.setText(itemName);
                textViewItemPrice.setText(itemPrice);

                textViewItemCreatedAt.setText(simpleDateFormat.format(new Date(itemCreatedAt)));
                Glide.with(ItemDetailActivity.this).load(url).into(imageView);

                Button buttonOrderItem = (Button) findViewById(R.id.buttonOrderItem);
                buttonOrderItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        final FirebaseUser currentUser = auth.getCurrentUser();
                        if (currentUser == null) {
                            startActivity(new Intent(ItemDetailActivity.this, LoginActivity.class));
                            return;
                        }

                        new AlertDialog.Builder(ItemDetailActivity.this)
                                .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        final ProgressDialog progressDialog = ProgressDialog.show(ItemDetailActivity.this, "註冊中", "請稍候");

                                        Map<String, Object> orderMap = new HashMap<>();
                                        orderMap.put("itemId", itemId);
                                        orderMap.put("buyerUserId", currentUser.getUid());
                                        orderMap.put("sellerUserId", dataSnapshot.child("userId").getValue().toString());
                                        orderMap.put("createdAt", new Date().getTime());
                                        final DatabaseReference newOrder = database.getReference().child("orders").push();
                                        newOrder.setValue(orderMap, new DatabaseReference.CompletionListener() {
                                            @Override
                                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                progressDialog.dismiss();

                                                database.getReference()
                                                        .child("ordersNotify")
                                                        .child(currentUser.getUid()).child(newOrder.getKey()).setValue(false);

                                                finish();
                                            }
                                        });

                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .setMessage("確認要訂購商品" + itemName + "?")
                                .show();
                    }
                });


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

}
