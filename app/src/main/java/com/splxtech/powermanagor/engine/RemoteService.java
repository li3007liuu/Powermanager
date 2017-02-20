package com.splxtech.powermanagor.engine;

import android.app.Activity;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.mockdata.MockService;
import com.splxtech.splxapplib.activity.BaseActivity;
import com.splxtech.splxapplib.net.DefaultThreadPool;
import com.splxtech.splxapplib.net.HttpRequest;
import com.splxtech.splxapplib.net.RequestCallback;
import com.splxtech.splxapplib.net.RequestManager;
import com.splxtech.splxapplib.net.RequestParameter;
import com.splxtech.splxapplib.net.Response;
import com.splxtech.splxapplib.net.URLData;
import com.splxtech.splxapplib.net.UrlConfigManager;


public class RemoteService {
	private static RemoteService service = null;

	private RemoteService() {

	}

	public static synchronized RemoteService getInstance() {
		if (RemoteService.service == null) {
			RemoteService.service = new RemoteService();
		}
		return RemoteService.service;
	}

	public void invoke(final BaseActivity activity,
			final String apiKey,
			final List<RequestParameter> params,
			final RequestCallback callBack) {

		final URLData urlData = UrlConfigManager.findURL(activity, apiKey);
		//从MockClass中读取数据
		if (urlData.getMockClass() != null) {
			try {
				MockService mockService = (MockService) Class.forName(
						urlData.getMockClass()).newInstance();
				String strResponse = mockService.getJsonData();

				final Response responseInJson = JSON.parseObject(strResponse,
						Response.class);
				if (callBack != null) {
					if (responseInJson.hasError()) {
						callBack.onFail(responseInJson.getErrorMessage());
					} else {
						callBack.onSuccess(responseInJson.getResult());
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			HttpRequest request = activity.getRequestManager().createRequest(
					urlData, params, callBack);
			DefaultThreadPool.getInstance().execute(request);
		}
	}
	public void invoke(final Activity activity,
					   RequestManager requestManager,
					   final String apiKey,
					   final List<RequestParameter> params,
					   final RequestCallback callBack) {

		final URLData urlData = UrlConfigManager.findURL(activity, apiKey);
		//从MockClass中读取数据
		if (urlData.getMockClass() != null) {
			try {
				MockService mockService = (MockService) Class.forName(
						urlData.getMockClass()).newInstance();
				String strResponse = mockService.getJsonData();

				final Response responseInJson = JSON.parseObject(strResponse,
						Response.class);
				if (callBack != null) {
					if (responseInJson.hasError()) {
						callBack.onFail(responseInJson.getErrorMessage());
					} else {
						callBack.onSuccess(responseInJson.getResult());
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			HttpRequest request = requestManager.createRequest(
					urlData, params, callBack);
			DefaultThreadPool.getInstance().execute(request);
		}
	}
}