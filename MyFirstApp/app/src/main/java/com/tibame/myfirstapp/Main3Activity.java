package com.tibame.myfirstapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);


        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setText("請按我");

        Button buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView1 = (TextView) findViewById(R.id.textView1);
                textView1.setText("取消");
            }
        });


        ImageView imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.amazon);
    }

    public void buttonClicked(View view) {
        TextView textView1 = (TextView) findViewById(R.id.textView1);
        textView1.setText("按到了！！");
    }
}
