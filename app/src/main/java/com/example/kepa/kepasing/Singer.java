package com.example.kepa.kepasing;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.kepa.kepasing.MainActivity.client;

/**
 * Created by Administrator on 2017/10/30 0030.
 */

public class Singer {
    private String index;
    private String name;
    private String id;
    public static List<Singer> singers;
    static String FromServer;
    static public boolean finished = false;

    public Singer(String index, String name, String id){
        this.index = index;
        this.name = name;
        this.id = id;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

    public String getID() { return id; }

    private static String BuildJson() throws JSONException {

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

    public static void ParseJson(String jsonString) throws JSONException,
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " + ja.getJSONObject(0).getString("type"));

        if (ja.getJSONObject(0).getString("type").equals("singer")) {
            for (int i = 1; i < ja.length(); i++) {
                singers.add(new Singer(ja.getJSONObject(i).getString("index"), ja.getJSONObject(i).getString("singer"), ja.getJSONObject(i).getString("singer_ID")));
                System.out.println("\nsinger" + i + " = " + singers.get(i - 1).getName());
            }
        }
        finished = true;
    }
    static Runnable runnable = new Runnable(){
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


    public static List<Singer> getSingers() {
        //List<Singer>
        singers = new ArrayList<>();
        finished = false;
        new Thread(runnable).start();
        while(!finished){}
        return singers;
    }
}