package com.example.kepa.kepasing;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.kepa.kepasing.MainActivity.client;

public class asingerssong extends AppCompatActivity {
    ArrayList<String> passedsingerinfo=new ArrayList<String>();
    //存放这个歌手名下的歌曲名数组
    private boolean finished = false;
    private String[] songid = null;
    String[] songname=null;

    //歌曲图片 到时候获取方式要改
    String[] newsongimages=null;
    AssetManager newsongimageAsset=null;
    private String FromServer;

    //下载歌曲的进度条框框
    private ProgressDialog progressDialog;
    private int count;//测试进度条用
    private Timer downloadtimer;
    private TimerTask downloadtask;
    private int mPlayTimeDuration = 1000;

    //zyt
    private boolean judgebuy = false;
    private boolean currentsong_buy = false;
    private boolean getfos = false;

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                FromServer = client.sendString(BuildJson());
                Log.i("client", FromServer);
                ParseJson(FromServer);
                File directory = new File(getCacheDir() + "/img/");
                if (!directory.exists())
                    directory.mkdirs();
                for(int i = 0; i < songid.length; i++){
                    if(!(new File(directory.toString() + songid[i] + ".png").exists()))
                        client.getFile(getSongPic(i), directory.toString());
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
        }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asingerssong);
        //页面头部信息 接收由choosesinger页面传过来的歌手名
        passedsingerinfo=(ArrayList<String>)getIntent().getStringArrayListExtra("Singerinfos");
        ImageView gobackbtn=(ImageView)findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        TextView singername=(TextView)findViewById(R.id.singername);
        singername.setText(passedsingerinfo.get(0));

        //ListView内容绑定
        ListView lv=(ListView)findViewById(R.id.songs);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();
        new Thread(runnable).start();
        while(!finished){}
        if(songid != null){
            for(int i=0;i<songid.length;i++){
                HashMap<String,Object>map=new HashMap<String,Object>();
                map.put("ItemImage",getBitmap(i));
                map.put("ItemTitle",songname[i]);
                map.put("button","演 唱");
                listItem.add(map);
            }
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(
                this,
                listItem,
                R.layout.asong_layout,
                new String[]{"ItemImage","ItemTitle","button"},
                new int[]{R.id.ItemImage,R.id.ItemTitle,R.id.button}) {

            @Override
            public View getView(int position, View convertView,ViewGroup parent) {
                final View view=super.getView(position, convertView, parent);
                final int now_position = position;
                Button button=(Button)view.findViewById(R.id.button);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new IfBuy(songid[now_position]).start();
                        while(!judgebuy){}
                        judgebuy = false;
                        if(currentsong_buy)
                        {
                            System.out.println("gou mai le ");
                            //进度条框框
                            if(progressDialog == null)
                            {
                                System.out.println("mdjindutiaonigeilaozichulai*********************************************************");
                                progressDialog=new ProgressDialog(view.getContext());
                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                progressDialog.setTitle("正在下载，请稍等");
                                progressDialog.setIndeterminate(false);
                                progressDialog.setCancelable(true);
                                progressDialog.setMax(100);
                                progressDialog.setProgress(0);
                                progressDialog.show();
                                System.out.println("mdjindutiaochulailema***************************************************************");
                            }
                            if(downloadtimer==null)
                            {
                                downloadtimer = new Timer();
                                System.out.println("****************timer*************");
                                downloadtask = new DownloadTask();
                                System.out.println("****************task**************");
                                downloadtimer.scheduleAtFixedRate(downloadtask,0,mPlayTimeDuration);
                                System.out.println("****************settime***********");
                            }
                            //加线程？？？进度条用的
                            System.out.println("****************goforsingpage*************");
                            GoForSingPage newintent = new GoForSingPage(now_position);
                            newintent.start();
                            //zyt
                            File file = new File(getExternalCacheDir().toString()+"/img/"+songid[now_position]+".png");
                            File newfile = new File(getExternalFilesDir("img").toString()+songid[now_position]+".png");
                            file.renameTo(newfile);
                            mainpage.current_songid = songid[now_position];
                            while(!getfos){}
                            TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                            final ArrayList<String> songname=new ArrayList<String>();
                            songname.add(tv.getText().toString());
                            if(downloadtimer!=null){
                                System.out.println("****************settimer==null************");
                                downloadtimer.cancel();
                                downloadtimer = null;
                            }
                            if(progressDialog!=null)
                            {
                                System.out.println("****************setprogress==null*************");
                                progressDialog.cancel();
                                progressDialog = null;
                            }
                            System.out.println("set getfos false");
                            getfos = false;

                            Intent intent=new Intent(view.getContext(),singasong.class);
                            intent.putStringArrayListExtra("Songinfos",songname);
                            startActivity(intent);
                        }
                        else{
                            System.out.println("mei you gou mai");
                            new AlertDialog.Builder(view.getContext())
                                    .setTitle("来自奇葩君的确认")
                                    .setMessage("您确定要花10个Ke币购买这首歌吗？")
                                    .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(view.getContext(), "购买成功！Ke币 -10", Toast.LENGTH_SHORT).show();

                                            //origin
                                            //进度条框框
                                            if(progressDialog == null)
                                            {
                                                System.out.println("mdjindutiaonigeilaozichulai*********************************************************");
                                                progressDialog=new ProgressDialog(view.getContext());
                                                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                                                progressDialog.setTitle("正在下载，请稍等");
                                                progressDialog.setProgress(100);
                                                progressDialog.setIndeterminate(false);
                                                progressDialog.setCancelable(true);
                                                progressDialog.show();
                                                System.out.println("mdjindutiaochulailema***************************************************************");
                                            }
                                            if(downloadtimer==null)
                                            {
                                                downloadtimer = new Timer();
                                                System.out.println("****************timer*************");
                                                downloadtask = new DownloadTask();
                                                System.out.println("****************task**************");
                                                downloadtimer.scheduleAtFixedRate(downloadtask,0,mPlayTimeDuration);
                                                System.out.println("****************settime***********");
                                            }
                                            //加线程？？？进度条用的
                                            System.out.println("****************goforsingpage*************");
                                            GoForSingPage newintent = new GoForSingPage(now_position);
                                            newintent.start();
                                            //zyt
                                            File file = new File(getExternalCacheDir()+"/img/"+songid[now_position]+".png");
                                            File newfile = new File(getExternalFilesDir(null)+"/img/"+songid[now_position]+".png");
                                            file.renameTo(newfile);
                                            mainpage.current_songid = songid[now_position];
                                            while(!getfos){}
                                            TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                                            final ArrayList<String> songname=new ArrayList<String>();
                                            songname.add(tv.getText().toString());
                                            if(downloadtimer!=null){
                                                System.out.println("****************settimer==null************");
                                                downloadtimer.cancel();
                                                downloadtimer = null;
                                            }
                                            if(progressDialog!=null)
                                            {
                                                System.out.println("****************setprogress==null*************");
                                                progressDialog.cancel();
                                                progressDialog = null;
                                            }
                                            System.out.println("set getfos false");
                                            getfos = false;

                                            Intent intent=new Intent(view.getContext(),singasong.class);
                                            intent.putStringArrayListExtra("Songinfos",songname);
                                            startActivity(intent);

                                        }
                                    })
                                    .setNegativeButton("否", null)
                                    .show();
                        }
                    }

                });
                return view;
            }
        };
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
        lv.setAdapter(simpleAdapter);
        lv.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
