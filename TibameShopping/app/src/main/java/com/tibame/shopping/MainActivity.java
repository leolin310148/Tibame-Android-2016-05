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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;

    int limit = 10;
    Query query;

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

                    //ItemGenerator.generateItems();

                    waitOrderNotify(currentUser);

                }

            }
        });


        final ListView listView = (ListView) findViewById(R.id.listView);
        final ItemListAdapter adapter = new ItemListAdapter(MainActivity.this);
        listView.setAdapter(adapter);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<DataSnapshot> list = new ArrayList<>();

                for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                    list.add(itemSnapshot);
                }

                Collections.sort(list, new Comparator<DataSnapshot>() {
                    @Override
                    public int compare(DataSnapshot lhs, DataSnapshot rhs) {
                        Long lhsCreatedAt = lhs.child("createdAt").getValue(Long.class);
                        Long rhsCreatedAt = rhs.child("createdAt").getValue(Long.class);
                        return rhsCreatedAt.compareTo(lhsCreatedAt);
                    }
                });

                adapter.setItems(list);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query = database.getReference().child("items")
                .orderByChild("createdAt")
                .limitToLast(limit + 1);

        query.addValueEventListener(valueEventListener);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getLastVisiblePosition() == adapter.getCount() - 1) {

                    if (adapter.getCount() > limit) {
                        limit += 10;
                        query.removeEventListener(valueEventListener);
                        query = database.getReference().child("items")
                                .orderByChild("createdAt")
                                .limitToLast(limit + 1);
                        query.addValueEventListener(valueEventListener);

                    }

                }

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DataSnapshot dataSnapshot = (DataSnapshot) parent.getItemAtPosition(position);

                Intent intent = new Intent(MainActivity.this, ItemDetailActivity.class);
                intent.putExtra("itemId", dataSnapshot.getKey());
                startActivity(intent);
            }
        });


    }

    private void waitOrderNotify(FirebaseUser currentUser) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String uid = currentUser.getUid();
        database.getReference().child("ordersNotify")
                .child(uid)
                .orderByValue().equalTo(false)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getChildrenCount() > 0) {
                            Toast.makeText(MainActivity.this, "新的訂單！", Toast.LENGTH_SHORT).show();
                        }
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
