package com.tibame.shopping;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class OrderNotifyIntentService extends IntentService {

    public OrderNotifyIntentService() {
        super("OrderNotifyIntentService");
    }

    Query query;
    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            if (dataSnapshot.getChildrenCount() > 0) {

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                PendingIntent pendingIntent = PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        new Intent(getApplicationContext(), MainActivity.class),
                        PendingIntent.FLAG_ONE_SHOT
                );

                Notification notification = new NotificationCompat.Builder(getApplicationContext())
                        .setContentTitle("你有新訂單")
                        .setContentText(dataSnapshot.getChildrenCount() + "筆")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentIntent(pendingIntent)
                        .build();

                notificationManager.notify(1209, notification);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                FirebaseAuth auth = FirebaseAuth.getInstance();
                FirebaseUser currentUser = auth.getCurrentUser();
                for (DataSnapshot orderNotifySnapshot : dataSnapshot.getChildren()) {
                    database.getReference().child("ordersNotify")
                            .child(currentUser.getUid())
                            .child(orderNotifySnapshot.getKey())
                            .setValue(true);
                }
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };
    boolean isInitialized = false;

    @Override
    protected void onHandleIntent(Intent intent) {

        if (isInitialized) {
            return;
        }

        isInitialized = true;

        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                if (currentUser == null) {

                    if (query != null) {
                        query.removeEventListener(valueEventListener);
                    }
                } else {

                    query = FirebaseDatabase.getInstance().getReference()
                            .child("ordersNotify")
                            .child(currentUser.getUid())
                            .orderByValue().equalTo(false);

                    query.addValueEventListener(valueEventListener);
                }

            }
        });


    }
}
