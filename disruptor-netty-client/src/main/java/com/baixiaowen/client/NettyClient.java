package com.baixiaowen.client;

import codec.MarshallingCodeCFactory;
import entity.TranslatorData;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyClient {

    public static final String HOST = "127.0.0.1";
    public static final int PORT = 8765;

    // 扩展完善 ConcurrentHashMap<Key -> String, Value -> Channel>
    private Channel channel; 
    // 1、创建一个工作线程组，用于实际处理业务的线程组 workGroup
    private EventLoopGroup workGroup = new NioEventLoopGroup();
    
    private ChannelFuture cf;
    
    public NettyClient() {
        this.connect(HOST, PORT);
    }

    private void connect(String host, int port) {

        
//        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 辅助类，帮助构建netty模型 （注意 Client 和 Server 不一样）
        Bootstrap bootstrap = new Bootstrap();


        try {
            // 传递线程组 
            bootstrap.group(workGroup)
                    // 链接的channel  netty 面向的都是NIO
                    .channel(NioSocketChannel.class)
                    // 表示缓存区动态调配(自适应)  NIO 发送或者接收数据的时候都是基于缓存来做的
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    // 缓存的对象池  缓存区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    // 添加netty日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 处理数据，    数据接收过来以后应该给哪个方法回调？这个方法回调的目的就是接收一个这个数据，然后异步的处理
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });
            // 建立连接
            this.cf = bootstrap.connect(HOST, PORT).sync();
            System.out.println("Client connected ....");
            
            // 接下来就进行数据的发送, 但是首先要获取通道
            this.channel = cf.channel();
            
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void setData(){
        for (int i = 0; i < 10; i++) {
//            System.out.println("-----------------" + this.channel);
            TranslatorData request = new TranslatorData();
            request.setId("" + i);
            request.setName("请求消息名称" + i);
            request.setMessage("请求消息内容" + i);
            
            this.channel.writeAndFlush(request);
        }
    }
    
    private void close() throws InterruptedException {
        cf.channel().closeFuture().sync();
        // 优雅停机
        workGroup.shutdownGracefully();
        System.out.println("Server shutdown ....");
    }
}
