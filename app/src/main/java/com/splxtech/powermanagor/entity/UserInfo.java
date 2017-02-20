// JSON Java Class Generator
// Written by Bruce Bao
// Used for API: http://www.weather.com.cn/data/sk/101010100.html
package com.splxtech.powermanagor.entity;

public class UserInfo {
	private String loginName;
	private String userName;
	private String loginEmail;
	private String loginMobileNo;
	private String loginUser;
	private String loginPass;
	private int score;
	private boolean loginStatus;

	public UserInfo() {

	}

	public String getLoginEmail() {return loginEmail; }
	public void setLoginEmail(String loginEmail){this.loginEmail = loginEmail;}

	public String getLoginMobileNo() {return loginMobileNo; }
	public void setLoginMobileNo(String loginMobileNo){this.loginMobileNo = loginMobileNo;}

	public String getLoginUser(){return loginUser; }
	public void setLoginUser(String loginUser){this.loginUser = loginUser; }

	public String getLoginPass(){return loginPass; }
	public void setLoginPass(String loginPass){this.loginPass = loginPass; }

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public boolean isLoginStatus() {
		return loginStatus;
	}

	public void setLoginStatus(boolean loginStatus) {
		this.loginStatus = loginStatus;
	}
}
