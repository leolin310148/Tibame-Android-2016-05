package com.tibame.shopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        callbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("email");

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                doFacebookLogin(  loginResult.getAccessToken()  );
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("fb", "fb login error", error);
                Toast.makeText(LoginActivity.this, "FB登入失敗", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void doFacebookLogin(AccessToken accessToken) {

        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        FirebaseAuth auth = FirebaseAuth.getInstance();

        final ProgressDialog dialog = ProgressDialog.show(this, "登入中", "請稍候");
        auth.signInWithCredential(credential)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            finish();
                        }

                    }
                });

    }

    public void doLogin(View view) {

        EditText editTextAccount = (EditText) findViewById(R.id.editTextAccount);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        String account = editTextAccount.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "帳號不能空白", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密碼不能空白", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();

        final ProgressDialog dialog = ProgressDialog.show(this, "登入中", "請稍候");

        auth.signInWithEmailAndPassword(account, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "登入失敗", Toast.LENGTH_SHORT).show();
                        Log.e("LoginActivity","Error during login ",e);

                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            finish();
                        }

                    }
                });


    }

    public void doSingUp(View view) {

        EditText editTextAccount = (EditText) findViewById(R.id.editTextAccount);
        EditText editTextPassword = (EditText) findViewById(R.id.editTextPassword);

        String account = editTextAccount.getText().toString();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(account)) {
            Toast.makeText(this, "帳號不能空白", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "密碼不能空白", Toast.LENGTH_SHORT).show();
            return;
        }


        FirebaseAuth auth = FirebaseAuth.getInstance();

        final ProgressDialog dialog = ProgressDialog.show(this, "註冊中", "請稍候");

        auth.createUserWithEmailAndPassword(account, password)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "註冊失敗", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();

                        if (task.isSuccessful()) {
                            finish();
                        }
                    }
                });

    }
}
