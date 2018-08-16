package com.android.tony.defenselib.core;

import android.os.Looper;

import com.android.tony.defenselib.handler.ExceptionDispatcher;

public class DefenseCore {

    private static boolean isSafeMode;


    /**
     * crashed by view measure,layout or draw method lead Choreographer dead.
     *
     * @param e
     * @param dispatcher
     */
    public static void maybeChoreographerException(Throwable e, ExceptionDispatcher dispatcher) {
        if (e == null || dispatcher == null) {
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
                dispatcher.mayBeBlackScreen(e);
                return;
            }
        }
    }

    /**
     * Keep the Looper loop when crashed
     *
     * @param dispatcher
     */
    public static void enterSafeModeKeepLoop(ExceptionDispatcher dispatcher) {
        isSafeMode = true;
        if (dispatcher != null) {
            dispatcher.enterSafeMode();
        }
        while (true) {
            try {
                Looper.loop();
            } catch (Throwable e) {
                DefenseCore.maybeChoreographerException(e, dispatcher);
                notifyException(e, dispatcher);
            }
        }
    }

    /**
     * Notify the exception we caught,and enter the safe mode
     *
     * @param throwable
     * @param dispatcher
     */
    public static void notifyException(Throwable throwable, ExceptionDispatcher dispatcher) {
        if (dispatcher != null) {
            dispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), throwable, isInSafeMode());
        }

        if (!isInSafeMode()) {
            enterSafeModeKeepLoop(dispatcher);
        }
    }

    /**
     * is in safe mode or not
     *
     * @return
     */
    public static boolean isInSafeMode() {
        return isSafeMode;
    }
}
