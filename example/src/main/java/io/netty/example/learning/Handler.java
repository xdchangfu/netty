package io.netty.example.learning;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * 业务线程
 * 该handler的功能就是在收到的请求信息，后面加上 hello,服务器收到了你的信息，然后返回给客户端
 */
public class Handler implements Runnable{
    // 服务端给客户端的响应
    private static final byte[] b = "hello,服务器收到了你的信息。".getBytes();

    private SocketChannel sc;
    private ByteBuffer reqBuffer;
    private SubReactorThread parent;

    public Handler(SocketChannel sc, ByteBuffer reqBuffer,
                   SubReactorThread parent) {
        super();
        this.sc = sc;
        this.reqBuffer = reqBuffer;
        this.parent = parent;
    }


    @Override
    public void run() {
        System.out.println("业务在handler中开始执行。。。");
        reqBuffer.put(b);
        parent.register(new NioTask(sc, SelectionKey.OP_WRITE, reqBuffer));
        System.out.println("业务在handler中执行结束。。。");
    }
}
