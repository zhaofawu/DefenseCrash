package com.android.tony.defenselib.core;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.PersistableBundle;

import com.android.tony.defenselib.handler.ExceptionDispatcher;

public class DefenseInstrumentation extends Instrumentation {

    private Instrumentation mInstrumentation;

    private ExceptionDispatcher mExceptionDispatcher;

    public DefenseInstrumentation(Instrumentation instrumentation) {
        mInstrumentation = instrumentation;
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        try {
            mInstrumentation.callActivityOnCreate(activity, icicle);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        try {
            mInstrumentation.callActivityOnCreate(activity, icicle, persistentState);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnDestroy(Activity activity) {
        try {
            mInstrumentation.callActivityOnDestroy(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState) {
        try {
            mInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnRestoreInstanceState(Activity activity, Bundle savedInstanceState, PersistableBundle persistentState) {
        try {
            mInstrumentation.callActivityOnRestoreInstanceState(activity, savedInstanceState, persistentState);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle) {
        try {
            mInstrumentation.callActivityOnPostCreate(activity, icicle);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnPostCreate(Activity activity, Bundle icicle, PersistableBundle persistentState) {
        try {
            mInstrumentation.callActivityOnPostCreate(activity, icicle, persistentState);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnNewIntent(Activity activity, Intent intent) {
        try {
            mInstrumentation.callActivityOnNewIntent(activity, intent);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnStart(Activity activity) {
        try {
            mInstrumentation.callActivityOnStart(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnRestart(Activity activity) {
        try {
            mInstrumentation.callActivityOnRestart(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnResume(Activity activity) {
        try {
            mInstrumentation.callActivityOnResume(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnStop(Activity activity) {
        try {
            mInstrumentation.callActivityOnStop(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState) {
        try {
            mInstrumentation.callActivityOnSaveInstanceState(activity, outState);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnSaveInstanceState(Activity activity, Bundle outState, PersistableBundle outPersistentState) {
        try {
            mInstrumentation.callActivityOnSaveInstanceState(activity, outState, outPersistentState);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        try {
            mInstrumentation.callActivityOnPause(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    @Override
    public void callActivityOnUserLeaving(Activity activity) {
        try {
            mInstrumentation.callActivityOnUserLeaving(activity);
        } catch (Exception e) {

            callException(e);
        }
    }

    private void callException(Throwable throwable) {
        if (mExceptionDispatcher != null) {
            mExceptionDispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), throwable, DefenseCore.isInSafeMode());
        }
    }

    public void setExceptionDispatcher(ExceptionDispatcher exceptionDispatcher) {
        mExceptionDispatcher = exceptionDispatcher;
    }
}
