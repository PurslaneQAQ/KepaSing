package com.example.kepa.kepasing;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class SingPageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    String[] hotsongimages=null;
    AssetManager hotsongimageAsset=null;

    //存放歌名和用户名
    String[] songnames=new String[]{
            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
    };
    String[] singername=new String[]{
            "Lana Del Rey","Adele","Avril Lavigne","Britney Spears","Bruno Mars","Emeli Sandé","Eminem ft. Rihanna",
            "Fall Out Boy","Halsey"
    };

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
        try {
            hotsongimageAsset=getContext().getAssets();
            hotsongimages=hotsongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.sing_layout, container, false);
        ListView lv=(ListView) view.findViewById(R.id.songs_container);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();
        //加热门歌
        for(int i=0;i<9;i++){
            HashMap<String,Object> map=new HashMap<String,Object>();
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
    //图片转bitmap
    public Bitmap getBitmap(int i){
        Bitmap mBitmap = null;
        InputStream assetFile=null;
        /*URL url = new URL(imageUrl);   如果是给网络上的URL
          HttpURLConnection conn = (HttpURLConnection) url.openConnection();
          InputStream is = conn.getInputStream();   */
        try{
            assetFile=hotsongimageAsset.open(hotsongimages[i]);
            mBitmap=BitmapFactory.decodeStream(assetFile);//如果是URL就decodestream(is),并删去上面那句
        }catch (IOException e){
            e.printStackTrace();
        }
        return mBitmap;
    }
}
