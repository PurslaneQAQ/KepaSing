package com.example.kepa.kepasing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.example.kepa.kepasing.ILrcView;
import com.example.kepa.kepasing.ILrcBuilder;
import com.example.kepa.kepasing.ILrcViewListener;
import com.example.kepa.kepasing.LrcRow;
import com.example.kepa.kepasing.DefaultLrcBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class singasong extends AppCompatActivity {
    private String song_id;
    private String FromServer;
    private static final String Extra_SongName = "com.example.kepa.kepasing.extra_songname";
    public static Intent newIntent(Context packageContext, boolean answerIsTrue){
        Intent i = new Intent(packageContext,singasong.class);
        i.putExtra(Extra_SongName,songName);
        return i;
    }
    private Button StartButton;
    private Button StopButton;
    private Button PlayButton;
    private Button UploadButton;
    private MediaPlayer TrackPlayer;
    private MediaPlayer recordPlayer;
    boolean CreateState;
    //Record
    public static final String TAG = "PCMSample";
    private File file;
    private MediaRecorder mRecorder;
    //lyric
    private String lyric;
    ILrcView mLrcView;//歌词视图
    private int mPlayTimeDuration = 1000;//更新时间间隔
    private Timer mTimer;//定时器
    private TimerTask mTask;//定时任务
    //connect
    private static String songName;
    //upload
    private boolean upload_result = false;
    private boolean upload_end = false;
    private String datetime;
    private boolean tipagain = false;
    //playbutton
    private int playorpause = 0;
    private boolean startfist = true;

    public void setSong_id(String str){
        song_id = str;
    }

    ArrayList<String> passedsonginfo=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sing_asong);
        song_id = mainpage.current_songid;
        System.out.println(song_id);
        file = new File(getExternalFilesDir(null)+"/song/"+song_id+MainActivity.UserID+".amr");
//        file = new File(getExternalFilesDir(null)+"/song/"+song_id+MainActivity.UserID+".aac");
        mLrcView = (ILrcView)findViewById(R.id.lrcView);
        //add lyrics
        //改路径
        lyric = getFile(mainpage.current_songid+".lrc");
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lyric);
        mLrcView.setLrc(rows);

        //add record
