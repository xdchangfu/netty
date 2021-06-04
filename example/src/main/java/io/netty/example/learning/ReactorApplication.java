package io.netty.example.learning;

import org.bouncycastle.asn1.cms.KEKIdentifier;

import javax.swing.text.AbstractDocument;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

public class ReactorApplication {

    public static void main(String[] args) {

        new Thread(new Reactor()).start();
    }

    private static final class Reactor implements Runnable {

        private static final byte[] b = "hello,服务器收到了你的信息。".getBytes();

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

                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();

                        try {

                            // 客户端建立连接
                            if (key.isAcceptable()) {
                                // 这里其实，可以直接使用ssl这个变量
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
//                              System.out.println(key);
                                SocketChannel clientChannel = (SocketChannel) key.channel();

//                              System.out.println("clientChannel.isConnected():" + clientChannel.isConnected());
//                              System.out.println("clientChannel.isConnectionPending():" +clientChannel.isConnectionPending());
//                              System.out.println("clientChannel.isOpen():" + clientChannel.isOpen());
//                              System.out.println("clientChannel.finishConnect():" + clientChannel.finishConnect());

                                ByteBuffer buf = ByteBuffer.allocate(1024);
                                System.out.println(buf.capacity());
                                clientChannel.read(buf);
                                buf.put(b);

                                // 注册写事件
                                clientChannel.register(selector, SelectionKey.OP_WRITE, buf);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            ssc.register(selector, SelectionKey.OP_ACCEPT);
                        }

                    }
                }
            } catch (IOException  e) {
                e.printStackTrace();
            }
        }
    }





























}
