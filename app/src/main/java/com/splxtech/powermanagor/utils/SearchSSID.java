package com.splxtech.powermanagor.utils;

import android.os.Message;
import android.os.Handler;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by li300 on 2016/9/29 0029.
 */

public class SearchSSID extends Thread{
    private Handler handler;
    private DatagramSocket socket;

    private final String IP = "255.255.255.255";
    private int PORT = 26000;

    //48899端口 c32x系列端口，用户可以用AT指令更改
    //49000端口  除c32x系列，其他wifi模块的端口
    //1902端口   有人掌控宝系列产品
    private int targetPort = 49000;
    private boolean receive = true;

    public SearchSSID(Handler handler)
    {
        this.handler = handler;
        init();
    }

    public void init()
    {
        try{
            socket = new DatagramSocket(null);
            socket.setBroadcast(true);
            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(PORT));
        }
        catch (SocketException e)
        {
            e.printStackTrace();
            sendErrorMsg("Search Thread init fail");
            return;
        }
    }

    public void run(){
        if(socket == null)
        {
            return;
        }
        try{
            byte[] data = new byte[1024];
            DatagramPacket revPacket = new DatagramPacket(data,data.length);
            while(receive)
            {
                socket.receive(revPacket);
                if(null!=handler)
                {
                    byte[] realData = new byte[revPacket.getLength()];
                    System.arraycopy(data,0,realData,0,realData.length);
                    //接收完成后将消息发送出去
                    Message msg = handler.obtainMessage(Tool.REC_DATA,realData);
                    handler.sendMessage(msg);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            socket.close();
        }
    }

    public void close()
    {
        if(socket == null)
            return;
        socket.close();
    }




    private void sendErrorMsg (String info)
    {

    }

    public void sendMsg(byte[] msg)
    {
        if(socket!=null)
        {
            try{
                DatagramPacket sendPacket = new DatagramPacket(msg,msg.length, InetAddress.getByName(IP),targetPort);
                socket.send(sendPacket);
            }
            catch (UnknownHostException e)
            {
                e.printStackTrace();
                sendErrorMsg("Send Error,UnKnownHost!");
            }
            catch (IOException e)
            {
                e.printStackTrace();
                sendErrorMsg("Send Error,IO Exception!");
            }
        }
    }
    public void setReceive (boolean receive)
    {
        this.receive = receive;
    }
    public void  setTargetPort(int targetPort)
    {
        this.targetPort = targetPort;
    }
}
