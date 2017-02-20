package com.splxtech.powermanagor.activity.dqjc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.RemoteException;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.Base.MqttAppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.DqsjkItemAdapter;
import com.splxtech.powermanagor.adapter.YjglDlgAdapter;
import com.splxtech.powermanagor.engine.MqttService;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.Appliance;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.MainButton;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.entity.YunService;
import com.splxtech.powermanagor.ui.swipemenulistview.SwipeMenu;
import com.splxtech.powermanagor.ui.swipemenulistview.SwipeMenuCreator;
import com.splxtech.powermanagor.ui.swipemenulistview.SwipeMenuItem;
import com.splxtech.powermanagor.ui.swipemenulistview.SwipeMenuListView;
import com.splxtech.powermanagor.utils.AppProtocol;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.splxtech.powermanagor.entity.YunService.*;


/**
 * Created by li300 on 2016/10/6 0006.
 * 测试消息发送主题是:helloapp
 * 接收主题为hello
 * 图标颜色列表 gray 157 157 158 #9D9D9E
 * 紫 232 106 204  #E86ACC 电暖气
 * 绿 49 194 124  #31C27C 电水壶
 * 红 243 102 102 #F36666 豆浆机
 * 橙 247 147 41 #F79329 饮水机
 * 蓝 0 174 239 #00AEEF 跑步机
 * 绿 42 209 123 #2AD17B 电动车
 * 黄 255 205 0 #ffCD00 摄像头
 * 蓝绿 17 194 210 #11C2D2 净水器
 */

public class DqsjkActivity extends MqttAppBaseActivity {

