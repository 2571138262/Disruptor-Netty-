package com.baixiaowen.server;

import codec.MarshallingCodeCFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class NettyServer {

    public NettyServer() {
        // 1、创建两个工作线程组，  一个用于接收网络请求的线程组 bossGroup， 另一个用于实际出来业务的线程组 workGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        // 辅助类，帮助构建netty模型
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        
        
        try {
            // 传递线程组 
            serverBootstrap.group(bossGroup, workGroup)
                    // 链接的channel  netty 面向的都是NIO
                    .channel(NioServerSocketChannel.class)
                    // 设置 backlog 的值  服务器端接收连接时用
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    // 表示缓存区动态调配(自适应)  NIO 发送或者接收数据的时候都是基于缓存来做的
                    .option(ChannelOption.RCVBUF_ALLOCATOR, AdaptiveRecvByteBufAllocator.DEFAULT)
                    // 缓存的对象池  缓存区 池化操作
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    // 添加netty日志
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 处理数据，    数据接收过来以后应该给哪个方法回调？这个方法回调的目的就是接收一个这个数据，然后异步的处理
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingDecoder());
                            socketChannel.pipeline().addLast(MarshallingCodeCFactory.buildMarshallingEncoder());
                            socketChannel.pipeline().addLast(new ServerHandler());
                        }
                    });
            // 绑定端口、同步等待请求连接
            ChannelFuture cf = serverBootstrap.bind(8765).sync();
            System.out.println("Server startup ....");
            // 异步的去关闭
            cf.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            // 优雅停机
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            System.out.println("Server shutdown ....");
        }
    }
}
