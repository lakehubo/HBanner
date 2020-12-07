package com.lake.banner.net;

import com.lake.banner.uitls.LogUtils;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * http执行的线程池
 */
public class HttpThreadPool {
    private volatile static HttpThreadPool mInstance;
    private ThreadPoolExecutor mThreadPoolExec;
    private ExecutorService mSingleExecutorService;
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();//核心线程数
    private static final int CORE_POOL_SIZE = Math.max(2, Math.min(CPU_COUNT - 1, 4));
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final int KEEP_ALIVE = 60;//保持存活时间，当线程数大于corePoolSize的空闲线程能保持的最大时间。
    private static final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();//阻塞队列

    private HttpThreadPool() {
        LogUtils.e("lake", "cpu核心数=" + CPU_COUNT);
        //自定义线程池
        mThreadPoolExec = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, workQueue);
        mThreadPoolExec.allowCoreThreadTimeOut(true);
        //单线程池
        mSingleExecutorService = Executors.newSingleThreadExecutor();
    }


    public static synchronized HttpThreadPool getInstance() {
        if (mInstance == null) {
            synchronized (HttpThreadPool.class) {
                if (mInstance == null) {
                    mInstance = new HttpThreadPool();
                }
            }
        }
        return mInstance;
    }

    //添加无需同步的线程任务
    public void post(Runnable runnable) {
        //需要判断是否是同一任务
        mThreadPoolExec.submit(runnable);
    }

    //单线程池执行
    public void postSingle(Runnable runnable) {
        mSingleExecutorService.submit(runnable);
    }

    public void release() {//完成任务后中断线程池执行队列
        mThreadPoolExec.shutdown();
        mSingleExecutorService.shutdown();
        mInstance = null;
    }

    public void releaseNow() {//立即中断线程池执行队列
        mThreadPoolExec.shutdownNow();
        mSingleExecutorService.shutdownNow();
        workQueue.clear();
        mThreadPoolExec = null;
        mSingleExecutorService = null;
        mInstance = null;
    }
}
