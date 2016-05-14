package com.tibame.hellofirebase;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        Firebase myFirebaseRef = new Firebase("https://tibame-leo-test.firebaseio.com/");

        myFirebaseRef.child("name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText( dataSnapshot.getValue().toString()  );


            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }
}
