package recode360.spreeadminapp.models.sessions;


//class to package functions related to sessions.

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

import recode360.spreeadminapp.Activities.LoginActivity;
import recode360.spreeadminapp.app.Config;


public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file name
    private static final String PREF_NAME = "SpreeAdmin";

    // All Shared Preferences Keys
    private static final String IS_LOGIN = "IsLoggedIn";

    // User name (make variable public to access from outside)
    public static final String KEY_NAME = "name";

    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";

    //make variable public to be accessed from outside
    public static final String KEY_TOKEN = "token";

    public static final String KEY_URL = "url";

    public static final String ONBOARDING_COMPLETE = "onboarding_complete";

    public static String KEY_PASSWORD = "password";


    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String name, String email, String token, String url, String password) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        //upon login the onborading is to be shown, so false
        editor.putBoolean(ONBOARDING_COMPLETE, false);

        // Storing name in pref
        editor.putString(KEY_NAME, name);

        // Storing email in pref
        editor.putString(KEY_EMAIL, email);

        editor.putString(KEY_TOKEN, token);

        editor.putString(KEY_URL, url);

        editor.putString(KEY_PASSWORD, password);

        // commit changes
        editor.commit();
    }

    /**
     * Check login method wil check user login status
     * If false it will redirect user to login page
     * Else won't do anything
     */
    public void checkLogin() {
        // Check login status
        if (!this.isLoggedIn()) {
            // user is not logged in redirect him to Login Activity
            Intent i = new Intent(_context, LoginActivity.class);
            // Closing all the Activities
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            // Add new Flag to start new Activity
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            // Staring Login Activity
            _context.startActivity(i);
        }

        //api key set getting it from pref if the user is logged in
        Config.API_KEY = pref.getString(KEY_TOKEN, null);
        Config.USER_FULL_NAME = pref.getString(KEY_NAME, null);
        Config.USER_EMAIL = pref.getString(KEY_EMAIL, null);
        Config.URL_STORE = pref.getString(KEY_URL, null);
        Config.USER_PASSWORD = pref.getString(KEY_PASSWORD,null);

    }


    /**
     * Get stored session data
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        // user name
        user.put(KEY_NAME, pref.getString(KEY_NAME, null));

        // user email id
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        //user token
        user.put(KEY_TOKEN, pref.getString(KEY_TOKEN, null));

        user.put(KEY_URL, pref.getString(KEY_URL, null));

        user.put(KEY_PASSWORD, pref.getString(KEY_PASSWORD,null));
        // return user
        return user;
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

        // After logout redirect user to Loing Activity
        Intent i = new Intent(_context, LoginActivity.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Add new Flag to start new Activity
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Staring Login Activity
        _context.startActivity(i);
    }

    /**
     * Quick check for login
     * *
     */
    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }
}