//       newRecord.startRecord();

        //设置自定义的LrcView上下拖动歌词时监听
        mLrcView.setListener(new ILrcViewListener() {
            public void onLrcSeeked(int newPosition, LrcRow row){
                if (TrackPlayer != null) {
                    TrackPlayer.seekTo((int) row.time);
                }
            }
        });
        StartButton = (Button)findViewById(R.id.start_button);
        StartButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                if(TrackPlayer!=null){
                    TrackPlayer.stop();
                    TrackPlayer.release();
                    TrackPlayer=null;
//                    newRecord.stopRecording();
                }
                if(mRecorder!=null)
                {
                    mRecorder.stop();
                    mRecorder.release();
                    //mRecorder = null;
                }
                BeginRecord();
                StartButton.setText("重录");
                StopButton.setEnabled(true);
            }
        });
        //stopbutton, used to stop the track and save user's audio.
        StopButton = (Button)findViewById(R.id.stop_button);
        StopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopLrcPlay();
                //Track
                if(TrackPlayer!=null){
                    TrackPlayer.stop();
                    TrackPlayer.release();
                    TrackPlayer=null;
//                    newRecord.stopRecording();
                }
                if (mRecorder != null) {
                    mRecorder.stop();
                    System.out.println("stopped******************************");
                    mRecorder.release();
                    System.out.println("release**********************");
//                mRecorder = null;
                }
                Toast.makeText(singasong.this, "QwQ已保存本地录音~", Toast.LENGTH_SHORT).show();
                StopButton.setEnabled(false);
            }
        });

        //playbutton, used to stop the track and save user's audio.
        PlayButton = (Button)findViewById(R.id.play_button);
        PlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playorpause += 1;
                if(playorpause==1) {
                    PlayRecord();
                    PlayButton.setText("暂停");
                }
                else if(playorpause%2==0){
                    if(recordPlayer!=null) recordPlayer.pause();
                    PlayButton.setText("播放");
                }
                else{
                    if(recordPlayer!=null) recordPlayer.start();
                    PlayButton.setText("暂停");
                }
            }
        });

        UploadButton = (Button)findViewById(R.id.upload_button);
        UploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
                Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                datetime = formatter.format(curDate);
                new Thread(runnable).start();
                while(!upload_end){}
                //upload_end = false;
                if(tipagain)
                {
                    tipagain = false;
                    new AlertDialog.Builder(singasong.this)
                            .setTitle("来自奇葩君的确认")
                            .setMessage("您已经上传过这首歌咯，确定要覆盖吗？")
                            .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new Thread(uploadrunnable).start();
                                    upload_result = true;
                                }
                            })
                            .setNegativeButton("否", null)
                            .show();

                }
                while(!upload_end){}
                upload_end = false;
                if(!upload_result) Toast.makeText(singasong.this, "QAQ上传失败……", Toast.LENGTH_SHORT).show();
                else{
                    Toast.makeText(singasong.this, "QwQ成功上传~", Toast.LENGTH_SHORT).show();
                    file.delete();
                }
            }
        });

        passedsonginfo=(ArrayList<String>)getIntent().getStringArrayListExtra("Songinfos");
        //加上歌曲id
        songName = getIntent().getStringExtra(Extra_SongName);
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

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if(TrackPlayer!=null){
            TrackPlayer.stop();
            TrackPlayer.release();
            TrackPlayer = null;
        }
    }

    //start
    public void BeginRecord(){
        //add track
        CreateState=false;
        if(TrackPlayer==null){
            TrackPlayer=createMP3();
            CreateState=true;
        }
        try{
            TrackPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    if(mTimer == null)
                    {
                        mTimer = new Timer();
                        mTask = new singasong.LrcTask();
                        mTimer.scheduleAtFixedRate(mTask,0,mPlayTimeDuration);
                    }
                }
            });
            TrackPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                    stopLrcPlay();
                    StopButton.setEnabled(false);
                }
            });
            if(CreateState) TrackPlayer.prepare();
            TrackPlayer.start();

        }catch(IllegalStateException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        try{
            if(startfist) {
                mRecorder = new MediaRecorder();
                startfist = false;
            }
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

//            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
//            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            File dir = new File(getExternalFilesDir(null)+"/song");
            boolean result;
            if(!dir.exists()) {
                result = dir.mkdirs();
                if(result) System.out.println("成功创建文件夹");
                else System.out.println("创建文件夹失败");
            }
            if(file.exists()){
                System.out.println("文件存在 删除");
                file.delete();
            }

            mRecorder.setOutputFile(file.getAbsolutePath());
            mRecorder.prepare();
            mRecorder.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //load track
    public MediaPlayer createMP3(){

        File mp3file = new File(getExternalFilesDir(null)+"/mp3/"+song_id+".mp3");
        MediaPlayer mp = new MediaPlayer();
        try{

            System.out.println(mp3file.getAbsolutePath());
            mp.setDataSource(mp3file.getAbsolutePath());
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return mp;
    }

    private void PlayRecord() {
        try {
            //file.getA..
            recordPlayer = new MediaPlayer();
            recordPlayer.setDataSource(file.getAbsolutePath());
            recordPlayer.prepare();
            recordPlayer.start();
            recordPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (recordPlayer != null) {
                        recordPlayer.stop();
                        recordPlayer.release();
                        recordPlayer = null;
                    }
                    PlayButton.setText("重播");
                    playorpause = 0;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getFile(String fileName){
        int flag = 1;
        String line = "";
        String result = "";
        try{
            File lyricfile = new File(getExternalFilesDir(null)+"/lrc/"+fileName);
            InputStreamReader inputReader = new InputStreamReader(new FileInputStream(lyricfile),"GB2312");
            BufferedReader bufReader = new BufferedReader(inputReader);
            while((line = bufReader.readLine())!=null){
                if(line.trim().equals(""))
                    continue;
                result+=line+"\r\n";  //将lyric文件读入string result中，以换行符连接
                System.out.println(line);
            }
        }catch(Exception e)
        {
            e.printStackTrace();
            flag = 0;
        }
        if(flag==1){
            return result;
        }
//        else{
        return "";
    }

    public void stopLrcPlay(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    class LrcTask extends TimerTask{
        @Override
        public void run(){
            final long timePassed = TrackPlayer.getCurrentPosition();
            singasong.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLrcView.seekLrcToTime(timePassed);
                }
            });
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            upload_result = false;

            try {
                FromServer = MainActivity.client.sendString(BuildJson());
                Log.i("client", FromServer);
                upload_result = ParseJson(FromServer,1);
                if(!upload_result){
                    System.out.println("send message failed");
                    upload_end = true;
                }
                else
                {
                    try{
                        MainActivity.client.sendFile(file.getAbsolutePath());
                        System.out.println();
                        upload_end = true;
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
                Log.i("client", "success");
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            } catch (ParseException e) {
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
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "upload");
            arr2.put("song_ID", song_id);
            arr2.put("user_ID", MainActivity.UserID);
            arr2.put("score", "0");
            arr2.put("date", datetime);
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
    private String upload2() throws JSONException {
        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "upload2");
            arr2.put("song_ID", song_id);
            arr2.put("user_ID", MainActivity.UserID);
            arr2.put("date", datetime);
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

    Runnable uploadrunnable= new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            upload_result = false;

            try {
                MainActivity.client.sendString(upload2());
                try{
                    MainActivity.client.sendFile(file.getAbsolutePath());
                    System.out.println("传输歌曲文件");
                    upload_end = true;
                }catch(Exception e){
                    e.printStackTrace();
                }
                Log.i("client", "success");
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");

//                FileTransferClient upload  = new FileTransferClient(file);
        }
    };

    public boolean ParseJson(String jsonString,int i) throws JSONException,
            ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");

        int count = 0;
        if(i==1){
            if(ja.getJSONObject(0).getString("type").equals("upload")){
                if(ja.getJSONObject(0).getString("result").equals("true")) {
                    return true;
                }
                else
                {
                    tipagain = true;
                }
            }
            System.out.println(count);
            return false;
        }
        else{
            if(ja.getJSONObject(0).getString("type").equals("upload_result")){
                if(ja.getJSONObject(0).getString("result").equals("true")) {
                    System.out.println("result"+ja.getJSONObject(0).getString("result"));
                    return true;
                }
            }
            System.out.println(count);
            return false;
        }

    }

}