package com.example.kepa.kepasing;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;


public class mainpage extends AppCompatActivity {
    FrameLayout mFrameLayout;
    private MainPageFragment mainpage;
    private SingPageFragment singpage;
    private ScorePageFragment scorepage;
    private CenterPageFragment centerpage;

    //zyt
    private boolean First_Come = true;
    private String FromServer;
    private String[] new_songnames;
    private String[] new_songids;
    private String[] new_singers;
    private String[] rank_scoredids;
    private String[] rank_songnames;
    private String[] rank_userids;
    private String[] rank_usernicks;
    private double[] rank_scores;
    private String[] hot_songnames;
    private String[] hot_songids;
    private String[] hot_singers;
    public static String current_songid;
    public static int current_position;
    public static String current_scoredid;
    public static String current_judgeuserid;
    public static double current_score;
    public static int new_count;
    public static int top_count;
    public static int hot_count;
    public static boolean getmessages=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        mFrameLayout=(FrameLayout)findViewById(R.id.fragment_container);

        //zyt
        System.out.println("First_come"+First_Come);
        if(First_Come)
        {
            new Thread(runnable).start();
        }
        if(!First_Come) getmessages = true;
        while(!getmessages){}
        System.out.println("set getmessage false");
        getmessages = false;

        setDefaultFragment();
        System.out.println("set default fragment");

        //----------------------Bottom Navigation-----------------------------------------------------
        AHBottomNavigation bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("首页",R.drawable.main_unpressed);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("唱歌",R.drawable.sing_unpressed);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("评分",R.drawable.score_unpressed);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("我的",R.drawable.center_unpressed);
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));
        bottomNavigation.setAccentColor(Color.parseColor("#4FB3A4"));
        bottomNavigation.setTitleTextSize(30,28);
        bottomNavigation.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomNavigation.setBehaviorTranslationEnabled(false);
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                FragmentManager fm=getSupportFragmentManager();
                FragmentTransaction transaction=fm.beginTransaction();
                switch (position){
                    case 0:
                        if(mainpage==null){
                            mainpage=MainPageFragment.newInstance();
                        }
                        transaction.replace(R.id.fragment_container,mainpage);
                        break;
                    case 1:
                        if(singpage==null){
                            singpage=SingPageFragment.newInstance();
                        }
                        transaction.replace(R.id.fragment_container,singpage);
                        break;
                    case 2:
                        if(scorepage==null){
                            scorepage=ScorePageFragment.newInstance();
                        }
                        transaction.replace(R.id.fragment_container,scorepage);
                        break;
                    case 3:
                        if(centerpage==null){
                            centerpage=CenterPageFragment.newInstance();
                        }
                        transaction.replace(R.id.fragment_container,centerpage);
                        break;
                    default:
                        break;
                }
                transaction.commit();
                return true;
            }
        });
        First_Come = false;
    }

    //zyt
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.i("client", "wozhendeyaojinqule");
            try {
                FromServer = MainActivity.client.sendString(BuildJson(0));
                Log.i("client", FromServer);
                ParseJson(0,FromServer);
                Log.i("client", "new_top success");
                FromServer = MainActivity.client.sendString(BuildJson(1));
                Log.i("client", FromServer);
                ParseJson(1,FromServer);
                Log.i("client", "top success");
                FromServer = MainActivity.client.sendString(BuildJson(2));
                Log.i("client", FromServer);
                ParseJson(2,FromServer);
                Log.i("client", "hot success");
                getmessages = true;
                System.out.println("getmessage = true");
            } catch (JSONException e) {
                System.out.println("build json failed");
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Log.i("client", "wotmyijingchulaile");
        }
    };

    private void setDefaultFragment(){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        mainpage=MainPageFragment.newInstance();
        transaction.replace(R.id.fragment_container,mainpage);
        transaction.commit();
    }

    public String[] getNew_songnames(){
        return new_songnames;
    }

    public String[] getNew_singers(){
        return new_singers;
    }

    public String[] getRank_songnames(){
        return rank_songnames;
    }

    public String[] getRank_usernicks(){
        return rank_usernicks;
    }

    public String[] getRank_scordids(){
        return rank_scoredids;
    }

    public double[] getRank_scores(){
        return rank_scores;
    }

    public String[] getNew_songids(){
        return new_songids;
    }

    public String[] getRank_userids(){
        return rank_userids;
    }

    public String[] getHot_songnames(){
        return hot_songnames;
    }

    public String[] getHot_singers(){
        return hot_singers;
    }

    public String[] getHot_songids(){
        return hot_songids;
    }


    //json zyt
    private String BuildJson(int i) throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            if (i == 0){
                arr2.put("type", "new_song");
                System.out.println(arr2.toString());
            }
            if(i==1) {
                arr2.put("type", "top");
                System.out.println(arr2.toString());
            }
            if(i==2) {
                arr2.put("type", "hot");
                System.out.println(arr2.toString());
            }
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
    public void ParseJson(int k,String jsonString) throws JSONException,
            ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
