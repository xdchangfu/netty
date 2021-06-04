package io.netty.example.learning;

import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

public class NioReactorThreadGroup {

    // 请求计数器
    private static final AtomicInteger requestCounter = new AtomicInteger();

    // 线程池IO线程的数量
    private final int nioThreadCount;
    private static final int DEFAULT_NIO_THREAD_COUNT;
    private NioReactorThread[] nioThreads;

    static {
        DEFAULT_NIO_THREAD_COUNT = 4;
    }

    public NioReactorThreadGroup() {
        this(DEFAULT_NIO_THREAD_COUNT);
    }

    public NioReactorThreadGroup(int threadCount) {
        if (threadCount < 1) {
            threadCount = DEFAULT_NIO_THREAD_COUNT;
        }

        this.nioThreadCount = threadCount;
        this.nioThreads = new NioReactorThread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            this.nioThreads[i] = new NioReactorThread();
            // 构造方法中启动线程，由于nioThreads不会对外暴露，故不会引起线程逃逸
            this.nioThreads[i].start();
        }
        System.out.println("Nio 线程数量：" + threadCount);
    }

    public void dispatch(SocketChannel channel) {
        if (channel != null) {
            next().register(channel);
        }
    }

    protected NioReactorThread next() {
        return this.nioThreads[requestCounter.getAndIncrement() % nioThreadCount];
    }
}
