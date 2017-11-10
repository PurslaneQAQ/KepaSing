package com.example.kepa.kepasing;

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
import android.widget.AdapterView;
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
    boolean finished = false;


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
                        songid[i] = local_Files.get(i).toString();
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

                /*try {
                    songid = new String[1];
                    songnames = new String[1];
                    singername = new String[1];

                        songid[0] = "s0001";
                        JSONObject inf;
                        inf = new JSONObject();
                        try {
                            JSONArray array = new JSONArray();
                            JSONObject arr2 = new JSONObject();
                            arr2.put("type", "song_info");
                            arr2.put("song_ID",songid[0]);
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
                        ParseName(FromServer, 0);
                } catch (JSONException e) {
                    System.out.println("build json failed");
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }*/
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
                    TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                    final ArrayList<String> songname=new ArrayList<String>();
                    songname.add(tv.getText().toString());
                    Intent intent=new Intent(parent.getContext(),singasong.class);
                    intent.putStringArrayListExtra("Songinfos",songname);
                    startActivity(intent);
                }
            });
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

}
