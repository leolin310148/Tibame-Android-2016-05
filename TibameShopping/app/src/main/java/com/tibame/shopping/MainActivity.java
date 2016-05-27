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
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    private ItemListAdapter itemListAdapter;
    private DrawerLayout drawerLayout;
    private ValueEventListener valueEventListener;
    private Query query;
    int limit = 5;
    int fetchedCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        listView = (ListView) findViewById(R.id.listView);
        itemListAdapter = new ItemListAdapter();
        listView.setAdapter(itemListAdapter);

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

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DataSnapshot> items = new ArrayList<>();
                fetchedCount = (int) dataSnapshot.getChildrenCount();
                for (DataSnapshot itemData : dataSnapshot.getChildren()) {
                    items.add(itemData);
                }

                Collections.sort(items, new Comparator<DataSnapshot>() {
                    @Override
                    public int compare(DataSnapshot lhs, DataSnapshot rhs) {
                        Long lhsCreatedAt = lhs.child("createdAt").getValue(Long.class);
                        Long rhsCreatedAt = rhs.child("createdAt").getValue(Long.class);
                        return rhsCreatedAt.compareTo(lhsCreatedAt);
                    }
                });
                itemListAdapter.setItems(items);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        query = database.getReference().child("items").orderByChild("createdAt").limitToLast(limit + 1);
        query.addValueEventListener(valueEventListener);


        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (listView.getLastVisiblePosition() == itemListAdapter.getCount() - 1) {

                    if (fetchedCount > limit) {
                        limit += 5;
                        System.out.println("scroll to bottom");
                        query.removeEventListener(valueEventListener);
                        query = database.getReference().child("items").orderByChild("createdAt").limitToLast(limit + 1);
                        query.addValueEventListener(valueEventListener);
                    }

                }
            }
        });
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ItemListAdapter extends BaseAdapter {

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
                convertView = getLayoutInflater().inflate(R.layout.listitem_item, null);
            }

            DataSnapshot dataSnapshot = items.get(position);
            String url = dataSnapshot.child("url").getValue().toString();
            String itemName = dataSnapshot.child("itemName").getValue().toString();
            String itemPrice = dataSnapshot.child("itemPrice").getValue().toString();
            Long itemCreatedAt = dataSnapshot.child("createdAt").getValue(Long.class);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

            ImageView imageView = (ImageView) convertView.findViewById(R.id.imageViewItemPicture);
            TextView textViewItemName = (TextView) convertView.findViewById(R.id.textViewItemName);
            TextView textViewItemPrice = (TextView) convertView.findViewById(R.id.textViewItemPrice);
            TextView textViewItemCreatedAt = (TextView) convertView.findViewById(R.id.textViewItemCreatedAt);

            textViewItemName.setText(itemName);
            textViewItemPrice.setText(itemPrice);

            textViewItemCreatedAt.setText(simpleDateFormat.format(new Date(itemCreatedAt)));
            Glide.with(MainActivity.this).load(url).into(imageView);

            return convertView;
        }
    }
}
