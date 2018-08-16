package com.android.tony.defenselib.handler;

public interface IExceptionHandler {
    /**
     * when we caught the exception,this method will be called.
     * you should print the stack of the throwable,let developer know this crash.
     * when you release this apk,you can upload this throwable to you bug collection sdk.
     *
     * @param thread     which thread crashed.
     * @param throwable  crash throwable.
     * @param isSafeMode it is already in safe mode,if it is true,will mean the previous crash led to this crash
     */
    void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode);

    /**
     * when crash happen ,we will keep the main looper loop.
     * you should know this event.you can do nothing or toast your users.
     */
    void onEnterSafeMode();

    /**
     * crashed by view measure,layout or draw method lead Choreographer dead.
     * when this method called,you should restart that activity or finish it.
     *
     * @param throwable
     */
    void onMayBeBlackScreen(Throwable throwable);
}
