package com.splxtech.powermanagor.activity.grzx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.entity.SsidItem;
import com.splxtech.powermanagor.utils.SearchSSID;
import com.splxtech.powermanagor.utils.Tool;
import com.splxtech.powermanagor.utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by li300 on 2016/9/26 0026.
 */

public class YjglAdd2Activity extends AppBaseActivity {

    private Button backButton; //跳转至添加电器界面
    private Button findButton;
    private Button configButton;
    private TextView ssidText;
    private EditText passEdit;
    private WifiManager.MulticastLock lock;
    private SearchSSID searchSSID;
    private SendMsgThread smt;
    private int targetPort = 49000;

    private ProgressDialog dialog;

    public final int RESQEST_SSID_LIST = 1;
    // 获得ssid列表指令
    private final byte[] searchCode = new byte[] {(byte)0xff,0x00,0x01,0x01,0x02};
    private Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case Tool.REC_DATA: //解析接收到的数据
                    byte[] data = (byte[])msg.obj;
                    Tool.bytesToHexString(data);
                    decodeData(data);
                    break;
                default:
                    break;
            }
        }
    };
    @Override
    protected void initVariables()
    {
        WifiManager manager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        lock = manager.createMulticastLock("fawifi");
        lock.acquire();

    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_yjgladd2);
        backButton = (Button)findViewById(R.id.yjgladd2_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoYjglAdd();
            }
        });
        ssidText = (TextView)findViewById(R.id.yjgladd2_text_wifiname);
        passEdit = (EditText)findViewById(R.id.yjgladd2_text_wifipass);
        dialog = new ProgressDialog(this);
        dialog.setMessage("Search......");
        findButton = (Button)findViewById(R.id.yjgladd2_button_findssid);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchSSID.setTargetPort(targetPort);
                dialog.show();
                smt.putMsg(searchCode);
                dismiss();
            }
        });
        configButton = (Button)findViewById(R.id.yjgladd2_button_startconfig);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ssid = ssidText.getText().toString();
                String pass = passEdit.getText().toString();
                if(TextUtils.isEmpty(ssid))
                {
                    return;
                }
                if(TextUtils.isEmpty(pass))
                {
                    pass = "";
                }
                searchSSID.setTargetPort(targetPort);
                byte[] data = Tool.generate_02_data(ssid,pass,0);
                smt.putMsg(data);
            }
        });
    }

    @Override
    protected void loadData()
    {
        searchSSID = new SearchSSID(handler);
        searchSSID.start();
        smt = new SendMsgThread(searchSSID);
        smt.start();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        lock.release();
        smt.setSend(false);
        searchSSID.setReceive(false);
        searchSSID.close();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESQEST_SSID_LIST && data != null) {
            String ssid = data.getStringExtra("ssid");
            ssidText.setText(ssid);
        }
    }


    private void gotoYjglAdd()
    {
        Intent intent = new Intent(YjglAdd2Activity.this,YjglAddActivity.class);
        startActivity(intent);
        this.finish();
    }

    private void dismiss()
    {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
            }
        },3000);
    }

    //解析数据
    private void decodeData(byte[] data)
    {
        if ((data[0] & 0xff) != 0xff)// 如果接收到的数据不是0xff开头,那么丢弃
            return;
        switch (data[3] & 0xff) {
            case 0x81:// 解析返回列表指令
                dialog.dismiss();
                ArrayList<SsidItem> ssids = Tool.decode_81_data(data);
                if (ssids.size() != 0) {
                    Intent intent = new Intent(YjglAdd2Activity.this, SsidListActivity.class);
                    intent.putExtra("ssids", ssids);
                    startActivityForResult(intent, RESQEST_SSID_LIST);
                }
                break;
            case 0x82:// 返回校验结果
                int[] values = Tool.decode_82_data(data);
                if (values[0] == 0)
                    Utils.toastShow(this,"没有可用的ssid");
                    //UIUtil.toastShow(this, R.string.no_ssid);
                else if (values[1] == 0)
                    Utils.toastShow(this,"wifi密码长度错误");
                    //UIUtil.toastShow(this, R.string.error_pasd_length);
                else if (values[0] == 1 && values[1] == 1)
                    //获取+跳转至添加成功页面

                    Utils.toastShow(this,"配置成功");
                    //UIUtil.toastShow(this, R.string.confing_end);
                break;
        }
    }
    /**
     * 发送消息的队列，每次发送数据时，只需要调用putMsg(byte[] data)方法
     *
     * @author usr_liujinqi
     *
     */
    private class SendMsgThread extends Thread {
        // 发送消息的队列
        private Queue<byte[]> sendMsgQuene = new LinkedList<byte[]>();
        // 是否发送消息
        private boolean send = true;

        private SearchSSID ss;

        public SendMsgThread(SearchSSID ss) {
            this.ss = ss;
        }

        public synchronized void putMsg(byte[] msg) {
            // 唤醒线程
            if (sendMsgQuene.size() == 0)
                notify();
            sendMsgQuene.offer(msg);
        }

        public void run() {
            synchronized (this) {
                while (send) {
                    // 当队列里的消息发送完毕后，线程等待
                    while (sendMsgQuene.size() > 0) {
                        byte[] msg = sendMsgQuene.poll();
                        if (ss != null)
                            ss.sendMsg(msg);
                    }
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void setSend(boolean send) {
            this.send = send;
        }
    }
}
