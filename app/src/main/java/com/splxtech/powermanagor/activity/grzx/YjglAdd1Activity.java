package com.splxtech.powermanagor.activity.grzx;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.splxapplib.activity.BaseActivity;

/**
 * Created by li300 on 2016/9/26 0026.
 */

public class YjglAdd1Activity extends AppBaseActivity
{
    private Button cancelButton;
    private Button nextButton;
    private BaseActivity baseActivity = this;

    @Override
    protected void initVariables()
    {

    }

    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_yjgladd1);
        cancelButton = (Button)findViewById(R.id.yjgladd1_button_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseActivity.finish();
            }
        });
        nextButton = (Button)findViewById(R.id.yjgladd1_button_next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoYjglAdd2();
            }
        });

    }

    @Override
    protected void loadData()
    {

    }

    private void gotoYjglAdd2()
    {
        //添加跳转至添加2页面
        Intent intent = new Intent(YjglAdd1Activity.this,SimpleLinkActivity.class);
        startActivity(intent);
        this.finish();
    }
}
