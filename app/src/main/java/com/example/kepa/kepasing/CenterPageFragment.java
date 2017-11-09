package com.example.kepa.kepasing;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class CenterPageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private CenterAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;

    //public static File[] mysongimages=null;
    public static MediaPlayer mMediaPlayer= null;

    //存放歌名和得分

    private ImageView last;
    private ImageView play;
    private ImageView next;
    private ContentResolver resolver;
    public static String email;
    public static String nickname;
    public static String password;
    private String FromServer;
    private SeekBar progress;
    private TextView onplaysongname;
    private PopupWindow mykebpage;
    private String kbNum;

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
        new Thread(runnable).start();
        /*try {
            mysongimageAsset=getContext().getAssets();
            mysongimages=mysongimageAsset.list("");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        //我的Ke币 弹窗界面设置
        View mykebpop=getLayoutInflater(savedInstanceState).inflate(R.layout.mykb,null);
        TextView mykbnum=(TextView)mykebpop.findViewById(R.id.numberofKB);
        mykbnum.setText(kbNum);
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
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            client = new Client();
            try {
                FromServer = client.sendString(BuildJson(false));
                File directory = new File(getContext().getFilesDir() + "/user/img/");
                if (!directory.exists())
                    directory.getParentFile().mkdirs();
                FromServer = client.getFile(BuildJson(true) ,directory.toString());
                Log.i("client", FromServer);
                if (!ParseJson(FromServer)) {
                    //
                }
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (Exception e){
                e.printStackTrace();
            }
            //FileTransferClient upload  = new FileTransferClient(recentTouxiang);
        }
    };

    private String BuildJson(boolean portrait) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            if(portrait)
                arr2.put("type", "portrait");
            else
                arr2.put("type", "mime");
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

        if (ja.getJSONObject(0).getString("type").equals("mime")) {
            nickname = ja.getJSONObject(0).getString("nick");
            email = ja.getJSONObject(0).getString("username");
            kbNum = ja.getJSONObject(0).getString("money");
            return true;
        }
        //Toast.makeText(getActivity().getApplicationContext(), "页面加载失败QwQ", Toast.LENGTH_SHORT).show();
        return false;
    }

    /*页面布局设置*/
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.center_layout, container, false);
        ImageView icon=(ImageView)view.findViewById(R.id.person);//头像 到时候从服务器上取
        resolver = getActivity().getApplicationContext().getContentResolver();
        while(nickname ==null){}
        File portrait = new File(getContext().getFilesDir()+ "/user/img/" + UserID + ".jpg");
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, Uri.fromFile(portrait));
            icon.setImageBitmap(bm);
        }catch (IOException e) {
            e.printStackTrace();
        }

        TextView nickname_Label=(TextView)view.findViewById(R.id.nickname);
        nickname_Label.setText(nickname);//昵称 也从服务器上取

        TextView emailaddr=(TextView)view.findViewById(R.id.emailaddress);
        emailaddr.setText(email);//邮箱地址 服务器上取

        //“我的作品”部分 歌曲item点击事件在mysongsfragment里设置
        pageAdapter = new CenterAdapter(getChildFragmentManager(), view.getContext());
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);
        tabLayout = (TabLayout)view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        //下面是页面底部的播放器 需要给按钮设定功能 绑定进度条 还要绑定“我的作品”和“本地作品”里的歌与播放器
        last=(ImageView)view.findViewById(R.id.btn_last);
        last.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {}});
        play=(ImageView)view.findViewById(R.id.btn_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {mMediaPlayer.start();
            }});
        next=(ImageView)view.findViewById(R.id.btn_next);
        progress=(SeekBar)view.findViewById(R.id.seekbar);//进度条

        last.setImageResource(R.drawable.last);
        play.setImageResource(R.drawable.pause);
        next.setImageResource(R.drawable.next);
        onplaysongname=(TextView)view.findViewById(R.id.songname);
        //正在播放的歌名
        onplaysongname.setText("无正在播放歌曲");

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

}
