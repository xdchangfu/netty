package io.netty.example.learning;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Nio 线程，专门负责nio read,write
 *      * 本类是实例行代码，不会对nio,断线重连，写半包等场景进行处理,旨在理解 Reactor模型（多线程版本）
 */
public class NioReactorThread extends Thread{

    // 服务端给客户端的响应
    private static final byte[] b = "hello,服务器收到了你的信息。".getBytes();

    private Selector selector;
    private List<SocketChannel> waitRegisterList = new ArrayList<>(512);
    private ReentrantLock registerLock = new ReentrantLock();

    public NioReactorThread() {
        try {
            this.selector = Selector.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(SocketChannel channel) {
        if (channel != null) {
            try {
                registerLock.lock();
                waitRegisterList.add(channel);
            } finally {
                registerLock.unlock();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            Set<SelectionKey> ops = null;
            try {
                selector.select(1000);
                ops = selector.selectedKeys();
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                SelectionKey key = it.next();
                it.remove();

                try {
                    // 向客户端发送请求
                    if (key.isWritable()) {
                        SocketChannel sc = (SocketChannel) key.channel();
                        ByteBuffer buf = (ByteBuffer) key.attachment();
                        buf.flip();
                        sc.write(buf);
                        System.out.println("服务端向客户端发送数据...");
                        // 重新注册读事件
                        sc.register(selector, SelectionKey.OP_READ);
                    } else if (key.isReadable()) { // 接受客户端请求
                        System.out.println("服务端接收客户端连接请求。。。");
                        SocketChannel clientChannel = (SocketChannel)key.channel();

                        ByteBuffer buf = ByteBuffer.allocate(1024);
                        System.out.println(buf.capacity());
                        clientChannel.read(buf);
                        buf.put(b);

                        // 注册写事件
                        clientChannel.register(selector, SelectionKey.OP_WRITE, buf);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    System.out.println("客户端主动断开连接。。。。。。。");
                }
            }

            // 注册事件
            if (!waitRegisterList.isEmpty()) {
                try {
                    registerLock.lock();

                    for (Iterator<SocketChannel> it = waitRegisterList.iterator(); it.hasNext();) {
                        SocketChannel sc = it.next();

                        try {
                            sc.register(selector, SelectionKey.OP_READ);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        it.remove();
                    }
                } finally {
                    registerLock.unlock();
                }
            }
        }
    }
}
