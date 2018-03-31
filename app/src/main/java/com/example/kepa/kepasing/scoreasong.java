package com.example.kepa.kepasing;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class scoreasong extends AppCompatActivity {
    ArrayList<String> passedsonginfo=new ArrayList<String>();//0是歌名 1是歌手
    private RatingBar mRatingBar;
    private MediaPlayer user_sound;
    //player
    private int play_sound = 0;
    private ImageView playbutton;

    private float current_score;
    public static double submit_score = 0;
    public static int submit_number = 0;
    private String FromServer;
    private TextView currentscore;
    private boolean getscore = false;
    private boolean getnumber = false;
    private int already;
    //player
    private Timer mTimer;
    private TimerTask mTask;
    private SeekBar progressbar;
    private int mPlayTimeDuration = 1000;//更新时间间隔

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreasong);
        passedsonginfo=(ArrayList<String>)getIntent().getStringArrayListExtra("Songinfos");
        TextView songname=(TextView)findViewById(R.id.songname);
        songname.setText(passedsonginfo.get(0));

        //zyt
        new GetNumber().start();
        while(!getnumber){}
        getnumber = false;

        ImageView gobackbtn=(ImageView)findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        final TextView dafenren=(TextView)findViewById(R.id.dafenrenshu);
        dafenren.setText(submit_number+"人已打分");//打分人数 需要获取

        System.out.println("come into scoreasong");
        playbutton=(ImageView)findViewById(R.id.play);
        //player
        try {
            user_sound = new MediaPlayer();
            File file = new File(getExternalCacheDir() + "/amr/"+mainpage.current_scoredid+".amr");
            user_sound.setDataSource(file.getAbsolutePath());
            user_sound.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //绑定进度条
                    if(mTimer == null)
                    {
                        mTimer = new Timer();
                        mTask = new scoreasong.SeekBarUpdate();
                        mTimer.scheduleAtFixedRate(mTask,0,mPlayTimeDuration);
                    }
                }
            });
            user_sound.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (user_sound != null) {
                        user_sound.stop();
                        user_sound.release();
                        user_sound = null;
                    }
                    play_sound = 0;
                    playbutton.setImageResource(R.drawable.pause);
                    progressbar.setProgress(0);
                    stopSeekBar();
                }
            });
