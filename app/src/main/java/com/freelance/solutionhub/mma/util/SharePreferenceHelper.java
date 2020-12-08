package com.freelance.solutionhub.mma.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharePreferenceHelper {

	private SharedPreferences sharedPreference;

	private static String SHARE_PREFRENCE = "showtimePref";

	private static String USER_NAME_KEY = "username";
	private static String USER_PWD = "pwd";
 	private static String TOKEN = "refreshToken";

	public SharePreferenceHelper(Context context)
	{
		sharedPreference = context.getSharedPreferences(SHARE_PREFRENCE, Context.MODE_PRIVATE);
	}

	public void setLogin(String name, String pwd, String token)
	{
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(USER_NAME_KEY, name);
		editor.putString(USER_PWD, pwd);
		editor.putString(TOKEN, token);
		editor.commit();
	}

	public void setToken(String token) {
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(TOKEN, token);
	}

	public String getUserName() {
		return sharedPreference.getString(USER_NAME_KEY,"");
	}

	public String getUserPwd() {
		return sharedPreference.getString(USER_PWD, "");
	}
	public String getToken() {
		return sharedPreference.getString(TOKEN, "");
	}

	public void logoutSharePreference()
	{
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.clear();
		editor.commit();
	}

	public boolean isLogin()
	{
		if(sharedPreference.contains(TOKEN) && sharedPreference.contains(USER_NAME_KEY))
		{
			return true;
		}
		else
		{
			return false;
		}
	}


}
