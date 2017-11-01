package com.example.kepa.kepasing;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class asingerssong extends AppCompatActivity {
    ArrayList<String> passedsingerinfo=new ArrayList<String>();
    //存放这个歌手名下的歌曲名数组
    String[] songname=new String[]{
      "Innocence","Paparazzi","Poker Face","Skater Boy","Try","Wonderful U","Born To Die","Love Story","City Of Stars"
    };
    //歌曲图片 到时候获取方式要改
    String[] newsongimages=null;
    AssetManager newsongimageAsset=null;

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

        //获取歌曲图片 请修改
        try {
            newsongimageAsset=getAssets();
            newsongimages=newsongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //ListView内容绑定
        ListView lv=(ListView)findViewById(R.id.songs);
        ArrayList<HashMap<String,Object>>listItem=new ArrayList<HashMap<String, Object>>();

        for(int i=0;i<9;i++){
            HashMap<String,Object>map=new HashMap<String,Object>();
            map.put("ItemImage",getBitmap(i));
            map.put("ItemTitle",songname[i]);
            listItem.add(map);
        }
        SimpleAdapter simpleAdapter=new SimpleAdapter(this,listItem,R.layout.asong_layout,
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