//            user_sound.stop();
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        ImageView person=(ImageView)findViewById(R.id.person);

        //zyt 改以下用户名
        String s = getExternalCacheDir().getAbsolutePath()+"/img/"+mainpage.current_judgeuserid+".png";
        Bitmap bitmap = BitmapFactory.decodeFile(s);
        person.setImageBitmap(bitmap);

        TextView username=(TextView)findViewById(R.id.usernickname);
        username.setText(passedsonginfo.get(1));

        currentscore=(TextView)findViewById(R.id.currentscore);
        currentscore.setText(Double.toString(mainpage.current_score));//目前的得分 需要获取


        playbutton.setImageResource(R.drawable.pause);
        playbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //player
                play_sound++;
                if(play_sound==1){
                    playbutton.setImageResource(R.drawable.play);
                    try {
                        //file.getA..
                        if(user_sound==null){
                            try {
                                user_sound = new MediaPlayer();
                                File file = new File(getExternalCacheDir() + "/amr/"+mainpage.current_scoredid+".amr");
                                user_sound.setDataSource(file.getAbsolutePath());
//                                user_sound.prepare();
//                                user_sound.stop();
                            }catch(Exception e)
                            {
                                e.printStackTrace();
                            }
                        }
                        if(mTimer == null)
                        {
                            mTimer = new Timer();
                            mTask = new scoreasong.SeekBarUpdate();
                            mTimer.scheduleAtFixedRate(mTask,0,mPlayTimeDuration);
                        }
                        user_sound.prepare();
                        user_sound.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(play_sound%2==0){
                    playbutton.setImageResource(R.drawable.pause);
                    if(user_sound!=null) user_sound.pause();
                    //stopSeekBar();
                }
                else if(play_sound%2==1){
                    playbutton.setImageResource(R.drawable.play);
                    if(user_sound!=null) user_sound.start();
                }

                //播放控制
                //更换图标为暂停， 判断要暂停还是要播放
            }
        });
        //获取歌曲资源

        //记得绑定一下歌曲进度条
        //player
        progressbar=(SeekBar)findViewById(R.id.seekbar);
        progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(user_sound!=null && fromUser){
                    user_sound.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

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
                current_score = 20*currentRating/max;
            }
        });
        //获取评分 json

        //提交按钮
        Button submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(already ==1)
                {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("来自奇葩君的确认")
                            .setMessage("确定要覆盖您之前的评分吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(runnable).start();
                                    Toast.makeText(scoreasong.this, "提交成功", Toast.LENGTH_SHORT).show();
                                    //socket提交分数 获取新的分数 更新current
                                    // currentscore.setText("5.5");
                                    while(!getscore){}
                                    getscore = false;
                                    currentscore.setText(Double.toString(submit_score));
                                    dafenren.setText(submit_number+"人已打分");
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();

                }
                else {
                    new Thread(runnable).start();
                    Toast.makeText(scoreasong.this, "提交成功", Toast.LENGTH_SHORT).show();
                    //socket提交分数 获取新的分数 更新current
                    // currentscore.setText("5.5");
                    while (!getscore) {
                    }
                    getscore = false;
                    currentscore.setText(Double.toString(submit_score));
                    dafenren.setText(submit_number + "人已打分");
                }
            }
        });
    }

    //player
    class SeekBarUpdate extends TimerTask {
        @Override
        public void run(){
            scoreasong.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(user_sound!=null) {
                        int timePassed = user_sound.getCurrentPosition() / 1000;
                        progressbar.setMax(user_sound.getDuration() / 1000);
                        progressbar.setProgress(timePassed);
                        System.out.println("update Seek Bar");
                    }
                }
            });
        }
    }

    //player
    public void stopSeekBar(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    //player
    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if(user_sound!=null){
            user_sound.stop();
        }
        stopSeekBar();
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            MainActivity.client = new Client();
            try {
                FromServer = MainActivity.client.sendString(BuildJson());
                ParseJson(FromServer);
                getscore = true;
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }catch (ParseException e)
            {
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
        }
    };
    //JSON
    //
    private String BuildJson() throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();

            JSONObject arr2 = new JSONObject();
            arr2.put("type", "submit");
            arr2.put("score", current_score);
            arr2.put("scored_ID", mainpage.current_scoredid);
            arr2.put("user_ID", MainActivity.UserID);
            System.out.println(arr2.toString());
            array.put(arr2);

            inf.put("kepa", array);
            System.out.println(array.toString());
            System.out.println(inf.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        System.out.println("\n最终构造的JSON数据格式：");
        System.out.println(inf.toString());

        return inf.toString();
    }

    public void ParseJson(String jsonString) throws JSONException,
            ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");

        int count = 0;
        if(ja.getJSONObject(0).getString("type").equals("submit")){
            submit_score = ja.getJSONObject(0).getDouble("score");
            submit_number = ja.getJSONObject(0).getInt("number");
        }
        System.out.println(submit_score);
    }
    class GetNumber extends Thread{
        @Override
        public void run(){
            JSONObject inf;
            inf = new JSONObject();

            try {
                JSONArray array = new JSONArray();

                JSONObject arr2 = new JSONObject();
                arr2.put("type", "number");
                arr2.put("scored_ID", mainpage.current_scoredid);
                arr2.put("user_ID",MainActivity.UserID);
                System.out.println(arr2.toString());
                array.put(arr2);

                inf.put("kepa", array);
                System.out.println(array.toString());
                System.out.println(inf.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("\n最终构造的JSON数据格式：");
            System.out.println(inf.toString());

            String str = MainActivity.client.sendString(inf.toString());

            try{
                JSONObject jo = new JSONObject(str);
                JSONArray ja = jo.getJSONArray("kepa");

                System.out.println("\n将Json数据解析为Map：");

                if(ja.getJSONObject(0).getString("type").equals("number")){
                    submit_number = ja.getJSONObject(0).getInt("number");
                    already = ja.getJSONObject(0).getInt("already");
                }
                System.out.println(submit_score);
            }catch(JSONException e)
            {
                e.printStackTrace();
            }

            getnumber = true;
        }
    }
}
