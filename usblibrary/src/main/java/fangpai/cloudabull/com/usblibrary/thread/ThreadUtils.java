package fangpai.cloudabull.com.usblibrary.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/6/20.
 */

public class ThreadUtils {

    private static ExecutorService executorService;

    public static ExecutorService getExecutorService() {
        if (null == executorService){
            executorService = Executors.newCachedThreadPool();
        }
        return executorService;
    }

}
