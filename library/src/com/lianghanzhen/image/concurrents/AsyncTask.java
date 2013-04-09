package com.lianghanzhen.image.concurrents;


public interface AsyncTask<P, R> {

    R doAsyncTask(P params);

}
