package com.example.kepa.kepasing;

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
import com.jph.takephoto.model.MultipleCrop;
import com.jph.takephoto.model.TException;
import com.jph.takephoto.model.TResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import static com.example.kepa.kepasing.CenterPageFragment.nickname;
import static com.example.kepa.kepasing.MainActivity.UserID;
import static com.example.kepa.kepasing.MainActivity.client;
import static com.example.kepa.kepasing.MainActivity.password;


public class profileedit extends TakePhotoActivity {

    private String FromServer;
    private TakePhoto takePhoto;
    private ImageView touxiang;
    private EditText password_label;
    //private String nickname;
    private EditText nickname_label;
    private Uri imageUri;
    private File temp_file;
    private File recentTouxiang;
    private String result = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profileedit);
        ImageView goback = (ImageView) findViewById(R.id.gobackbutton);
        touxiang = (ImageView) findViewById(R.id.touxiang);
        password_label = (EditText) findViewById(R.id.password);
        nickname_label = (EditText) findViewById(R.id.nickname);
        nickname_label.setText(nickname);
        password_label.setText(password);
        temp_file=new File(getExternalCacheDir() + "/img/"+ UserID + ".png");//abd should be replaced by userID
        recentTouxiang =new File(getExternalFilesDir("img") + "/user/" + UserID + ".png");//abd should be replaced by userID

        goback.setOnClickListener(new View.OnClickListener() {//左上角返回键
            @Override
            public void onClick(View v) {
                if(temp_file.exists()){
                    deleteFile(temp_file.getName());
                }
                onBackPressed();
            }
        });
        //头像
        if(!recentTouxiang.exists()){
            touxiang.setImageResource(R.drawable.touxiang);
        }
        else {
            ContentResolver resolver = getContentResolver();
            try {
                imageUri = Uri.fromFile(recentTouxiang);
                Bitmap bm = MediaStore.Images.Media.getBitmap(resolver, imageUri);
                touxiang.setImageBitmap(bm);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        touxiang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(profileedit.this, "修改头像", Toast.LENGTH_SHORT).show();
                if (!temp_file.getParentFile().exists())temp_file.getParentFile().mkdirs();
                imageUri = Uri.fromFile(temp_file);
                CropOptions.Builder builder=new CropOptions.Builder().setAspectX(1).setAspectY(1);
                CompressConfig compressConfig=new CompressConfig.Builder().setMaxSize(2*1024).setMaxPixel(800).create();
                takePhoto = getTakePhoto();
                takePhoto.onEnableCompress(compressConfig,true);
                takePhoto.onPickFromGalleryWithCrop(imageUri,builder.create());
            }
        });

        //保存按钮
        Button saveprofile = (Button) findViewById(R.id.saveprofile);
        saveprofile.setOnClickListener(new View.OnClickListener() {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    Log.i("client", "wozhendeyaojinqule");
                    try {
                        FromServer = client.sendString(BuildJson());
                        Log.i("client", FromServer);
                        ParseJson(FromServer);
                        if(result.equals("success") && temp_file.exists()){
                            imgWatermark wm = new imgWatermark();
                            wm.watermarkBitmap(temp_file.toString(), getResources());
                            temp_file.renameTo(recentTouxiang);
                            client.sendFile(recentTouxiang.toString());
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

            private String BuildJson() throws JSONException {

                JSONObject inf;
                inf = new JSONObject();

                try {
                    //inf.put("number", );
                    JSONArray array = new JSONArray();
                    JSONObject arr2 = new JSONObject();
                    arr2.put("type", "edit");
                    arr2.put("user_ID", UserID);
                    arr2.put("nickname", nickname);
                    arr2.put("password", password);
                    if(temp_file.exists())
                        arr2.put("portrait", 1);
                    else
                        arr2.put("portrait", 0);
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
            public void ParseJson(String jsonString) throws JSONException,
                    java.text.ParseException {

                JSONObject jo = new JSONObject(jsonString);
                JSONArray ja = jo.getJSONArray("kepa");

                System.out.println("\n将Json数据解析为Map：");
                System.out.println("type: " +
                        "" + ja.getJSONObject(0).getString("type")
                        + " result: " + ja.getJSONObject(0).getString("result"));

                if (ja.getJSONObject(0).getString("type").equals("edit")) {
                    result = ja.getJSONObject(0).getString("result");
                    System.out.println(result);
                }
            }

            @Override
            public void onClick(View v) {

                nickname = nickname_label.getText().toString();
                password = password_label.getText().toString();

                if (password.length() < 6) {
                    Toast.makeText(profileedit.this, "江湖危险，请设置更长一点的密码哟！", Toast.LENGTH_SHORT).show();
                }
                else if (password.length() > 12) {
                    Toast.makeText(profileedit.this, "服务器表示这么长的密码我记不住啦！", Toast.LENGTH_SHORT).show();
                }
                else {
                    new Thread(runnable).start();
                    while(result == null){}
                    if(result.equals("success")){
                        Toast.makeText(profileedit.this, "保存成功(●ˇ∀ˇ●)", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                    else {
                        Toast.makeText(profileedit.this, "程序猿开小差了/(ㄒoㄒ)/~~", Toast.LENGTH_SHORT).show();
                    }
                }
                //
            }
        });
    }
    /**
     *  获取TakePhoto实例
     * @return
     */
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
            touxiang.setImageBitmap(bm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
