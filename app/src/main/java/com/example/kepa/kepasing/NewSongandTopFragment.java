package com.example.kepa.kepasing;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class NewSongandTopFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    //存放新歌歌名和歌手名
    String[] songnames=new String[]{
            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
    };
    String[] singername=new String[]{
            "Lana Del Rey","Adele","Avril Lavigne","Britney Spears","Bruno Mars","Emeli Sandé","Eminem ft. Rihanna",
            "Fall Out Boy","Halsey"
    };
    //存放排行歌名和用户名
    String[] topsongname=new String[]{
            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
    };
    String[] topsingername=new String[]{
            "大大","小小","长长","短短","前前","后后","左左",
            "右右","下下"
    };


    /*新歌界面的歌曲图片 先用本地的顶着了*/
    String[] newsongimages=null;
    AssetManager newsongimageAsset=null;
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
        try {
            newsongimageAsset=getContext().getAssets();
            newsongimages=newsongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.newsongandtop_layout, container, false);

        ListView lv=(ListView)view.findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();

        switch(mPage){
            case 1://新歌
                for(int i=0;i<9;i++){
                    HashMap<String,Object>map=new HashMap<String,Object>();
                    map.put("ItemImage",getBitmap(i));
                    map.put("ItemTitle",songnames[i]);
                    map.put("ItemText",singername[i]);
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
                            TextView tv=(TextView)view.findViewById(R.id.ItemTitle);
                            final ArrayList<String> songname=new ArrayList<String>();
                            songname.add(tv.getText().toString());
                            Intent intent=new Intent(parent.getContext(),singasong.class);
                            intent.putStringArrayListExtra("Songinfos",songname);
                            startActivity(intent);
                        }
                    });
                }
                break;
            case 2://排行
                for(int i=0;i<9;i++){
                    HashMap<String,Object>map=new HashMap<String,Object>();
                    map.put("Num",Integer.toString(i+1));
                    map.put("ItemImage",getBitmap(i));
                    map.put("ItemTitle",topsongname[i]);
                    map.put("ItemText",topsingername[i]);
                    listItem.add(map);
                    SimpleAdapter simpleAdapter=new SimpleAdapter(getContext(),listItem,R.layout.item_atopsong,
                            new String[]{"Num","ItemImage","ItemTitle","ItemText"},new int[]{R.id.Num,R.id.ItemImage,R.id.ItemTitle,R.id.ItemText});
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
                            TextView tv1=(TextView)view.findViewById(R.id.ItemText);
                            final ArrayList<String> songinfo=new ArrayList<String>();
                            songinfo.add(tv.getText().toString());
                            songinfo.add(tv1.getText().toString());
                            Intent intent=new Intent(parent.getContext(),scoreasong.class);
                            intent.putStringArrayListExtra("Songinfos",songinfo);
                            startActivity(intent);
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
        InputStream assetFile=null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        try{
            assetFile=newsongimageAsset.open(newsongimages[i]);
            mBitmap=BitmapFactory.decodeStream(assetFile);//如果是URL就decodestream(is),并删去上面那句
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
    }
}
