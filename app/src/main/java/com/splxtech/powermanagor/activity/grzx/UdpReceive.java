package com.splxtech.powermanagor.activity.grzx;

/**
 * Created by li300 on 2016/10/24 0024.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.os.Handler;

public class UdpReceive extends Thread {
    public static int CONNECT_STATE_UNRECEIVED = 0;
    public static int CONNECT_STATE_RECEIVED = 1;


    //上一版本端口是5353
//    private final static int DEFAULT_PORT = 49001;
//    private final static String DEFAULT_IP = "255.255.255.255";

    private int currentState = CONNECT_STATE_UNRECEIVED;
    private Handler handler;
    private DatagramSocket datagramSocket;

    private ConfigSuccessListener configSuccessListener;

    public UdpReceive( Handler handler,int port) {
        init(port);
        this.handler = handler;
    }

    private void init(int port){
        try {
            datagramSocket = new DatagramSocket(null);
            datagramSocket.setBroadcast(true);
            datagramSocket.setReuseAddress(true);
//            datagramSocket.bind(new InetSocketAddress(DEFAULT_IP,DEFAULT_PORT));
            datagramSocket.bind(new InetSocketAddress(port));
            currentState = CONNECT_STATE_RECEIVED;
        }catch (SocketException ex){
            ex.printStackTrace();
            currentState = CONNECT_STATE_UNRECEIVED;

        }
    }

    @Override
    public void run() {
        try {
            byte[] data = new byte[1024];
            DatagramPacket revPacket = new DatagramPacket(data, data.length);
            while (currentState == CONNECT_STATE_RECEIVED) {
                datagramSocket.receive(revPacket);
                final byte[] realData = new byte[revPacket.getLength()];
                System.arraycopy(data, 0, realData, 0, realData.length);
                System.out.println("UdpReceiver----------------->"+new String(realData));
                decode(realData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void decode(byte[] data){
        String str = new String(data);
//    	simplelink，10.10.100.254，D8B04CFC0000，USR-C322，01.01.10
        final String[] contens = str.split(",");
        if(str.startsWith("simplelink") && contens.length >= 5){
            if (configSuccessListener != null) {
//    			final String msg = "IP:"+contens[1]+"  "+"MAC:"+contens[2]+"  "+
//    		                        "DEV:"+contens[3]+"  "+"VER:"+contens[4]+"\n";
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        configSuccessListener.onConfigSuccess(contens);
                    }
                });
            }
        }
    }

    public void stopReceive(){
        currentState = CONNECT_STATE_UNRECEIVED;
        if (datagramSocket != null){
            datagramSocket.close();
            datagramSocket = null;
        }
    }


    public void setConfigSuccessListener(
            ConfigSuccessListener configSuccessListener) {
        this.configSuccessListener = configSuccessListener;
    }


    public interface ConfigSuccessListener{
        public void onConfigSuccess(String[] msgs);
    }
}
