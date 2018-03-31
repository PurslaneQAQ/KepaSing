package com.example.kepa.kepasing;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;
import static com.example.kepa.kepasing.MySongsFragment.local_Files;
import static com.example.kepa.kepasing.MySongsFragment.local_song;
import static com.example.kepa.kepasing.MySongsFragment.localsongname;
import static com.example.kepa.kepasing.MySongsFragment.my_song;
import static com.example.kepa.kepasing.MySongsFragment.songid;
import static com.example.kepa.kepasing.MySongsFragment.songnames;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class CenterPageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private CenterAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //public static File[] mysongimages=null;
    public static MediaPlayer mMediaPlayer= null;
    public static  boolean play_music = false;

    public static Timer mTimer;
    public static TimerTask mTask;
    public static SeekBar progressbar;
    public static int mPlayTimeDuration = 1000;//更新时间间隔
    private boolean judgemoney = false;
    private String currentsong_money;
    private  boolean getfos = false;
    public static boolean portrait_finished = false;

    private boolean finished;


    //存放歌名和得分

    private ImageView last;
    public static ImageView play;
    private ImageView next;
    private ContentResolver resolver;
    public static String email;
    public static String nickname;
    private String FromServer;
    private SeekBar progress;
    public static TextView onplaysongname;
//    private PopupWindow mykebpage;
    private String kbNum;

    public static CenterPageFragment newInstance() {
        Bundle args = new Bundle();
        CenterPageFragment pageFragment = new CenterPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        if(mMediaPlayer!= null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        new Thread(runnable).start();
        /*try {
            mysongimageAsset=getContext().getAssets();
            mysongimages=mysongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //我的Ke币 弹窗界面设置
//        View mykebpop=getLayoutInflater(savedInstanceState).inflate(R.layout.mykb,null);
//        TextView mykbnum=(TextView)mykebpop.findViewById(R.id.numberofKB);
//        while(kbNum == null){}
//        mykbnum.setText(kbNum);
//        Button chongzhi=(Button)mykebpop.findViewById(R.id.chongzhi);
//        chongzhi.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //充值
//            }
//        });

//        mykebpage=new PopupWindow(mykebpop, LinearLayout.LayoutParams.MATCH_PARENT,480);
//        mykebpage.setTouchable(true);
//        mykebpage.setOutsideTouchable(true);
//        mykebpage.setBackgroundDrawable(new BitmapDrawable(getResources(),(Bitmap)null));

    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            try {
                FromServer = client.sendString(BuildJson(false));
                File directory = new File(getContext().getExternalFilesDir("img") + "/user/");
                if (!directory.exists())
                    directory.getParentFile().mkdirs();
                FromServer = client.getFile(BuildJson(true) ,directory.toString());
                Log.i("client", FromServer);
                if (!ParseJson(FromServer)) {}
                portrait_finished = true;
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            //FileTransferClient upload  = new FileTransferClient(recentTouxiang);
        }
    };

    private String BuildJson(boolean portrait) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            if(portrait)
                arr2.put("type", "portrait");
            else
                arr2.put("type", "mime");
            arr2.put("user_ID", UserID);
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
    public boolean ParseJson(String jsonString) throws JSONException,
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " +ja.getJSONObject(0).getString("type"));

        if (ja.getJSONObject(0).getString("type").equals("mime")) {
            nickname = ja.getJSONObject(0).getString("nickname");
            email = ja.getJSONObject(0).getString("username");
            kbNum = ja.getJSONObject(0).getString("money");
            return true;
        }
        //Toast.makeText(getActivity().getApplicationContext(), "页面加载失败QwQ", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*页面布局设置*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.center_layout, container, false);
        ImageView icon=(ImageView)view.findViewById(R.id.person);//头像 到时候从服务器上取
        //portrait_finished = false;
        resolver = getActivity().getApplicationContext().getContentResolver();
        while(!portrait_finished){}
        File portrait = new File(getContext().getExternalFilesDir("img").toString()+ "/user/" + UserID + ".png");
        System.out.println("头像为： " + portrait.toString());
        try {
            //Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, Uri.fromFile(portrait));
            //icon.setImageBitmap(bm);

            if(portrait.exists()){
                FileInputStream fis = new FileInputStream(portrait.toString());
                Bitmap bm = BitmapFactory.decodeStream(fis);
                icon.setImageBitmap(bm);
            }
            else
                icon.setImageResource(R.drawable.touxiang);

        }catch (IOException e) {
            e.printStackTrace();
        }

        TextView nickname_Label=(TextView)view.findViewById(R.id.nickname);
        nickname_Label.setText(nickname);//昵称 也从服务器上取

        TextView emailaddr=(TextView)view.findViewById(R.id.emailaddress);
        emailaddr.setText(email);//邮箱地址 服务器上取

        //“我的作品”部分 歌曲item点击事件在mysongsfragment里设置
        pageAdapter = new CenterAdapter(getChildFragmentManager(), view.getContext());
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);
        tabLayout = (TabLayout)view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //下面是页面底部的播放器 需要给按钮设定功能 绑定进度条 还要绑定“我的作品”和“本地作品”里的歌与播放器
        progressbar=(SeekBar)view.findViewById(R.id.seekbar);
        progressbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mMediaPlayer!=null && fromUser){
                    mMediaPlayer.seekTo(progress*1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        last=(ImageView)view.findViewById(R.id.btn_last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}});
        play=(ImageView)view.findViewById(R.id.btn_play);
        next=(ImageView)view.findViewById(R.id.btn_next);

        last.setImageResource(R.drawable.last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(local_song < 0)
                    my_song = play_music(my_song -1);
                else
                    local_song = play_local_music(local_song -1);
            }});
        play.setImageResource(R.drawable.pause);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMediaPlayer != null){
                    if(!play_music){
                        mMediaPlayer.start();
                        play.setImageResource(R.drawable.play);
                        play_music = true;
                    }
                    else{
                        mMediaPlayer.pause();
                        play.setImageResource(R.drawable.pause);
                        play_music = false;
                    }
                }
                else
                    System.out.println("没歌啦！");
            }
        });
        next.setImageResource(R.drawable.next);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(local_song < 0)
                    my_song = play_music(my_song +1);
                else
                    local_song = play_local_music(local_song +1);
            }});
        onplaysongname=(TextView)view.findViewById(R.id.songname);
        //正在播放的歌名
        onplaysongname.setText("无正在播放歌曲");


        //右上角的设置按钮
        final TextView setup=(TextView)view.findViewById(R.id.setup);
        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),setup.class);
                startActivity(intent);
            }
        });

        //我的K币 点击
        TextView myKB=(TextView)view.findViewById(R.id.myKB);
        myKB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                //mykebpage.showAsDropDown(v,0,1);
                LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
                final View kbview = inflater.inflate(R.layout.mykb, null);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Ke币充值")
                        .setMessage("您目前拥有的Ke币："+kbNum)
                        .setView(kbview)
                        .setPositiveButton("充值", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EditText kbnum=(EditText)kbview.findViewById(R.id.kbnum);
                                if(!TextUtils.isEmpty(kbnum.getText())){
                                    int origin = Integer.valueOf(kbNum);
                                    int now = origin+Integer.valueOf(kbnum.getText().toString());
                                    kbNum = String.valueOf(now);
                                    new Charge(kbNum).start();
                                    while(!judgemoney){}
                                    judgemoney = false;
                                    Toast.makeText(getActivity(),"充值"+kbnum.getText()+"Ke币成功！", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getActivity(), "请输入充值数哟", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
            }
        });
        return view;
    }
    class SeekBarUpdate extends TimerTask {
        @Override
        public void run(){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayer!=null) {
                        int timePassed = mMediaPlayer.getCurrentPosition() / 1000;
                        progressbar.setMax(mMediaPlayer.getDuration() / 1000);
                        progressbar.setProgress(timePassed);
                        System.out.println("update Seek Bar");
                    }
                }
            });
        }
    }

    class Charge extends Thread{
        private String money;
        public Charge(String cmoney){
            money = cmoney;
        }
        @Override
        public void run(){

            JSONObject inf;
            inf = new JSONObject();

            try {
                JSONArray array = new JSONArray();

                JSONObject arr2 = new JSONObject();
                arr2.put("type", "charge");
                arr2.put("money",money);
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

            try {
                String FromServer = MainActivity.client.sendString(inf.toString());
                chargemoney(FromServer);
                judgemoney = true;
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }catch (ParseException e){
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");

        }
    }

    public void chargemoney(String jsonString) throws JSONException,
            ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");

        if(ja.getJSONObject(0).getString("type").equals("charge")){
            //currentsong_money = ja.getJSONObject(0).getString("money");
        }
    }

    //player
    public static void stopSeekBar(){
        if(mTimer!=null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    //player
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
        }
        stopSeekBar();
    }

    //chunyao
    class GetSong extends Thread{
        private String songid;
        public GetSong(String id){
            songid = id;
        }
        @Override
        public void run(){
            File file = new File(getContext().getExternalFilesDir("song").toString() + "/" + songid + ".amr");
            System.out.println("The file to load is " +getContext().getExternalFilesDir("song").toString() + "/" + songid + ".amr");
            if(!file.exists()) {
                Log.i("client", "wozhendeyaojinqule");
                try {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(songid), file.getParent());
                    mMediaPlayer.setDataSource(file.toString());
                }catch (JSONException e) {
                    System.out.println("Failed to get file");
                }catch (IOException e) {
                    System.out.println("Can not load the song.");
                }
                Log.i("client", "wotmyijingchulaile");
            }
            finished = true;
        }
    }
    private String BuildJson_GoForSingPage(String song_id) throws JSONException {
        JSONObject inf;
        inf = new JSONObject();
        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "scored_song");
            arr2.put("scored_ID",song_id);
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

    public int play_music(int position){
        local_song = -1;
        if(position >= songid.length) {
            Toast.makeText(getContext(), "已经是最后一首歌啦！",Toast.LENGTH_SHORT).show();
            return (position-1);
        }
        if (position < 0){
            Toast.makeText(getContext(), "已经是第一首歌啦！", Toast.LENGTH_SHORT).show();
            return (position + 1);
        }

        File file = new File(getContext().getExternalFilesDir("song").toString() + "/" + songid[position] + ".amr");
        System.out.println(file.toString());
        if(mMediaPlayer!= null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                //绑定进度条
                if(mTimer == null)
                {
                    mTimer = new Timer();
                    mTask = new CenterPageFragment.SeekBarUpdate();
                    mTimer.scheduleAtFixedRate(mTask,0,mPlayTimeDuration);
                }
            }
        });
        mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mMediaPlayer != null) {
                    mMediaPlayer.stop();
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                stopSeekBar();
            }
        });
        try {
            finished = false;
            GetSong newintent = new GetSong(songid[position]);
            newintent.start();
            while(!finished){}

            try {
                mMediaPlayer.setDataSource(file.toString());
                System.out.println("Setting:" + file.toString());

                try{
                    mMediaPlayer.prepare();
                }catch (IOException e){
                    System.out.println("Can not prepare!");
                }
                mMediaPlayer.start();
                play_music = true;
                play.setImageResource(R.drawable.play);
            }catch (IOException e){
                System.out.println("Failed to load the media.");
            }
            onplaysongname.setText(songnames[position]);
//            user_sound.stop();
        }catch(Exception e) {
            e.printStackTrace();
        }
        return  position;
    }

    public int play_local_music(int position){
        //Toast.makeText(view.getContext(), "播放歌曲", Toast.LENGTH_SHORT).show();
        my_song = -1;

        if(position >= localsongname.length) {
            Toast.makeText(getContext(), "已经是最后一首歌啦！",Toast.LENGTH_SHORT).show();
            return (position-1);
        }
        if (position < 0){
            Toast.makeText(getContext(), "已经是第一首歌啦！", Toast.LENGTH_SHORT).show();
            return (position + 1);
        }

        try {
            if(mMediaPlayer!= null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
            }
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //绑定进度条
                    if (mTimer == null) {
                        mTimer = new Timer();
                        mTask = new CenterPageFragment.SeekBarUpdate();
                        mTimer.scheduleAtFixedRate(mTask, 0, mPlayTimeDuration);
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mMediaPlayer != null) {
                        mMediaPlayer.stop();
                        mMediaPlayer.release();
                        mMediaPlayer = null;
                    }
                    stopSeekBar();
                }
            });
            mMediaPlayer.setDataSource(getContext().getExternalFilesDir("song").toString() + "/" + local_Files.get(position).toString());
            play.setImageResource(R.drawable.play);
            try{
                mMediaPlayer.prepare();
            }catch (IOException e){
                System.out.println("Can not prepare!");
            }
            mMediaPlayer.start();
        } catch (IOException e) {
            System.out.println("Failed to load the media.");
        }
        onplaysongname.setText(localsongname[position]);
        return position;
    }
}