package com.valleyforge.cdi.utils;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefUtils {
    private static final String TAG = LogUtils.makeLogTag(PrefUtils.class);
    /**
     * Keys of the shared preferences stored
     */
   /* public static String KEY_AUTH_TOKEN = "user_auth_token";
    public static String LOGGED_IN_USER_GENDER = "user_gender";*/
    public static String KEY_USER_LOGGED_IN = "user_logged_in";
    public static String USER_STATUS = "user_status";
    public static String USER_FK_ID = "user_fk_id";
    public static String USER_ID = "user_id";
    public static String USER_NAME = "user_name";
    public static String USER_PASSWORD = "user_password";
    public static String USER_TYPE = "user_type";
    public static String USER_IMAGE= "user_image";
    public static String USER_FRAG_SELECTED= "user_frah_selected";
    public static String PROJECT= "project";
    public static String PROJECT_OWNER= "project_owner";
    public static String COMMISION_DATE= "commision_date";
    public static String OM_STARTED= "om_started";
    public static String OTHER_DETAILS= "other_detail";
    public static String PHONE= "PHONE";
    public static String LOGGED_IN_USER_ADD = "address";
    public static String LOGGED_IN_USER_EMAIL = "user_email";
  /*  public static String LOGGED_IN_USER_EMAIL = "user_email";
    public static String LOGGED_IN_USER_ADD = "user_add";
    public static String LOGGED_IN_USER_ROLE_ID = "user_role_id";
    public static String LOGGED_IN_USER_ROLE= "user_role";
    public static String LOGGED_IN_USER_ROLE_PARENT = "user_role_parent";
    public static String LOGGED_IN_USER_ID = "user_id";
    public static String GENERATED_OTP= "gen_otp";
    public static String USER_COUNTRY= "user_country";
    public static String USER_TITLE= "user_title";
    public static String USER_CITY= "user_city";
    public static String USER_ZIP= "user_zip";
    public static String USER_PASSPORT= "user_passport";
    public static String USER_TIN_NUMBER= "user_tin_number";
    public static String USER_BIO= "user_bio";
    public static String USER_STATE= "user_state";
    public static String USER_IMAGE= "user_image";
*/
    /**
     * Constant string for file name to store the SharedPreferences of the
     * application. This is required to get the same SharedPreferences object
     * regardless of the package name of the class
     */
    private static final String FILE_NAME = "default.prefs";

    /**
     * static preference manager that is returned when any class calls
     * for a preference manager
     */
    private static SharedPreferences mSharedPreferences;

    /**
     * returns same SharedPrefs
     * through out the application
     *
     * @param context context
     * @return SharedPreference object
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(FILE_NAME,
                    Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
    }

    /**
     * Stores the auth token in the shared preferences using the PrefUtil.KEY_AUTH_TOKEN key
     *
     * @param authToken Auth token of the user received from the user
     */
   /* public static void storeAuthToken(String authToken, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(PrefUtils.KEY_AUTH_TOKEN, authToken)
                .apply();
        LogUtils.LOGE(TAG, "Auth token has been saved+" + authToken);
    }

    *//**
     * returns the auth token of the user
     * returns "" if no auth token is present
     *
     * @param context context
     * @return auth token
     *//*
    public static String getAuthToken(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PrefUtils.KEY_AUTH_TOKEN, "");
    }*/

    public static void storeProject(String project, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(PROJECT, project)
                .apply();

    }

    public static String getProject(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PROJECT, "");
    }

    public static void storeProjectOwner(String owner, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(PROJECT_OWNER, owner)
                .apply();

    }

    public static String getProjectOwner(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PROJECT_OWNER, "");
    }

    public static void storeCommisionDate(String commision, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(COMMISION_DATE, commision)
                .apply();

    }

    public static String getCommisionDate(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(COMMISION_DATE, "");
    }

    public static void storeOM(String om, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(OM_STARTED, om)
                .apply();

    }

    public static String getOM(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(OM_STARTED, "");
    }

    public static void storeOtherDetails(String other, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(OTHER_DETAILS, other)
                .apply();

    }

    public static String getOtherDetails(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(OTHER_DETAILS, "");
    }


    public static void storeUserLoggedIn(Boolean isLoggedIn, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putBoolean(PrefUtils.KEY_USER_LOGGED_IN, isLoggedIn)
                .apply();
        LogUtils.LOGD(TAG, "User loggedIn true");
    }

    public static Boolean getUserLoggedIn(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getBoolean(PrefUtils.KEY_USER_LOGGED_IN, false);
    }



    public static void storeUserStatus(String status, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_STATUS, status)
                .apply();

    }

    public static String getUserStatus(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_STATUS, "");
    }


    public static void storeUserName(String name, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_NAME, name)
                .apply();

    }

    public static String getUserName(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_NAME, "");
    }

    public static void storeUserPhone(String name, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(PHONE, name)
                .apply();

    }

    public static String getUserPhone(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PHONE, "");
    }


    public static void storeUserPassword(String password, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_PASSWORD, password)
                .apply();

    }

    public static String getUserPasspord(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_PASSWORD, "");
    }

    public static void storeFkId(String fkId, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_FK_ID, fkId)
                .apply();

    }

    public static String getFkId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_FK_ID, "");
    }

    public static void storeUserId(String userId, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_ID, userId)
                .apply();

    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_ID, "");
    }

    public static void storeUserType(String roleId, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_TYPE, roleId)
                .apply();

    }

    public static String getUserType(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_TYPE, "");
    }

    public static void storeUserImage(String image, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_IMAGE, image)
                .apply();

    }

    public static String getUserImage(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_IMAGE, "");
    }

    public static void storeUserFrag(String frag, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_FRAG_SELECTED, frag)
                .apply();

    }

    public static String getUserFrag(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_FRAG_SELECTED, "");


    }

    public static void storeUserAdd(String add, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_ADD, add)
                .apply();

    }

    public static String getUserAdd(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_ADD, "");
    }

    public static void storeEmail(String email, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_EMAIL, email)
                .apply();

    }

    public static String getEmail(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_EMAIL, "");
    }
  /*  public static void storeRoleParent(String roleParent, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_ROLE_PARENT, roleParent)
                .apply();

    }

    public static String getRoleParent(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_ROLE_PARENT, "");
    }

    public static void storeUsernId(String userId, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_ID, userId)
                .apply();

    }

    public static String getUserId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_ID, "");
    }

    public static void storeLogin(String login, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(KEY_USER_LOGGED_IN, login)
                .apply();

    }

    public static String getLogin(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(KEY_USER_LOGGED_IN, "");
    }

    public static void storeLoginDate(String loginDate, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(KEY_USER_LOGGED_DATE, loginDate)
                .apply();

    }

    public static String getLoginDate(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(KEY_USER_LOGGED_DATE, "");
    }

    public static void storeGender(String gender, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_GENDER, gender)
                .apply();

    }

    public static String getGender(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_GENDER, "");
    }

    public static void storePhone(String phone, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(KEY_USER_PHONE, phone)
                .apply();

    }

    public static String getPhone(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(KEY_USER_PHONE, "");
    }

    public static void storeOTP(String otp, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(GENERATED_OTP, otp)
                .apply();

    }

    public static String getSavedOTP(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(GENERATED_OTP, "");
    }

    public static void storeCountry(String country, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_COUNTRY, country)
                .apply();

    }

    public static String getCountry(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_COUNTRY, "");
    }

    public static void storeZip(String zip, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_ZIP, zip)
                .apply();

    }

    public static String getUserZip(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_ZIP, "");
    }

    public static void storeUserCity(String city, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_CITY, city)
                .apply();

    }

    public static String getUserCity(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_CITY, "");
    }

    public static void storeUserPassport(String passport, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_PASSPORT, passport)
                .apply();

    }

    public static String getUserPassport(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_PASSPORT, "");
    }

    public static void storeUserTinNumber(String tin, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_TIN_NUMBER, tin)
                .apply();

    }

    public static String getUserTinNumber(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_TIN_NUMBER, "");
    }

    public static void storeUserTitle(String title, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_TITLE, title)
                .apply();

    }

    public static String getUserTitle(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_TITLE, "");
    }


    public static void storeUserAdd(String add, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_ADD, add)
                .apply();

    }

    public static String getUserAdd(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_ADD, "");
    }

    public static void storeUserBio(String bio, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_BIO, bio)
                .apply();

    }

    public static String getUserBio(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_BIO, "");
    }

    public static void storeUserState(String state, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_STATE, state)
                .apply();

    }

    public static String getUserState(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_STATE, "");
    }

    public static void storeUserImage(String image, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(USER_IMAGE, image)
                .apply();

    }

    public static String getUserImage(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(USER_IMAGE, "");
    }
*/
}
