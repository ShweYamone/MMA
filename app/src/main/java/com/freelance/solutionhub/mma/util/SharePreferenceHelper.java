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

 	private static String CurrentMSOID = "CurrentMSOId";

 	private static String PHONE_NUMBER = "PhoneNumber";

 	public void setCurrentMSOID(String msoid) {
 		SharedPreferences.Editor editor = sharedPreference.edit();
 		editor.putString(CurrentMSOID, msoid);
 		editor.commit();
	}

	public String getPhoneNumber(){ return sharedPreference.getString(PHONE_NUMBER, ""); }
	public String getCurrentMSOID() {
 		return sharedPreference.getString(CurrentMSOID, "");
	}

 	public void userClickCMStepOne(boolean click) {
 		SharedPreferences.Editor editor = sharedPreference.edit();
 		editor.putBoolean(CMStepOneClick, click);
 		editor.commit();
	}

	public void setPhoneNumber(String phoneNumber){
		SharedPreferences.Editor editor = sharedPreference.edit();
		editor.putString(PHONE_NUMBER, phoneNumber);
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
		editor.remove(CMStepOneClick);
		editor.remove(CMStepTwoClick);
		editor.remove(PMStepOneClick);
		editor.remove(CurrentMSOID);

		/*
		* sharePreferenceHelper.userClickPMStepOne(false);
        sharePreferenceHelper.userClickCMStepOne(false);
        sharePreferenceHelper.userClickCMStepTwo(false);
        sharePreferenceHelper.setCurrentMSOID("");*/
		editor.commit();
	}

	public void deletePinCode() {
 		SharedPreferences.Editor editor = sharedPreference.edit();
 		editor.remove(PIN_CODE);
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
