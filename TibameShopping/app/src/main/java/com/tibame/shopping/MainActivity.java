package com.tibame.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawLayout);

        final FirebaseAuth auth = FirebaseAuth.getInstance();


        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser currentUser = auth.getCurrentUser();

                TextView textViewCurrentUserName = (TextView) findViewById(R.id.textViewCurrentUserName);
                Button buttonLogin = (Button) findViewById(R.id.buttonLogin);
                FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

                if (currentUser == null) {
                    textViewCurrentUserName.setText("還沒有登入");
                    buttonLogin.setText("登入");
                    fab.setVisibility(View.GONE);
                } else {
                    if (currentUser.getDisplayName() != null) {
                        textViewCurrentUserName.setText(currentUser.getDisplayName());
                    } else {
                        textViewCurrentUserName.setText(currentUser.getEmail());
                    }

                    buttonLogin.setText("登出");
                    fab.setVisibility(View.VISIBLE);
                }

            }
        });


        final ListView listView = (ListView) findViewById(R.id.listView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("items").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<DataSnapshot> list = new ArrayList<>();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    list.add(itemSnapshot);
                }

                //ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, list);
                ItemListAdapter adapter = new ItemListAdapter(MainActivity.this);
                listView.setAdapter(adapter);

                adapter.setItems(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void doLogin(View view) {

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {

            // 到登入頁面去
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);

        } else {
            // 登出
            auth.signOut();

        }

    }

    public void goNewItem(View view) {
        Intent intent = new Intent(this, NewItemActivity.class);
        startActivity(intent);
    }
}
