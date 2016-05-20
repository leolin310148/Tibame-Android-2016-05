Android Facebook SDK & Login 設定
===

1. build.gradle
---

    compile 'com.facebook.android:facebook-android-sdk:[4,5)'
    
    
2. src/main/res/values/strings.xml.
---     
   
     
    <string name="facebook_app_id">你的facebook app id</string>
         

3. AndroidManifest.xml
---
          
    <uses-permission android:name="android.permission.INTERNET"/>          

    <application android:label="@string/app_name" ...>
        ...
        <meta-data android:name="com.facebook.sdk.ApplicationId" android:value="@string/facebook_app_id"/>
        ...
    </application>
              

4. 打開CMD，執行
---


    keytool -exportcert -alias androiddebugkey -keystore %HOMEPATH%\.android\debug.keystore | openssl sha1 -binary | openssl base64


5. MainActivity
---
           
    @Override
    public void onCreate(Builde saveInstance) {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
    }    
           
           
Login
===
           
1. 新增FacebookActivity至AndroidManifest中
---
           
    <activity android:name="com.facebook.FacebookActivity"
             android:configChanges="keyboard|keyboardHidden|screenLayout|screenSize|orientation"
             android:theme="@android:style/Theme.Translucent.NoTitleBar"
             android:label="@string/app_name" />
             
             
2. 新增LoginButton到layout檔
---
             
    <com.facebook.login.widget.LoginButton
        android:id="@+id/login_button"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_horizontal"
        android:layout_marginTop="30dp"
        android:layout_marginBottom="30dp" />    
                    
3. MainActivity
---
           
           
    CallbackManager callbackManager;
                
    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate();
        FacebookSdk.sdkInitialize(getApplicationContext());
        
        setContentView(R.layout.activity_main);
        
        
        callbackManager = CallbackManager.Factory.create();
        
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.usersettings_fragment_login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("登入成功");
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Log.e("fb", "fb login error", error);
                TextView textView = (TextView) findViewById(R.id.textView);
                textView.setText("登入失敗");
            }
        });
            
    }    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
                               