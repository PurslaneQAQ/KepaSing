package com.example.kepa.kepasing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class singasong extends AppCompatActivity {
    ArrayList<String> passedsonginfo=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_asong);
        passedsonginfo=(ArrayList<String>)getIntent().getStringArrayListExtra("Songinfos");
        TextView songname=(TextView)findViewById(R.id.songname);
        songname.setText(passedsonginfo.get(0));
        ImageView gobackbtn=(ImageView)findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}
