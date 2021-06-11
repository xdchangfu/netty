/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

/**
 * 入端类型的事件处理器
 * {@link ChannelHandler} which adds callbacks for state changes. This allows the user
 * to hook in to state changes easily.
 */
public interface ChannelInboundHandler extends ChannelHandler {

    /**
     * 通道注册到 Selector 时触发。
     * 客户端在调用 connect 方法，通过 TCP 建立连接后，获取 SocketChannel 后将该通道注册在 Selector 时或服务端在调用 bind 方法后创建 ServerSocketChannel，
     * 通过将通道注册到 Selector 时监听客户端连接上时被调用
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered with its {@link EventLoop}
     */
    void channelRegistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * 通道取消注册到Selector时被调用，通常在通道关闭时触发，首先触发channelInactive 事件，然后再触发 channelUnregistered 事件
     * The {@link Channel} of the {@link ChannelHandlerContext} was unregistered from its {@link EventLoop}
     */
    void channelUnregistered(ChannelHandlerContext ctx) throws Exception;

    /**
     * 通道处于激活的事件，在 Netty 中，处于激活状态表示底层 Socket 的isOpen() 方法与 isConnected() 方法返回 true
     * The {@link Channel} of the {@link ChannelHandlerContext} is now active
     */
    void channelActive(ChannelHandlerContext ctx) throws Exception;

    /**
     * 通道处于非激活（关闭），调用了 close 方法时，会触发该事件，然后触发channelUnregistered 事件
     * The {@link Channel} of the {@link ChannelHandlerContext} was registered is now inactive and reached its
     * end of lifetime.
     */
    void channelInactive(ChannelHandlerContext ctx) throws Exception;

    /**
     * 通道从对端读取数据，当事件轮询到读事件，调用底层 SocketChanne 的 read 方法后，将读取的字节通过事件链进行处理，
     * NIO 的触发入口为AbstractNioByteChannel 的内部类 NioByteUnsafe 的 read 方法
     * Invoked when the current {@link Channel} has read a message from the peer.
     */
    void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception;

    /**
     * 处理完一次通道读事件后触发，在 Netty 中一次读事件处理中，会多次调用SocketChannel 的 read方法。
     * 触发入口为AbstractNioByteChannel 的内部类NioByteUnsafe 的 read 方法
     * Invoked when the last message read by the current read operation has been consumed by
     * {@link #channelRead(ChannelHandlerContext, Object)}.  If {@link ChannelOption#AUTO_READ} is off, no further
     * attempt to read an inbound data from the current {@link Channel} will be made until
     * {@link ChannelHandlerContext#read()} is called.
     */
    void channelReadComplete(ChannelHandlerContext ctx) throws Exception;

    /**
     * 触发用户自定义的事件，目前只定义了ChannelInputShutdownEvent（如果允许半关闭（输入端关闭而服务端不关闭））事件
     * Gets called if an user event was triggered.
     */
    void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception;

    /**
     * Netty 写缓存区可写状态变更事件（可写–》不可写、不可写–》可写），入口消息发送缓存区ChannelOutboundBuffer
     * Gets called once the writable state of a {@link Channel} changed. You can check the state with
     * {@link Channel#isWritable()}.
     */
    void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception;

    /**
     * 异常事件
     * Gets called if a {@link Throwable} was thrown.
     */
    @Override
    @SuppressWarnings("deprecation")
    void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception;
}
