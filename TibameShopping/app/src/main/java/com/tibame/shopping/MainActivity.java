package com.tibame.shopping;

import android.content.Intent;
import android.os.Bundle;
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

        FirebaseAuth auth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = auth.getCurrentUser();

        TextView textViewCurrentUserName = (TextView) findViewById(R.id.textViewCurrentUserName);
        Button buttonLogin = (Button) findViewById(R.id.buttonLogin);

        if (currentUser == null) {
            textViewCurrentUserName.setText(  "還沒有登入"  );
            buttonLogin.setText(  "登入"  );
        } else {
            textViewCurrentUserName.setText(  currentUser.getEmail()  );
            buttonLogin.setText(  "登出"  );
        }

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
}
