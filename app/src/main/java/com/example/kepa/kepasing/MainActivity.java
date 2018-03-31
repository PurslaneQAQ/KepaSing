package com.example.kepa.kepasing;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class MainActivity extends AppCompatActivity {

    public static String UserID;
    public static Client client;
    private String FromServer;
    private String email;
    private EditText email_label;
    public static String password;
    private EditText password_label;
    private String result = null;
    private boolean finished = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new Client();
        email_label = (EditText)findViewById(R.id.email);
        password_label = (EditText)findViewById(R.id.password);

    }
    Runnable runnable = new Runnable(){
        @Override
        public void run() {
            Log.i("client","wozhendeyaojinqule");
            try{
                FromServer = client.sendString(BuildJson());
                Log.i("client",FromServer);
                UserID = ParseJson(FromServer);
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

    /*登录按钮*/
    public void login(View view){
        email = email_label.getText().toString();
        password = password_label.getText().toString();
        if(email.length()< 1)
            Toast.makeText(MainActivity.this, "邮箱要输入完整哟！", Toast.LENGTH_SHORT).show();
        else if(password.length() < 6)
            Toast.makeText(MainActivity.this, "咦，密码不大对吧QwQ", Toast.LENGTH_SHORT).show();
        else {
            finished = false;
            new Thread(runnable).start();
            while(!finished){}
            if (result.equals("true")) {
                Intent intent = new Intent(MainActivity.this, mainpage.class);
                startActivity(intent);
            }
            else {
                if (result.equals("fail1")) {
                    Toast.makeText(MainActivity.this, "小主！还记得大明湖畔的夏雨荷吗？失忆啦，用户名错啦！", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "妈诶手滑了吗，密码不对哟！", Toast.LENGTH_SHORT).show();
                }
                result = null;
            }
        }
    }

        /******************for_try***********************
         Intent intent=new Intent(this,mainpage.class);
         startActivity(intent);
         ************************************************/
    /*注册按钮*/
    public void signup(View view){
        Intent intent = new Intent(this, signup.class);
        startActivity(intent);
    }
    private String BuildJson() throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "login");
            arr2.put("username",email);
            arr2.put("password", password);
            System.out.println(arr2.toString());
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
    public String ParseJson(String jsonString) throws JSONException,
            ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " + ja.getJSONObject(0).getString("type")
                + " result: " + ja.getJSONObject(0).getString("result") + " user_ID: "
                + ja.getJSONObject(0).getString("user_ID"));

        if (ja.getJSONObject(0).getString("type").equals("login")) {
            result = ja.getJSONObject(0).getString("result");
            System.out.println(result);
            finished = true;
            if (result.equals("true")) {
                System.out.println(ja.getJSONObject(0).getString("user_ID"));
                return ja.getJSONObject(0).getString("user_ID");
            } else {
                return null;
            }
        }
        return null;
    }
}