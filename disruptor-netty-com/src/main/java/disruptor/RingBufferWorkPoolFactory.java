package disruptor;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.ProducerType;
import entity.TranslatorDataMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * 环形缓存工作池工厂
 */
public class RingBufferWorkPoolFactory {
    
    private static class SingletonHolder{
        static final RingBufferWorkPoolFactory instance = new RingBufferWorkPoolFactory();
    }
    
    private RingBufferWorkPoolFactory(){}
    
    public static RingBufferWorkPoolFactory getInstance(){
        return SingletonHolder.instance;
    }

    /**
     * 生产者的池
     */
    private static Map<String, MessageProducer> producers = new ConcurrentHashMap<>();

    /**
     * 消费者的池
     */
    private static Map<String, MessageConsumer> consumers = new ConcurrentHashMap<>();
    
    private RingBuffer<TranslatorDataMapper> ringBuffer;
    
    private SequenceBarrier sequenceBarrier;
    
    private WorkerPool<TranslatorDataMapper> workerPool;

    /**
     * 无论是客户端还是服务端，所有业务逻辑的处理都是要交给Disruptor来异步处理的，(当然也可以使用性能较低的线程池)
     * 这样就能够在不影响Netty性能的情况下实现百万级别长链接的高性能Netty网络通讯架构
     * 初始化 Disruptor 多生产者 多消费者模式， 参考之前的多生产者多消费者模式 
     * @param type
     * @param bufferSize
     * @param waitStrategy
     * @param messageConsumers
     */
    public void initAndStart(ProducerType type, int bufferSize, WaitStrategy waitStrategy, MessageConsumer[] messageConsumers){
        // 1、构建RingBuffer
        this.ringBuffer = RingBuffer.create(type,
                new EventFactory<TranslatorDataMapper>() {
                    @Override
                    public TranslatorDataMapper newInstance() {
                        return new TranslatorDataMapper();
                    }
                },
                bufferSize,
                waitStrategy);
        
        // 2、设置序号栅栏
        this.sequenceBarrier = this.ringBuffer.newBarrier();
        
        // 3、设置工作池
        this.workerPool = new WorkerPool<TranslatorDataMapper>(
                this.ringBuffer,
                this.sequenceBarrier,
                new EventExceptionHandler(),
                messageConsumers
        );
        
        // 4、把所构建的消费者置入池中
        for (MessageConsumer mc : messageConsumers){
            this.consumers.put(mc.getConsumerId(), mc);
        }
        
        // 5、添加我们的Sequences
        this.ringBuffer.addGatingSequences(this.workerPool.getWorkerSequences());
        
        // 6、启动工作池
        this.workerPool.start(Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()));
    }

    
    public MessageProducer getMessageProducer(String producerId){
        MessageProducer messageProducer = this.producers.get(producerId);
        if (null == messageProducer){
            messageProducer = new MessageProducer(producerId, this.ringBuffer);
            this.producers.put(producerId, messageProducer);
        }
        return messageProducer;
    }
    
    /**
     * 异常静态内部类
     */
    static class EventExceptionHandler implements ExceptionHandler<TranslatorDataMapper>{
        @Override
        public void handleEventException(Throwable throwable, long l, TranslatorDataMapper translatorDataMapper) {
            
        }

        @Override
        public void handleOnStartException(Throwable throwable) {

        }

        @Override
        public void handleOnShutdownException(Throwable throwable) {

        }
    }
}
