package com.android.tony.defenselib.core.hook;

import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.android.tony.defenselib.core.DefenseCore;
import com.android.tony.defenselib.handler.ExceptionDispatcher;
import com.android.tony.defenselib.killcompat.ActivityKillerV15_V20;
import com.android.tony.defenselib.killcompat.ActivityKillerV21_V23;
import com.android.tony.defenselib.killcompat.ActivityKillerV24_V25;
import com.android.tony.defenselib.killcompat.ActivityKillerV26;
import com.android.tony.defenselib.killcompat.IActivityKiller;

import java.lang.reflect.Field;

public class HookH implements IHook {
    private static final int LAUNCH_ACTIVITY = 100;

    private static final int PAUSE_ACTIVITY = 101;

    private static final int PAUSE_ACTIVITY_FINISHING = 102;

    private static final int STOP_ACTIVITY_HIDE = 104;

    private static final int RESUME_ACTIVITY = 107;

    private static final int DESTROY_ACTIVITY = 109;

    private static final int NEW_INTENT = 112;

    private static final int RELAUNCH_ACTIVITY = 126;

    private IActivityKiller sActivityKiller;

    private ExceptionDispatcher mExceptionDispatcher;

    private boolean isHookedInstrumentation;

    private Handler mHHandler;

    private boolean isHooked;

    private Handler.Callback mCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (mHHandler == null) {
                return false;
            }
            if (!isHooked) {
                return false;
            }
            switch (msg.what) {
                case LAUNCH_ACTIVITY:// startActivity--> activity.attach  activity.onCreate  r.activity!=null  activity.onStart  activity.onResume
                    try {
                        mHHandler.handleMessage(msg);
                    } catch (Throwable throwable) {
                        if (!isHookedInstrumentation) {
                            sActivityKiller.finishLaunchActivity(msg);
                        } else {
                            DefenseCore.maybeChoreographerException(throwable, mExceptionDispatcher);
                        }
                        DefenseCore.notifyException(throwable, mExceptionDispatcher);
                    }
                    return true;
                case RESUME_ACTIVITY://回到activity onRestart onStart onResume
                    try {
                        mHHandler.handleMessage(msg);
                    } catch (Throwable throwable) {
                        if (!isHookedInstrumentation) {
                            sActivityKiller.finishResumeActivity(msg);
                        } else {
                            DefenseCore.maybeChoreographerException(throwable, mExceptionDispatcher);
                        }
                        DefenseCore.notifyException(throwable, mExceptionDispatcher);
                    }
                    return true;
                case PAUSE_ACTIVITY_FINISHING://按返回键 onPause
                    try {
                        mHHandler.handleMessage(msg);
                    } catch (Throwable throwable) {
                        if (!isHookedInstrumentation) {
                            sActivityKiller.finishPauseActivity(msg);
                        } else {
                            DefenseCore.maybeChoreographerException(throwable, mExceptionDispatcher);
                        }
                        DefenseCore.notifyException(throwable, mExceptionDispatcher);
                    }
                    return true;
                case PAUSE_ACTIVITY://开启新页面时，旧页面执行 activity.onPause
                    try {
                        mHHandler.handleMessage(msg);
                    } catch (Throwable throwable) {
                        if (!isHookedInstrumentation) {
                            sActivityKiller.finishPauseActivity(msg);
                        } else {
                            DefenseCore.maybeChoreographerException(throwable, mExceptionDispatcher);
                        }
                        DefenseCore.notifyException(throwable, mExceptionDispatcher);
                    }
                    return true;
                case STOP_ACTIVITY_HIDE://开启新页面时，旧页面执行 activity.onStop
                    try {
                        mHHandler.handleMessage(msg);
                    } catch (Throwable throwable) {
                        if (!isHookedInstrumentation) {
                            sActivityKiller.finishStopActivity(msg);
                        } else {
                            DefenseCore.maybeChoreographerException(throwable, mExceptionDispatcher);
                        }
                        DefenseCore.notifyException(throwable, mExceptionDispatcher);
                    }
                    return true;
                case DESTROY_ACTIVITY:// 关闭activity onStop  onDestroy
                    try {
                        mHHandler.handleMessage(msg);
                    } catch (Throwable throwable) {
                        if (isHookedInstrumentation) {
                            DefenseCore.maybeChoreographerException(throwable, mExceptionDispatcher);
                        }
                        DefenseCore.notifyException(throwable, mExceptionDispatcher);
                    }
                    return true;
            }
            return false;
        }
    };

    public HookH(ExceptionDispatcher exceptionDispatcher) {
        this.mExceptionDispatcher = exceptionDispatcher;
        initActivityKiller();
    }

    @Override
    public void hook() {
        if (isHooked()) {
            return;
        }
        try {
            Class activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null);

            Field mhField = activityThreadClass.getDeclaredField("mH");
            mhField.setAccessible(true);
            mHHandler = (Handler) mhField.get(activityThread);
            Field callbackField = Handler.class.getDeclaredField("mCallback");
            callbackField.setAccessible(true);
            callbackField.set(mHHandler, mCallback);
            isHooked = true;
        } catch (Exception e) {
            e.printStackTrace();
            isHooked = false;
        }
    }

    @Override
    public void unHook() {
        isHooked = false;
    }

    @Override
    public boolean isHooked() {
        return isHooked;
    }

    public void setHookedInstrumentation(boolean hookedInstrumentation) {
        isHookedInstrumentation = hookedInstrumentation;
    }

    private void initActivityKiller() {
        //各版本android的ActivityManager获取方式，finishActivity的参数，token(binder对象)的获取不一样
        if (Build.VERSION.SDK_INT >= 26) {
            sActivityKiller = new ActivityKillerV26();
        } else if (Build.VERSION.SDK_INT == 25 || Build.VERSION.SDK_INT == 24) {
            sActivityKiller = new ActivityKillerV24_V25();
        } else if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT <= 23) {
            sActivityKiller = new ActivityKillerV21_V23();
        } else if (Build.VERSION.SDK_INT >= 15 && Build.VERSION.SDK_INT <= 20) {
            sActivityKiller = new ActivityKillerV15_V20();
        } else if (Build.VERSION.SDK_INT < 15) {
            sActivityKiller = new ActivityKillerV15_V20();
        }
    }

}
