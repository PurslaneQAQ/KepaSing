package com.example.kepa.kepasing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    /*登录按钮*/
    public void login(View view){
        Intent intent=new Intent(this,mainpage.class);
        startActivity(intent);
    }
    /*注册按钮*/
    public void signup(View view){
        Intent intent = new Intent(this, mainpage.class);
        startActivity(intent);
    }
}
