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
    public static String CONTEXT_ID = "context_id";
    public static String PROJECT_ID = "project_id";
    public static String IMAGE = "image";
    public static String ACTIVE_COUNT = "active_count";
    public static String COMPLETED_COUNT = "completed_count";
    public static String LOGGED_IN_USER_ROLE= "user_role";

    public static String MEASUREMENT_GRID= "measurement_grid";
    public static String DETAIL_SCREEN_STATUS= "DETAIL_SCREEN_STATUS";

    public static String FLOOR_ID= "FLOOR_ID";
    public static String ROOM_ID= "ROOM_ID";
    public static String FLOOR_NAME= "FLOOR_NAME";
    public static String ROOM_NAME= "ROOM_NAME";

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


    public static void storeMeasurementGrid(String status, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(PrefUtils.MEASUREMENT_GRID, status)
                .apply();
        LogUtils.LOGD(TAG, "storeMeasurementGrid");
    }

    public static String getMeasurementGrid(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PrefUtils.MEASUREMENT_GRID, "0");
    }


    public static void storeActiveCount(String activeCount, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(ACTIVE_COUNT, activeCount)
                .apply();

    }

    public static String getActiveCount(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(ACTIVE_COUNT, "");
    }

    public static void storeCompletedCount(String completedCount, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(COMPLETED_COUNT, completedCount)
                .apply();

    }

    public static String getCompletedCount(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(COMPLETED_COUNT, "");
    }

    public static void storeProjectId(String project, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(PROJECT_ID, project)
                .apply();

    }

    public static String getProjectId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PROJECT_ID, "");
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

    public static String getContextId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(CONTEXT_ID, "");
    }

    public static void storeContextId(String con, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(CONTEXT_ID, con)
                .apply();

    }

    public static String getImageUrl(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(IMAGE, "");
    }

    public static void storeImageUrl(String image, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(IMAGE, image)
                .apply();

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

    public static void storeRole(String role, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(LOGGED_IN_USER_ROLE, role)
                .apply();

    }

    public static String getRole(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(LOGGED_IN_USER_ROLE, "");
    }

    public static void storeTempRoomId(String roomId, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(ROOM_ID, roomId)
                .apply();
        LogUtils.LOGD(TAG, "storeMeasurementGrid");
    }

    public static String getTempRoomId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(PrefUtils.ROOM_ID, "0");
    }


    public static void storeTempRoomName(String roomName, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(ROOM_NAME, roomName)
                .apply();

    }

    public static String getTempRoomName(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(ROOM_NAME, "");
    }

    public static void storeTempFloorName(String floorName, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(FLOOR_NAME, floorName)
                .apply();

    }

    public static String getTempFloorName(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(FLOOR_NAME, "");
    }

    public static void storeTempFloorId(String floorId, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(FLOOR_ID, floorId)
                .apply();

    }

    public static String getTempFloorId(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(FLOOR_ID, "");
    }
    public static void storedetailsScreenStatus(String detailStatus, Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        sharedPreferences.edit()
                .putString(DETAIL_SCREEN_STATUS, detailStatus)
                .apply();

    }

    public static String getDetailsScreenStatus(Context context) {
        SharedPreferences sharedPreferences = PrefUtils.getSharedPreferences(context);
        return sharedPreferences.getString(DETAIL_SCREEN_STATUS, "");
    }


}
