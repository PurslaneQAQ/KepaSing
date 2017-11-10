package com.example.kepa.kepasing;

import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.jph.takephoto.app.TakePhoto;
import com.jph.takephoto.app.TakePhotoActivity;
import com.jph.takephoto.compress.CompressConfig;
import com.jph.takephoto.model.CropOptions;
import com.jph.takephoto.model.TResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileWriter;
import java.text.ParseException;

import java.io.File;
import java.io.IOException;

import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;
import static com.example.kepa.kepasing.MainActivity.password;

public class signup extends TakePhotoActivity {
    //private Client client;
    //private String UserID;
    private String FromServer;

    private ImageView addicon;
    private String email;
    private EditText email_label;
    private EditText password_label;
    private String nickname;
    private EditText nickname_label;
    private Uri imageUri = null;
    private TakePhoto takePhoto;
    private File recentTouxiang;

    private String result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        email_label = (EditText) findViewById(R.id.email);
        password_label = (EditText) findViewById(R.id.password);
        nickname_label = (EditText) findViewById(R.id.nickname);
        recentTouxiang = new File(getExternalCacheDir() +  "/img/" + "user" + ".png");//abc should be replaced by userID

        ImageView gobackbtn = (ImageView) findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recentTouxiang.exists()) {
                    deleteFile(recentTouxiang.getName());
                }
                onBackPressed();
            }
        });

        Button submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {//提交按钮点击监听
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.i("client", "wozhendeyaojinqule");
                    try {
                        FromServer = client.sendString(BuildJson());
                        Log.i("client", FromServer);
                        UserID = ParseJson(FromServer);
                        Log.i("client", UserID);
                        if (UserID != null) {
                            File Touxiang = new File(getExternalFilesDir("img") + "/user/" + UserID + ".png");
                            if(!Touxiang.getParentFile().exists()){
                                Touxiang.getParentFile().mkdir();
                            }
                            if(recentTouxiang.exists()){
                                recentTouxiang.renameTo(Touxiang);
                                System.out.println("I beg you come in");
                                imgWatermark wm = new imgWatermark();
                                wm.watermarkBitmap(recentTouxiang.toString(), getResources());
                                client.sendFile(Touxiang.toString());
                            }
                            Intent intent = new Intent(signup.this, mainpage.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        System.out.println("build json failed");
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    //FileTransferClient upload  = new FileTransferClient(recentTouxiang);
                }
            };

            @Override
            public void onClick(View v) {
                email = email_label.getText().toString();
                nickname = nickname_label.getText().toString();
                password = password_label.getText().toString();
                //if (email.length() < 5 || email.indexOf("@")<=0 || email.indexOf(".") <=0) {
                if (email.length() < 5 || email.indexOf(".") <=0) {
                    Toast.makeText(signup.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
                }
                else if (nickname.length()  < 1) {
                    Toast.makeText(signup.this, "请为自己起一个可爱的名字吧！", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() < 6) {
                    Toast.makeText(signup.this, "江湖危险，请设置更长一点的密码哟！", Toast.LENGTH_SHORT).show();
                }
                else if (password.length()> 12) {
                    Toast.makeText(signup.this, "服务器表示这么长的密码我记不住啦！", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(runnable).start();
                    while (result == null){}
                    if(result.equals("true")){
                        Toast.makeText(signup.this, "注册成功！奇葩君欢迎你的加入(●ˇ∀ˇ●)", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(v.getContext(), mainpage.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(signup.this, "已经用这个账号注册过了哟！", Toast.LENGTH_SHORT).show();
                    }

                    /******************for_try***********************

                     Toast.makeText(signup.this, "注册成功！奇葩君欢迎你的加入(●ˇ∀ˇ●)", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(v.getContext(), mainpage.class);
                     startActivity(intent);
                     *************************************************/
                }
            }
        });
        addicon = (ImageView) findViewById(R.id.addicon);
        addicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加头像
                //Toast.makeText(signup.this, "加头像", Toast.LENGTH_SHORT).show();
                if (!recentTouxiang.getParentFile().exists())
                    recentTouxiang.getParentFile().mkdirs();
                imageUri = Uri.fromFile(recentTouxiang);
                CropOptions.Builder builder=new CropOptions.Builder().setAspectX(1).setAspectY(1);
                CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(2 * 1024).setMaxPixel(200).create();
                takePhoto = getTakePhoto();
                takePhoto.onPickFromGalleryWithCrop(imageUri,builder.create());
                takePhoto.onEnableCompress(compressConfig,true);
            }
        });
    }

    @Override
    public void takeCancel() {
        super.takeCancel();
    }

    @Override
    public void takeFail(TResult result, String msg) {
        super.takeFail(result, msg);
    }

    @Override
    public void takeSuccess(TResult result) {
        ContentResolver resolver = getContentResolver();
        try {
            Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, imageUri);
            addicon.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String BuildJson() throws JSONException {

        JSONObject inf;
        inf = new JSONObject();

        try {
            //inf.put("number", );
            JSONArray array = new JSONArray();
            JSONObject arr2 = new JSONObject();
            arr2.put("type", "sign_up");
            arr2.put("username",email);
            arr2.put("nickname", nickname);
            arr2.put("password", password);
            if(recentTouxiang.exists())
                arr2.put("portrait", "true");
            else
                arr2.put("portrait", "false");
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
            java.text.ParseException {

        JSONObject jo = new JSONObject(jsonString);
        JSONArray ja = jo.getJSONArray("kepa");

        System.out.println("\n将Json数据解析为Map：");
        System.out.println("type: " +
                "" + ja.getJSONObject(0).getString("type")
                + " result: " + ja.getJSONObject(0).getString("result") + " user_ID: "
                + ja.getJSONObject(0).getString("user_ID"));

        if (ja.getJSONObject(0).getString("type").equals("sign_up")) {
            result = ja.getJSONObject(0).getString("result");
            System.out.println(result);
            if (result.equals("true")) {
                System.out.println(ja.getJSONObject(0).getString("user_ID"));
                return ja.getJSONObject(0).getString("user_ID");
            }
        }
        return null;
    }
}
