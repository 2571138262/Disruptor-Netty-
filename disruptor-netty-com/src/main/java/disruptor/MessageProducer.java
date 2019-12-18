package disruptor;

import com.lmax.disruptor.RingBuffer;
import entity.TranslatorData;
import entity.TranslatorDataMapper;
import io.netty.channel.ChannelHandlerContext;

/**
 * 公共的生产者抽象
 */
public class MessageProducer {
    
    private String producerId;
    
    private RingBuffer<TranslatorDataMapper> ringBuffer;
    
    public MessageProducer(String producerId, RingBuffer<TranslatorDataMapper> ringBuffer) {
        this.producerId = producerId;
        this.ringBuffer = ringBuffer;
    }
    
    public void onData(TranslatorData data, ChannelHandlerContext ctx){
        long sequence = ringBuffer.next();
        try{
            TranslatorDataMapper mapper = ringBuffer.get(sequence);
            mapper.setData(data);
            mapper.setCtx(ctx);
        }finally {
            ringBuffer.publish(sequence);
        }

    }
}
