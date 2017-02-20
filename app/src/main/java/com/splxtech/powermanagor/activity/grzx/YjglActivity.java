package com.splxtech.powermanagor.activity.grzx;


import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.PowerManagerApp;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.YjglDlgAdapter;
import com.splxtech.powermanagor.adapter.YjglItemAdapter;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.powermanagor.entity.MainButton;
import com.splxtech.powermanagor.entity.PMUserInfo;
import com.splxtech.powermanagor.entity.PhalEntity;
import com.splxtech.powermanagor.utils.Utils;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestParameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by li300 on 2016/9/24 0024.
 */

public class YjglActivity extends AppBaseActivity
{
    private GridView gridView;
    private List<ElectricityMeter> meterList = new ArrayList<>();
    private YjglItemAdapter yjglItemAdapter;
    private List<MainButton> mainButtonList = new ArrayList<>();
    private YjglDlgAdapter yjglDlgAdapter;
    private Button returnButton;
    private Button lbButton;
    private BaseActivity baseActivity=this;
    private PopupWindow popupWindow;
    private PopupWindow setNameWindow;
    private boolean isDelect;
    private TextView pdTitle;
    private EditText pdName;
    private TextView pdType;
    private TextView pdId;
    private ImageView pdImage;
    private int selectI=0;

    private PowerManagerApp app;
    private PMUserInfo pmUserInfo;



