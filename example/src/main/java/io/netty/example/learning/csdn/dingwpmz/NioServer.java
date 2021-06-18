package io.netty.example.learning.csdn.dingwpmz;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 * Nio服务器，
 * 本例主要用来增加对 Ractor 线程模型的理解，不会考虑半包等网络问题
 *
 * 例子程序的功能：服务器接受客户端的请求数据，然后在后面再追加 (hello,服务器收到了你的信息。)
 */
public class NioServer {

    public static void main(String[] args) {

    }

    /**
     *
     * Reactor模型，反应堆
     */
    private static final class Reactor implements Runnable {
        private static final byte[] b = "hello,服务器收到了你的信息。".getBytes();

        @Override
        public void run() {
            System.out.println("服务端启动成功，等待客户端接入");

            ServerSocketChannel ssc = null;
            Selector selector = null;

            try {
                ssc = java.nio.channels.ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ssc.bind(new InetSocketAddress(9000));

                selector = Selector.open();

                Set<SelectionKey> ops = null;

                while (true) {
                    try {
                        selector.select();
                        ops = selector.selectedKeys();
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }

                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();

                        try {
                            if (key.isAcceptable()) {   // 客户端建立连接
                                ServerSocketChannel serverSc = (ServerSocketChannel) key.channel();
                                SocketChannel clientChannel = serverSc.accept();
                                clientChannel.configureBlocking(false);

                                // 向选择器注册读事件，客户端向服务端发送数据准备好后，再处理
                                clientChannel.register(selector, SelectionKey.OP_READ);
                                System.out.println("收到客户端的连接请求。。。");
                            } else if (key.isWritable()) {  // 向客户端发送请求
                                SocketChannel clientChannel = (SocketChannel) key.channel();
                                ByteBuffer buf = (ByteBuffer) key.attachment();
                                buf.flip();
                                clientChannel.write(buf);
                                System.out.println("服务端向客户端发送数据。。。");

                                // 重新注册读事件
                                clientChannel.register(selector, SelectionKey.OP_READ);
                            } else if (key.isReadable()) {  // 处理客户端发送的数据
                                System.out.println("服务端接收客户端连接请求。。。");
//								System.out.println(key);

                                SocketChannel clientChannel = (SocketChannel) key.channel();
//                              System.out.println("clientChannel.isConnected():" + clientChannel.isConnected());
//								System.out.println("clientChannel.isConnectionPending():" +clientChannel.isConnectionPending());
//								System.out.println("clientChannel.isOpen():" + clientChannel.isOpen());
//								System.out.println("clientChannel.finishConnect():" + clientChannel.finishConnect());

                                ByteBuffer buf = ByteBuffer.allocate(1024);
                                System.out.println(buf.capacity());

                                clientChannel.read(buf);
                                buf.put(b);
                                clientChannel.register(selector, SelectionKey.OP_WRITE, buf);
                            }
                        } catch (Exception e) {
                            System.out.println("客户端主动断开连接。。。。。。。");

                            e.printStackTrace();
                            ssc.register(selector, SelectionKey.OP_ACCEPT);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
