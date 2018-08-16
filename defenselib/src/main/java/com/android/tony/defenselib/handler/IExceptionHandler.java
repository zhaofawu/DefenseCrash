package com.android.tony.defenselib.handler;

public interface IExceptionHandler {
    /**
     * 子线程抛出异常时始终调用该方法。
     * 主线程只有第一次抛出异常时才会调用该方法，
     * 该方法中到的throwable都会上报到bugly。
     *
     * @param thread
     * @param throwable
     * @param isSafeMode
     */
    void onUncaughtExceptionHappened(Thread thread, Throwable throwable, boolean isSafeMode);

    /**
     * 崩溃后进入安全模式
     */
    void onEnterSafeMode();

    /**
     * 可能Activity因为生命周期抛出异常导致黑屏
     * 该方法具有一定的概率性
     *
     * @param throwable
     */
    void onMayBeBlackScreen(Throwable throwable);
}
