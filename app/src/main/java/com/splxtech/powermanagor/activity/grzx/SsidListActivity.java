package com.splxtech.powermanagor.activity.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.adapter.SsidItemAdapter;
import com.splxtech.powermanagor.entity.SsidItem;

import java.util.ArrayList;

/**
 * Created by li300 on 2016/9/29 0029.
 */

public class SsidListActivity extends AppBaseActivity{
    private Button backButton;
    private ListView listView;
    private ArrayList<SsidItem> itemArrayList = new ArrayList<>();
    private SsidItemAdapter ssidItemAdapter;

    @Override
    protected void initVariables()
    {
        itemArrayList = (ArrayList<SsidItem>)getIntent().getSerializableExtra("ssids");
    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_ssidlist);
        backButton = (Button)findViewById(R.id.ssidlist_button_return);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        listView = (ListView)findViewById(R.id.ssidlist_list_content);
        ssidItemAdapter = new SsidItemAdapter(this,R.layout.item_ssid_dis,itemArrayList);
        listView.setAdapter(ssidItemAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SsidItem ssidItem = itemArrayList.get(i);
                Intent data = new Intent();
                data.putExtra("ssid",ssidItem.getName());
                setResult(RESULT_OK,data);
                finish();
            }
        });
    }

    @Override
    protected void loadData()
    {

    }
}
