package com.splxtech.powermanagor.activity.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.RemoteService;
import com.splxtech.splxapplib.net.RequestCallback;


public class CookieExpiredActivity extends AppBaseActivity {
	private static final int LOGIN_EXPIRED = 3002;	//Cookie失效

	private Button btnMockExpiredAction;
	
	@Override
	protected void initVariables() {
		Bundle bundle = getIntent().getExtras();
		if (bundle == null)
			return;

		needCallback = bundle.getBoolean(AppConstants.NeedCallback, false);
	}

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login);

		// 登录事件
		btnMockExpiredAction = (Button) findViewById(R.id.btnMockExpiredAction);
		btnMockExpiredAction.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mockExpiredAction();
			}
		});
	}

	@Override
	protected void loadData() {

	}

	private void mockExpiredAction() {
		RequestCallback weatherCallback = new AbstractRequestCallback() {

			@Override
			public void onSuccess(String content) {
				// do something
			}
		};

		RemoteService.getInstance().invoke(this, "getWeatherInfo", null,
				weatherCallback);
	}
}