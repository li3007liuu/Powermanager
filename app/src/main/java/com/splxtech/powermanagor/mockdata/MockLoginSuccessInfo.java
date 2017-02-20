package com.splxtech.powermanagor.mockdata;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.entity.UserInfo;
import com.splxtech.splxapplib.net.Response;


public class MockLoginSuccessInfo extends MockService {
	@Override
	public String getJsonData()
	{
		UserInfo userInfo = new UserInfo();
		userInfo.setLoginName("li3007liuu");
		userInfo.setUserName("萧玉竹");
		userInfo.setLoginEmail("lx2018fc@qq.com");
		userInfo.setLoginMobileNo("18818901257");
		userInfo.setLoginUser("li3007liuu");
		userInfo.setLoginPass("li3007liu");
		userInfo.setScore(100);
		Response response = getSuccessResponse();
		response.setResult(JSON.toJSONString(userInfo));
		return JSON.toJSONString(response);
	}
}
