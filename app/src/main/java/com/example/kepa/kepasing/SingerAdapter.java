package com.example.kepa.kepasing;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingersViewHolder> {

    private List<Singer> singers;
    private String FromServer;
    private int layoutId;

    public SingerAdapter(List<Singer> singers, int layoutId) {
        this.singers = singers;
        this.layoutId = layoutId;
    }


    @Override
    public SingersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layoutId, null);
        return new SingersViewHolder(view);
    }

    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            Log.i("client","wozhendeyaojinqule");
            client = new Client();
            try{
                FromServer = client.sendString(BuildJson());
                Log.i("client",FromServer);
                ParseJson(FromServer);

            }catch(JSONException e){
                System.out.println("build json failed");
                e.printStackTrace();
            }catch(ParseException e){
                e.printStackTrace();
            }
            Log.i("client","wotmyijingchulaile");
//                FileTransferClient upload  = new FileTransferClient(file);
        }
    };

    private String BuildJson() throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "singer");
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

    public void ParseJson(String jsonString) throws JSONException,
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");
        JSONArray jSinger = ja.getJSONArray(1);

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " + ja.getJSONObject(0).getString("type"));

        if (ja.getJSONObject(0).getString("type").equals("singer")) {
            for (int i = 0; i < jSinger.length(); i++) {
                singers.add(new Singer(jSinger.getJSONObject(i).getString("index"), jSinger.getJSONObject(i).getString("name")));
            }
        }
    }

    @Override
    public void onBindViewHolder(final SingersViewHolder holder, final int position) {
        while(singers == null){}
        Singer singer = singers.get(position);
        new Thread(runnable).start();
        if (position == 0 || !singers.get(position-1).getIndex().equals(singer.getIndex())) {
            holder.tvIndex.setVisibility(View.VISIBLE);
            holder.tvIndex.setText(singer.getIndex());
        } else {
            holder.tvIndex.setVisibility(View.GONE);
        }
        holder.tvName.setText(singer.getName());
        //监听每行的点击事件 把歌手名传到歌手详情页面
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),asingerssong.class);
                final ArrayList<String> SingerOrder=new ArrayList<String>();
                SingerOrder.add(holder.tvName.getText().toString());
                intent.putStringArrayListExtra("Singerinfos",SingerOrder);
                v.getContext().startActivity(intent);
            }
        });

        final RecyclerView.ViewHolder vh=(RecyclerView.ViewHolder)holder;

        //如果设置了回调，就设置点击事件
        /*if (mOnItemClickListener != null){
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(vh.itemView,position);
                }
            });
        }*/
    }

    @Override
    public int getItemCount() {
        return singers.size();
    }

    class SingersViewHolder extends RecyclerView.ViewHolder {
        public RelativeLayout asinger;
        public TextView tvIndex;
        public TextView tvName;

        public SingersViewHolder(View itemView) {
            super(itemView);
            asinger=(RelativeLayout)itemView.findViewById(R.id.asinger);
            tvIndex = (TextView) itemView.findViewById(R.id.tv_index);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    /**
     * ItemClick的回调接口
     */
   /* public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }
    private OnItemClickListener mOnItemClickListener;
    public void setmOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }*/
}
