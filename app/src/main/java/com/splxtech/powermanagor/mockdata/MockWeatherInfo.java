package com.splxtech.powermanagor.mockdata;

import com.alibaba.fastjson.JSON;
import com.splxtech.powermanagor.entity.WeatherInfo;
import com.splxtech.splxapplib.net.Response;


public class MockWeatherInfo extends MockService {	
	@Override
	public String getJsonData() {
		WeatherInfo weather = new WeatherInfo();
		weather.setCity("Beijing");
		weather.setCityid("10000");

		Response response = getSuccessResponse();
		response.setResult(JSON.toJSONString(weather));
		return JSON.toJSONString(response);
	}
}
