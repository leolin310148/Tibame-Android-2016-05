package com.tibame.shopping;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class NewItemActivity extends AppCompatActivity {

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
                Bitmap bitmap = (Bitmap) extras.get("data");
                ImageView imageViewItemPicture = (ImageView) findViewById(R.id.imageViewItemPicture);
                imageViewItemPicture.setImageBitmap(bitmap);
            }

        } else if (requestCode == 1002) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                ImageView imageViewItemPicture = (ImageView) findViewById(R.id.imageViewItemPicture);
                imageViewItemPicture.setImageBitmap(bitmap);
            } catch (Exception e) {
                Toast.makeText(this, "無法取得圖片", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
