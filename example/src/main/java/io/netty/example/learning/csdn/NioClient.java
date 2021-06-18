package io.netty.example.learning.csdn;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioClient {

    private Selector selector;

    public static void main(String[] args) throws IOException {

        NioClient client = new NioClient();
        client.initClient("127.0.0.1", 9000);
        client.connect();
    }

    /**
     * 获得一个Socket通道，并对该通道做一些初始化的工作
     *
     * @param ip   连接的服务器的ip
     * @param port 连接的服务器的端口号
     * @throws IOException
     */
    private void initClient(String ip, int port) throws IOException {
        // 获得一个Socket通道
        SocketChannel sc = SocketChannel.open();
        // 设置通道为非阻塞
        sc.configureBlocking(false);
        // 获得一个通道管理器
        this.selector = Selector.open();
        sc.connect(new InetSocketAddress(ip, port));
        // 将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_CONNECT事件。
        sc.register(this.selector, SelectionKey.OP_CONNECT);
    }

    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     *
     * @throws IOException
     */
    private void connect() throws IOException {
        while (true) {
            this.selector.select();
            // 获得selector中选中的项的迭代器
            Iterator<SelectionKey> keys = this.selector.selectedKeys().iterator();
            while (keys.hasNext()) {
                SelectionKey key = keys.next();
                // 删除已选的key,以防重复处理
                keys.remove();

                if (key.isConnectable()) {
                    SocketChannel sc = (SocketChannel) key.channel();
                    // 如果正在连接，则完成连接
                    if (sc.isConnectionPending()) {
                        sc.finishConnect();
                    }
                    sc.configureBlocking(false);

                    ByteBuffer buffer = ByteBuffer.wrap("HelloServer".getBytes());
                    sc.write(buffer);

                    sc.register(selector, SelectionKey.OP_READ);

                } else if (key.isReadable()) {
                    read(key);
                }
            }
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel sc = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int read = sc.read(buffer);
        if (read != -1) {
            System.out.println("客户端收到信息：" + new String(buffer.array(), 0, read));
        }
    }
}