    private Button returnButton;
    private Button lbButton;
    private ArrayList<Appliance> applianceList;
    private SwipeMenuListView listView;
    private BaseActivity baseActivity = this;
    private PopupWindow popupWindow;
    private List<MainButton> mainButtonList = new ArrayList<>();
    private YjglDlgAdapter yjglDlgAdapter;
    private int addAppId;
    private int removedAppId;
    private int addAppState;
    private boolean addAppHandleflag = true;
    private Handler addAppHandleToUi;
    private int imageselect;
    private ProgressDialog progressDialog;
    private int PortNum;
    private DqsjkItemAdapter dqsjkItemAdapter;
    private ElectricityMeter device;
    //private int sendnum=0;
    private Appliance addappliance=new Appliance();
    private Appliance editappliance;
    @Override
    protected void initVariables()
    {
        initDqsjkDlgList();
        Intent intent = this.getIntent();
        Bundle bundle = (Bundle)intent.getBundleExtra("dvbundle");
        device =  (ElectricityMeter)bundle.getSerializable("device");
        applianceList = device.getApplianceArrayList();

        mReciver = new MessageMqttReciver(){
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                if(action.equals(MqttService.MQTT_RECE_MESSAGE_ACTION))
                {
                    String topic = intent.getStringExtra("Topic");

                    byte[] data = intent.getByteArrayExtra("Message");
                    if(topic.equals(DqActivity.hardrtopic+device.getProductId())) {
                        int[] receData = AppProtocol.getReceiverData(AppProtocol.ADDDQPORTNUM, data);
                        int[] receData2 = AppProtocol.getReceiverData(AppProtocol.REMOVEDQPORTNUM, data);
                        if (receData != null) {
                            if (receData.length == 2 && receData[0] == 1) {
                                addAppState = AppProtocol.ADDDQPORTNUM;
                            }

                        }
                        if (receData2 != null) {
                            if (receData2.length == 2 && receData2[0] == 1) {
                                addAppState = AppProtocol.REMOVEDQPORTNUM;
                            }
                        }
                    }
                    /*
                    else if(topic.equals(frtopic+Utils.getAndroidMac(DqsjkActivity.this)))
                    {
                        if(sendnum==1)
                        {
                            sendnum = 0;
                            YunReceData yunReceData = YunService.StringToObject(new String(data).toString());
                            if(yunReceData.getMsg()==1)
                            {
                                //提示添加成功
                                Utils.toastShow(baseActivity,"新模式添加成功");
                            }
                            else
                            {
                                Utils.toastShow(baseActivity,"新模式添加失败");
                            }
                        }
                        else if(sendnum == 2)
                        {
                            sendnum = 0;
                            YunReceData yunReceData = YunService.StringToObject(new String(data).toString());
                            if(yunReceData.getMsg()==1)
                            {
                                if (yunReceData.getData() != null) {
                                    if (yunReceData.getData().size() > 0) {
                                        addappliance.setId(yunReceData.getData().get(0).getId());
                                        applianceList.add(addappliance);
                                        dqsjkItemAdapter.notifyDataSetChanged();
                                    }
                                    else
                                    {
                                        Utils.toastShow(baseActivity,"添加数据库数据返回为空");
                                    }
                                }
                                else
                                {
                                    Utils.toastShow(baseActivity,"添加数据库数据返回为空");
                                }
                            }
                            else
                            {
                                Utils.toastShow(baseActivity,"添加数据库操作失败");
                            }
                        }
                        else if(sendnum==3)
                        {
                            sendnum = 0;
                            YunReceData yunReceData = YunService.StringToObject(new String(data).toString());
                            if(yunReceData.getMsg()==1)
                            {
                                Utils.toastShow(baseActivity,"电器编辑成功");
                            }
                            else
                            {
                                Utils.toastShow(baseActivity,"电器编辑失败");
                            }
                        }
                        else if(sendnum==4)
                        {
                            sendnum = 0;
                            YunReceData yunReceData = YunService.StringToObject(new String(data).toString());
                            if(yunReceData.getMsg()==1)
                            {
                                applianceList.removeAll(applianceList);
                                dqsjkItemAdapter.notifyDataSetChanged();
                            }
                            else
                            {
                                Utils.toastShow(baseActivity,"电器删除失败");
                            }

                        }
                        else if(sendnum==5)
                        {
                            sendnum = 0;
                            YunReceData yunReceData = YunService.StringToObject(new String(data).toString());
                            if(yunReceData.getMsg()==1)
                            {
                                Utils.toastShow(baseActivity,"电器删除成功");
                            }
                            else
                            {
                                Utils.toastShow(baseActivity,"电器删除失败");
                            }
                        }
                    }*/
                }
                else
                {

                }
            }
        };
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_dqsjk);
        returnButton = (Button)findViewById(R.id.dqsjk_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent data = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("applianceList",applianceList);
                data.putExtra("bundle",bundle);
                setResult(RESULT_OK,data);
                finish();
            }
        });
        lbButton = (Button)findViewById(R.id.dqsjk_button_lb);
        yjglDlgAdapter = new YjglDlgAdapter(this,R.layout.item_yjgl_dlg,mainButtonList);
        initPopupWindow();
        lbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lbButton(view);
            }
        });
        listView = (SwipeMenuListView) findViewById(R.id.dqsjk_list_content);
        SwipeMenuCreator creator = new SwipeMenuCreator(){
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem updataItem = new SwipeMenuItem(getApplicationContext());
                updataItem.setWidth(Utils.dip2px(baseActivity,90));
                updataItem.setBackground(new ColorDrawable(Color.GRAY));
                updataItem.setTitle("新模式");
                updataItem.setTitleSize(20);
                updataItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(updataItem);

                SwipeMenuItem deleteItem = new SwipeMenuItem(getApplicationContext());
                deleteItem.setWidth(Utils.dip2px(baseActivity,90));
                deleteItem.setBackground(new ColorDrawable(Color.RED));
                deleteItem.setTitle("删除");
                deleteItem.setTitleSize(20);
                deleteItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(deleteItem);
            }
        };
        listView.setMenuCreator(creator);
        dqsjkItemAdapter = new DqsjkItemAdapter(this,R.layout.item_dqsjklist,applianceList);
        listView.setAdapter(dqsjkItemAdapter);
        listView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                //索引为上面菜单的列表 1为删除
                int appid = position + 1;
                int temp = (appid&0xff)*256;
                switch (index)
                {
                    case 0:
                        showAddPickDialog("选择添加电器"+appid+"新模式的方法",temp+3);
                        break;
                    case 1:
                        //添加删除某一电器函数
                        deleteApp(appid);
                        break;
                }
                return false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Appliance appliance = applianceList.get(i);
                dialog_appliance("编辑电器",appliance);
            }
        });



    }
    @Override
    protected void loadData()
    {

    }

    @Override
    public void onBackPressed()
    {
        Intent data = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("applianceList",applianceList);
        data.putExtra("bundle",bundle);
        setResult(RESULT_OK,data);
        super.onBackPressed();
        //finish();
    }

    private void initDqsjkDlgList()
    {
        if(mainButtonList.size()==0)
        {
            mainButtonList.add(new MainButton("添加",R.drawable.add));
            mainButtonList.add(new MainButton("全部删除",R.drawable.delete));
        }
    }
    private void initPopupWindow()
    {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view2 = inflater.inflate(R.layout.pop_yjgl_dlg,null);
        ListView listView=(ListView)view2.findViewById(R.id.pyjgl_list_content);
        listView.setAdapter(yjglDlgAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                popupWindow.dismiss();
                if(i==0)
                {
                    //显示添加对话框
                    showAddPickDialog("请选择添加电器的方式",1);
                }
                else if(i==1)
                {
                    //显示全部删除对话框
                    deleteAllSure();
                }
            }

        });
        popupWindow = new PopupWindow(view2,480, WindowManager.LayoutParams.WRAP_CONTENT,false);
        Resources resources = getResources();
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_corners_popbkg));
        popupWindow.setAnimationStyle(R.style.AnimTools);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);

    }
    private void lbButton(View view)
    {
        if(popupWindow.isShowing())
        {
            popupWindow.dismiss();
        }
        else
        {
            popupWindow.showAsDropDown(view, 0, 30);
        }
    }
    //添加电器对话框
    public void showAddPickDialog(String title,int id)
    {
        addAppId = id;
        String[] items = new String[]{"增量添加","直接添加"};
        new AlertDialog.Builder(baseActivity)
                .setTitle(title)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        switch (i)
                        {
                            case 0:
                                addApp(addAppId);
                                break;
                            case 1:
                                addApp(addAppId+1);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .show();
    }
    private void addApp(int id)
    {
        addAppState = 0;
        try {
            String Message = new String(AppProtocol.getSendData(AppProtocol.ADDDQPORTNUM, addAppId),"ISO-8859-1");
            PortNum = AppProtocol.ADDDQPORTNUM;
            try {
                iMqttService.mqttPubMessage(DqActivity.hardstopic+device.getProductId(), Message, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        addAppHandleflag = true;
        //启动等待接收任务
        addAppHandleToUi = new Handler() {
            @Override
            public void handleMessage(android.os.Message msg)
            {
                if(msg.what == 0x110) {
                    if (addAppId / 256 == 0) {
                        //如果是新增电器显示新增电器对话框
                        dialog_appliance("新增电器",null);
                    } else {

                        //设置对应电器的模式数
                        int nmode = applianceList.get(addAppId / 256 - 1).getModeNum();
                        editappliance = applianceList.get(addAppId / 256 - 1);
                        editappliance.setModeNum(nmode+1);
                        upDateAppList();
                        //applianceList.get(addAppId / 256 - 1).setModeNum(nmode + 1);
                        //发送添加更新模式至数据库
                        /*
                        Map<String,Object> map = new HashMap<String,Object>();
                        map.put(YunService.tappliance_id,applianceList.get(addAppId / 256 - 1).getId());
                        map.put(YunService.tappliance_modenum,applianceList.get(addAppId / 256 - 1).getModeNum());
                        String smsg = YunService.ToJsonString(YunService.serupdateapplist,0,map);
                        //sendnum = 1;
                        //更新电器
                        //sendMessages(smsg);

                        dqsjkItemAdapter.notifyDataSetChanged();*/

                    }
                }
                else if(msg.what == 0x113)
                {
                    //提示添加失败
                    Utils.toastShow(baseActivity,"电器添加失败,请重新添加");
                }
                super.handleMessage(msg);
            }
        };
        //打开等待对话框，等待用户进行添加
        progressDlg("新增电器","请打开需要添加的电器,等待……");
    }

    //进度对话框
    private void progressDlg(String title,String message){
        progressDialog = new ProgressDialog(baseActivity);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.setMessage(message);
        progressDialog.setTitle(title);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setProgress(5);
        new Thread(){
            public void run(){
                while (addAppHandleflag){
                    try{
                        Thread.sleep(400);
                        int p = progressDialog.getProgress();
                        p++;
                        progressDialog.setProgress(p);
                        if(addAppState==AppProtocol.ADDDQPORTNUM)
                        {
                            progressDialog.dismiss();
                            Looper.prepare();
                            android.os.Message message1 = new android.os.Message();
                            message1.what = 0x110;
                            message1.obj = 1;
                            addAppHandleToUi.sendMessage(message1);
                            Looper.loop();
                            addAppHandleflag = false;
                        }
                        else if(addAppState==AppProtocol.REMOVEDQPORTNUM)
                        {
                            progressDialog.dismiss();
                            Looper.prepare();
                            android.os.Message message1 = new android.os.Message();
                            message1.what = 0x112;
                            message1.obj = 1;
                            addAppHandleToUi.sendMessage(message1);
                            Looper.loop();
                            addAppHandleflag = false;
                        }
                        else if(progressDialog.getProgress()>=100)
                        {
                            progressDialog.dismiss();
                            try {
                                String mss = new String(AppProtocol.getSendData(AppProtocol.ADDDQPORTNUM, 0), "ISO-8859-1");
                                PortNum = 0;
                                try {
                                    iMqttService.mqttPubMessage(DqActivity.hardstopic+device.getProductId(),mss , 0);
                                }
                                catch (RemoteException e){
                                    e.printStackTrace();
                                }
                            }
                            catch (Exception e)
                            {
                                e.printStackTrace();
                            }

                            Looper.prepare();
                            android.os.Message message1 = new android.os.Message();
                            message1.what = 0x113;
                            message1.obj = 1;
                            addAppHandleToUi.sendMessage(message1);
                            Looper.loop();
                            addAppHandleflag = false;
                        }
                    }
                    catch (InterruptedException e){
                        e.printStackTrace();
                    }
                }
            };
        }.start();

    }

    private void dialog_appliance(final String title, final Appliance appliance)
    {
        LinearLayout dialog_view = (LinearLayout)getLayoutInflater().inflate(R.layout.dialog_appliance,null);
        final int[] imageIds = new int[]
                {
                    R.drawable.bjbdn1,R.drawable.bx1,R.drawable.cfj1,R.drawable.dd1,
                    R.drawable.ddc1,R.drawable.dfg1,R.drawable.dfs1,R.drawable.djj1,
                    R.drawable.dnq1,R.drawable.dsj1,R.drawable.gskt1,R.drawable.jsq1,
                    R.drawable.kt1,R.drawable.kx1,R.drawable.pbj1,R.drawable.rsq1,
                    R.drawable.ssh1,R.drawable.sxt1,R.drawable.xyj1,R.drawable.xcq1,
                    R.drawable.tsdn1,R.drawable.ysj1,R.drawable.yx1,R.drawable.wbl1
                };
        final int[] imageIds2 = new int[]
                {
                        R.drawable.bjbdn0,R.drawable.bx0,R.drawable.cfj0,R.drawable.dd0,
                        R.drawable.ddc0,R.drawable.dfg0,R.drawable.dfs0,R.drawable.djj0,
                        R.drawable.dnq0,R.drawable.dsj0,R.drawable.gskt0,R.drawable.jsq0,
                        R.drawable.kt0,R.drawable.kx0,R.drawable.pbj0,R.drawable.rsq0,
                        R.drawable.ssh0,R.drawable.sxt0,R.drawable.xyj0,R.drawable.xcq0,
                        R.drawable.tsdn0,R.drawable.ysj0,R.drawable.yx0,R.drawable.wbl0
                };
        final String[] names = new String[]
                {
                        "笔记本电脑","冰箱","电吹风","电灯",
                        "电动车","电饭锅","电风扇","豆浆机",
                        "电暖气","电视机","挂式空调","净水器",
                        "空调","烤箱","跑步机","热水器",
                        "热水壶","监控摄像头","洗衣机","吸尘器",
                        "台式机电脑","饮水机","音响","微波炉"
                };
        final ImageView switcher;
        final EditText editTextName;
        TextView textViewId;
        List<Map<String,Object>> mapList = new ArrayList<>();
        for(int i=0;i<imageIds.length;i++)
        {
            Map<String,Object> listitem = new HashMap<>();
            listitem.put("image",imageIds[i]);
            mapList.add(listitem);
        }
        switcher = (ImageView)dialog_view.findViewById(R.id.dialogapp_image_sicon);
        editTextName = (EditText)dialog_view.findViewById(R.id.dialogapp_edit_name);
        textViewId = (TextView)dialog_view.findViewById(R.id.dialogapp_text_id);
        if(title=="新增电器")
        {
            editTextName.setText("未命名"+applianceList.size());
            textViewId.setText(applianceList.size()+1+"");
            switcher.setImageResource(R.drawable.dsj1);
            imageselect = 9;
        }
        else if(title=="编辑电器")
        {
            editTextName.setText(appliance.getName());
            textViewId.setText(appliance.getAppId()+"");
            for(int i=0;i<imageIds.length;i++){
                if(imageIds[i]==appliance.getImageId())
                {
                    imageselect = i;
                }
            }
            switcher.setImageResource(imageIds[imageselect]);
        }
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,mapList,R.layout.idialogapp_cell,new String[]{"image"},new int[]{R.id.idialogapp_image} );
        GridView gridView = (GridView)dialog_view.findViewById(R.id.dialogapp_grid_image);
        gridView.setAdapter(simpleAdapter);
        gridView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switcher.setImageResource(imageIds[i]);
                imageselect = i;
                editTextName.setText(names[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                switcher.setImageResource(imageIds[i]);
                imageselect = i;
                editTextName.setText(names[i]);
            }
        });
        new AlertDialog.Builder(baseActivity)
                .setTitle(title)
                .setView(dialog_view)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                       if(title=="新增电器"){
                           //applianceList.add(new Appliance(applianceList.size()+1,applianceList.size()+1,imageIds[imageselect],imageIds2[imageselect],editTextName.getText().toString(),1));
                           addappliance.setAppId(applianceList.size()+1);
                           addappliance.setImageId(imageIds[imageselect]);
                           addappliance.setImageId2(imageIds2[imageselect]);
                           addappliance.setName(editTextName.getText().toString());
                           addappliance.setModeNum(1);
                           addAppList();
                           //发送添加命令至数据库
                           /*Map<String,Object> map = new HashMap<String,Object>();
                           map.put(YunService.tappliance_appid,addappliance.getAppId());
                           map.put(YunService.tappliance_img1,addappliance.getImageId());
                           map.put(YunService.tappliance_img2,addappliance.getImageId2());
                           map.put(YunService.tappliance_modenum,addappliance.getModeNum());
                           map.put(YunService.tappliance_name,Utils.utf8encode(addappliance.getName()));
                           map.put(YunService.tappliance_hardid,device.getTableid());
                           String smsg = YunService.ToJsonString(YunService.seraddapplist,0,map);*/
                           //sendnum = 2;
                           //新增电器
                           //sendMessages(smsg);
                       }
                        else if(title=="编辑电器")
                       {
                           editappliance = applianceList.get(appliance.getAppId()-1);
                           editappliance.setName(editTextName.getText().toString());
                           editappliance.setImageId(imageIds[imageselect]);
                           editappliance.setImageId2(imageIds2[imageselect]);
                           upDateAppList();
                           /*
                           Map<String,Object> map = new HashMap<String,Object>();
                           map.put(YunService.tappliance_id,appliance.getId());
                           map.put(YunService.tappliance_name,Utils.utf8encode(editTextName.getText().toString()));
                           map.put(YunService.tappliance_img1,imageIds[imageselect]);
                           map.put(YunService.tappliance_img2,imageIds2[imageselect]);
                           String smsg = YunService.ToJsonString(YunService.serupdateapplist,0,map);*/
                           //sendnum = 3;
                           //更新电器
                           //sendMessages(smsg);
                       }
                        //dqsjkItemAdapter.notifyDataSetChanged();
                    }
                })
                .create()
                .show();
    }

    private void deleteAllSure()
    {
        new AlertDialog.Builder(baseActivity)
                .setTitle("删除电器")
                .setMessage("是否确认删除当前数据库中全部电器？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //删除all 函数
                        deleteAll();
                    }
                })
                .setNegativeButton("取消删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    private void deleteAll()
    {
        //检查是否有在线电器
        boolean IsOnline = false;
        for(int i=0;i<applianceList.size();i++){
            if(applianceList.get(i).getOnline()==true)
            {
                IsOnline = true;
            }
        }
        if(IsOnline==false)
        {
            addAppState = 0;
            try {
                String Message = new String(AppProtocol.getSendData(AppProtocol.REMOVEDQPORTNUM, 0),"ISO-8859-1");
                PortNum = AppProtocol.REMOVEDQPORTNUM;
                try {
                    iMqttService.mqttPubMessage(DqActivity.hardstopic+device.getProductId(), Message, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            addAppHandleflag = true;
            progressDlg("删除电器","全部电器正在删除中……");
            addAppHandleToUi = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    if(msg.what == 0x112)
                    {
                        deleteAllAppList();
                        //String smsg = YunService.ToJsonString(YunService.serdeleteallapplist,device.getTableid(),null);
                        //sendnum = 4;
                        //delete全部电器
                        //sendMessages(smsg);
                        //applianceList.removeAll(applianceList);
                        //dqsjkItemAdapter.notifyDataSetChanged();
                    }
                    else if(msg.what ==0x113){
                        Utils.toastShow(baseActivity,"电器删除失败，请重新删除！");
                    }
                    super.handleMessage(msg);
                }
            };
        }
        else{
            Utils.toastShow(baseActivity,"在线电器不能删除，请关闭当前在线电器再操作！");
        }
    }
    private void deleteApp(int appid){
        //检查是否有在线电器
        boolean IsOnline = applianceList.get(appid-1).getOnline();
        if(IsOnline==false){
            addAppState = 0;
            removedAppId = appid;
            try {
                String Message = new String(AppProtocol.getSendData(AppProtocol.REMOVEDQPORTNUM, removedAppId),"ISO-8859-1");
                PortNum = AppProtocol.REMOVEDQPORTNUM;
                try {
                    iMqttService.mqttPubMessage(DqActivity.hardstopic+device.getProductId(), Message, 0);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            addAppHandleflag = true;
            progressDlg("删除电器","电器"+appid+"正在删除中……");
            addAppHandleToUi = new Handler() {
                @Override
                public void handleMessage(android.os.Message msg) {
                    if(msg.what == 0x112)
                    {
                        deleteAppList();
                    }
                    else if(msg.what ==0x113){
                        Utils.toastShow(baseActivity,"电器删除失败，请重新删除！");
                    }
                    super.handleMessage(msg);
                }
            };
        }
        else{
            Utils.toastShow(baseActivity,"在线电器不能删除，请关闭当前在线电器再操作！");
        }
    }
    private void addAppList()
    {
        //更新回调函数
        RequestCallback addAppListCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0)
                {
                    addappliance.setId(Integer.parseInt(phalEntity.getData()));
                    applianceList.add(addappliance);
                    dqsjkItemAdapter.notifyDataSetChanged();
                }
                else
                {
                    Utils.toastShow(DqsjkActivity.this,"电器写入服务器数据库失败！");
                    applianceList.add(addappliance);
                    dqsjkItemAdapter.notifyDataSetChanged();
                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("hard_id",device.getTableid()+"");
        RequestParameter rp2 = new RequestParameter("app_id",addappliance.getAppId()+"");
        RequestParameter rp3 = new RequestParameter("app_name",Utils.utf8encode(addappliance.getName()));
        RequestParameter rp4 = new RequestParameter("app_img1",addappliance.getImageId()+"");
        RequestParameter rp5 = new RequestParameter("app_img2",addappliance.getImageId2()+"");
        RequestParameter rp6 = new RequestParameter("app_modenum",addappliance.getModeNum()+"");
        params.add(rp1);
        params.add(rp2);
        params.add(rp3);
        params.add(rp4);
        params.add(rp5);
        params.add(rp6);
        RemoteService.getInstance().invoke(this, "App_Addapplist", params,
                addAppListCallback);

    }
    private void upDateAppList()
    {
        //更新回调函数
        RequestCallback upDateAppListCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    applianceList.get(editappliance.getAppId()-1).setName(editappliance.getName());
                    applianceList.get(editappliance.getAppId()-1).setImageId(editappliance.getImageId());
                    applianceList.get(editappliance.getAppId()-1).setImageId2(editappliance.getImageId2());
                    applianceList.get(editappliance.getAppId()-1).setModeNum(editappliance.getModeNum());
                    dqsjkItemAdapter.notifyDataSetChanged();
                }
                else
                {

                }
            }
        };
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name",Utils.utf8encode(editappliance.getName()));
        map.put("imageid1",editappliance.getImageId()+"");
        map.put("imageid2",editappliance.getImageId2()+"");
        map.put("modenum",editappliance.getModeNum()+"");
        String jsonString = JSON.toJSONString(map);
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("id",editappliance.getId()+"");
        RequestParameter rp2 = new RequestParameter("data",jsonString);
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(this, "App_Updateapplist", params,
                upDateAppListCallback);

    }
    private void deleteAppList()
    {
        //更新回调函数
        RequestCallback deleteAppListCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0)
                {
                    dqsjkItemAdapter.notifyDataSetChanged();
                }
                else
                {

                }
            }
        };
        applianceList.remove(removedAppId-1);
        Map<String, Object> map = new HashMap<String, Object>();
        if(applianceList!=null&&applianceList.size()>0) {

            //整理列表id值
            for (int i = 0; i < applianceList.size(); i++) {
                map.put("appid" + (i + 1), applianceList.get(i).getId() + "");
                applianceList.get(i).setAppId(i + 1);
            }

        }
        else
        {
            map.put("appid0",device.getAllapp().getId());
        }
        String jsonString = JSON.toJSONString(map);
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("id",applianceList.get(removedAppId-1).getId()+"");
        RequestParameter rp2 = new RequestParameter("data",jsonString);
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(this, "App_Deleteapplist", params,
                deleteAppListCallback);

    }
    private void deleteAllAppList()
    {
        //更新回调函数
        RequestCallback deleteAllAppListCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0)
                {
                    if(applianceList!=null&&applianceList.size()>0)
                    {
                        applianceList.removeAll(applianceList);
                        dqsjkItemAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("hard_id",device.getTableid()+"");
        params.add(rp1);
        RemoteService.getInstance().invoke(this, "App_Deleteallapplist", params,
                deleteAllAppListCallback);

    }
    /*
    private void sendMessages(String msg)
    {
        try
        {
            iMqttService.mqttPubMessage(fstopic+Utils.getAndroidMac(DqsjkActivity.this), msg, 0);
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
    }*/
}
