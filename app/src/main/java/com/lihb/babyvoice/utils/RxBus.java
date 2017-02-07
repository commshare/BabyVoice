package com.lihb.babyvoice.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.jakewharton.rxbinding.view.RxView;
import com.trello.rxlifecycle.ActivityEvent;
import com.trello.rxlifecycle.FragmentEvent;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.components.RxActivity;
import com.trello.rxlifecycle.components.support.RxFragmentActivity;

import java.security.InvalidParameterException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;

/**
 * 基于Rx的事件总线
 * 内置了一个默认实例便于全局使用，也可通过create创建新实例在自定义范围内使用
 * <p/>
 * Created by caijw on 2015/8/31.
 */
public class RxBus {
    private static final String TAG = "RxBus";
    private final static RxBus mDefault = new RxBus();
    private final SerializedSubject<Object, Object> mSubject;

    private RxBus() {
        mSubject = new SerializedSubject<>(PublishSubject.create());
    }

    /**
     * 获得默认总线实例
     *
     * @return
     */
    public static RxBus getDefault() {
        return mDefault;
    }

    /**
     * 创建一个新总线实例
     *
     * @return
     */
    public static RxBus create() {
        return new RxBus();
    }

    /**
     * 向总线填入一个事件对象
     *
     * @param event
     */
    public void post(Object event) {
        mSubject.onNext(event);
    }

