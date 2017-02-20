package com.splxtech.powermanagor.activity.grzx;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;

/**
 * Created by li300 on 2016/10/7 0007.
 */

public class AboutCompanyActivity extends AppBaseActivity{

    private Button returnButton;
    @Override
    protected void initVariables()
    {

    }
    @Override
    protected void initViews(Bundle savedInstanceState)
    {
        setContentView(R.layout.activity_aboutcompany);
        returnButton = (Button)findViewById(R.id.aboutcompany_button_return);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    @Override
    protected void loadData()
    {

    }
}
