package com.example.kepa.kepasing;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class NewSongandTopFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    //判断换页面的时候传完了没有
    private boolean getfos = false;
    //存放新歌歌名和歌手名
    String[] topsingername;
    String[] topsongname;
    double[] topscores;
    String[] singername;
    String[] songnames;
    String[] songids;
    String[] userids;
    String[] topscoredids;
    private int count;
    /*新歌界面的歌曲图片 先用本地的顶着了*/
    String[] newsongimages=null;
    private boolean files_all_get = false;
    private boolean First_Come = true;
    //下载歌曲进度条
    private ProgressDialog progressDialog;
    private Timer downloadtimer;
    private TimerTask downloadtask;
    private int mPlayTimeDuration = 1000;
    private boolean judgebuy = false;
    private boolean currentsong_buy = false;
    private boolean ifbuy = false;
    private boolean endalert = false;
    /*************************************/
    public static NewSongandTopFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        NewSongandTopFragment pageFragment = new NewSongandTopFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        //zyt
        System.out.println("get messages");
        mainpage activity = (mainpage)getActivity();
        singername = activity.getNew_singers();
        songnames = activity.getNew_songnames();
        topsongname = activity.getRank_songnames();
        topsingername = activity.getRank_usernicks();
        topscores = activity.getRank_scores();
        userids = activity.getRank_userids();
        songids = activity.getNew_songids();
        topscoredids = activity.getRank_scordids();
        //Rank的歌曲id
        //  System.out.println(songids[0]+"1");

//        new Thread(runnable).start();


    }
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            try {
                for(int i = 0; i<mainpage.new_count;i++)
                {
                    System.out.println(songids[i]+"2"+i);
                    File file1 = new File(getContext().getExternalCacheDir()+"/img/"+songids[i]+".png");
                    System.out.println(file1.toString());
                    if(!file1.exists())
                    {
                        System.out.println("want to get picture");
                        MainActivity.client.getFile(BuildJson(0,songids[i]),getContext().getExternalCacheDir().toString()+"/img");
                    }
                    System.out.println("getfile"+songids[i]+i);
                }
                for(int i = 0; i<mainpage.top_count;i++)
                {
                    System.out.println(songids[i]+"2"+i);

                    File file2 = new File(getContext().getExternalCacheDir()+"/img/"+userids[i]+".png");
                    System.out.println(file2.toString());
                    if(!file2.exists())
                    {
                        System.out.println("want to get picture");
                        MainActivity.client.getFile(BuildJson(1,userids[i]),getContext().getExternalCacheDir().toString()+"/img");
                    }
                    System.out.println("getfile"+userids[i]+i);
                }
                files_all_get = true;
                System.out.println("files_all_get = "+files_all_get);
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newsongandtop_layout, container, false);

        ListView lv=(ListView)view.findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();
        System.out.println(songids[0]+"3");
////
//        progressDialog=new ProgressDialog(view.getContext());
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//        progressDialog.setTitle("正在下载，请稍等");
//        progressDialog.setIndeterminate(false);
//        progressDialog.setCancelable(true);

