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
    public static final String IP_ADDR = "10.180.87.86";//鏈嶅姟鍣ㄥ湴鍧
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

            //璇诲彇鏈嶅姟鍣ㄧ鏁版嵁
            DataInputStream input = new DataInputStream(socket.getInputStream());
//                    //鍚戞湇鍔″櫒绔彂閫佹暟鎹
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.print("请输入: \t");
            out.writeUTF(str);
//
            FromServer = input.readUTF();
            System.out.println("服务端发过来的内容: " + FromServer);
            // 濡傛帴鏀跺埌 "OK" 鍒欐柇寮 杩炴帴

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

            //璇诲彇鏈嶅姟鍣ㄧ鏁版嵁
            DataInputStream input = new DataInputStream(socket.getInputStream());
//                    //鍚戞湇鍔″櫒绔彂閫佹暟鎹
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            System.out.print("请输入: \t");
            out.writeUTF(str);
//
            // 鏂囦欢鍚嶅拰闀垮害
            String fileName = input.readUTF();
            File directory = new File(dictionaryname);//换文件夹
            if(!directory.exists()) {
                directory.mkdir();
            }
            File file = new File(directory.getAbsolutePath() + File.separatorChar + fileName);
            FileOutputStream fos;
            System.out.println("get fos");
            fos = new FileOutputStream(file);

            // 寮 濮嬫帴鏀舵枃浠
            byte[] bytes = new byte[1024];
            int length = 0;
            while((length = input.read(bytes, 0, bytes.length)) != -1) {
                fos.write(bytes, 0, length);
                fos.flush();
            }
            System.out.println("======== 鏂囦欢鎺ユ敹鎴愬姛 [File Name锛 " + fileName + "] [Size锛 ] ========");

            // 濡傛帴鏀跺埌 "OK" 鍒欐柇寮 杩炴帴

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

                // 闁哄倸娲ｅ▎銏ゅ触瀹ュ懏瀚查梻锟介崹顔碱唺
                dos.writeUTF(file.getName());
                dos.flush();

                // 鐎殿噯鎷峰┑顔碱儎缁辫埖娼忛幘瀛樼 ù鐙呮嫹
                System.out.println("======== 鐎殿噯鎷峰┑顔碱儎缁辫埖娼忛幘瀛樼 ù鐙呮嫹 ========");
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
//        while (true) {
//            Socket socket = null;
//            try {
//                Log.i("client", "socket connect");
//                //鍒涘缓涓 涓祦濂楁帴瀛楀苟灏嗗叾杩炴帴鍒版寚瀹氫富鏈轰笂鐨勬寚瀹氱鍙ｅ彿
//
//                {
//                    String str = file.toString();
//                    Log.i("client", "str:" + str);
//                    if (file.exists()) {
//                        Log.i("socket", "file exists");
//                        fis = new FileInputStream(file);
//                        Log.i("socket", "fis:" + fis.toString());
//                        dos = new DataOutputStream(client.getOutputStream());
////                        dos = new DataOutputStream();
//                        Log.i("socket", "dos:" + dos.toString());
//
//
//                        dos.writeUTF(file.getName());
//                        Log.i("socket", "dos:writeUTF");
//                        dos.flush();
//                        Log.i("socket", "dos:flush");
//                        dos.writeLong(file.length());
//                        Log.i("socket", "dos:writelong");
//                        dos.flush();
//                        Log.i("socket", "dos:flush");
//
//
//                        System.out.println("======== 寮 濮嬩紶杈撴枃浠  ========");
//                        byte[] bytes = new byte[1024];
//                        int length = 0;
//                        long progress = 0;
//                        while ((length = fis.read(bytes, 0, bytes.length)) != -1) {
//                            dos.write(bytes, 0, length);
//                            Log.i("socket", "dos:write" + length);
//                            dos.flush();
//                            progress += length;
//                            System.out.print("| " + (100 * progress / file.length()) + "% |");
//                        }
//                        System.out.println();
//                        System.out.println("======== 鏂囦欢浼犺緭鎴愬姛 ========");
//                    }
//
//                }
////
//            } catch (Exception e) {
//                System.out.println("客户端run异常:" + e.getMessage());
//            } finally {
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        socket = null;
//                        System.out.println("客户端finally异常:" + e.getMessage());
//                    }
//                }
//            }
//            //delete
//            break;
//        }
    }
}