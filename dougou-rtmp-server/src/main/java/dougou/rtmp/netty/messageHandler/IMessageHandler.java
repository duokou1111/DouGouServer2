package dougou.rtmp.netty.messageHandler;

import dougou.rtmp.netty.model.RTMPChunk;
import io.netty.channel.ChannelHandlerContext;

/**
 * @program: DouGouServer2
 * @description
 * @author: zihan.wu
 * @create: 2020/12/24/024
 **/
public interface IMessageHandler {

    Boolean isSupport(String b);
    void handle(RTMPChunk receive,ChannelHandlerContext ctx);

}
