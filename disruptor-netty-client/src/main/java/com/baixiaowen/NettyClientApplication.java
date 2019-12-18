package com.baixiaowen;

import com.baixiaowen.client.MessageConsumerImpl4Client;
import com.baixiaowen.client.NettyClient;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import disruptor.MessageConsumer;
import disruptor.RingBufferWorkPoolFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyClientApplication.class, args);

        // 4个消费者
        MessageConsumer[] consumers = new MessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImpl4Client("code:clientId:" + i);
            consumers[i] = messageConsumer;
        }

        RingBufferWorkPoolFactory.getInstance().initAndStart(ProducerType.MULTI,
                1024 * 1024,
//                new YieldingWaitStrategy(),
                new BlockingWaitStrategy(),
                consumers);
        
        // 建立连接并发送消息
        new NettyClient().setData();
    }

}
