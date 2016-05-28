package com.tibame.shopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NewItemActivity extends AppCompatActivity {

    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
    }

    public void takePicture(View view) {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1001);

    }

    public void pickPicture(View view) {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1002);

    }

    public void doAddItem(View view) {

        EditText editTextItemName = (EditText) findViewById(R.id.editTextItemName);
        EditText editTextItemPrice = (EditText) findViewById(R.id.editTextItemPrice);

        final String itemName = editTextItemName.getText().toString();

        Integer itemPrice = null;
        try {
            itemPrice = Integer.parseInt(editTextItemPrice.getText().toString());
        } catch (NumberFormatException e) {
        }

        if (TextUtils.isEmpty(itemName)) {
            Toast.makeText(this, "請輸入商品名稱", Toast.LENGTH_SHORT).show();
            return;
        }

        if (itemPrice == null) {
            Toast.makeText(this, "請輸入商品價格", Toast.LENGTH_SHORT).show();
            return;
        }

        if (bitmap == null) {
            Toast.makeText(this, "請提供商品圖片", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();

        final ProgressDialog dialog = ProgressDialog.show(this, "上傳中", "請稍候");

        final Integer finalItemPrice = itemPrice;
        storage.getReference().child(UUID.randomUUID().toString())
                .putBytes(bytes)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Toast.makeText(NewItemActivity.this, "無法上傳圖片", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Log.i("downloadUrl", downloadUrl.toString());
                        saveItem(itemName, finalItemPrice, downloadUrl.toString());

                    }
                });

    }

    private void saveItem(String itemName, Integer itemPrice, String url) {

        Map<String, Object> itemMap = new HashMap<>();
        itemMap.put("itemName", itemName);
        itemMap.put("itemPrice", itemPrice);
        itemMap.put("url", url);

        itemMap.put("createdAt", new Date().getTime());

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        itemMap.put("userId", userId);

        final ProgressDialog dialog = ProgressDialog.show(this, "新增商品中", "請稍候");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        database.getReference().child("items")
                .push()
                .setValue(itemMap)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(NewItemActivity.this, "無法新增商品", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            finish();
                        }

                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        if (requestCode == 1001) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                bitmap = (Bitmap) extras.get("data");
                ImageView imageViewItemPicture = (ImageView) findViewById(R.id.imageViewItemPicture);
                imageViewItemPicture.setImageBitmap(bitmap);
            }

        } else if (requestCode == 1002) {

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageViewItemPicture = (ImageView) findViewById(R.id.imageViewItemPicture);
                imageViewItemPicture.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "無法取得圖片", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
