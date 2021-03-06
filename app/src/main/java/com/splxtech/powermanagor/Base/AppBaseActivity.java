package com.splxtech.powermanagor.Base;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;


import com.splxtech.powermanagor.activity.login.LoginActivity;
import com.splxtech.powermanagor.engine.AppConstants;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.RequestCallback;


public abstract class AppBaseActivity extends BaseActivity {
	protected boolean needCallback;

	protected ProgressDialog dlg;

	public abstract class AbstractRequestCallback implements RequestCallback {

		public abstract void onSuccess(String content);

		public void onFail(String errorMessage) {
			//dlg.dismiss();

			new AlertDialog.Builder(AppBaseActivity.this).setTitle("出错啦")
					.setMessage(errorMessage).setPositiveButton("确定", null)
					.show();
		}

		public void onCookieExpired() {
			//dlg.dismiss();

			new AlertDialog.Builder(AppBaseActivity.this)
					.setTitle("出错啦")
					.setMessage("Cookie过期，请重新登录")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									Intent intent = new Intent(
											AppBaseActivity.this,
											LoginActivity.class);
									intent.putExtra(AppConstants.NeedCallback,
											true);
									startActivity(intent);
								}
							}).show();
		}
	}


}
