package com.splxtech.powermanagor.activity.personcenter;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.engine.User;


public class PersonCenterActivity extends AppBaseActivity {
	TextView tvPersonCenter;
	
	@Override
	protected void initVariables() {

	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_personcenter);
		
		tvPersonCenter = (TextView)findViewById(R.id.tvPersonCenter);
		tvPersonCenter.setText(
				User.getInstance().getUserName());
	}

	
	
	@Override
	protected void loadData() {

	}
}
