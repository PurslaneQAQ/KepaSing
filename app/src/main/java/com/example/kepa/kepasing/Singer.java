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
    public static List<Singer> singers;
    static String FromServer;
    static boolean finished = false;

    public Singer(String index, String name) {
        this.index = index;
        this.name = name;
    }

    public String getIndex() {
        return index;
    }

    public String getName() {
        return name;
    }

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
                singers.add(new Singer(ja.getJSONObject(i).getString("index"), ja.getJSONObject(i).getString("singer")));
                System.out.println("\nsinger" + i + " = "+singers.get(i-1).getName());
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
        new Thread(runnable).start();
        /*singers.add(new Singer("A", "Avril Lavigne"));
        singers.add(new Singer("B", "B.O.B"));
        singers.add(new Singer("C", "Carrie Underwood"));
        singers.add(new Singer("D", "迪玛希"));
        singers.add(new Singer("E", "Ellie Goulding"));
        singers.add(new Singer("E", "Eminem"));
        singers.add(new Singer("F", "Fall Out Boy"));
        singers.add(new Singer("I", "Imagine Dragons"));
        singers.add(new Singer("J", "Jessica Simpson"));
        singers.add(new Singer("K", "Katy Perry"));
        singers.add(new Singer("L", "Lana Del Rey"));
        singers.add(new Singer("P", "Paul"));
        singers.add(new Singer("P", "Peter"));
        singers.add(new Singer("S", "Sarah Brightman"));
        singers.add(new Singer("R", "Rihanna"));
        singers.add(new Singer("T", "Taylor Swift"));
        singers.add(new Singer("T", "Tonya Mitchell"));
        singers.add(new Singer("W", "王菲"));
        singers.add(new Singer("X", "薛之谦"));
        singers.add(new Singer("Y", "杨宗纬"));
        singers.add(new Singer("Z", "张杰"));*/
        while(!finished){}
        return singers;
    }
}
