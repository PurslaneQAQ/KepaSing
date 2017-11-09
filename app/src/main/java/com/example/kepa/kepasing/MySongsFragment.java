package com.example.kepa.kepasing;

import android.content.ContentResolver;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import static com.example.kepa.kepasing.CenterPageFragment.mMediaPlayer;
import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;

/**
 * Created by Administrator on 2017/11/2 0002.
 */

public class MySongsFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private String FromServer;
    String[] songid = null;
    /**********************
     *  String[] songid=new String[]{
     "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
     "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
     };*/

    private String[] songnames = null;
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
    String[] localsongname=new String[]{
            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
    };


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
            /*File directory = new File(getCacheDir(), "/media/cover/");
            for(int i = 0; i < ja.getJSONArray(1).length(); i++){
                client.getFile(songid[i] +".jpg",directory.toString());
                mysongimages[i] = new File(directory.toString() + songid[i] +".jpg");
            }*/
            return true;
        }
        return false;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Thread(runnable).start();
        mPage = getArguments().getInt(ARG_PAGE);
        /*try {
            songimageAsset=getContext().getAssets();
            songimages=songimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mysongs_layout, container, false);
        GridView gv=(GridView)view.findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>> listItem=new ArrayList<HashMap<String, Object>>();

        switch(mPage){
            case 1://我的作品
                while(songid ==null){}
                for(int i=0;i<songid.length;i++){
                    HashMap<String,Object>map=new HashMap<String,Object>();
                    map.put("ItemImage",getBitmap(i));
                    map.put("ItemTitle",songnames[i]);
                    map.put("ItemScore",scores[i]);
                    listItem.add(map);
                    SimpleAdapter simpleAdapter=new SimpleAdapter(getContext(),listItem,R.layout.item_mysong,
                            new String[]{"ItemImage","ItemTitle","ItemScore"},new int[]{R.id.ItemImage,R.id.ItemTitle,R.id.ItemScore});
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
                            //Toast.makeText(view.getContext(), "播放歌曲", Toast.LENGTH_SHORT).show();
                            //点击链接底下的播放器播放
                            mMediaPlayer.start();
                        }
                    });
                }
                break;
            case 2://本地作品
                for(int i=0;i<9;i++){
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
                            Toast.makeText(view.getContext(), "播放歌曲", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                break;
            default:
                break;
        }
        return view;
    }
    //图片转bitmap
    public Bitmap getBitmap(int i){
        Bitmap mBitmap = null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        ContentResolver resolver = getContext().getContentResolver();
        try {
            mBitmap = MediaStore.Images.Media.getBitmap(resolver, Uri.fromFile(new File(getContext().getFilesDir() +  "/user/img/" + UserID + ".jpg")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mBitmap;
    }
}
