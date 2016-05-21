package com.tibame.shopping;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        Intent intent = new Intent(this,NewItemActivity.class);
        startActivity(intent);
    }
}
