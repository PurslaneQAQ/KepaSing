package com.example.kepa.kepasing;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by ASUS on 2017/11/3.
 */

class Client {
    private FileInputStream fis;
//    private Socket client;
    private DataOutputStream dos;
    private int flag = 0;
    public static final String IP_ADDR = "207.246.108.253";//鏈嶅姟鍣ㄥ湴鍧�
    public static final int PORT = 12345;//鏈嶅姟鍣ㄧ鍙ｅ彿
    private String FromServer = null;

    public String getString(){
        return FromServer;
    }

    public void ChangeFlag(int flag){
        this.flag = flag;
    }

    public String sendString(String str){
        Log.i("client", "sendingstring");
        Socket socket = null;
        try{
            Log.i("client", "socket connecting");
            socket = new Socket(IP_ADDR, PORT);
//            client = socket;
            Log.i("client", "socket connected");

                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.print("请输入: \t");
                out.writeUTF(str);
//
                    FromServer = input.readUTF();
                    System.out.println("服务端发过来的内容: " + FromServer);

                    out.close();
                    input.close();

        }catch(Exception e){
            System.out.println("客户端run异常:" + e.getMessage());
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    socket = null;
                    System.out.println("客户端finally异常:" + e.getMessage());
                }
            }
        }
        return FromServer;
    }

    public String getFile(String str,String dictionaryname){
        Log.i("client", "sendingstring");
        Socket socket = null;
        try{
            Log.i("client", "socket connecting");
            socket = new Socket(IP_ADDR, PORT);
//            client = socket;
            Log.i("client", "socket connected");

            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.print("请输入: \t");
            out.writeUTF(str);
//
            String fileName = input.readUTF();
            long suibian = input.readLong();
            File directory = new File(dictionaryname);//换文件夹
            if(!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
            FileOutputStream fos;
            System.out.println("get fos");
            fos = new FileOutputStream(file);

            byte[] bytes = new byte[1024];
            int length = 0;
            long progress = 0;
            while((length = input.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
                progress+=length;
                mainpage.processdownload = 100*(int)progress/(int)file.length();
            }
            System.out.println("======== 鏂囦欢鎺ユ敹鎴愬姛 [File Name锛�" + fileName + "] [Size锛�] ========");

            out.close();
            input.close();
            if(fos != null)
                fos.close();
            if(input != null)
                input.close();

        }catch(Exception e){
            System.out.println("客户端run异常:" + e.getMessage());
            e.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();

                } catch (IOException e) {
                    socket = null;
                    System.out.println("客户端finally异常:" + e.getMessage());
                }
            }
        }
        return FromServer;
    }

    public void sendFile(String filename) throws Exception {
        Log.i("client", "sendingstring");
        Socket socket = null;
        try{
            Log.i("client", "socket connecting");
            socket = new Socket(IP_ADDR, PORT);
//          client = socket;
            Log.i("client", "socket connected");
            File file = new File(filename);
            if(file.exists()) {
                fis = new FileInputStream(file);
                dos = new DataOutputStream(socket.getOutputStream());

                dos.writeUTF(file.getName());
                dos.flush();

                System.out.println("======== 鐎殿噯鎷峰┑顔碱儎缁辫埖娼忛幘瀛樼�ù鐙呮嫹 ========");
                byte[] bytes = new byte[1024];
                int length = 0;
                long progress = 0;
                while((length = fis.read(bytes, 0, bytes.length)) != -1) {
                    dos.write(bytes, 0, length);
                    dos.flush();
                    progress += length;
                    System.out.print("| " + (100*progress/file.length()) + "% |");
                    System.out.print(length);
                }
                System.out.println();
                System.out.println("======== 闁哄倸娲ｅ▎銏″閻樿櫣缈婚柟瀛樺姇婵拷 ========");
                if(dos != null)
                    dos.close();

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(fis != null)
                fis.close();
            if (socket != null) {
                try {
                    socket.close();

                } catch (IOException e) {
                    socket = null;
                    System.out.println("客户端finally异常:" + e.getMessage());
                }
            }
        }
    }

    public Client() {
        //改掉
        //File file = new File("sdcard/new.wav");
        System.out.println("客户端启动...");
        System.out.println("客户端收到 \"OK\" 停止运行\n");
        System.out.println("客户端启动leba...");
    }
}