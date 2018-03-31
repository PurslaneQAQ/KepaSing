package com.example.kepa.kepasing;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.kepa.kepasing.CenterPageFragment.mMediaPlayer;
import static com.example.kepa.kepasing.CenterPageFragment.mPlayTimeDuration;
import static com.example.kepa.kepasing.CenterPageFragment.mTask;
import static com.example.kepa.kepasing.CenterPageFragment.mTimer;
import static com.example.kepa.kepasing.CenterPageFragment.play;
import static com.example.kepa.kepasing.CenterPageFragment.play_music;
import static com.example.kepa.kepasing.CenterPageFragment.onplaysongname;
import static com.example.kepa.kepasing.CenterPageFragment.portrait_finished;
import static com.example.kepa.kepasing.CenterPageFragment.progressbar;
import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class MySongsFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    public static int local_song = -1;
    public static int my_song = -1;
    private int mPage;
    private String FromServer;
    public static String[] songid = null;
    private boolean finished = false;
    /**********************
     *  String[] songid=new String[]{
     "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
     "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
     };*/

    public static String[] songnames = null;
    /*****************
     * String[] songnames = new String[]{
     "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
     "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
     };*/
    private String[] scores = null;
    /********************
     * String[] scores=new String[]{
     "5.0","1.2","3.8","6.7","9.8","6.4","7.2",
     "6.5","8.1"
     };*/

    //存放本地作品
    public static String[] localsongname=null;
    /***************new String[]{
     "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
     "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
     };*/

    public static List<String> local_Files = null;

    /*歌曲图片*/
    //String[] songimages=null;
    File songimage;
    //AssetManager songimageAsset=null;
    /*************************************/
    public static MySongsFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        MySongsFragment pageFragment = new MySongsFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }


    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            client = new Client();
            try {
                FromServer = client.sendString(BuildJson());
                Log.i("client", FromServer);
                if (!ParseJson(FromServer)) {
                    //Toast.makeText(getActivity().getApplicationContext(), "页面加载失败QwQ", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                System.out.println("parse json failed");
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            //FileTransferClient upload  = new FileTransferClient(recentTouxiang);
        }
    };

    private String BuildJson() throws JSONException {

        JSONObject inf;
        inf = new JSONObject();
        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "mime_song");
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
        System.out.println("type: " +
                "" + ja.getJSONObject(0).getString("type"));
        System.out.println("\n songid_length:" + ja.length());
        if (ja.getJSONObject(0).getString("type").equals("mime_song")) {
            int length = ja.length() -1;
            songid = new String[length];
            songnames = new String[length];
            scores = new String[length];
            for(int i = 1; i < ja.length(); i++){
                songid[i -1] = ja.getJSONObject(i).getString("song_ID");
                songnames[i -1] = ja.getJSONObject(i).getString("song_name");
                scores[i -1] = ja.getJSONObject(i).getString("score");
                System.out.println("\n songid:" + songid[i - 1]);
            }
            /*File directory = new File(getExternalCacheDir(), "/media/cover/");
            for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                client.getFile(songid[i] +".jpg",directory.toString());
                mysongimages[i] = new File(directory.toString() + songid[i] +".png");
            }*/
            return true;
        }
        return false;
    }

    Runnable getSongNames = new Runnable() {
        @Override
        public void run() {
            try {
                for(int i = 0; i < local_Files.size(); i++) {
                    JSONObject inf;
                    inf = new JSONObject();
                    try {
                        //inf.put("number", );
                        JSONArray array = new JSONArray();
                        JSONObject arr2 = new JSONObject();
                        arr2.put("type", "song_info");
                        arr2.put("song_ID",local_Files.get(i).toString().substring(0,5));
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
                    FromServer = client.sendString(inf.toString());
                    Log.i("client", FromServer);
                    localsongname[i] = ParseName(FromServer);
                }
                finished = true;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(runnable).start();
        if(mMediaPlayer!= null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        File local = getContext().getExternalFilesDir("song");
        if(!local.exists()){
            local.mkdirs();
        }
        local_Files = getFileList(local);
        mPage = getArguments().getInt(ARG_PAGE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mysongs_layout, container, false);
        GridView gv=(GridView)view.findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>> listItem=new ArrayList<HashMap<String, Object>>();
        while (!portrait_finished){}

        switch(mPage){
            case 1://我的作品
                while(songid ==null){}
                for(int i=0;i<songid.length;i++) {
                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemImage", getBitmap(i));
                    map.put("ItemTitle", songnames[i]);
                    map.put("ItemScore", scores[i]);
                    listItem.add(map);
                    SimpleAdapter simpleAdapter = new SimpleAdapter(getContext(), listItem, R.layout.item_mysong,
                            new String[]{"ItemImage", "ItemTitle", "ItemScore"}, new int[]{R.id.ItemImage, R.id.ItemTitle, R.id.ItemScore});
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if (view instanceof ImageView && data instanceof Bitmap) {
                                ImageView iv = (ImageView) view;
                                iv.setImageBitmap((Bitmap) data);
                                return true;
                            } else {
                                return false;
                            }
                        }
                    });
                    gv.setAdapter(simpleAdapter);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            my_song = play_music(position);
                        }
                    });
                }

                break;
            case 2://本地作品
                localsongname = new String[local_Files.size()];
                finished = false;
                new Thread(getSongNames).start();
                while(!finished){}
                for(int i=0;i<local_Files.size();i++){
                    HashMap<String,Object>map=new HashMap<String,Object>();
                    map.put("ItemImage",getBitmap(i));
                    map.put("ItemTitle",localsongname[i]);
                    listItem.add(map);
                    SimpleAdapter simpleAdapter=new SimpleAdapter(getContext(),listItem,R.layout.item_alocalsong,
                            new String[]{"ItemImage","ItemTitle"},new int[]{R.id.ItemImage,R.id.ItemTitle});
                    simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue(View view, Object data, String textRepresentation) {
                            if(view instanceof ImageView && data instanceof Bitmap){
                                ImageView iv=(ImageView)view;
                                iv.setImageBitmap((Bitmap)data);
                                return true;
                            }
                            else{
                                return false;
                            }
                        }
                    });
                    gv.setAdapter(simpleAdapter);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            //点击播放
                            local_song = play_local_music(position);
                        }
                    });
                }
                break;
            default:
                break;
        }
        return view;
    }

    public String ParseName(String jsonString) throws JSONException,
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " +
                "" + ja.getJSONObject(0).getString("type"));
        if (ja.getJSONObject(0).getString("type").equals("song_info")) {
            return ja.getJSONObject(0).getString("songname");
        }
        return null;
    }

    public static List<String> getFileList(File file) {
        List<String> result = new ArrayList<String>();
        if (!file.isDirectory()) {
            System.out.println(file.getAbsolutePath());
            result.add(file.getAbsolutePath());
        }
        else {
            // 内部匿名类，用来过滤文件类型
            File[] directoryList = file.listFiles(new FileFilter() {
                public boolean accept(File file) {
                    if (file.isFile() && (file.getName().indexOf("amr") > -1 || file.getName().indexOf("mp3") > -1)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            for (int i = 0; i < directoryList.length; i++) {
                result.add(directoryList[i].getName());
                System.out.println("Local file:" + result.get(i));
            }
        }
        return result;
    }

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

    //图片转bitmap
    public Bitmap getBitmap(int i){
        Bitmap mBitmap = null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        ContentResolver resolver = getContext().getContentResolver();
        try {
            System.out.println(getContext().getExternalFilesDir("img").toString() +  "/user/" + UserID + ".png");
            File Touxiang =new File(getContext().getExternalFilesDir("img").toString() + "/user/" + UserID + ".png");
            if(Touxiang.exists()) {
                FileInputStream fis = new FileInputStream(Touxiang.toString());
                mBitmap = BitmapFactory.decodeStream(fis);
            }
            else
                mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.touxiang);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    class SeekBarUpdate extends TimerTask {
        @Override
        public void run(){
             getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mMediaPlayer!=null) {
                        int timePassed =mMediaPlayer.getCurrentPosition() / 1000;
                        progressbar.setMax(mMediaPlayer.getDuration() / 1000);
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
    public void onDestroy(){
        super.onDestroy();
        if(mMediaPlayer!=null){
            mMediaPlayer.stop();
        }
        stopSeekBar();
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
                    mTask = new MySongsFragment.SeekBarUpdate();
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
                        mTask = new SeekBarUpdate();
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