    /**
     * 向总线填入一个事件对象，延迟发送
     *
     * @param event
     * @param milliSecs 延迟毫秒时间
     */
    public void postDelay(final Object event, long milliSecs) {
        Observable.timer(milliSecs, TimeUnit.MILLISECONDS)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        mSubject.onNext(event);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Post Delay failed.", throwable);
                    }
                });
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 注意：订阅该事件源后必须自行调用 unsubscribe 进行释放，避免内存泄露
     *
     * @param cls 要过滤的事件对象类型
     * @return 事件源Observable
     */
    public <T> Observable<T> register(final Class<T> cls) {
        return mSubject
                .onBackpressureBuffer(100, new Action0() {
                    @Override
                    public void call() {
                        Log.e(TAG, "Back pressure Buffer Overflow.");
                    }
                })
                .filter(new Func1<Object, Boolean>() {
                    @Override
                    public Boolean call(Object o) {
                        return cls.isInstance(o);
                    }
                }).cast(cls);
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。
     *
     * @param cls             要过滤的事件载体类型
     * @param lifecycleObject 绑定订阅的生命周期到一个支持生命周期的对象上,这个对象的类型可以是:
     *                        RxActivity/RxFragmentActivity/RxFragment/RxDialogFragment/View
     * @return 事件源Observable
     */
    public <T> Observable<T> register(@NonNull final Class<T> cls, final Object lifecycleObject) {
        if (lifecycleObject == null) {
            throw new InvalidParameterException("lifecycleObject can not be null");
        }

        Log.v(TAG, "Register for class: " + cls.getName() + ", lifecycleObject type: " + lifecycleObject.getClass().getName());

        if (lifecycleObject instanceof RxActivity) {
            RxActivity rxActivity = (RxActivity) lifecycleObject;
            return registerOnActivity(cls, rxActivity);
        }

        if (lifecycleObject instanceof RxFragmentActivity) {
            RxFragmentActivity rxActivity = (RxFragmentActivity) lifecycleObject;
            return registerOnActivity(cls, rxActivity);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle.components.support.RxFragment) {
            com.trello.rxlifecycle.components.support.RxFragment rxFragment
                    = (com.trello.rxlifecycle.components.support.RxFragment) lifecycleObject;
            return registerOnFragment(cls, rxFragment);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle.components.RxFragment) {
            com.trello.rxlifecycle.components.RxFragment rxFragment
                    = (com.trello.rxlifecycle.components.RxFragment) lifecycleObject;
            return registerOnFragment(cls, rxFragment);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle.components.RxDialogFragment) {
            com.trello.rxlifecycle.components.RxDialogFragment rxFragment
                    = (com.trello.rxlifecycle.components.RxDialogFragment) lifecycleObject;
            return registerOnDialogFragment(cls, rxFragment);
        }

        if (lifecycleObject instanceof com.trello.rxlifecycle.components.support.RxDialogFragment) {
            com.trello.rxlifecycle.components.support.RxDialogFragment rxFragment
                    = (com.trello.rxlifecycle.components.support.RxDialogFragment) lifecycleObject;
            return registerOnDialogFragment(cls, rxFragment);
        }

        if (lifecycleObject instanceof View) {
            View view = (View) lifecycleObject;
            return registerOnView(cls, view);
        }

        Log.w(TAG, "Type of lifecycleObject is: ["
                + lifecycleObject.getClass().getName()
                + "], which is not supported. You should un-subscribe from the returned Observable object yourself.");

        throw new IllegalArgumentException("lifecycleObject is not supported.");
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。
     *
     * @param cls      要过滤的事件载体类型
     * @param activity 绑定订阅的生命周期到一个 activity 上
     * @return 事件源Observable
     */
    public <T> Observable<T> registerOnActivity(final Class<T> cls, final RxActivity activity) {
        if (activity == null) {
            throw new InvalidParameterException("activity can not be null");
        }

        return register(cls).compose(RxLifecycle.<T>bindUntilActivityEvent(activity.lifecycle(), ActivityEvent.DESTROY));
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 activity onDestroy 时将自动销毁。
     *
     * @param cls      要过滤的事件载体类型
     * @param activity 绑定订阅的生命周期到一个 activity 上
     * @return 事件源Observable
     */
    public <T> Observable<T> registerOnActivity(final Class<T> cls, final RxFragmentActivity activity) {
        if (activity == null) {
            throw new InvalidParameterException("activity can not be null");
        }

        return register(cls).compose(RxLifecycle.<T>bindUntilActivityEvent(activity.lifecycle(), ActivityEvent.DESTROY));
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 fragment onDestroy 时将自动销毁。
     *
     * @param cls      要过滤的事件载体类型
     * @param fragment 绑定订阅的生命周期到一个 fragment 上
     * @return 事件源Observable
     */
    public <T> Observable<T> registerOnFragment(final Class<T> cls,
                                                final com.trello.rxlifecycle.components.support.RxFragment fragment) {
        if (fragment == null) {
            throw new InvalidParameterException("fragment can not be null");
        }

        return register(cls).compose(RxLifecycle.<T>bindUntilFragmentEvent(fragment.lifecycle(), FragmentEvent.DESTROY));
    }


    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 fragment onDestroy 时将自动销毁。
     *
     * @param cls      要过滤的事件载体类型
     * @param fragment 绑定订阅的生命周期到一个 fragment 上
     * @return 事件源Observable
     */
    public <T> Observable<T> registerOnFragment(final Class<T> cls, final com.trello.rxlifecycle.components.RxFragment fragment) {
        if (fragment == null) {
            throw new InvalidParameterException("fragment can not be null");
        }

        return register(cls).compose(RxLifecycle.<T>bindUntilFragmentEvent(fragment.lifecycle(), FragmentEvent.DESTROY));
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 使用该函数可以不用调用者管理 Subscription 的退订，在 fragment onDestroy 时将自动销毁。
     *
     * @param cls         要过滤的事件载体类型
     * @param dlgFragment 绑定订阅的生命周期到一个 DialogFragment 上
     * @return 事件源Observable
     */
    public <T> Observable<T> registerOnDialogFragment(final Class<T> cls,
                                                      final com.trello.rxlifecycle.components.RxDialogFragment dlgFragment) {
        if (dlgFragment == null) {
            throw new InvalidParameterException("dlgFragment can not be null");
        }

        return register(cls).compose(RxLifecycle.<T>bindUntilFragmentEvent(dlgFragment.lifecycle(), FragmentEvent.DESTROY));
    }

    public <T> Observable<T> registerOnDialogFragment(final Class<T> cls,
                                                      final com.trello.rxlifecycle.components.support.RxDialogFragment dlgFragment) {
        if (dlgFragment == null) {
            throw new InvalidParameterException("dlgFragment can not be null");
        }

        return register(cls).compose(RxLifecycle.<T>bindUntilFragmentEvent(dlgFragment.lifecycle(), FragmentEvent.DESTROY));
    }

    /**
     * 过滤事件类型，获得一个可订阅的事件源
     * 不同于 register，该函数可以不用调用者管理 Subscription 的退订，在view detached时将自动销毁。
     * 注意：该函数必须在UI现场调用
     *
     * @param cls  要过滤的事件载体类型
     * @param view 绑定订阅的生命周期到一个 view 上
     * @return 事件源Observable
     */
    public <T> Observable<T> registerOnView(final Class<T> cls, final View view) {
        if (view == null) {
            throw new InvalidParameterException("view can not be null");
        }

        return register(cls).takeUntil(RxView.detaches(view));
    }
}
