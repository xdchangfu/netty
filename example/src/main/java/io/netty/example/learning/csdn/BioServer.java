package io.netty.example.learning.csdn;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * https://www.jianshu.com/p/34bdff0adfa5
 */
public class BioServer {

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9000);
        System.out.println("等待连接。。");

        Socket socket = serverSocket.accept();
        System.out.println("有客户端连接了。。");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handler(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static void handler(Socket socket) throws IOException {
        System.out.println("thread id = " + Thread.currentThread().getId());
        byte[] bytes = new byte[1024];
        System.out.println("准备read。。");

        int read = socket.getInputStream().read(bytes);
        System.out.println("read完毕。。");

        if (read != 1) {
            System.out.println("接收到客户端的数据：" + new String(bytes, 0, read));
            System.out.println("thread id = " + Thread.currentThread().getId());
        }

        socket.getOutputStream().write("HelloClient".getBytes());
        socket.getOutputStream().flush();



    }
}
