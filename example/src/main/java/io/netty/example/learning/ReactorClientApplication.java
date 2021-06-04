package io.netty.example.learning;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ReactorClientApplication {

    public static void main(String[] args) {

        SocketChannel clientClient;
        Selector selector;

        try {
            clientClient = SocketChannel.open();
            clientClient.configureBlocking(false);

            selector = Selector.open();
            clientClient.register(selector, SelectionKey.OP_CONNECT);
            clientClient.connect(new InetSocketAddress("127.0.0.1", 9000));

            Set<SelectionKey> ops = null;

            while (true) {
                try {
                    selector.select();
                    ops = selector.selectedKeys();

                    for (Iterator<SelectionKey> it = ops.iterator(); it.hasNext();) {
                        SelectionKey key = it.next();
                        it.remove();

                        if (key.isConnectable()) {
                            System.out.println("client connect");
                            SocketChannel sc = (SocketChannel) key.channel();

                            // 判断此通道上是否正在进行连接操作。
                            // 完成套接字通道的连接过程。
                            if (sc.isConnectionPending()) {
                                sc.finishConnect();
                                System.out.println("完成连接");
                                ByteBuffer buf = ByteBuffer.allocate(1024);
                                buf.put("Hello Server".getBytes());
                                buf.flip();
                                sc.write(buf);
                            }
                            sc.register(selector, SelectionKey.OP_READ);
                        } else if (key.isWritable()) {
                            System.out.println("客户端写");
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buf = ByteBuffer.allocate(1024);
                            buf.put("hello server".getBytes());
                            buf.flip();
                            sc.write(buf);
                        } else if (key.isReadable()) {
                            System.out.println("客户端收到服务器的响应...");
                            SocketChannel sc = (SocketChannel) key.channel();
                            ByteBuffer buf = ByteBuffer.allocate(1024);
                            int count = sc.read(buf);
                            if (count > 0) {
                                buf.flip();
                                byte[] response = new byte[buf.remaining()];
                                buf.get(response);
                                System.out.println(new String(response));
                            }

                        }
                    }

                } catch (Throwable  e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