//        while(!files_all_get){}
//        System.out.println("set files_all_get false");
//        files_all_get = false;
//        System.out.println(songnames[0]);
//        System.out.println(singername[1]);

        switch(mPage){
            case 1://新歌
                new Thread(runnable).start();
                while(!files_all_get){}
                System.out.println("set files_all_get false");
                files_all_get = false;
                for(int i=0;i<mainpage.new_count;i++){
                    HashMap<String,Object>map=new HashMap<String,Object>();
                    map.put("ItemImage",getBitmap("new",i));
                    map.put("ItemTitle",songnames[i]);
                    map.put("ItemText",singername[i]);
                    map.put("button","演 唱");

                    listItem.add(map);
                    SimpleAdapter simpleAdapter=new SimpleAdapter(
                            getContext(),
                            listItem,
                            R.layout.item_asong,
                            new String[]{"ItemImage","ItemTitle","ItemText","button"},
                            new int[]{R.id.ItemImage,R.id.ItemTitle,R.id.ItemText,R.id.button})
                    {

                        @Override
                        public View getView(final int position, final View convertView, ViewGroup parent) {
                            final View view=super.getView(position, convertView, parent);
                            final int now_position = position;
                            final Button button=(Button)view.findViewById(R.id.button);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    new IfBuy(songids[now_position]).start();
                                    while(!judgebuy){}
                                    judgebuy = false;
                                    if(currentsong_buy)
                                    {

                                        System.out.println("gou mai le ");
                                        //进度条框框
//                                        if(progressDialog == null)
//                                        {
//                                            System.out.println("mdjindutiaonigeilaozichulai*********************************************************");
//                                            progressDialog=new ProgressDialog(getActivity());
//                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                                            progressDialog.setTitle("正在下载，请稍等");
//                                            progressDialog.setIndeterminate(false);
//                                            progressDialog.setCancelable(true);
//                                            progressDialog.setMax(100);
//                                            progressDialog.setProgress(0);
//                                            progressDialog.show();
//                                            System.out.println("mdjindutiaochulailema***************************************************************");
//                                        }
//                                        if(downloadtimer==null)
//                                        {
//                                            downloadtimer = new Timer();
//                                            System.out.println("****************timer*************");
//                                            downloadtask = new DownloadTask();
//                                            System.out.println("****************task**************");
//                                            downloadtimer.scheduleAtFixedRate(downloadtask,0,mPlayTimeDuration);
//                                            System.out.println("****************settime***********");
//                                        }
                                        //加线程？？？进度条用的
                                        System.out.println("****************goforsingpage*************");
                                        GoForSingPage newintent = new GoForSingPage(now_position);
                                        newintent.start();
                                        //zyt
                                        File file = new File(getContext().getExternalCacheDir()+"/img/"+songids[now_position]+".png");
                                        File newfile = new File(getContext().getExternalFilesDir(null)+"/img/"+songids[now_position]+".png");
                                        file.renameTo(newfile);
                                        mainpage.current_songid = songids[now_position];
                                        while(!getfos){}
                                        TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                                        final ArrayList<String> songname=new ArrayList<String>();
                                        songname.add(tv.getText().toString());
//                                        if(downloadtimer!=null){
//                                            System.out.println("****************settimer==null************");
//                                            downloadtimer.cancel();
//                                            downloadtimer = null;
//                                        }
//                                        if(progressDialog!=null)
//                                        {
//                                            System.out.println("****************setprogress==null*************");
//                                            progressDialog.cancel();
//                                            progressDialog = null;
//                                        }
                                        System.out.println("set getfos false");
                                        getfos = false;

                                        Intent intent=new Intent(view.getContext(),singasong.class);
                                        intent.putStringArrayListExtra("Songinfos",songname);
                                        startActivity(intent);
                                    }
                                    else{
                                        System.out.println("mei you gou mai");
                                        new AlertDialog.Builder(getActivity())
                                                .setTitle("来自奇葩君的确认")
                                                .setMessage("您确定要花10个Ke币购买这首歌吗？")
                                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Toast.makeText(getActivity(), "购买成功！Ke币 -10", Toast.LENGTH_SHORT).show();

                                                        //origin
                                                        //进度条框框
//                                                        if(progressDialog == null)
//                                                        {
//                                                            System.out.println("mdjindutiaonigeilaozichulai*********************************************************");
//                                                            progressDialog=new ProgressDialog(getActivity());
//                                                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                                                            progressDialog.setTitle("正在下载，请稍等");
//                                                            progressDialog.setProgress(100);
//                                                            progressDialog.setIndeterminate(false);
//                                                            progressDialog.setCancelable(true);
//                                                            progressDialog.show();
//                                                            System.out.println("mdjindutiaochulailema***************************************************************");
//                                                        }
//                                                        if(downloadtimer==null)
//                                                        {
//                                                            downloadtimer = new Timer();
//                                                            System.out.println("****************timer*************");
//                                                            downloadtask = new DownloadTask();
//                                                            System.out.println("****************task**************");
//                                                            downloadtimer.scheduleAtFixedRate(downloadtask,0,mPlayTimeDuration);
//                                                            System.out.println("****************settime***********");
//                                                        }
                                                        //加线程？？？进度条用的
                                                        System.out.println("****************goforsingpage*************");
                                                        GoForSingPage newintent = new GoForSingPage(now_position);
                                                        newintent.start();
                                                        //zyt
                                                        File file = new File(getContext().getExternalCacheDir()+"/img/"+songids[now_position]+".png");
                                                        File newfile = new File(getContext().getExternalFilesDir(null)+"/img/"+songids[now_position]+".png");
                                                        file.renameTo(newfile);
                                                        mainpage.current_songid = songids[now_position];
                                                        while(!getfos){}
                                                        TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                                                        final ArrayList<String> songname=new ArrayList<String>();
                                                        songname.add(tv.getText().toString());
//                                                        if(downloadtimer!=null){
//                                                            System.out.println("****************settimer==null************");
//                                                            downloadtimer.cancel();
//                                                            downloadtimer = null;
//                                                        }
//                                                        if(progressDialog!=null)
//                                                        {
//                                                            System.out.println("****************setprogress==null*************");
//                                                            progressDialog.cancel();
//                                                            progressDialog = null;
//                                                        }
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
//                            button.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    new IfBuy(songids[now_position]).start();
//                                    while(!judgebuy){}
//                                    judgebuy = false;
////                                    if(!currentsong_buy)
////                                    {
////                                        System.out.println("mei you gou mai");
////                                        new AlertDialog.Builder(getActivity())
////                                                .setTitle("来自奇葩君的确认")
////                                                .setMessage("您确定要花10个Ke币购买这首歌吗？")
////                                                .setPositiveButton("是", new DialogInterface.OnClickListener() {
////                                                    @Override
////                                                    public void onClick(DialogInterface dialog, int which) {
////                                                        Toast.makeText(getActivity(), "购买成功！Ke币 -10", Toast.LENGTH_SHORT).show();
////                                                        ifbuy = true;
////                                                        endalert = true;
////                                                    }
////                                                })
////                                                .setNegativeButton("否", new DialogInterface.OnClickListener() {
////                                                    @Override
////                                                    public void onClick(DialogInterface dialog, int which) {
////                                                        endalert = true;
////                                                    }
////                                                })
////                                                .show();
////                                        while(!endalert){}
////                                        endalert = false;
////                                        System.out.println(" current:"+ifbuy);
////                                    }
//
//                                    if(true)
//                                    {
//                                        System.out.println("gou mai le ");
//                                        //进度条框框
//                                        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//                                        progressDialog.setTitle("正在下载，请稍等");
//                                        progressDialog.setIndeterminate(false);
//                                        progressDialog.setCancelable(true);
//                                        progressDialog.setMax(100);
//                                        progressDialog.setProgress(1);
//                                        progressDialog.show();
//                                        new Thread(progress).start();
//                                        GoForSingPage newintent = new GoForSingPage(now_position);
//                                        newintent.start();
////                                        if(progressDialog == null)
////                                        {
//                                            System.out.println("mdjindutiaonigeilaozichulai*********************************************************");
////                                            progressDialog.setMax(100);
////                                            progressDialog.setProgress(100);
////                                            progressDialog.show();
//
//                                            System.out.println("mdjindutiaochulailema***************************************************************");
////                                        }
////                                        if(downloadtimer==null)
////                                        {
////                                            downloadtimer = new Timer();
////                                            System.out.println("****************timer*************");
////                                            downloadtask = new DownloadTask();
////                                            System.out.println("****************task**************");
////                                            downloadtimer.scheduleAtFixedRate(downloadtask,0,mPlayTimeDuration);
////                                            System.out.println("****************settime***********");
////                                        }
//                                        //加线程？？？进度条用的
//                                        System.out.println("****************goforsingpage*************");
//
//                                        //zyt
//                                        File file = new File(getContext().getExternalCacheDir()+"/img/"+songids[now_position]+".png");
//                                        File newfile = new File(getContext().getExternalFilesDir(null)+"/img/"+songids[now_position]+".png");
//                                        file.renameTo(newfile);
//                                        mainpage.current_songid = songids[now_position];
//                                        while(!getfos){}
//                                        TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
//                                        final ArrayList<String> songname=new ArrayList<String>();
//                                        songname.add(tv.getText().toString());
////                                        if(downloadtimer!=null){
////                                            System.out.println("****************settimer==null************");
////                                            downloadtimer.cancel();
////                                            downloadtimer = null;
////                                        }
////                                        if(progressDialog!=null)
////                                        {
////                                            System.out.println("****************setprogress==null*************");
////                                            progressDialog.cancel();
////                                            progressDialog = null;
////                                        }
//                                        System.out.println("set getfos false");
//                                        getfos = false;
//                                        mainpage.processdownload = 1;
//
//                                        Intent intent=new Intent(view.getContext(),singasong.class);
//                                        intent.putStringArrayListExtra("Songinfos",songname);
//                                        startActivity(intent);
//                                    }
//                                }
//
//                            });
//                            return view;
//                        }
//                    };
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
                }
                break;
            case 2://排行
//                new Thread(runnable).start();
//                while(!files_all_get){}
//                System.out.println("set files_all_get false");
//                files_all_get = false;
//                System.out.println(songnames[0]+2);
//                System.out.println(singername[1]+2);
                for(int i=0;i<mainpage.top_count;i++){
                    HashMap<String,Object>map=new HashMap<String,Object>();
                    map.put("Num",Integer.toString(i+1));
                    map.put("ItemImage",getBitmap("top",i));
                    map.put("ItemTitle",topsongname[i]);
                    map.put("ItemText",topsingername[i]);
                    map.put("button","评 分");
                    listItem.add(map);
                    SimpleAdapter simpleAdapter=new SimpleAdapter(
                            getContext(),
                            listItem,
                            R.layout.item_atopsong,
                            new String[]{"Num","ItemImage","ItemTitle","ItemText","button"},
                            new int[]{R.id.Num,R.id.ItemImage,R.id.ItemTitle,R.id.ItemText,R.id.button}){
                        @Override
                        public View getView(int position, final View convertView, ViewGroup parent) {
                            final View view=super.getView(position, convertView, parent);
                            final int now_position = position;
                            Button button=(Button)view.findViewById(R.id.button);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //zyt
                                    GoForScorePage newintent = new GoForScorePage(now_position);
                                    newintent.start();

                                    TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                                    TextView tv1=(TextView)view.findViewById(R.id.ItemText);
                                    final ArrayList<String> songinfo=new ArrayList<String>();
                                    songinfo.add(tv.getText().toString());
                                    songinfo.add(tv1.getText().toString());

                                    //zyt
                                    mainpage.current_scoredid = topscoredids[now_position];
                                    mainpage.current_judgeuserid = userids[now_position];
                                    mainpage.current_score = topscores[now_position];
                                    System.out.println(mainpage.current_judgeuserid+mainpage.current_score+"getfos:"+getfos);
                                    while(!getfos){}
                                    System.out.println("set getfos false");
                                    getfos = false;

                                    Intent intent=new Intent(view.getContext(),scoreasong.class);
                                    intent.putStringArrayListExtra("Songinfos",songinfo);
                                    startActivity(intent);
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
                }
                break;
            default:
                break;
        }
        return view;
    }

    //新线程：与socket通信，得到lyric和mp3
    class GoForSingPage extends Thread{
        private int position;
        public GoForSingPage(int position){
            this.position = position;
        }
        @Override
        public void run(){
            Log.i("client", "wozhendeyaojinqule");
            try {

//                new DownloadTask().start();
                if(!currentsong_buy)
                    MainActivity.client.sendString(BuildJson_GoForSingPage(0,songids[position]));
                File file = new File(getContext().getExternalFilesDir(null).toString()+"/lrc/"+songids[position]+".lrc");
                if(!file.exists())
                {
//                    progressDialog.setTitle("正在下载歌词，请稍等");
                    MainActivity.client.getFile(BuildJson_GoForSingPage(1,songids[position]),getContext().getExternalFilesDir(null).toString()+"/lrc");
                }
                System.out.println("getfile"+songids[position]);

                File file_sound = new File(getContext().getExternalFilesDir(null).toString()+"/mp3/"+songids[position]+".mp3");

                if(!file_sound.exists())
                {
//                    mainpage.processdownload = 1;
//                    new Thread(progress).start();
//                    progressDialog.setTitle("正在下载歌曲，请稍等");
                    MainActivity.client.getFile(BuildJson_GoForSingPage(2,songids[position]),getContext().getExternalFilesDir(null).toString()+"/mp3");
                }
                System.out.println("getfile"+songids[position]+"mp3");
                getfos = true;
                System.out.println("getfos = true *******************************************************");
//                Log.i("client", FromServer);
//                ParseJson(FromServer);
//                Log.i("client", "success");
//                if(downloadtimer!=null){
//                    System.out.println("****************settimer==null************");
//                    downloadtimer.cancel();
//                    downloadtimer = null;
//                }
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
        }
    }

    private class DownloadTask extends TimerTask{
        @Override
        public void run(){
            final int timePassed = mainpage.processdownload;
            System.out.println("time: *********"+timePassed);
            mainpage activity = (mainpage)getActivity();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("正在下载，请稍等");
                    progressDialog.setIndeterminate(false);
                    progressDialog.setCancelable(true);
                    progressDialog.setMax(100);
                    progressDialog.setProgress(timePassed);
                    progressDialog.show();
                }
            });
        }
    }
    Runnable progress = new Runnable() {
        @Override
        public void run() {
            int i = 1;
            int last = mainpage.processdownload;
            while (i<100) {

                try {
                    Thread.sleep(10000);
                    // 更新进度条的进度,可以在子线程中更新进度条进度
                    progressDialog.incrementProgressBy(i);
                    i++;
//                    progressDialog.incrementProgressBy(mainpage.processdownload-last);
//                    progressDialog.setProgress(mainpage.processdownload);
                    System.out.println("********************************************************" + mainpage.processdownload);
                    //progressDialog.show();
                } catch (Exception e) {
                    // TODO: handle exception
                }
            }
            // 在进度条走完时删除Dialog
            progressDialog.dismiss();
        }
    };
    private class IfBuy extends Thread{
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

    class GoForScorePage extends Thread{
        private int position;
        public GoForScorePage(int position){
            this.position = position;
        }
        @Override
        public void run(){
            Log.i("client", "wozhendeyaojinqule");
            try {
                File file = new File(getContext().getExternalCacheDir().toString()+"/amr/"+topscoredids[position]+".amr");
                if(!file.exists())
                {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(3,topscoredids[position]),getContext().getExternalCacheDir().toString()+"/amr");
                }
                System.out.println("getfile"+topscoredids[position]);
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
            getfos = true;
        }
    }

    //图片转bitmap
    public Bitmap getBitmap(String type,int i){
        Bitmap mBitmap = null;
//        InputStream assetFile=null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        try{
            if(type.equals("new"))
            {
                File file = new File(getContext().getExternalCacheDir().toString()+"/img/"+songids[i]+".png");
                System.out.println("new*****************************"+file.toString());
                FileInputStream fis = new FileInputStream(file.toString());
                mBitmap=BitmapFactory.decodeStream(fis);//如果是URL就decodestream(is),并删去上面那句
            }
            else if(type.equals("top"))
            {
                File file = new File(getContext().getExternalCacheDir().toString()+"/img/"+userids[i]+".png");
                System.out.println("top*****************************"+file.toString());
                FileInputStream fis = new FileInputStream(file.toString());
                mBitmap=BitmapFactory.decodeStream(fis);//如果是URL就decodestream(is),并删去上面那句
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
    }
    //JSON
    //

    private String BuildJson(int i,String song_id) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();

            if(i==0) {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "picture_song");
                arr2.put("song_ID", song_id);
                System.out.println(arr2.toString());
                array.put(arr2);
            }
            if(i==1){
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "portrait");
                arr2.put("user_ID",song_id);
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
            else if(i == 2)
            {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "sing_mp3");
                arr2.put("song_ID",song_id);
                System.out.println(arr2.toString());
                array.put(arr2);
            }
            else if(i == 3)
            {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "scored_song");
                arr2.put("scored_ID",song_id);
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
    public void ParseJson(String jsonString) throws JSONException,
            ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");

        if(ja.getJSONObject(0).getString("type").equals("ifbuy")){
            currentsong_buy = ja.getJSONObject(0).getBoolean("result");
        }


    }
}
