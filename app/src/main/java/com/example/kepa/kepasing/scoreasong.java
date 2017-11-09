package com.example.kepa.kepasing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class scoreasong extends AppCompatActivity {
    ArrayList<String> passedsonginfo=new ArrayList<String>();//0是歌名 1是歌手
    private RatingBar mRatingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreasong);
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

        ImageView person=(ImageView)findViewById(R.id.person);
        person.setImageResource(R.drawable.touxiang);
        TextView username=(TextView)findViewById(R.id.usernickname);
        username.setText(passedsonginfo.get(1));

        TextView currentscore=(TextView)findViewById(R.id.currentscore);
        currentscore.setText("8.5");//目前的得分 需要获取

        ImageView playbutton=(ImageView)findViewById(R.id.play);
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放控制  前边点进来就自动播放
            }
        });

        //记得绑定一下歌曲进度条
        SeekBar progressbar=(SeekBar)findViewById(R.id.seekbar);


        //打分的星级
        mRatingBar=(RatingBar) findViewById(R.id.ratingBar);
        //为RatingBar设置监听
        mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                //得到RatingBar的最大等级
                int max=ratingBar.getMax();
                //得到RatingBar现在的等级
                float currentRating=rating;
                String s=Float.toString(20*currentRating/max);
                TextView score=(TextView)findViewById(R.id.score);
                score.setText(s);
            }
        });

        //提交按钮
        Button submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(scoreasong.this, "提交成功", Toast.LENGTH_SHORT).show();
            }
        });


    }
}
