package io.netty.example.learning;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 主从多线程模型(Reactor)
 * 职责维护java.nio.ServerSocketChannel类，绑定服务端监听端口，然后将该通道注册到MainRector中
 */
public class ConcurrentNioServer {

    private static final int DEFAULT_PORT = 9080;

    public static void main(String[] args) {
        new Thread(new Acceptor()).start();
    }

    private static final class Acceptor implements Runnable {

        // main Reactor 线程池，用于处理客户端的连接请求
        private static ExecutorService mainReactor = Executors.newSingleThreadExecutor();

        @Override
        public void run() {
            ServerSocketChannel ssc = null;

            try {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress(DEFAULT_PORT));

                //转发到 MainReactor反应堆
                dispatch(ssc);

                System.out.println("服务端成功启动。。。。。。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void dispatch(ServerSocketChannel ssc) {
            mainReactor.submit(new MainReactor(ssc));
        }
    }
}
