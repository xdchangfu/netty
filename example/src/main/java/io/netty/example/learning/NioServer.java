package io.netty.example.learning;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void main(String[] args) {

    }

    /**
     * 连接线程模型，反应堆，转发器 Acceptor
     */
    private static final class Acceptor implements Runnable {

        private NioReactorThreadGroup nioReactorThreadGroup;
        public Acceptor() {
            this.nioReactorThreadGroup = new NioReactorThreadGroup();
        }

        @Override
        public void run() {
            System.out.println("服务端启动成功，等待客户端接入");
            ServerSocketChannel ssc = null;
            Selector selector = null;

            try {
                ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress("127.0.0.1", 9000));

                selector = Selector.open();
                ssc.register(selector, SelectionKey.OP_ACCEPT);

                Set<SelectionKey> ops = null;
                while (true) {
                    try {
                        // 如果没有感兴趣的事件到达，阻塞等待
                        selector.select();
                        ops = selector.selectedKeys();
                    } catch (Throwable e) {
                        e.printStackTrace();
                        break;
                    }

                    // 处理相关事件
                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();

                        try {
                            if (key.isAcceptable()) {
                                System.out.println("收到客户端的连接请求。。。");
                                ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();

                                SocketChannel clientChannel = serverSc.accept();
                                clientChannel.configureBlocking(false);
                                // 转发该请求
                                nioReactorThreadGroup.dispatch(clientChannel);

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("客户端主动断开连接。。。。。。。");
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
