package com.tibame.shopping;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class OrderNotifyIntentService extends IntentService {

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            final FirebaseDatabase database = FirebaseDatabase.getInstance();

            for (DataSnapshot orderToNotify : dataSnapshot.getChildren()) {
                System.out.println(orderToNotify);
                database.getReference().child("orders").child(orderToNotify.getKey()).child("itemId").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        database.getReference().child("items").child(dataSnapshot.getValue().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                                        .setPriority(Notification.PRIORITY_HIGH)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("New Order")
                                        .setContentText(dataSnapshot.child("itemName").getValue().toString())
                                        .build();

                                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).notify(dataSnapshot.getKey(), 199, notification);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    private Query query;

    public OrderNotifyIntentService() {
        super("OrderNotifyIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();


            System.out.println("action = " + action);

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            FirebaseAuth auth = FirebaseAuth.getInstance();

            if (action.equals("login")) {
                query = database.getReference()
                        .child("ordersNotify")
                        .child(auth.getCurrentUser().getUid())
                        .orderByValue().equalTo(false);
                query.addValueEventListener(valueEventListener);
            } else {
                query.removeEventListener(valueEventListener);
            }

        }
    }


}
