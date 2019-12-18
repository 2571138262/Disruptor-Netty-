package com.baixiaowen.client;

import disruptor.MessageConsumer;
import entity.TranslatorData;
import entity.TranslatorDataMapper;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.ReferenceCountUtil;

public class MessageConsumerImpl4Client extends MessageConsumer {


    public MessageConsumerImpl4Client(String consumerId) {
        super(consumerId);
    }

    @Override
    public void onEvent(TranslatorDataMapper event) throws Exception {

        TranslatorData response = event.getData();
        ChannelHandlerContext ctx = event.getCtx();
        // 业务逻辑处理
        try{
            System.out.println("Client端 ：id = " + response.getId() + "; name = " + response.getName() + "; message = " + response.getMessage());   
        }finally {
            ReferenceCountUtil.release(response);
        }

    }
}
