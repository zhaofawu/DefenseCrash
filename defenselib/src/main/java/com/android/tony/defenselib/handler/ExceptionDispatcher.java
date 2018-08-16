package com.android.tony.defenselib.handler;

public class ExceptionDispatcher {

    private IExceptionHandler mIExceptionHandler;

    public ExceptionDispatcher() {
    }

    public final void uncaughtExceptionHappened(Thread thread, Throwable throwable, boolean isSafeMode) {
        if (mIExceptionHandler == null) {
            return;
        }
        try {
            mIExceptionHandler.onCaughtException(thread, throwable, isSafeMode);
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

    public void setIExceptionHandler(IExceptionHandler IExceptionHandler) {
        mIExceptionHandler = IExceptionHandler;
    }
}
