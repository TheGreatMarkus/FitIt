package com.ui.fitit;

import android.content.SharedPreferences;

public class SPUtilities {
    public static String SP_LOGIN = "login";
    public static String SP_LOGIN_USERNAME = "user";
    public static String SP_LOGIN_LOGGED_IN = "logged_in";

    public static String SP_LOGIN_NO_USER = "$no-user$";

    // Deny creating instances of this class
    private SPUtilities() {
    }

    public static String getLoggedInUserName(SharedPreferences spLogin) {
        if (spLogin.getBoolean(SPUtilities.SP_LOGIN_LOGGED_IN, false)) {
            return spLogin.getString(SPUtilities.SP_LOGIN_USERNAME, SPUtilities.SP_LOGIN_NO_USER);
        }
        return SPUtilities.SP_LOGIN_NO_USER;
    }


}