//        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
//                final ArrayList<String> songname=new ArrayList<String>();
//                songname.add(tv.getText().toString());
//                Intent intent=new Intent(parent.getContext(),singasong.class);
//                intent.putStringArrayListExtra("Songinfos",songname);
//                startActivity(intent);
//            }
//        });

    }

    //zyt

    class DownloadTask extends TimerTask {
        @Override
        public void run(){
            final int timePassed = mainpage.processdownload;
//            mainpage activity = (mainpage)getActivity();
//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            progressDialog.setMax(100);
            progressDialog.setProgress(timePassed);
//                }
//            });
        }
    }
    class IfBuy extends Thread{
        private String song_ID;
        public IfBuy(String songid){
            song_ID = songid;
        }
        @Override
        public void run(){

            JSONObject inf;
            inf = new JSONObject();

            try {
                JSONArray array = new JSONArray();

                JSONObject arr2 = new JSONObject();
                arr2.put("type", "ifbuy");
                arr2.put("song_ID",song_ID);
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
                ParseJson(FromServer);
                judgebuy = true;
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }catch (ParseException e){
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");

        }
    }
    //图片转bitmap
    public Bitmap getBitmap(int i){
        Bitmap mBitmap = null;
        InputStream assetFile=null;
        ContentResolver resolver = getContentResolver();
        File songFile = new File(getCacheDir() + "/img/" + songid[i] + ".png");
        try{
            mBitmap = MediaStore.Images.Media.getBitmap(resolver, Uri.fromFile(songFile));
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
    }

    private String BuildJson() throws JSONException {
        JSONObject inf;
        inf = new JSONObject();
        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "A_singer");
            passedsingerinfo = (ArrayList<String>)getIntent().getStringArrayListExtra("Singerinfos");
            arr2.put("singer_ID",passedsingerinfo.get(1).toString());
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

    private String getSongPic(int i) throws JSONException {
        JSONObject inf;
        inf = new JSONObject();
        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "picture_song");
            arr2.put("song_ID",songid[i]);
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
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " + ja.getJSONObject(0).getString("type"));

        if (ja.getJSONObject(0).getString("type").equals("A_singer")) {
            songid = new String[ja.length() - 1];
            songname = new String[ja.length() - 1];
            for(int i = 1; i < ja.length(); i++){
                System.out.println("\n song_ID: " +  ja.getJSONObject(i).getString("song_ID"));
                System.out.println(" song_name: " +  ja.getJSONObject(i).getString("song_name"));
                songid[i-1] = ja.getJSONObject(i).getString("song_ID");
                songname[i-1] = ja.getJSONObject(i).getString("song_name");
            }
        }
        if(ja.getJSONObject(0).getString("type").equals("ifbuy"))
        {
            currentsong_buy = ja.getJSONObject(0).getBoolean("result");
        }
//        else Toast.makeText(asingerssong.this, "加载失败了QwQ请再试一次吧！", Toast.LENGTH_SHORT).show();
    }
    class GoForSingPage extends Thread{
        private int position;
        public GoForSingPage(int position){
            this.position = position;
        }
        @Override
        public void run(){
            Log.i("client", "wozhendeyaojinqule");
            try {
                if(!currentsong_buy)
                    MainActivity.client.sendString(BuildJson_GoForSingPage(0,songid[position]));
                File file = new File(getExternalFilesDir(null).toString()+ "/lrc/"+ songid[position] + ".lrc");
                if(!file.exists()) {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(1,songid[position]),getExternalFilesDir(null).toString() +"/lrc");
                }
                System.out.println("getfile" + songid[position]);
                File file_sound = new File(getExternalFilesDir(null).toString()+ "/mp3/"+ songid[position] + ".mp3");
                if(!file_sound.exists()) {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(2,songid[position]),getExternalFilesDir(null).toString()+ "/mp3");
                }
                System.out.println("getfile" + songid[position] + "mp3");
                getfos = true;
//                Log.i("client", FromServer);
                //                ParseJson(FromServer);
                //                Log.i("client", "success");
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
        }
    }

    private String BuildJson_GoForSingPage(int i, String song_id) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            if(i==0)//purchase
            {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "buy");
                arr2.put("song_ID",song_id);
                arr2.put("user_ID",MainActivity.UserID);
                System.out.println(arr2.toString());
                array.put(arr2);
            }
            if(i==1)
            {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "sing_lyric");
                arr2.put("song_ID",song_id);
                System.out.println(arr2.toString());
                array.put(arr2);
            }
            else if(i == 2) {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "sing_mp3");
                arr2.put("song_ID",song_id);
                System.out.println(arr2.toString());
                array.put(arr2);
            }
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
}