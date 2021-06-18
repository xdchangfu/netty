package io.netty.example.learning;

import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * nio 线程组;简易的NIO线程组
 */
public class SubReactorThreadGroup {
    // 请求计数器
    private static final AtomicInteger requestcounter = new AtomicInteger();

    // 线程池IO线程的数量
    private final int nioThreadCount;
    private static final int DEFAULT_NIO_THREAD_COUNT;
    private SubReactorThread[] nioThreads;
    //业务线程池
    private ExecutorService businessExecutePool;

    static {
        DEFAULT_NIO_THREAD_COUNT = 4;
    }

    public SubReactorThreadGroup() {
        this(DEFAULT_NIO_THREAD_COUNT);
    }

    public SubReactorThreadGroup(int threadCount) {
        if (threadCount < 1) {
            threadCount  = DEFAULT_NIO_THREAD_COUNT;
        }

        businessExecutePool = Executors.newFixedThreadPool(threadCount);

        this.nioThreadCount = threadCount;
        this.nioThreads = new SubReactorThread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            this.nioThreads[i] = new SubReactorThread(businessExecutePool);
            //构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
            this.nioThreads[i].start();
        }
        System.out.println("Nio 线程数量：" + threadCount);
    }

    public void dispatch(SocketChannel socketChannel) {
        if (socketChannel != null) {
            next().register(new NioTask(socketChannel, SelectionKey.OP_READ));
        }
    }

    protected SubReactorThread next() {
        return this.nioThreads[ requestcounter.getAndIncrement() % nioThreadCount];
    }

}
