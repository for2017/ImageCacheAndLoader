package com.lianghanzhen.image.concurrents;


public interface AsyncTaskListener<P, R> {

    void onTaskSuccess(P params, R result);

    void onTaskError(P params, Throwable error);

}
