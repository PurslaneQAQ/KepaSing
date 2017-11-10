package com.example.kepa.kepasing;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextPaint;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import static java.security.AccessController.getContext;

/**
 * Created by 10591 on 2017/11/9.
 */


public class imgWatermark{
    static Bitmap watermark;
    public Resources r;

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        r = getResources();
        System.out.println("Successfully created the watermark object");
    }*/

    public void watermarkBitmap(String srcPath, Resources r) {
        this.r = r;
        System.out.println("Successfully created the watermark object");
        if (srcPath == null) {
            return;
        }
        try{
            InputStream assetFile= new FileInputStream(new File(srcPath));
            Bitmap src =BitmapFactory.decodeStream(assetFile);
            //Resources r = imgWatermark.getApplicationContext().getResources();
            watermark = BitmapFactory.decodeResource(r, R.drawable.mainicon);
            int w = src.getWidth();
            int h = src.getHeight();
            Bitmap newb= Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
            Canvas cv = new Canvas(newb);
            cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
            Paint paint=new Paint();
            //加入图片
            if (watermark != null) {
                // 缩放原图
                //bmp.getWidth(), bmp.getHeight()分别表示缩放后的位图宽高
                int ww = watermark.getWidth();
                int wh = watermark.getHeight();
                float scaleWidth = ((float)w) / ww;
                float scaleHeight = ((float)h) / wh;
                // 取得想要缩放的matrix参数
                Matrix matrix = new Matrix();
                matrix.postScale(scaleWidth, scaleHeight);
                watermark = Bitmap.createBitmap(watermark, 0, 0, ww, wh, matrix, true);
                /*for(int i = 0; i < src_pixels.length; i++){
                    int clr = src_pixels[i];
                    int  red   = (clr & 0x00ff0000) >> 16;  //取高两位
                    int  green = (clr & 0x0000ff00) >> 8; //取中两位
                    int  blue  =  clr & 0x000000ff; //取低两位
                    src_pixels[i] = clr + ((wm_pixels[i] & 0x00ff0000) >> 16 >> 2 << 2 - ((clr & 0x00ff0000) >> 16)>> 6)<< 16 +
                            ((wm_pixels[i] & 0x0000ff00) >> 8 >> 2 << 2 - ((clr & 0x0000ff00) >> 8)>> 6)<< 8 +
                            ((wm_pixels[i] & 0x000000ff) >> 2 << 2 - (clr & 0x000000ff)>> 6);
                }*/
                for(int row=0; row<h; row++){
                    for(int col=0; col<w; col++){
                        int src_pixel = src.getPixel(col, row);// ARGB
                        int wm_pixel = watermark.getPixel(col,row);
                        float f_alpha = ((float)(Color.alpha(wm_pixel)))/255;
                        int red = (Color.red(src_pixel) >> 2 << 2) + ((int)(Color.red(wm_pixel)* f_alpha)>>6); // same as (pixel >> 16) &0xff
                        int green = (Color.green(src_pixel) >> 2 << 2) + ((int)(Color.green(wm_pixel)* f_alpha)>>6);// same as (pixel >> 8) &0xff
                        int blue = (Color.blue(src_pixel) >> 2 << 2) + ((int)(Color.blue(wm_pixel)* f_alpha)>>6); // same as (pixel & 0xff)
                        int alpha = 255; // same as (pixel >>> 24)
                        newb.setPixel(col, row, Color.argb(alpha, red, green, blue));
                    }
                }

                //cv.drawBitmap(watermark, 0, 0, paint);// 在src的右下角画入水印
            }
            else
                System.out.println("meiyouzhaodao tu pian ne");


            cv.save(Canvas.ALL_SAVE_FLAG);// 保存
            cv.restore();// 存储
            try{
                saveBitmapToPNG(newb, new File(srcPath));
            }catch(IOException e){
                System.out.println("failed to save the image.");
            }
        }catch(IOException e){
            System.out.println("Unable to get file.");
        }
        return;
    }

    /*private Bitmap GetBitmap(String url)
    {
        Bitmap bitmap = null;
        InputStream in = null;
        BufferedOutputStream out = null;
        try
        {
            System.out.print(url);
            in = new BufferedInputStream(new URL.fromFile(url).openStream(), 50*1024);
            final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
            out = new BufferedOutputStream(dataStream, 50*1024);
            int ch=0;
            while((ch=(in.read()))!=-1){
                out.write(ch);
            }
            out.flush();
            byte[] data = dataStream.toByteArray();
            bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            data = null;
            return bitmap;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }*/

    private void saveBitmapToPNG(Bitmap bitmap, File file) throws IOException {
        Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(bitmap, 0, 0, null);
        OutputStream stream = new FileOutputStream(file);
        newBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        stream.close();
    }
}
