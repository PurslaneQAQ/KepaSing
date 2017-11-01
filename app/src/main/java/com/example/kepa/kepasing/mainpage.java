package com.example.kepa.kepasing;

import android.graphics.Color;
import android.os.Bundle;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;


public class mainpage extends AppCompatActivity {
    FrameLayout mFrameLayout;
    private MainPageFragment mainpage;
    private SingPageFragment singpage;
    private ScorePageFragment scorepage;
    private CenterPageFragment centerpage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        mFrameLayout=(FrameLayout)findViewById(R.id.fragment_container);
        setDefaultFragment();
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
    }
    private void setDefaultFragment(){
        FragmentManager fm=getSupportFragmentManager();
        FragmentTransaction transaction=fm.beginTransaction();
        mainpage=MainPageFragment.newInstance();
        transaction.replace(R.id.fragment_container,mainpage);
        transaction.commit();
    }
}
