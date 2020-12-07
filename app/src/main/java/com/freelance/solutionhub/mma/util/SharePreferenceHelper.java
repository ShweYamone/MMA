package com.freelance.solutionhub.mma.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class SharePreferenceHelper {

	private SharedPreferences sharedPreference;

	private static String SHARE_PREFRENCE = "showtimePref";

	private static String USER_NAME_KEY = "username";
	private static String REFRESH_TOKEN = "refreshToken";

	public SharePreferenceHelper(Context context)
	{
		sharedPreference = context.getSharedPreferences(SHARE_PREFRENCE, Context.MODE_PRIVATE);
	}

	public void setLogin(String name, String token)
	{
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(USER_NAME_KEY, name);
		editor.putString(REFRESH_TOKEN, token);
		editor.commit();
	}

	public void setRefreshToken(String token) {
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(REFRESH_TOKEN, token);
		editor.commit();
	}

	public String getUserName() {
		return sharedPreference.getString(USER_NAME_KEY,"");
	}

	public String getToken() {
		return sharedPreference.getString(REFRESH_TOKEN, "");
	}

	public void logoutSharePreference()
	{
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.clear();
		editor.commit();
	}

	public boolean isLogin()
	{
		if(sharedPreference.contains(REFRESH_TOKEN) && sharedPreference.contains(USER_NAME_KEY))
		{
			return true;
		}
		else
		{
			return false;
		}
	}


}
