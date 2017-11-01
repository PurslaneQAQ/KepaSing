package com.example.kepa.kepasing;

import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import static android.R.attr.layout_centerHorizontal;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class CenterPageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    String[] mysongimages=null;
    AssetManager mysongimageAsset=null;
    public MediaPlayer mMediaPlayer= null;

    //存放歌名和得分
    String[] songnames=new String[]{
            "Born To Die","Rolling In the Deep","Innocence","Toxic","Grenade","Read All About It",
            "Love the Way You Lie","My Songs Know What You Did In the Dark","Castle"
    };
    String[] scores=new String[]{
            "5.0","1.2","3.8","6.7","9.8","6.4","7.2",
            "6.5","8.1"
    };

    private ImageView last;
    private ImageView play;
    private ImageView next;
    private TextView onplaysongname;
    private PopupWindow mykebpage;

    public static CenterPageFragment newInstance() {
        Bundle args = new Bundle();
        CenterPageFragment pageFragment = new CenterPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);
        try {
            mysongimageAsset=getContext().getAssets();
            mysongimages=mysongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //我的Ke币 弹窗界面设置
        View mykebpop=getLayoutInflater(savedInstanceState).inflate(R.layout.mykb,null);
        TextView mykbnum=(TextView)mykebpop.findViewById(R.id.numberofKB);
        mykbnum.setText("5000");
        Button chongzhi=(Button)mykebpop.findViewById(R.id.chongzhi);
        chongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //充值
            }
        });

        mykebpage=new PopupWindow(mykebpop, LinearLayout.LayoutParams.MATCH_PARENT,480);
        mykebpage.setTouchable(true);
        mykebpage.setOutsideTouchable(true);
        mykebpage.setBackgroundDrawable(new BitmapDrawable(getResources(),(Bitmap)null));
    }
    /*页面布局设置*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.center_layout, container, false);
        ImageView icon=(ImageView)view.findViewById(R.id.person);//头像 到时候从服务器上取
        icon.setImageResource(R.drawable.touxiang);

        TextView nickname=(TextView)view.findViewById(R.id.nickname);
        nickname.setText("Chaine");//昵称 也从服务器上取

        TextView emailaddr=(TextView)view.findViewById(R.id.emailaddress);
        emailaddr.setText("464848541@qq.com");//邮箱地址 服务器上取

        //“我的作品”部分
        LinearLayout mysongs=(LinearLayout)view.findViewById(R.id.mysongs_container);
        for(int i=0;i<9;i++){
            mysongs.addView(addMysongs(view,i));
        }
        //下面是页面底部的播放器 需要给按钮设定功能 还要绑定“我的作品”里的歌与播放器
        last=(ImageView)view.findViewById(R.id.btn_last);
        play=(ImageView)view.findViewById(R.id.btn_play);
        next=(ImageView)view.findViewById(R.id.btn_next);
        last.setImageResource(R.drawable.last);
        play.setImageResource(R.drawable.pause);
        next.setImageResource(R.drawable.next);
        onplaysongname=(TextView)view.findViewById(R.id.songname);
        //正在播放的歌名先设一个看看样子
        onplaysongname.setText("Born To Die");

        //右上角的编辑按钮
        TextView profile=(TextView)view.findViewById(R.id.profileedit);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),profileedit.class);
                startActivity(intent);
            }
        });

        //我的K币 点击
        TextView myKB=(TextView)view.findViewById(R.id.myKB);
        myKB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mykebpage.showAsDropDown(v,0,1);
            }
        });
        return view;
    }

    private View addMysongs(View view,int i){
        LinearLayout asong=new LinearLayout(view.getContext());
        ImageView mysongpic=new ImageView(view.getContext());
        TextView mysongname=new TextView(view.getContext());
        TextView mysongscore=new TextView(view.getContext());
        asong.setOrientation(LinearLayout.VERTICAL);
        mysongname.setTextSize(15);
        mysongscore.setTextSize(12);
        mysongscore.setTextColor(Color.parseColor("#FF7073"));

        InputStream assetFile=null;
        try{
            assetFile=mysongimageAsset.open(mysongimages[i]);
        }catch (IOException e){
            e.printStackTrace();
        }
        mysongpic.setImageBitmap(BitmapFactory.decodeStream(assetFile));//歌曲图片 从服务器上取
        LinearLayout.LayoutParams asongparam=new LinearLayout.LayoutParams(350,LinearLayout.LayoutParams.MATCH_PARENT);
        asongparam.leftMargin=10;
        asong.setLayoutParams(asongparam);


        LinearLayout.LayoutParams songpicparam=new LinearLayout.LayoutParams(340,340);
        mysongpic.setLayoutParams(songpicparam);

        LinearLayout.LayoutParams songnameparam=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mysongname.setLayoutParams(songnameparam);

        LinearLayout.LayoutParams scoreparam=new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        mysongscore.setLayoutParams(scoreparam);

        mysongname.setText(songnames[i]);
        mysongscore.setText("得分："+scores[i]);

        asong.addView(mysongpic);
        asong.addView(mysongname);
        asong.addView(mysongscore);
        return asong;
    }
}
