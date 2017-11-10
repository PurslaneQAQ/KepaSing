package com.example.kepa.kepasing;

import android.content.Context;
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
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class SingPageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private SearchView sv;
    String[] hotsongimages=null;
    public static String user_query;
    private boolean getfos = false;
    private boolean files_all_get = false;

    //存放歌名和用户名
//    String[] songnames=new String[]{
//            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
//            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
//    };
    String[] hot_songnames;
    String[] hot_singername;
    String[] hot_songids;
//    String[] singername=new String[]{
//            "Lana Del Rey","Adele","Avril Lavigne","Britney Spears","Bruno Mars","Emeli Sandé","Eminem ft. Rihanna",
//            "Fall Out Boy","Halsey"
//    };

    public static SingPageFragment newInstance() {
        Bundle args = new Bundle();
        SingPageFragment pageFragment = new SingPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

        mainpage activity = (mainpage)getActivity();
        hot_singername = activity.getHot_singers();
        System.out.println(hot_singername[0]+hot_singername[1]);
        hot_songnames = activity.getHot_songnames();
        hot_songids = activity.getHot_songids();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sing_layout, container, false);
        ListView lv=(ListView) view.findViewById(R.id.songs_container);
        sv=(SearchView)view.findViewById(R.id.search_bar);
        sv.setIconifiedByDefault(false);
        sv.setSubmitButtonEnabled(true);
        new Thread(runnable).start();
        while(!files_all_get){}
        System.out.println("set files_all_get false");
        files_all_get = false;

        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {//搜索框提交事件监听
                //query是搜索内容
                //final ArrayList<String> keywords=new ArrayList<String>();
                //keywords.add(query);
                user_query = query;
                sv.clearFocus();
                Intent intent=new Intent(getContext(),searchresult.class);
                //intent.putStringArrayListExtra("SearchKeywords",keywords);
                startActivity(intent);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();
        //加热门歌
        for(int i=0;i<mainpage.hot_count;i++){
            HashMap<String,Object> map=new HashMap<String,Object>();
            //根据i获取资源
            map.put("ItemImage",getBitmap(i));
            map.put("ItemTitle",hot_songnames[i]);
            map.put("ItemText",hot_singername[i]);
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
                    GoForSingPage newintent = new GoForSingPage(position);
                    newintent.start();

                    TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                    final ArrayList<String> songname=new ArrayList<String>();
                    songname.add(tv.getText().toString());

                    //zyt
                    File file = new File(getContext().getExternalCacheDir()+"/img/"+hot_songids[position]+".png");
                    File newfile = new File(getContext().getExternalFilesDir(null)+"/img/"+hot_songids[position]+".png");
                    file.renameTo(newfile);
                    mainpage.current_songid = hot_songids[position];
                    while(!getfos){}
                    System.out.println("set getfos false");
                    getfos = false;

                    Intent intent=new Intent(parent.getContext(),singasong.class);
                    intent.putStringArrayListExtra("Songinfos",songname);
                    startActivity(intent);
                }
            });
        }
        //两个大按钮
        ImageView geshoubtn=(ImageView)view.findViewById(R.id.btn_geshou);
        ImageView yidianbtn=(ImageView)view.findViewById(R.id.btn_yidian);

        geshoubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),choosesinger.class);
                startActivity(intent);
            }
        });
        yidianbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),havesongs.class);
                startActivity(intent);
            }
        });

        return view;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            try {
                for(int i = 0; i<10;i++)
                {
                    System.out.println(hot_songids[i]+"2"+i);
                    File file1 = new File(getContext().getExternalCacheDir()+"/img/"+hot_songids[i]+".png");
                    System.out.println(file1.toString());
                    if(!file1.exists())
                    {
                        System.out.println("want to get picture");
                        MainActivity.client.getFile(BuildJson(hot_songids[i]),getContext().getExternalCacheDir().toString()+"/img");
                    }
                    System.out.println("getfile"+hot_songids[i]+i);
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

    class GoForSingPage extends Thread{
        private int position;
        public GoForSingPage(int position){
            this.position = position;
        }
        @Override
        public void run(){
            Log.i("client", "wozhendeyaojinqule");
            try {
                File file = new File(getContext().getExternalFilesDir(null).toString()+"/lrc/"+hot_songids[position]+".lrc");
                if(!file.exists())
                {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(1,hot_songids[position]),getContext().getExternalFilesDir(null).toString()+"/lrc");
                }
                System.out.println("getfile"+hot_songids[position]);
                File file_sound = new File(getContext().getExternalFilesDir(null).toString()+"/mp3/"+hot_songids[position]+".mp3");
                if(!file_sound.exists())
                {
                    MainActivity.client.getFile(BuildJson_GoForSingPage(2,hot_songids[position]),getContext().getExternalFilesDir(null).toString()+"/mp3");
                }
                System.out.println("getfile"+hot_songids[position]+"mp3");
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
    private String BuildJson(String song_id) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();

            JSONObject arr2 = new JSONObject();
            arr2.put("type", "picture_song");
            arr2.put("song_ID", song_id);
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
        InputStream assetFile=null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        try{
            File file = new File(getContext().getExternalCacheDir().toString()+"/img/"+hot_songids[i]+".png");
            FileInputStream fis = new FileInputStream(file.toString());
            mBitmap=BitmapFactory.decodeStream(fis);//如果是URL就decodestream(is),并删去上面那句
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
    }
    @Override
    public void onResume() {
        super.onResume();
        sv.setQuery("", false);
        sv.setSubmitButtonEnabled(true);
        getView().requestFocus();
    }

}
