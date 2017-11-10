package com.example.kepa.kepasing;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by ASUS on 2017/10/26.
 */

public class LrcRow implements Comparable<LrcRow> {

    public final static String TAG = "LrcRow";
    public String strTime;//
    public long time;
    public String content;
    public LrcRow(){}
    public LrcRow(String strTime, long time, String content){
        this.strTime = strTime;
        this.time = time;
        this.content = content;
    }
    @Override
    public String toString(){
        return "["+strTime+"]"+content;
    }

    public static List<LrcRow> createRows(String standardLrcLine){
        int flag = 1;
        try{
            if(standardLrcLine.indexOf("[")!=0){
                return null;
            }
            if(standardLrcLine.indexOf("]")!=9)
            {
                if(standardLrcLine.indexOf("]")==10){
                    flag = 1;
                }
                else return null;
            }
            int lastIndexOfRightBracket = standardLrcLine.lastIndexOf("]");
            //读取歌词，存入content
            String content = standardLrcLine.substring(lastIndexOfRightBracket+1,standardLrcLine.length());
            //读取时间，存入times；若有多个时间，用-分割开
            String times = standardLrcLine.substring(0,lastIndexOfRightBracket+1).replace("[","-").replace("]","-");
            String arrTimes[] = times.split("-");
            List<LrcRow> listTimes = new ArrayList<LrcRow>();
            for(String temp:arrTimes){
                if(temp.trim().length()==0){
                    continue;
                }
                LrcRow lrcrow = new LrcRow(temp,timeConvert(temp),content);
                listTimes.add(lrcrow);
            }
            return listTimes;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    private static long timeConvert(String timeString){
        timeString = timeString.replace(".",":");
        String[] times = timeString.split(":");
        return Integer.valueOf(times[0])*60*1000+Integer.valueOf(times[1])*1000+Integer.valueOf(times[2]);
    }
    public int compareTo(LrcRow another){
        return (int)(this.time - another.time);
    }
}


