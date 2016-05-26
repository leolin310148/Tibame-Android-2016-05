新版 Firebase設定
===


1. 進入新的firebase網站
---
[https://firebase.google.com](https://firebase.google.com)

2. 選擇 Get Start For Free
---
<img src="http://i.imgur.com/Z41mc6S.png"/>


3. 將舊的App匯入或新增一個新的App
---

4. 點選App進入App的Dashboard, 選擇 `將Firebase加入您的Android應用程式`
---
<img src="http://i.imgur.com/eW0ypc4.png"/>

5. 回到Android Studio，將專案打開後，開啟右側Gradle視窗，點兩下執行singingReport
---
<img src="http://i.imgur.com/ctf5LW5.png"/>

6. 執行後，Android Studio下方的Run視窗會出現SHA-1，將SHA-1複製
---  
<img src="http://i.imgur.com/z2SqCW9.png"/>
  
7. 回到Firebase網站，填入Android App的package name及剛剛複製的SHA-1後，點選`新增應用程式`
---
<img src="http://i.imgur.com/M4pUlf9.png"/>  

8. 瀏覽器將下載`google-services.json`，將下載的`google-services.json`放到專案的app資料夾
---
<img src="http://i.imgur.com/540ZtY6.png"/>

9. 打開專案的`build.gradle` (是專案的，不是app的)，找到`classpath 'com.android.tools.build:gradle:2.1.0'`，在下方新增`classpath 'com.google.gms:google-services:3.0.0'`
---

    
    // Top-level build file where you can add configuration options common to all sub-projects/modules.
    
    buildscript {
        repositories {
            jcenter()
        }
        dependencies {
            classpath 'com.android.tools.build:gradle:2.1.0'
            classpath 'com.google.gms:google-services:3.0.0'
            // NOTE: Do not place your application dependencies here; they belong
            // in the individual module build.gradle files
        }
    }


10. 打開app下的`build.gradle`，新增dependencies，以及在最下面新增`apply plugin: 'com.google.gms.google-services'`
---

    
    dependencies {
        ....
    
        compile 'com.google.firebase:firebase-core:9.0.0'
        compile 'com.google.firebase:firebase-database:9.0.0'
        compile 'com.google.firebase:firebase-storage:9.0.0'
        compile 'com.google.firebase:firebase-auth:9.0.0'
    }
    
    apply plugin: 'com.google.gms.google-services'


11. 完成設定並sync gradle後，即可在程式內使用新的Firebase library

 
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    database.getReference().child("messages").addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
 