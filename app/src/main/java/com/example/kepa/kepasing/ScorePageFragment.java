package com.example.kepa.kepasing;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class ScorePageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;

    //zyt
    private boolean First_Come = true;
    private String[] scored_ids;
    private String[] scored_user_ids;
    private String[] scored_song_names;
    public double[] scored_scores;
    public int current_position = 0;
    private String[] scored_user_nicknames;
    private int count;
    private String FromServer;
    boolean getfos = false;

    public static ScorePageFragment newInstance() {
        Bundle args = new Bundle();
        ScorePageFragment pageFragment = new ScorePageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        //zyt
        new Thread(runnable).start();

        //图片资源 改掉
//        try {
//            scoresongimageAsset=getContext().getAssets();
//            scoresongimages=scoresongimageAsset.list("");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.score_layout, container, false);
        ListView lv=(ListView) view.findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();

        if(!First_Come) getfos = true;
        while(!getfos){}
        getfos = false;

        //9改为count
        for(int i=0;i<count;i++){
            HashMap<String,Object> map=new HashMap<String,Object>();
            //String songnames;
            //String singername;根据i值查找获取
            map.put("ItemImage",getBitmap(i));
            map.put("ItemTitle",scored_song_names[i]);
            map.put("ItemText",scored_user_nicknames[i]);
            listItem.add(map);
            SimpleAdapter simpleAdapter=new SimpleAdapter(getContext(),listItem,R.layout.item_asong,
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
                    ScorePageFragment.GoForScorePage newintent = new ScorePageFragment.GoForScorePage(position);
                    newintent.start();

                    TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                    TextView tv1=(TextView)view.findViewById(R.id.ItemText);
                    final ArrayList<String> songinfo=new ArrayList<String>();

                    //zyt
                    mainpage.current_position = position;
                    mainpage.current_scoredid = scored_ids[position];
                    mainpage.current_judgeuserid = scored_user_ids[position];
                    mainpage.current_score = scored_scores[position];

                    songinfo.add(tv.getText().toString());
                    songinfo.add(tv1.getText().toString());

                    //zyt
                    while(!getfos){}
                    System.out.println("set getfos fault in click score");
                    getfos = false;

                    Intent intent=new Intent(parent.getContext(),scoreasong.class);
                    intent.putStringArrayListExtra("Songinfos",songinfo);
                    startActivity(intent);

//                    //zyt
//                    while(scoreasong.submit_score==0){}
//                    scored_scores[position] = scoreasong.submit_score;
                }
            });
            First_Come = false;
        }
        return view;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            try {
                if(First_Come==true)
                {
                    FromServer = MainActivity.client.sendString(BuildJson(0,null));
                    ParseJson(FromServer);
                    First_Come = false;
                }

                for(int i = 0; i<count;i++)
                {
                    File file = new File(getContext().getExternalCacheDir()+"/img/"+scored_user_ids[i]+".jpg");
                    if(!file.exists())
                    {
                        MainActivity.client.getFile(BuildJson(1,scored_user_ids[i]),getContext().getExternalCacheDir().toString()+"/img");
                    }
                    System.out.println("getfile"+scored_user_ids[i]+i);
                }
                getfos = true;
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

    class GoForScorePage extends Thread{
        private int position;
        public GoForScorePage(int position){
            this.position = position;
        }
        @Override
        public void run(){
            Log.i("client", "wozhendeyaojinqule");
            try {
                File file = new File(getContext().getExternalCacheDir().toString()+"/mp3/"+scored_ids[position]+".mp3");
                if(!file.exists())
                {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(scored_ids[position]),getContext().getExternalCacheDir().toString()+"/mp3");
                }
                System.out.println("getfile"+scored_ids[position]);
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
    public Bitmap getBitmap(int i){
        Bitmap mBitmap = null;
        InputStream assetFile=null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        //改图片资源获取
        try{
            File file = new File(getContext().getExternalCacheDir().toString()+"/img/"+scored_user_ids[i]+".png");
            FileInputStream fis = new FileInputStream(file.toString());
            mBitmap=BitmapFactory.decodeStream(fis);//如果是URL就decodestream(is),并删去上面那句
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
    }
    //JSON
    //
    private String BuildJson(int i,String user_id) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            if(i==0)
            {
                JSONObject arr2 = new JSONObject();
                arr2.put("type", "score");
                System.out.println(arr2.toString());
                array.put(arr2);
            }
            if(i==1)
            {

                JSONObject arr2 = new JSONObject();
                arr2.put("type", "portrait");
                arr2.put("user_ID",user_id);
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
    private String BuildJson_GoForSingPage(String scored_ID) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "scored_song");
            arr2.put("scored_ID",scored_ID);
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
        if(ja.getJSONObject(0).getString("type").equals("score")){
            count = ja.length()-1;
            this.count = count;
        }
        System.out.println(count);

        scored_ids = new String[count];
        scored_user_ids = new String[count];
        scored_user_nicknames = new String[count];
        scored_song_names = new String[count];
        scored_scores = new double[count];
        int j = 0;
        for(int i =0;i<count;i++)
        {
            if(ja.getJSONObject(i+1).getString("user_ID")!=MainActivity.UserID)
            {
                scored_ids[j] = ja.getJSONObject(i+1).getString("scored_ID");
                scored_user_ids[j] = ja.getJSONObject(i+1).getString("user_ID");
                scored_song_names[j] = ja.getJSONObject(i+1).getString("song_name");
                scored_scores[j] = ja.getJSONObject(i+1).getDouble("score");
                scored_user_nicknames[j] = ja.getJSONObject(i+1).getString("nickname");
                j++;
            }
        }
        this.count = j;
    }
}