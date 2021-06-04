package io.netty.example;

import io.netty.util.NettyRuntime;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class AIOClientExample {

    public static void main(String[] args) {
        System.out.println(NettyRuntime.availableProcessors());
    }
}
