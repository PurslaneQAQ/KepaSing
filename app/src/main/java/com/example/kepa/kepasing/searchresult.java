package com.example.kepa.kepasing;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

public class searchresult extends AppCompatActivity {
    //看要不要改成根据i查找歌手名和歌曲名
    //搜索结果为：全匹配歌曲名 全匹配歌手名 包含歌曲名 包含歌手名

    private String[] songnames;
    private String[] singername;
    private String[] song_ids;

    String[] songimages=null;
    private String FromServer;
    private int count;
    boolean files_all_get = false;
    boolean getfos = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchresult);
        ImageView gobackbtn=(ImageView)findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        new Thread(runnable).start();
        while(!files_all_get){}
        files_all_get = false;

        //填充搜索结果表单
        ListView lv=(ListView)findViewById(R.id.search_result);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();
        //9改成count
        for(int i=0;i<count;i++){
            HashMap<String,Object> map=new HashMap<String,Object>();
            map.put("ItemImage",getBitmap(i));
            map.put("ItemTitle",songnames[i]);
            map.put("ItemText",singername[i]);
            listItem.add(map);
            SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItem,R.layout.item_asong,
                    new String[]{"ItemImage","ItemTitle","ItemText"},new int[]{R.id.ItemImage,R.id.ItemTitle,R.id.ItemText});
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
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    //zyt
                    GoForSingPage newintent = new GoForSingPage(position);
                    newintent.start();

                    TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                    final ArrayList<String> songname=new ArrayList<String>();
                    songname.add(tv.getText().toString());

                    //zyt
                    File file = new File(getExternalCacheDir()+"/img/"+song_ids[position]+".png");
                    File newfile = new File(getExternalFilesDir(null)+"/img/"+song_ids[position]+".png");
                    file.renameTo(newfile);
                    mainpage.current_songid = song_ids[position];
                    while(!getfos){}
                    getfos = false;
                    Intent intent=new Intent(parent.getContext(),singasong.class);
                    intent.putStringArrayListExtra("Songinfos",songname);
                    startActivity(intent);
                }
            });
        }
    }//图片转bitmap
    public Bitmap getBitmap(int i){
        Bitmap mBitmap = null;
        InputStream assetFile=null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        try{
            File file = new File(getExternalCacheDir()+"/img/"+song_ids[i]+".png");
            FileInputStream fis = new FileInputStream(file.toString());
            mBitmap=BitmapFactory.decodeStream(fis);//如果是URL就decodestream(is),并删去上面那句
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
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
                File file = new File(getExternalCacheDir().toString()+"/lrc");
                if(!file.exists()) {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(1, song_ids[position]), getExternalCacheDir().toString() + "/lrc");
                    System.out.println("getfile" + song_ids[position]);
                }
                file = new File(getExternalCacheDir().toString()+"/mp3");
                if(!file.exists()) {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(2, song_ids[position]), getExternalCacheDir().toString() + "/mp3");
                    System.out.println("getfile" + song_ids[position] + "mp3");
                }
//                Log.i("client", FromServer);
//                ParseJson(FromServer);
//                Log.i("client", "success");
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
            getfos = true;
        }
    }
    private String BuildJson_GoForSingPage(int i, String song_id) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
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


            //arr2.put("type", "picture_song");
            //arr2.put("user_ID","true");

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
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            try {
                FromServer = MainActivity.client.sendString(BuildJson(0,null));
                ParseJson(FromServer);
                for(int i = 0; i<count;i++)
                {
                    File file = new File(getExternalCacheDir()+"/img/"+song_ids[i]+".png");
                    if(!file.exists())
                    {
                        MainActivity.client.getFile(BuildJson(1,song_ids[i]),getExternalCacheDir().toString()+"/img");
                    }
                    System.out.println("getfile"+song_ids[i]+i);
                }
                files_all_get = true;
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            }catch(ParseException e)
            {
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
        }
    };
    //JSON
    //
    private String BuildJson(int i,String song_ID) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if(i==0)
            {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "search_song");
                arr2.put("content",SingPageFragment.user_query);
                System.out.println(arr2.toString());
                array.put(arr2);
            }
            if(i==1)
            {

                JSONObject arr2 = new JSONObject();
                arr2.put("type", "picture_song");
                arr2.put("song_ID",song_ID);
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

        int count = 0;
        if(ja.getJSONObject(0).getString("type").equals("search_song")){
            count = ja.length()-1;
            this.count = count;
        }
        System.out.println(count);

        song_ids = new String[count];
        songnames = new String[count];
        singername = new String[count];
        for(int i =0;i<count;i++)
        {
            song_ids[i] = ja.getJSONObject(i+1).getString("song_ID");
            songnames[i] = ja.getJSONObject(i+1).getString("song_name");
            singername[i] = ja.getJSONObject(i+1).getString("singer");

        }
    }
}