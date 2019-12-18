package entity;

import io.netty.channel.ChannelHandlerContext;

/**
 * 实际Disruptor需要进行内存传输的对象
 */
public class TranslatorDataMapper {
    
    private TranslatorData data;
    
    private ChannelHandlerContext ctx;

    public TranslatorData getData() {
        return data;
    }

    public void setData(TranslatorData data) {
        this.data = data;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }
}
