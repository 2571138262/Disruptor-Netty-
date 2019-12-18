package com.baixiaowen;

import com.baixiaowen.server.MessageConsumerImpl4Server;
import com.baixiaowen.server.NettyServer;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import com.lmax.disruptor.dsl.ProducerType;
import disruptor.MessageConsumer;
import disruptor.RingBufferWorkPoolFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NettyServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NettyServerApplication.class, args);

        // 4个消费者
        MessageConsumer[] consumers = new MessageConsumer[4];
        for (int i = 0; i < consumers.length; i++) {
            MessageConsumer messageConsumer = new MessageConsumerImpl4Server("code:serverId:" + i);
            consumers[i] = messageConsumer;
        }

        RingBufferWorkPoolFactory.getInstance().initAndStart(ProducerType.MULTI, 
                1024 * 1024, 
//                new YieldingWaitStrategy(),
                new BlockingWaitStrategy(), 
                consumers);
        
        new NettyServer();
    }

}
