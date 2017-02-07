package com.lihb.babyvoice.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.HashMap;
import java.util.Map;

/**
 * IOS NSNotificationCenter for Android
 */
public enum NotificationCenter {
    INSTANCE;

    private static final String TAG = "TApp:NotificationCenter";
    private Map<Class<?>, Notification> notificationMap;
    private long mainThreadId;
    private Handler handler;

    NotificationCenter() {
        notificationMap = new HashMap<>();
        Looper mainLooper = Looper.getMainLooper();
        handler = new Handler(mainLooper);
        mainThreadId = mainLooper.getThread().getId();
    }

    public void addCallbacks(Class callbackParent) {
        for (Class callback : callbackParent.getDeclaredClasses()) {
            addNotification(callback);
        }
        addNotification(callbackParent);
    }

    /**
     * add observer by annotation
     *
     * @param observer
     */
    public void addObserver(final Object observer) {
        if (notificationMap.isEmpty()) {
            throw new IllegalStateException("call addCallbacks before add observer!");
        }
        long threadId = Thread.currentThread().getId();
        if (threadId == mainThreadId) {
            doAddObserver(observer);
        } else {
            android.util.Log.w(TAG, String.format("trying to add observer in non main thread: %s", observer.getClass()));
            handler.post(new Runnable() {
                @Override
                public void run() {
                    doAddObserver(observer);
                }
            });
        }
    }

    private void doAddObserver(Object observer) {
        for (Map.Entry<Class<?>, Notification> entry : notificationMap.entrySet()) {
            Class callback = entry.getKey();
            if (callback.isInstance(observer)) {
                Notification notification = entry.getValue();
                notification.add(observer);
            }
        }
    }

    /**
     * Make sure to call this function in main thread, or will be post in the message queue.
     * Do not call this function in notification callback or will cause concurrent exception
     *
     * @param observer
     */
    public void removeObserverNow(Object observer) {
        long threadId = Thread.currentThread().getId();
        if (threadId == mainThreadId) {
            doRemoveObserver(observer);
        } else {
            android.util.Log.w(TAG, String.format("trying to remove observer in non main thread: %s", observer.getClass()));
            removeObserverLater(observer);
        }
    }

    /**
     * see {@link #removeObserverNow(Object)}
     *
     * @param observer
     */
    public void removeObserver(final Object observer) {
        removeObserverNow(observer);
    }

    /**
     * observer will be removed later
     *
     * @param observer
     */
    public void removeObserverLater(final Object observer) {
        handler.post(new Runnable() {
            public void run() {
                doRemoveObserver(observer);
            }
        });
    }

    private void doRemoveObserver(Object observer) {
        for (Notification notification : notificationMap.values()) {
            notification.remove(observer);
        }
    }

    /**
     * @param callback
     * @return not null
     */
    private <T> Notification<T> getNotification(Class<T> callback) {
        Notification notification = notificationMap.get(callback);
        if (notification == null) {
            notification = addNotification(callback);
        }
        return notification;
    }

    private <T> Notification<T> addNotification(Class<T> callback) {
        Notification<T> notification = notificationMap.get(callback);
        if (notification == null) {
            notification = new Notification<T>(callback, handler);
            notificationMap.put(callback, notification);
        }
        return notification;
    }

    public <T> T getObserver(Class<T> callback) {
        return getNotification(callback).getObserver();
    }

    public void removeAll() {
        for (Notification notification : notificationMap.values()) {
            notification.removeAll();
        }
    }
}

