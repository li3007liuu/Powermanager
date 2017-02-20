package com.splxtech.powermanagor.utils;

/**
 * Created by li300 on 2016/10/31 0031.
 * App与下位机进行通信的协议
 * 里面包含两个方法 getSendData;getReceiverData;
 * getSendData通过通道或者其他请求去设置发送数据包
 * getReceiverData通过接收到的数据返回经过校验之后的接收到的数据包
 */

public class AppProtocol {
    //通信端口号，标记请求的端口，在等待消息队列里通过该端口号进行判断；
    //该端口号在请求数据时附值，在获取完值之后清除，以保证通信的正常；
    public static int DLCSPORTNUM = 1;
    public static int DLBXPORTNUM = 2;
    public static int DLXBPORTNUM = 4;
    public static int DQLJSBPORTNUM = 6;
    public static int ADDDQPORTNUM = 11;
    public static int REMOVEDQPORTNUM = 12;
    public static int DRLJSCPORTNUM = 13;
    public static int XBLBPORTNUM = 14;

    private static byte[] dataHeader = new byte[]{0,9,0,5,0,15,1};
    private static byte[] reqDlcs = new byte[]{0,9,0,5,0,15,1,0,12,0,0,0,0,13,10};//电力参数
    private static byte[] reqDlbx = new byte[]{0,9,0,5,0,15,1,0,13,2,0,0,1,13,10};//读取512个电流电压波形点
    private static byte[] reqDlxb = new byte[]{0,9,0,5,0,15,1,0,14,0,80,0,1,13,10};//读取40个点的电压、电流谐波
    private static byte[] reqDqljsb = new byte[]{0,9,0,5,0,15,1,0,11,0,0,0,1,13,10};//获取当前连接的设备号
    private static byte[] reqAdddq = new byte[]{0,9,0,5,0,15,1,0,16,0,2,0,0,13,10};//新增电器
    private static byte[] reqRemovedq = new byte[]{0,9,0,5,0,15,1,0,17,0,2,0,0,13,10};//删除电器
    private static byte[] reqDrjrsc  = new byte[]{0,9,0,5,0,15,1,0,18,0,0,0,0,13,10};//请求接入时长
    private static byte[] reqXblb = new byte[]{0,9,0,5,0,15,1,0,15,0,0,0,1,13,10};

    public AppProtocol()
    {

    }
    //第一个参数为端口号，第二个为其他参数
    public static byte[] getSendData(int portnum,int paramet)
    {
        if(portnum==DLCSPORTNUM)
        {
            return reqDlcs;
        }
        else if(portnum==DLBXPORTNUM)
        {
            return reqDlbx;
        }
        else if(portnum==DLXBPORTNUM)
        {
            return reqDlxb;
        }
        else if(portnum==DQLJSBPORTNUM)
        {
            return reqDqljsb;
        }
        else if(portnum==ADDDQPORTNUM)
        {
            reqAdddq[11]=(byte)(paramet/256);
            reqAdddq[12]=(byte)(paramet&0xff);
            return reqAdddq;
        }
        else if(portnum==REMOVEDQPORTNUM)
        {
            reqRemovedq[11]=(byte)(paramet/256);
            reqRemovedq[12]=(byte)(paramet&0xff);
            return reqRemovedq;
        }
        else if(portnum==DRLJSCPORTNUM)
        {
            return reqDrjrsc;
        }
        else if(portnum==XBLBPORTNUM)
        {
            return reqXblb;
        }
        else
        {
            return null;
        }
    }

    //第一个参数、端口号，第二个参数接收到的数据
    public static int[] getReceiverData(int portnum,byte[] recebyte)
    {
        if(recebyte.length>=15) {
            int cmd2 = byte2short(recebyte[7], recebyte[8]);
            if (HeadEcc(recebyte)) {
                int bytenum = byte2short(recebyte[4], recebyte[5]);
                int datanum = byte2short(recebyte[9], recebyte[10]);
                int cmd = byte2short(recebyte[7], recebyte[8]);
                if (cmd == 13 && portnum == DLBXPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else if (cmd == 12 && portnum == DLCSPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else if (cmd == 14 && portnum == DLXBPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else if (cmd == 11 && portnum == DQLJSBPORTNUM) {
                    if(datanum==0)
                    {
                        int[] tt = new int[1];
                        tt[0]=0;
                        return tt;
                    }
                    else {
                        return WriteData(recebyte, datanum);
                    }
                } else if (cmd == 16 && portnum == ADDDQPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else if (cmd == 17 && portnum == REMOVEDQPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else if (cmd == 18 && portnum == DRLJSCPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else if (cmd == 15 && portnum == XBLBPORTNUM) {
                    return WriteData(recebyte, datanum);
                } else {
                    int[] cc = new int[0];
                    return cc;
                }
            } else {
                int[] cc = new int[0];
                return cc;

            }
        }
        else
        {
            int[] cc = new int[0];
            return cc;
        }
    }

    //参数头校验 校验接收到数据头是否一致，校验接收到数据总数是否与设置的一致，
    //校验接收到数据点数是否与设置的一致
    private static boolean HeadEcc(byte[] recebyte)
    {
        int bynum = recebyte.length;
        dataHeader[4]=(byte)((bynum/256)&0xff);
        dataHeader[5]=(byte)(bynum&0xff);
        int j=0;
        for(int i=0;i<7;i++)
        {
            if(recebyte[i]==dataHeader[i])
            {
                j++;
            }
        }
        if(j==7)
        {
            byte datanumH = (byte)((byte2short(recebyte[4],recebyte[5])-15)/2/256);
            byte datanumL = (byte)(((byte2short(recebyte[4],recebyte[5])-15)/2)&0xff);
            if(datanumH==recebyte[9]&&datanumL==recebyte[10]) {
                return true;
            }
            else
            {
                return false;
            }
        }
        else
        {
            return false;
        }
    }
    //更新为有符号数
    private static int[] WriteData(byte [] inputByte,int num)
    {
        int[] receData = new int[num];
        int j;
        for(int i=0;i<num;i++)
        {
            j=i*2+13;
            receData[i] =(int)byte2short(inputByte[j],inputByte[j+1]); //temp1*256+temp2;
        }
        return receData;
    }
    private static short byte2short(byte datah,byte datal)
    {
        short s0 = (short)(datah&0xff);
        short s1 = (short)(datal&0xff);
        s0 <<=8;
        return (short)(s0|s1);
    }
}
