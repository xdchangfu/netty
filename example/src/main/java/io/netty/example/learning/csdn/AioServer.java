package io.netty.example.learning.csdn;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * 异步非阻塞， 由操作系统完成后回调通知服务端程序启动线程去处理， 一般适用于连接数较多且连接时间较长的应用。
 */
public class AioServer {

    public static void main(String[] args) throws IOException, InterruptedException {

        AsynchronousServerSocketChannel assc = AsynchronousServerSocketChannel.open();
        assc.bind(new InetSocketAddress(9000));
        assc.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            @Override
            public void completed(AsynchronousSocketChannel socketChannel, Object attachment) {
                // 再此接收客户端连接，如果不写这行代码后面的客户端连接连不上服务端
                assc.accept(attachment, this);
//                System.out.println(assc.getRemoteAddress());

                ByteBuffer buffer = ByteBuffer.allocate(1024);
                socketChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer attachment) {
                        buffer.flip();
                        System.out.println(new String(buffer.array(), 0, result));

                        socketChannel.write(ByteBuffer.wrap("HelloClient".getBytes()));
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        exc.printStackTrace();
                    }
                });

            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
            }
        });

        Thread.sleep(Integer.MAX_VALUE);
    }
}
