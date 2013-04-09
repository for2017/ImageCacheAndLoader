package com.lianghanzhen.image.concurrents;


import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


/**
 * An async tasks scheduler
 * @param <P> params
 * @param <R> result
 * @param <T> async task
 */
public class AsyncTaskScheduler<P, R, T extends AsyncTask<P, R>> {

    private static final String TAG = AsyncTaskScheduler.class.getSimpleName();

    private static final int WHAT_SUCCESS = 1;
    private static final int WHAT_ERROR = 2;

    private static final int DEFAULT_CONCURRENTS = 2;
    private static final int DEFAULT_THREAD_PRIORITY = Thread.NORM_PRIORITY - 1;

    private final T mAsyncTask;
    private final List<AsyncTaskListener<P, R>> mAsyncTaskListeners = new ArrayList<AsyncTaskListener<P, R>>();
    private final List<P> mRunningTasks = new ArrayList<P>();
    private final List<P> mWaitingTasks = new ArrayList<P>();

    private final int mConcurrents;
    private final ExecutorService mFixedExecutorService;
    private final InternalHandler<P, R, T> mInternalHandler;

    public AsyncTaskScheduler(final T asyncTask) {
        this(asyncTask, DEFAULT_CONCURRENTS, DEFAULT_THREAD_PRIORITY);
    }

    public AsyncTaskScheduler(final T asyncTask, final int concurrents, final int threadPriority) {
        mAsyncTask = asyncTask;
        mConcurrents = concurrents;
        mFixedExecutorService = Executors.newFixedThreadPool(mConcurrents, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                Thread thread = new Thread(runnable);
                thread.setPriority(threadPriority);
                return thread;
            }
        });
        mInternalHandler = new InternalHandler<P, R, T>(this);
    }

    /**
     * add task to running or waiting task queue.
     * @param params added task params
     */
    public void addTask(P params) {
        if (mRunningTasks.contains(params) || mWaitingTasks.contains(params)) {
            return;
        }

        if (mRunningTasks.size() < mConcurrents) {
            startAsyncTask(params);
        } else {
            mWaitingTasks.add(params);
        }
    }

    private void startAsyncTask(P params) {
        mRunningTasks.add(params);
        mFixedExecutorService.execute(new InternalAsyncTask<P, R, T>(mInternalHandler, mAsyncTask, params));
    }

    private void onTaskSuccess(P params, R result) {
        mRunningTasks.remove(params);
        for (AsyncTaskListener<P, R> asyncTaskListener : mAsyncTaskListeners) {
            asyncTaskListener.onTaskSuccess(params, result);
        }
        scheduleNextAsyncTask();
    }

    private void onTaskError(P params, Throwable error) {
        mRunningTasks.remove(params);
        for (AsyncTaskListener<P, R> asyncTaskListener : mAsyncTaskListeners) {
            asyncTaskListener.onTaskError(params, error);
        }
        scheduleNextAsyncTask();
    }

    private void scheduleNextAsyncTask() {
        int runningSize = mRunningTasks.size();
        int waitingSize = mWaitingTasks.size();
        while (runningSize < mConcurrents && waitingSize > 0) {
            startAsyncTask(mWaitingTasks.remove(0));
        }
    }

    /**
     * handle the async task result
     * @param <P> params
     * @param <R> result
     * @param <T> async task
     */
    private static class InternalHandler<P, R, T extends AsyncTask<P, R>> extends Handler {

        private final WeakReference<AsyncTaskScheduler<P, R, T>> mAsyncTaskSchedulerRef;

        private InternalHandler(AsyncTaskScheduler<P, R, T> asyncTaskScheduler) {
            mAsyncTaskSchedulerRef = new WeakReference<AsyncTaskScheduler<P, R, T>>(asyncTaskScheduler);
        }

        @Override
        public void handleMessage(Message msg) {
            AsyncTaskScheduler<P, R, T> asyncTaskScheduler = mAsyncTaskSchedulerRef.get();
            if (asyncTaskScheduler != null) {
                int what = msg.what;
                switch (what) {
                    case WHAT_SUCCESS:
                        AsyncTaskResult<P, R> asyncTaskResult = (AsyncTaskResult<P, R>) msg.obj;
                        asyncTaskScheduler.onTaskSuccess(asyncTaskResult.mParams, asyncTaskResult.mResult);
                        break;
                    case WHAT_ERROR:
                        AsyncTaskError<P> asyncTaskError = (AsyncTaskError<P>) msg.obj;
                        asyncTaskScheduler.onTaskError(asyncTaskError.mParams, asyncTaskError.mError);
                        break;
                }
            }
        }

    }

    /**
     * represent an async task success result
     * @param <P> params
     * @param <R> result
     */
    private static class AsyncTaskResult<P, R> {

        private final P mParams;
        private final R mResult;

        private AsyncTaskResult(P params, R result) {
            mParams = params;
            mResult = result;
        }

    }

    /**
     * represent an async task error
     * @param <P> params
     */
    private static class AsyncTaskError<P> {

        private final P mParams;
        private final Throwable mError;

        private AsyncTaskError(P params, Throwable error) {
            mParams = params;
            mError = error;
        }

    }

    /**
     * do an async task in a separate thread, when it is done, send result or error to handler
     * @param <P> params
     * @param <R> result
     * @param <T> async task
     */
    private static class InternalAsyncTask<P, R, T extends AsyncTask<P, R>> implements Runnable {

        private final InternalHandler<P, R, T> mInternalHandler;
        private final T mAsyncTask;
        private final P mParams;

        private InternalAsyncTask(InternalHandler<P, R, T> internalHandler, T asyncTask, P params) {
            mInternalHandler = internalHandler;
            mAsyncTask = asyncTask;
            mParams = params;
        }

        @Override
        public void run() {
            try {
                R result = mAsyncTask.doAsyncTask(mParams);
                mInternalHandler.obtainMessage(WHAT_SUCCESS, new AsyncTaskResult<P, R>(mParams, result)).sendToTarget();
            } catch (Throwable error) {
                Log.w(TAG, error.getMessage(), error);
                mInternalHandler.obtainMessage(WHAT_ERROR, new AsyncTaskError<P>(mParams, error)).sendToTarget();
            }
        }

    }

}
