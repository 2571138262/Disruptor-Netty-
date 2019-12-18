package com.baixiaowen.client;

import disruptor.MessageProducer;
import disruptor.RingBufferWorkPoolFactory;
import entity.TranslatorData;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 数据持久化操作 IO读写 会影响性能  ----> 交给一个线程池 去异步的调用执行
        
//        try{
//            TranslatorData response = (TranslatorData) msg;
//            System.out.println("Client端 ：id = " + response.getId() + "; name = " + response.getName() + "; message = " + response.getMessage());
//        }finally {
//            // 一定要注意，用完了缓存一定要释放Buffer缓存
//            ReferenceCountUtil.release(msg);
//        }

        TranslatorData response = (TranslatorData) msg;
        String producerId = "code:sessionId:002";
        MessageProducer messageProducer = RingBufferWorkPoolFactory.getInstance().getMessageProducer(producerId);
        messageProducer.onData(response, ctx);

    }
}
