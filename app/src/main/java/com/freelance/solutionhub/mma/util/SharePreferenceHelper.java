package com.freelance.solutionhub.mma.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.provider.ContactsContract;

public class SharePreferenceHelper {

	private SharedPreferences sharedPreference;

	private static String SHARE_PREFRENCE = "showtimePref";


	private static String USER_NAME_KEY = "username";
	private static String USER_DISPLAY_NAME_KEY = "userdisplayname";
	private static String USER_ID = "userid";
 	private static String TOKEN = "refreshToken";

 	private static String PIN_CODE = "pinCode";
 	private static String LOCK = "lock";

 	private static String CMStepOneClick = "CMStepONE";
 	private static String CMStepTwoClick = "CMStepTwo";
 	private static String PMStepOneClick = "PMStepOne";

 	public void userClickCMStepOne(boolean click) {
 		SharedPreferences.Editor editor = sharedPreference.edit();
 		editor.putBoolean(CMStepOneClick, click);
 		editor.commit();
	}

	public void userClickPMStepOne(boolean click){
 		SharedPreferences.Editor editor = sharedPreference.edit();
 		editor.putBoolean(PMStepOneClick, click);
 		editor.commit();
	}

	public void userClickCMStepTwo(boolean click) {
 		SharedPreferences.Editor editor = sharedPreference.edit();
 		editor.putBoolean(CMStepTwoClick, click);
 		editor.commit();
	}

	public boolean userClickPMStepOneOrNot(){
 		return sharedPreference.getBoolean(PMStepOneClick, false);
	}
	public boolean userClickStepOneOrNot() {
 		return sharedPreference.getBoolean(CMStepOneClick, false);
	}

	public boolean userClickStepTwoOrNot() {
 		return sharedPreference.getBoolean(CMStepTwoClick, false);
	}


	public SharePreferenceHelper(Context context)
	{
		sharedPreference = context.getSharedPreferences(SHARE_PREFRENCE, Context.MODE_PRIVATE);
	}

	public void setLogin(String name, String token)
	{
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(USER_NAME_KEY, name);
		editor.putString(TOKEN, token);
		editor.commit();
	}

	public void setUserIdAndDisplayName(String userId, String displayName) {
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(USER_ID, userId);
		editor.putString(USER_DISPLAY_NAME_KEY, displayName);
		editor.commit();
	}

	public void setPinCode(String pinCode){
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(PIN_CODE, pinCode);
		editor.commit();
	}

	public void setLock(boolean lock) {
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putBoolean(LOCK, lock);
		editor.commit();
	}

	public boolean getLock() {
		return sharedPreference.getBoolean(LOCK, false);
	}

	public void setToken(String token) {
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(TOKEN, token);
		editor.commit();
	}

	public String getPinCode(){return sharedPreference.getString(PIN_CODE,"");}

	public String getUserName() {
		return sharedPreference.getString(USER_NAME_KEY,"");
	}

	public String getToken() {
		return sharedPreference.getString(TOKEN, "");
	}

	public String getDisplayName() {
		return sharedPreference.getString(USER_DISPLAY_NAME_KEY, "");
	}

	public String getUserId() {
		return sharedPreference.getString(USER_ID, "");
	}

	public void logoutSharePreference()
	{
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.remove(USER_NAME_KEY);
		editor.remove(TOKEN);
		editor.remove(USER_DISPLAY_NAME_KEY);
		editor.remove(USER_ID);
		editor.commit();
	}

	public boolean isLogin()
	{
		if(sharedPreference.contains(TOKEN) && sharedPreference.contains(USER_NAME_KEY))
		{
			return true;
		}
		return false;
	}

	public boolean hasPinCode(){
		if(sharedPreference.contains(PIN_CODE)) return true;
		return false;
	}


}
