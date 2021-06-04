package io.netty.example;

import java.io.IOException;
import java.net.Socket;

public class SocketClientExample {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("127.0.0.1", 9000);
        socket.getOutputStream().write("HelloServer".getBytes());
        socket.getOutputStream().flush();

        byte[] bytes = new byte[1024];
        socket.getInputStream().read(bytes);
        socket.close();
    }
}
