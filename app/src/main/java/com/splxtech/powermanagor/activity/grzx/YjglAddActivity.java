package com.splxtech.powermanagor.activity.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.YjglItemAdapter;
import com.splxtech.powermanagor.entity.ElectricityMeter;
import com.splxtech.splxapplib.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by li300 on 2016/9/26 0026.
 */

public class YjglAddActivity extends AppBaseActivity
{
    private Button backButton;
    private List<ElectricityMeter> meterList = new ArrayList<>();
    private YjglItemAdapter yjglItemAdapter;
    private GridView gridView;
    private BaseActivity baseActivity=this;

    @Override
    protected void initVariables()
    {
        initYjglItemList();
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_yjgladd);
        backButton = (Button)findViewById(R.id.yjgladd_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.finish();
            }
        });
        yjglItemAdapter = new YjglItemAdapter(this,R.layout.item_yjgl_grid,meterList);
        gridView = (GridView)findViewById(R.id.yjgladd_grid_content);
        gridView.setAdapter(yjglItemAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1)
                {
                    gotoYjgl1Add();
                }
            }
        });
    }

    @Override
    protected void loadData()
    {

    }

    private void initYjglItemList()
    {
        if(meterList.size()==0)
        {
            meterList.add(new ElectricityMeter("智能开关",2,R.drawable.znkg));
            meterList.add(new ElectricityMeter("智能电表",1,R.drawable.db));
            meterList.add(new ElectricityMeter("智能电器监测表",3,R.drawable.pmsb));
        }
    }
    private void gotoYjgl1Add()
    {
        Intent intent = new Intent(YjglAddActivity.this,YjglAdd1Activity.class);
        startActivity(intent);
        this.finish();
    }
}
