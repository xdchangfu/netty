package io.netty.example.learning;

import java.io.IOException;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class MainReactor implements Runnable{

    private Selector selector;
    private SubReactorThreadGroup subReactorThreadGroup;

    public MainReactor(SelectableChannel channel) {
        try {
            selector = Selector.open();
            channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.subReactorThreadGroup = new SubReactorThreadGroup();
    }

    @Override
    public void run() {
        System.out.println("MainReactor is running");

        while (!Thread.interrupted()) {

            Set<SelectionKey> ops = null;

            try {
                selector.select(1000);
                ops = selector.selectedKeys();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // 处理相关事件
            for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                SelectionKey key = it.next();
                it.remove();

                try {
                    // 客户端建立连接
                    if (key.isAcceptable()) {
                        System.out.println("收到客户端的连接请求。。。");
                        ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();
                        SocketChannel sc = serverSc.accept();
                        sc.configureBlocking(false);
                        subReactorThreadGroup.dispatch(sc);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
