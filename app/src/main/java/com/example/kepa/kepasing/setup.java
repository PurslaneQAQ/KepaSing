package com.example.kepa.kepasing;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

public class setup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        ImageView gobackbtn = (ImageView) findViewById(R.id.gobackbutton);
        gobackbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //编辑个人资料
        final LinearLayout editprofile = (LinearLayout) findViewById(R.id.editprofile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), profileedit.class);
                startActivity(intent);
            }
        });
        //清除缓存
        LinearLayout clearcache = (LinearLayout) findViewById(R.id.clearcache);
        clearcache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("来自奇葩君的确认")
                        .setMessage("您确定要清除所有缓存文件？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (deleteDirectory(getExternalFilesDir("img").toString()) &&
                                        deleteDirectory(getExternalFilesDir("mp3").toString()) &&
                                        deleteDirectory(getExternalFilesDir("lrc").toString())&&
                                        deleteDirectory(getExternalCacheDir().toString()))
                                    Toast.makeText(setup.this, "清除缓存文件成功！", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(setup.this, "啊偶，似乎出了点小问题QwQ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });
        //清除本地歌曲
        final LinearLayout clearlocalsongs = (LinearLayout) findViewById(R.id.clearlocalsongs);
        clearlocalsongs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("来自奇葩君的确认")
                        .setMessage("您确定要清除所有本地歌曲？")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (deleteDirectory(getExternalFilesDir("song").toString()))
                                    Toast.makeText(setup.this, "清除本地歌曲成功！", Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(setup.this, "啊偶，似乎出了点小bug", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("否", null)
                        .show();
            }
        });
        //关于我们
        LinearLayout aboutus = (LinearLayout) findViewById(R.id.aboutus);
        aboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("关于我们")
                        .setMessage("这里是一群可爱的小天使们\n\n应用版本1.0")
                        .show();
            }
        });
    }

    public boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = files[i].delete();
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        return dirFile.delete();
    }
}

