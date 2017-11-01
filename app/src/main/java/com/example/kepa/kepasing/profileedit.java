package com.example.kepa.kepasing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class profileedit extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileedit);
        ImageView goback = (ImageView) findViewById(R.id.gobackbutton);
        goback.setOnClickListener(new View.OnClickListener() {//左上角返回键
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //头像
        ImageView touxiang = (ImageView) findViewById(R.id.touxiang);
        touxiang.setImageResource(R.drawable.touxiang);
        touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //修改头像
            }
        });

        //保存按钮
        Button saveprofile = (Button) findViewById(R.id.saveprofile);
        saveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();//修改这个函数，这里只是按下跳回了个人主页而已
            }
        });
    }
}
