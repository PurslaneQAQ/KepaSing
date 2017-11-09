package com.example.kepa.kepasing;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;

import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;

/**
 * Created by Administrator on 2017/10/25 0025.
 */

public class MainPageFragment extends Fragment {

    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPage;
    private MainPageViewAdapter pageAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    public static MainPageFragment newInstance() {
        Bundle args = new Bundle();
        MainPageFragment pageFragment = new MainPageFragment();
        pageFragment.setArguments(args);
        return pageFragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPage = getArguments().getInt(ARG_PAGE);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainpage_layout, container, false);
        pageAdapter = new MainPageViewAdapter(getChildFragmentManager(), view.getContext());
        viewPager = (ViewPager)view.findViewById(R.id.viewpager);
        viewPager.setAdapter(pageAdapter);
        tabLayout = (TabLayout)view.findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        return view;
    }
}
