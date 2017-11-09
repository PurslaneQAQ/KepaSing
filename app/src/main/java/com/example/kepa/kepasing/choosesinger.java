package com.example.kepa.kepasing;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.gjiazhe.wavesidebar.WaveSideBar;

import java.util.ArrayList;

public class choosesinger extends AppCompatActivity {

    private RecyclerView rvSingers;
    private WaveSideBar sideBar;
    private ArrayList<Singer>singers=new ArrayList<>();
    final SingerAdapter sadapter=new SingerAdapter(singers,R.layout.item_singers);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choosesinger);
        initData();
        initView();
        ImageView gobackbtn=(ImageView)findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    private void initView() {
        rvSingers = (RecyclerView) findViewById(R.id.rv_singers);
        rvSingers.setLayoutManager(new LinearLayoutManager(this));
        rvSingers.setAdapter(sadapter);


        /*sadapter.setmOnItemClickListener(new SingerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final ArrayList<String> SingerOrder=new ArrayList<String>();
                SingerOrder.add(Integer.toString(position));
                Intent intent=new Intent(view.getContext(),asingerssong.class);
                intent.putStringArrayListExtra("Singerinfos",SingerOrder);
                startActivity(intent);
            }
        });*/


        sideBar = (WaveSideBar) findViewById(R.id.side_bar);
        sideBar.setOnSelectIndexItemListener(new WaveSideBar.OnSelectIndexItemListener() {
            @Override
            public void onSelectIndexItem(String index) {
                for (int i=0; i<singers.size(); i++) {
                    if (singers.get(i).getIndex().equals(index)) {
                        ((LinearLayoutManager) rvSingers.getLayoutManager()).scrollToPositionWithOffset(i, 0);
                        return;
                    }
                }
            }
        });
    }

    private void initData() {
        //singers.addAll(Singer.getSingers());
    }

}
