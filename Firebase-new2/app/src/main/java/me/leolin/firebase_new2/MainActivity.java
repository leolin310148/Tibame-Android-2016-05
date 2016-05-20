package me.leolin.firebase_new2;

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
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private FirebaseAuth firebaseAuth;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                render();
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    private void render() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        TextView textViewCurrentUser = (TextView) findViewById(R.id.textViewCurrentUser);
        Button loginButton = (Button) findViewById(R.id.loginButton);
        if (currentUser != null) {
            textViewCurrentUser.setText(currentUser.getEmail());
            loginButton.setText("登出");
            floatingActionButton.setVisibility(View.VISIBLE);
        } else {
            textViewCurrentUser.setText("尚未登入");
            loginButton.setText("登入");
            floatingActionButton.setVisibility(View.GONE);
        }

        System.out.println("currentUser = " + currentUser);
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

    public void onLoginButtonClicked(View view) {

        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            firebaseAuth.signOut();
        }
    }

    public void goNewItemActivity(View view) {
        startActivity(new Intent(this, NewItemActivity.class));
    }
}
