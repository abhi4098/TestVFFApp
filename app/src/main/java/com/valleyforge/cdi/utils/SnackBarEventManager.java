package com.valleyforge.cdi.utils;

import java.util.ArrayList;
import java.util.List;

public class SnackBarEventManager {

//    private static volatile SnackBarEventManager _instance = new SnackBarEventManager();
    static List<SnackBarNotifyListener> mNotifyListeners = new ArrayList<>();

    private SnackBarEventManager() {}

    public static void addListener(SnackBarNotifyListener listener) {
        mNotifyListeners.add(listener);
    }

    public static void removeListener(SnackBarNotifyListener listener) {
        mNotifyListeners.remove(listener);
    }

    public static void sendNotification(SnackBarNotification e) {
        for(SnackBarNotifyListener listener: mNotifyListeners) {
            listener.onNotify(e);
        }
    }

    public interface SnackBarNotifyListener {
        void onNotify(SnackBarNotification e);
    }

    public static class SnackBarNotification {
        String message;

        public SnackBarNotification(String message) {
            this.message = message;
        }

        public String getMessage() {
            return this.message;
        }
    }
}