    @Override
    protected void initVariables()
    {
        app = (PowerManagerApp)getApplication();
        pmUserInfo = app.getPmUserInfo();
        meterList = app.getElectricityMeters(pmUserInfo.getId());
        initYjglDlgList();
        isDelect = false;
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_yjgl);
        gridView = (GridView)findViewById(R.id.yjgl_grid_content);
        yjglItemAdapter = new YjglItemAdapter(this,R.layout.item_yjgl_grid,meterList);
        gridView.setAdapter(yjglItemAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(isDelect==true)
                {
                    meterList.get(i).setChecked( !meterList.get(i).getChecked());
                }
                else
                {
                    setNameDlg(view,i);
                }
                yjglItemAdapter.notifyDataSetChanged();
            }
        });
        returnButton = (Button)findViewById(R.id.yjgl_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishA();
            }
        });
        lbButton = (Button)findViewById(R.id.yjgl_spinner_lb);
        yjglDlgAdapter = new YjglDlgAdapter(this,R.layout.item_yjgl_dlg,mainButtonList);
        initPopupWindow();
        initSetNamePop();
        lbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lbButton(view);
            }
        });


    }

    @Override
    protected void loadData()
    {

    }

    @Override
    public void onResume()
    {
        super.onResume();
        yjglItemAdapter.notifyDataSetChanged();
    }
    @Override
    public void onDestroy()
    {
        app.setElectricityMeters(meterList,pmUserInfo.getId());
        super.onDestroy();
    }

    private void initYjglDlgList()
    {
        if(mainButtonList.size()==0)
        {
            mainButtonList.add(new MainButton("添加",R.drawable.add));
            mainButtonList.add(new MainButton("删除",R.drawable.delete));
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
                if(i==0)
                {
                    //去添加页面
                    gotoYjglAdd();

                }
                else if(i==1)
                {
                    if(isDelect==false)
                    {
                        isDelect=true;
                        lbButton.setBackgroundResource(R.drawable.delete);
                    }
                }
                popupWindow.dismiss();
            }
        });
        popupWindow = new PopupWindow(view2,400, WindowManager.LayoutParams.WRAP_CONTENT, false);
        Resources resources = getResources();
        popupWindow.setBackgroundDrawable(resources.getDrawable(R.drawable.shape_corners_popbkg));
        popupWindow.setAnimationStyle(R.style.AnimTools);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setFocusable(true);
    }
    private void gotoYjglAdd()
    {
        Intent intent = new Intent(YjglActivity.this,YjglAddActivity.class);
        startActivity(intent);
    }
    private void finishA()
    {
        this.finish();
    }

    private void lbButton(View view)
    {
        if(isDelect==false) {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            } else {
                popupWindow.showAsDropDown(view, 0, 30);
            }
        }
        else
        {
            deleteMeterList();
            isDelect = false;
            lbButton.setBackgroundResource(R.drawable.selector_lb_button);
        }
    }

    private void initSetNamePop(){
        LayoutInflater inflater = LayoutInflater.from(this);
        View view3 = inflater.inflate(R.layout.dlg_yjgl_dq,null);
        pdTitle = (TextView)view3.findViewById(R.id.dyjgl_text_title);
        pdName = (EditText)view3.findViewById(R.id.dyjgl_edit_name);
        pdType = (TextView)view3.findViewById(R.id.dyjgl_text_producttype);
        pdId = (TextView)view3.findViewById(R.id.dyjgl_text_productid);
        pdImage = (ImageView)view3.findViewById(R.id.dyjgl_image_product);
        Button okbutton = (Button)view3.findViewById(R.id.dyjgl_button_ok);
        okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upDateMeterList();
                setNameWindow.dismiss();
            }
        });
        Button cancelbutton = (Button)view3.findViewById(R.id.dyjgl_button_cancel);
        cancelbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setNameWindow.dismiss();
            }
        });
        setNameWindow = new PopupWindow(view3,600, WindowManager.LayoutParams.WRAP_CONTENT, false);
        setNameWindow.setOutsideTouchable(false);
        setNameWindow.setFocusable(true);
    }

    private void setNameDlg(View view,int i){
        selectI = i;
        pdTitle.setText("编辑");
        pdId.setText(meterList.get(i).getProductId());
        pdType.setText(meterList.get(i).getProductType()+"");
        pdName.setText(meterList.get(i).getName());
        pdImage.setImageResource(meterList.get(i).getProductImageId());
        setNameWindow.showAtLocation(view, Gravity.CENTER,0,0);
    }

    private void deleteMeterList()
    {
        //删除回调函数
        RequestCallback deleteCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    for(int i=0;i<meterList.size();i++)
                    {
                        if(meterList.get(i).getChecked()==true){
                            meterList.remove(meterList.get(i));
                        }
                    }
                    yjglItemAdapter.notifyDataSetChanged();
                    Utils.toastShow(YjglActivity.this,"设备删除完成");
                }
                else
                {
                    Utils.toastShow(YjglActivity.this,"设备删除失败");
                }
            }
        };
        String nname=pdName.getText().toString();
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("user_id",pmUserInfo.getId()+"");
        Map<String,Object> map = new HashMap<String,Object>();
        for(int i=0;i<meterList.size();i++)
        {
            if(meterList.get(i).getChecked()==true){
                map.put("hardid"+i,meterList.get(i).getTableid());
            }
        }
        String jsonString = JSON.toJSONString(map);
        RequestParameter rp2 = new RequestParameter("hard_data",jsonString);
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(this, "Hard_Deletehardlist", params,
                deleteCallback);
    }
    private void upDateMeterList()
    {

        //更新回调函数
        RequestCallback upDateCallback = new AbstractRequestCallback() {

            @Override
            public void onSuccess(String content) {
                PhalEntity phalEntity = JSON.parseObject(content, PhalEntity.class);
                if (phalEntity.getCode() == 0) {
                    meterList.get(selectI).setName(pdName.getText().toString());
                    yjglItemAdapter.notifyDataSetChanged();
                    Utils.toastShow(YjglActivity.this,"设备名称更新完成");
                }
                else
                {
                    Utils.toastShow(YjglActivity.this,"设备名称更新失败");
                }
            }
        };
        String nname=pdName.getText().toString();
        ArrayList<RequestParameter> params = new ArrayList<RequestParameter>();
        RequestParameter rp1 = new RequestParameter("hard_id",meterList.get(selectI).getTableid()+"");
        RequestParameter rp2 = new RequestParameter("hard_name",Utils.utf8encode(nname));
        params.add(rp1);
        params.add(rp2);
        RemoteService.getInstance().invoke(this, "Hard_Updatehardlist", params,
                upDateCallback);
    }
}
