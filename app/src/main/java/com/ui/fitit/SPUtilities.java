package com.ui.fitit;

import android.content.SharedPreferences;

public class SPUtilities {
    public static String SP_ID = "sp-fit-it";
    public static String SP_USERNAME = "user";
    public static String SP_LOGGED_IN = "logged_in";
    public static String SP_GROUP_ID = "groupId";

    public static String SP_NO_USER = "$no-user$";

    // Deny creating instances of this class
    private SPUtilities() {
    }

    public static String getLoggedInUserName(SharedPreferences spLogin) {
        if (spLogin.getBoolean(SPUtilities.SP_LOGGED_IN, false)) {
            return spLogin.getString(SPUtilities.SP_USERNAME, SPUtilities.SP_NO_USER);
        }
        return SPUtilities.SP_NO_USER;
    }


}
