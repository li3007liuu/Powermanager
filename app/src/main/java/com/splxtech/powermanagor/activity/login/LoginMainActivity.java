package com.splxtech.powermanagor.activity.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.splxtech.powermanagor.Base.AppBaseActivity;
import com.splxtech.powermanagor.R;
import com.splxtech.powermanagor.activity.news.NewsActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.powermanagor.engine.User;


public class LoginMainActivity extends AppBaseActivity {

	private static final int LOGIN_REDIRECT_OUTSIDE = 3000;	//登录后跳转到其它页面
	private static final int LOGIN_REDIRECT_INSIDE = 3001;	//登录后仍然在本页面

	@Override
	protected void initVariables() {

	}	

	@Override
	protected void initViews(Bundle savedInstanceState) {
		setContentView(R.layout.activity_login_main);
		
		Button btnLogin1 = (Button)findViewById(R.id.btnLogin1);
		btnLogin1.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//按钮1跳转至登录页面
				Intent intent = new Intent(LoginMainActivity.this, 
						LoginActivity.class);
				startActivity(intent);
			}		
		});

		Button btnLogin2 = (Button)findViewById(R.id.btnLogin2);
		btnLogin2.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//检查是否已经登录，如果已经登录则调到新闻页面，没有登录则进行登录
				if(User.getInstance().isLogin()) {
					gotoNewsActivity();
				} else {
					Intent intent = new Intent(LoginMainActivity.this, 
							LoginActivity.class);
					intent.putExtra(AppConstants.NeedCallback, true);
					startActivityForResult(intent, LOGIN_REDIRECT_OUTSIDE);
				}
			}		
		});

		Button btnLogin3 = (Button)findViewById(R.id.btnLogin3);
		btnLogin3.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if(User.getInstance().isLogin()) {
					changeText();
				} else {
					Intent intent = new Intent(LoginMainActivity.this, 
							LoginActivity.class);
					intent.putExtra(AppConstants.NeedCallback, true);
					startActivityForResult(intent, LOGIN_REDIRECT_INSIDE);
				}
			}		
		});
		
		Button btnLogin4 = (Button)findViewById(R.id.btnLogin4);
		btnLogin4.setOnClickListener(new OnClickListener(){
			//调用cookie过期
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(LoginMainActivity.this, 
						CookieExpiredActivity.class);
				intent.putExtra(AppConstants.NeedCallback, true);
				startActivity(intent);
			}		
		});
		
		Button btnLogout = (Button)findViewById(R.id.btnLogout);
		btnLogout.setOnClickListener(new OnClickListener(){
			//重置user状态
			@Override
			public void onClick(View v) {
				User.getInstance().reset();
			}		
		});
	}

	@Override
	protected void loadData() {

	}
	
	@Override
	protected void onActivityResult(int requestCode, 
			int resultCode, Intent data) {
		if (resultCode != Activity.RESULT_OK) {
			return;
		}
		
		switch (requestCode) {
		case LOGIN_REDIRECT_OUTSIDE:
			gotoNewsActivity();
			break;
		case LOGIN_REDIRECT_INSIDE:
			changeText();
			break;
		default:
			break;
		}
	} 
		
	private void gotoNewsActivity() {
		Intent intent = new Intent(LoginMainActivity.this, 
				NewsActivity.class);
		startActivity(intent);
	}
	
	private void changeText() {
		TextView textView1 = (TextView)findViewById(R.id.textView1);
		textView1.setText("1");
	}
}