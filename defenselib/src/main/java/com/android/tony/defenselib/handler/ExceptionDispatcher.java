package com.android.tony.defenselib.handler;

/**
 * Created by wanjian on 2018/5/29.
 */

public class ExceptionDispatcher {

    private IExceptionHandler mIExceptionHandler;

    public ExceptionDispatcher(IExceptionHandler IExceptionHandler) {
        mIExceptionHandler = IExceptionHandler;
    }

    public final void uncaughtExceptionHappened(Thread thread, Throwable throwable, boolean isSafeMode) {
        if (mIExceptionHandler == null) {
            return;
        }
        try {
            mIExceptionHandler.onUncaughtExceptionHappened(thread, throwable, isSafeMode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public final void enterSafeMode() {
        if (mIExceptionHandler == null) {
            return;
        }
        try {
            mIExceptionHandler.onEnterSafeMode();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    public final void mayBeBlackScreen(Throwable e) {
        if (mIExceptionHandler == null) {
            return;
        }
        try {
            mIExceptionHandler.onMayBeBlackScreen(e);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
