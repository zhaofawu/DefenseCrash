package com.android.tony.defenselib;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.android.tony.defenselib.compat.ActivityKillerV15_V20;
import com.android.tony.defenselib.compat.ActivityKillerV21_V23;
import com.android.tony.defenselib.compat.ActivityKillerV24_V25;
import com.android.tony.defenselib.compat.ActivityKillerV26;
import com.android.tony.defenselib.compat.IActivityKiller;
import com.android.tony.defenselib.handler.ExceptionDispatcher;
import com.android.tony.defenselib.handler.IExceptionHandler;

import java.lang.reflect.Field;

/**
 * Created by wanjian on 2017/2/14.
 */

public final class DefenseCrash {

    private static IActivityKiller sActivityKiller;
    private static ExceptionDispatcher mExceptionDispatcher;
    private static boolean sInstalled = false;//标记位，避免重复安装卸载
    private static boolean isHooked = false;
    private static boolean sIsSafeMode;

    private DefenseCrash() {
    }

    public static void initialize(IExceptionHandler exceptionHandler, boolean isHooked) {
        if (sInstalled) {
            return;
        }
        DefenseCrash.isHooked = isHooked;
        sInstalled = true;
        mExceptionDispatcher = new ExceptionDispatcher(exceptionHandler);
        initActivityKiller();
        initHookH();
        initUnCaughtExceptionHandler();
    }

    private static void initUnCaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                if (mExceptionDispatcher != null) {
                    mExceptionDispatcher.uncaughtExceptionHappened(t, e, isSafeMode());
                }
                if (t == Looper.getMainLooper().getThread()) {
                    maybeChoreographerException(e);
                    safeMode();
                }
            }
        });
    }

    private static void initActivityKiller() {
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

    private static void initHookH() {
        try {
            hookmH();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private static void hookmH() throws Exception {
        final int LAUNCH_ACTIVITY = 100;
        final int PAUSE_ACTIVITY = 101;
        final int PAUSE_ACTIVITY_FINISHING = 102;
        final int STOP_ACTIVITY_HIDE = 104;
        final int RESUME_ACTIVITY = 107;
        final int DESTROY_ACTIVITY = 109;
        final int NEW_INTENT = 112;
        final int RELAUNCH_ACTIVITY = 126;
        Class activityThreadClass = Class.forName("android.app.ActivityThread");
        Object activityThread = activityThreadClass.getDeclaredMethod("currentActivityThread").invoke(null);

        Field mhField = activityThreadClass.getDeclaredField("mH");
        mhField.setAccessible(true);
        final Handler mhHandler = (Handler) mhField.get(activityThread);
        Field callbackField = Handler.class.getDeclaredField("mCallback");
        callbackField.setAccessible(true);
        callbackField.set(mhHandler, new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case LAUNCH_ACTIVITY:// startActivity--> activity.attach  activity.onCreate  r.activity!=null  activity.onStart  activity.onResume
                        try {
                            mhHandler.handleMessage(msg);
                        } catch (Throwable throwable) {
                            if (!isHooked) {
                                sActivityKiller.finishLaunchActivity(msg);
                            } else {
                                maybeChoreographerException(throwable);
                            }
                            notifyException(throwable);
                        }
                        return true;
                    case RESUME_ACTIVITY://回到activity onRestart onStart onResume
                        try {
                            mhHandler.handleMessage(msg);
                        } catch (Throwable throwable) {
                            if (!isHooked) {
                                sActivityKiller.finishResumeActivity(msg);
                            } else {
                                maybeChoreographerException(throwable);
                            }
                            notifyException(throwable);
                        }
                        return true;
                    case PAUSE_ACTIVITY_FINISHING://按返回键 onPause
                        try {
                            mhHandler.handleMessage(msg);
                        } catch (Throwable throwable) {
                            if (!isHooked) {
                                sActivityKiller.finishPauseActivity(msg);
                            } else {
                                maybeChoreographerException(throwable);
                            }
                            notifyException(throwable);
                        }
                        return true;
                    case PAUSE_ACTIVITY://开启新页面时，旧页面执行 activity.onPause
                        try {
                            mhHandler.handleMessage(msg);
                        } catch (Throwable throwable) {
                            if (!isHooked) {
                                sActivityKiller.finishPauseActivity(msg);
                            } else {
                                maybeChoreographerException(throwable);
                            }
                            notifyException(throwable);
                        }
                        return true;
                    case STOP_ACTIVITY_HIDE://开启新页面时，旧页面执行 activity.onStop
                        try {
                            mhHandler.handleMessage(msg);
                        } catch (Throwable throwable) {
                            if (!isHooked) {
                                sActivityKiller.finishStopActivity(msg);
                            } else {
                                maybeChoreographerException(throwable);
                            }
                            notifyException(throwable);
                        }
                        return true;
                    case DESTROY_ACTIVITY:// 关闭activity onStop  onDestroy
                        try {
                            mhHandler.handleMessage(msg);
                        } catch (Throwable throwable) {
                            if (isHooked) {
                                maybeChoreographerException(throwable);
                            }
                            notifyException(throwable);
                        }
                        return true;
                }
                return false;
            }
        });
    }


    private static void notifyException(Throwable throwable) {
        if (mExceptionDispatcher != null) {
            mExceptionDispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), throwable, isSafeMode());
        }

        if (!isSafeMode()) {
            safeMode();
        }
    }

    public static boolean isSafeMode() {
        return sIsSafeMode;
    }

    private static void safeMode() {
        sIsSafeMode = true;
        if (mExceptionDispatcher != null) {
            mExceptionDispatcher.enterSafeMode();
        }
        while (true) {
            try {
                Looper.loop();
            } catch (Throwable e) {
                maybeChoreographerException(e);
                notifyException(e);
            }
        }
    }

    /**
     * view measure layout draw时抛出异常会导致Choreographer挂掉
     * <p>
     * 建议直接杀死app。以后的版本会只关闭黑屏的Activity
     *
     * @param e
     */
    private static void maybeChoreographerException(Throwable e) {
        if (e == null || mExceptionDispatcher == null) {
            return;
        }
        StackTraceElement[] elements = e.getStackTrace();
        if (elements == null) {
            return;
        }

        for (int i = elements.length - 1; i > -1; i--) {
            if (elements.length - i > 20) {
                return;
            }
            StackTraceElement element = elements[i];
            if ("android.view.Choreographer".equals(element.getClassName())
                    && "Choreographer.java".equals(element.getFileName())
                    && "doFrame".equals(element.getMethodName())) {
                mExceptionDispatcher.mayBeBlackScreen(e);
                return;
            }

        }
    }

    public static void injectDispatcherToInstrumentation(DefenseInstrumentation instrumentation) {
        if (instrumentation != null) {
            instrumentation.setExceptionDispatcher(mExceptionDispatcher);
        }
    }
}
