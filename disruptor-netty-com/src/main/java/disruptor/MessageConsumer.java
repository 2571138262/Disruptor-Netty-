package disruptor;

import com.lmax.disruptor.WorkHandler;
import entity.TranslatorDataMapper;

/**
 * 公共的消费者抽象
 */
public abstract class MessageConsumer implements WorkHandler<TranslatorDataMapper> {
    
    protected String consumerId;
    
    public MessageConsumer(String consumerId){
        this.consumerId = consumerId;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }
}
