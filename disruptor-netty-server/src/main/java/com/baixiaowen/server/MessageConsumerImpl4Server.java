package com.baixiaowen.server;

import disruptor.MessageConsumer;
import entity.TranslatorData;
import entity.TranslatorDataMapper;
import io.netty.channel.ChannelHandlerContext;

public class MessageConsumerImpl4Server extends MessageConsumer {

    public MessageConsumerImpl4Server(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataMapper event) throws Exception {
        TranslatorData request = event.getData();
        ChannelHandlerContext ctx = event.getCtx();

        // 1、业务处理逻辑
        System.out.println("Server端 ： id = " + request.getId() + "; name = " + request.getName() + "; message = " + request.getMessage() );
        
        // 2、会送响应信息
         TranslatorData response = new TranslatorData();
         response.setId("resp：" + request.getId());
         response.setName("resp：" + request.getName());
         response.setMessage("resp：" + request.getMessage());
         // 写出response响应信息
         ctx.writeAndFlush(response);
    }
}