//        System.out.println("type: " + ja.getJSONObject(0).getString("type")
//                + " song_ID: " + ja.getJSONObject(0).getString("song_ID") + " user_ID: "
//                + ja.getJSONObject(0).getString("user_ID"));
        if(k==0)
        {
            int count = 0;
            if(ja.getJSONObject(0).getString("type").equals("new_song")){
                count = ja.length()-1;
                this.new_count = count;
            }
            System.out.println(count);

            new_songids = new String[count];
            new_songnames = new String[count];
            new_singers = new String[count];
            for(int i =0;i<count;i++)
            {
                new_songids[i] = ja.getJSONObject(i+1).getString("song_ID");
                new_songnames[i] = ja.getJSONObject(i+1).getString("song_name");
                new_singers[i] = ja.getJSONObject(i+1).getString("singer");
//                System.out.println(new_songids[i]+new_songnames[i]+new_singers[i]+i);
            }
        }
        if(k==1)
        {
            int count = 0;
            if(ja.getJSONObject(0).getString("type").equals("top")){
                count = ja.length()-1;
                this.top_count = count;
            }
            System.out.println(count);

            rank_scores = new double[count];
            rank_scoredids = new String[count];
            rank_userids = new String[count];
            rank_usernicks = new String[count];
            rank_songnames = new String[count];
            int j = 0;
            for(int i =0;i<count;i++)
            {
                if(ja.getJSONObject(i+1).getString("user_ID")!=MainActivity.UserID) {
                    rank_scoredids[j] = ja.getJSONObject(i + 1).getString("scored_ID");
                    rank_songnames[j] = ja.getJSONObject(i + 1).getString("song_name");
                    rank_userids[j] = ja.getJSONObject(i + 1).getString("user_ID");
                    rank_usernicks[j] = ja.getJSONObject(i + 1).getString("user_nick");
                    rank_scores[j] = ja.getJSONObject(i + 1).getDouble("score");
//                    System.out.println(rank_scoredids[j] + rank_songnames[j] + rank_userids[j] + rank_usernicks[j] + rank_scores[j]);
                    j++;
                }
            }
            this.top_count = j;
        }
        if(k==2)
        {
            int count = 0;
            if(ja.getJSONObject(0).getString("type").equals("hot")){
                count = ja.length()-1;
                this.hot_count = count;
            }
            hot_songids = new String[count];
            hot_songnames = new String[count];
            hot_singers = new String[count];
            for(int i =0;i<count;i++)
            {
                hot_songids[i] = ja.getJSONObject(i+1).getString("song_ID");
                hot_songnames[i] = ja.getJSONObject(i+1).getString("song_name");
                hot_singers[i] = ja.getJSONObject(i+1).getString("singer");
//                System.out.println(hot_songids[i]+hot_songnames[i]+hot_singers[i]+i);
            }
        }
    }
}