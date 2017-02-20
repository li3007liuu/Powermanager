package com.splxtech.powermanagor.activity.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.MainButtonAdapter;
import com.splxtech.powermanagor.entity.MainButton;
import com.splxtech.splxapplib.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by li300 on 2016/10/7 0007.
 */

public class GyActivity extends AppBaseActivity{
    private Button returnButton;
    private ListView listView;
    private TextView versionText;
    private TextView companyText;
    private List<MainButton> mainButtonList = new ArrayList<>();
    private MainButtonAdapter mainButtonAdapter;
    private BaseActivity baseActivity = this;


    @Override
    protected void initVariables()
    {
        initMainButtonList();
    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_gy);
        returnButton = (Button)findViewById(R.id.gy_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView)findViewById(R.id.gy_list_content);
        mainButtonAdapter = new MainButtonAdapter(this,R.layout.item_main_button,mainButtonList);
        listView.setAdapter(mainButtonAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MainButton mainButton = mainButtonList.get(i);
                if(mainButton.getName()=="官方信息")
                {
                    Intent intent = new Intent(baseActivity,AboutCompanyActivity.class);
                    baseActivity.startActivity(intent);
                }
            }
        });
        versionText = (TextView)findViewById(R.id.gy_text_version);
        versionText.setText("V2.0.0");
        companyText = (TextView)findViewById(R.id.gy_text_company);
        companyText.setText("思普莱斯科技 版权所有\nCopyright@2016 SPLX Corporation.All rights reserved");

    }
    @Override
    protected void loadData()
    {

    }

    private void initMainButtonList()
    {
        if(mainButtonList.size()==0)
        {
            mainButtonList.add(new MainButton("系统信息",R.drawable.gy));
            mainButtonList.add(new MainButton("官方信息",R.drawable.gy));
            mainButtonList.add(new MainButton("帮助与反馈",R.drawable.gy));
        }
    }
}
