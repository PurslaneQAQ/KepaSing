package com.example.kepa.kepasing;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingersViewHolder> {

    private List<Singer> singers;
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

    @Override
    public void onBindViewHolder(final SingersViewHolder holder, final int position) {
        final Singer singer = singers.get(position);
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
            SingerOrder.add(singer.getName());
                SingerOrder.add(singer.getID());
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
