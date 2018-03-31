package com.example.kepa.kepasing;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;
import static com.example.kepa.kepasing.MySongsFragment.getFileList;

public class havesongs extends AppCompatActivity {

    String[] havesongimages=null;
    private String FromServer;
    AssetManager havesongimageAsset=null;
    //已点歌名和歌手名
    String[] songid = null;
    String[] songnames=null;
    /*new String[]{
            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
    };*/
    String[] singername=null;
    /*new String[]{
    "Lana Del Rey","Adele","Avril Lavigne","Britney Spears","Bruno Mars","Emeli Sandé","Eminem ft. Rihanna",
    "Fall Out Boy","Halsey"
};*/
    private boolean finished = false;
    private boolean getfos = false;

    private ProgressDialog progressDialog;
    private Timer downloadtimer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_havesongs);
        ImageView gobackbtn=(ImageView)findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ListView lv=(ListView)findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();

        try {
            havesongimageAsset=getAssets();
            havesongimages=havesongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File local = new File(getExternalFilesDir("mp3").toString());
        if(!local.exists()){
            local.mkdirs();
        }
        final List<String> local_Files = getFileList(local);
        Runnable getSongNames = new Runnable() {
            @Override
            public void run() {
                try {
                    songid = new String[local_Files.size()];
                    songnames = new String[local_Files.size()];
                    singername = new String[local_Files.size()];
                    for(int i = 0; i < local_Files.size(); i++) {
                        songid[i] = local_Files.get(i).toString().substring(0, 5);
                        JSONObject inf;
                        inf = new JSONObject();
                        try {
                            JSONArray array = new JSONArray();
                            JSONObject arr2 = new JSONObject();
                            arr2.put("type", "song_info");
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
                        FromServer = client.sendString(inf.toString());
                        Log.i("client", FromServer);
                        ParseName(FromServer, i);
                    }
                } catch (JSONException e) {
                    System.out.println("build json failed");
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

                finished = true;

                //FileTransferClient upload  = new FileTransferClient(recentTouxiang);
            }
        };

        finished = false;
        new Thread(getSongNames).start();
        while(!finished){}

        for(int i=0;i<songid.length;i++){
            HashMap<String,Object>map=new HashMap<String,Object>();
            map.put("ItemImage",getBitmap(i));
            map.put("ItemTitle",songnames[i]);
            map.put("ItemText",singername[i]);
            map.put("button","K 歌");
            listItem.add(map);
            SimpleAdapter simpleAdapter=new SimpleAdapter(
                    this,
                    listItem,
                    R.layout.item_asong,
                    new String[]{"ItemImage","ItemTitle","ItemText","button"},
                    new int[]{R.id.ItemImage,R.id.ItemTitle,R.id.ItemText,R.id.button})
            {

                @Override
                public View getView(final int position, View convertView,ViewGroup parent) {
                    final View view=super.getView(position, convertView, parent);
                    Button button=(Button)view.findViewById(R.id.button);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                            final ArrayList<String> songname=new ArrayList<String>();
                            songname.add(tv.getText().toString());
                            getfos = false;
                            GoForSingPage newintent = new GoForSingPage(position);
                            while (getfos){}
                            newintent.start();

                            File file = new File(getExternalCacheDir()+"/img/"+songid[position]+".png");
                            File newfile = new File(getExternalFilesDir(null)+"/img/"+songid[position]+".png");
                            if(file.exists())
                                file.renameTo(newfile);
                            mainpage.current_songid = songid[position];
                            while(!getfos){}
                            TextView new_tv= (TextView)view.findViewById(R.id.ItemTitle);
                            final ArrayList<String> this_songname=new ArrayList<String>();
                            this_songname.add(tv.getText().toString());
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

                            Intent intent=new Intent(v.getContext(),singasong.class);
                            intent.putStringArrayListExtra("Songinfos",this_songname);
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
//            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                    TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
//                    final ArrayList<String> songname=new ArrayList<String>();
//                    songname.add(tv.getText().toString());
//                    Intent intent=new Intent(parent.getContext(),singasong.class);
//                    intent.putStringArrayListExtra("Songinfos",songname);
//                    startActivity(intent);
//                }
//            });
        }
    }
    //图片转bitmap
    public Bitmap getBitmap(int i){
        /*Bitmap mBitmap = null;
        InputStream assetFile=null;
        URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        /*try{
            assetFile=havesongimageAsset.open(havesongimages[i]);
            mBitmap=BitmapFactory.decodeStream(assetFile);//如果是URL就decodestream(is),并删去上面那句
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;*/
        Bitmap mBitmap = null;
        ContentResolver resolver = getContentResolver();
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(resolver, Uri.fromFile(new File(getExternalFilesDir("img") + "/" + songid[i] + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }

    public void ParseName(String jsonString, int i) throws JSONException,
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " +
                "" + ja.getJSONObject(0).getString("type"));
        if (ja.getJSONObject(0).getString("type").equals("song_info")) {
            songnames[i] = ja.getJSONObject(0).getString("songname");
            singername[i] = ja.getJSONObject(0).getString("singer");
        }
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
                File file = new File(getExternalFilesDir(null).toString()+"/lrc/"+ songid[position]+".lrc");
                if(!file.exists())
                {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(1,songid[position]),getExternalFilesDir(null).toString()+"/lrc");
                }
                System.out.println("getfile"+songid[position]);
                File file_sound = new File(getExternalFilesDir(null).toString()+"/mp3/"+songid[position]+".mp3");
                if(!file_sound.exists())
                {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(2,songid[position]),getExternalFilesDir(null).toString()+"/mp3");
                }
                System.out.println("getfile"+songid[position]+"mp3");
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