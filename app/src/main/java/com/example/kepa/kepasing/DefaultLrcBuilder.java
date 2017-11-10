package com.example.kepa.kepasing;


/**
 * Created by ASUS on 2017/10/26.
 */
import android.util.Log;

import com.example.kepa.kepasing.ILrcBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultLrcBuilder implements ILrcBuilder {
    static final String TAG = "DefaultLrcBuilder";
    public List<LrcRow> getLrcRows(String rawLrc){
        Log.d(TAG,"getLrcRows by rawString");
        if(rawLrc == null||rawLrc.length()==0){
            Log.e(TAG,"getLrcRows rawLrc null or empty");
            return null;
        }
        //读取字符串（即歌词文件）
        StringReader reader = new StringReader(rawLrc);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LrcRow> rows = new ArrayList<LrcRow>();//歌词集合
        try{
            do{
                //按句读歌词，存入字符串line
                line = br.readLine();
                Log.d(TAG,"lrc raw line:"+line);
                if(line!=null&&line.length()>0){
                    //提取歌词时间和歌词内容
                    List<LrcRow> lrcRows = LrcRow.createRows(line);
                    if(lrcRows!=null&&lrcRows.size()>0){
                        for(LrcRow row : lrcRows){
                            rows.add(row);//将歌词插入歌词集合中
                        }
                    }
                }
            }while(line!=null);
            if(rows.size()>0)
            {
                Collections.sort(rows);//歌词按时间排序
                if(rows!=null&&rows.size()>0){
                    for(LrcRow lrcRow : rows){
                        Log.d(TAG,"lrcRow:"+lrcRow.toString());//按句打印
                    }
                }
            }
        }catch(Exception e){
            Log.e(TAG,"parse exceptioned:"+e.getMessage());
            return null;
        }finally{
            try{
                br.close();
            }catch (IOException e){
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
    }
}
