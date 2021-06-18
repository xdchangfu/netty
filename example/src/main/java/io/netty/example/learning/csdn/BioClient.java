package io.netty.example.learning.csdn;

import java.io.IOException;
import java.net.Socket;

public class BioClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9000);
        socket.getOutputStream().write("HelloServer".getBytes());
        socket.getOutputStream().flush();
        System.out.println("向服务端发送数据结束");

        byte[] bytes = new byte[1024];
        socket.getInputStream().read(bytes);
        System.out.println("接收到服务端的数据：" + new String(bytes));

        socket.close();
    }
}
