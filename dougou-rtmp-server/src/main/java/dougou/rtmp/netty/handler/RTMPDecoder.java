package dougou.rtmp.netty.handler;

import dougou.rtmp.netty.model.RTMPChunk;
import dougou.rtmp.netty.model.RTMPDecodeState;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * @program: DouGouServer2
 * @description RTMP消息解码
 * @author: zihan.wu
 * @create: 2020/12/23/023
 **/
public class RTMPDecoder extends ReplayingDecoder<RTMPDecodeState> {
    private  int chunkSize = 128;
    private RTMPChunk currentChunk;
    private ByteBuf payloadBuf;
    @Override
    //解码(解析传输过来的二进制)
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //获取当前状态，为了防止粘包问题需要在读消息的时候区分成读消息头和消息体
        RTMPDecodeState state = state();
        //如果没有状态则设置成读消息头
        if (state == null) {
            state(RTMPDecodeState.DECODE_HEADER);
        }
        //最大消息头长度为18，所以readableBytes要大于18
        if (state == RTMPDecodeState.DECODE_HEADER && in.readableBytes() >= 18) {
            RTMPChunk rtmpChunk = new RTMPChunk();
            //因为currentChunk还没赋值，所以当前的currentChunk其实是上一个Chunk
            rtmpChunk.generateHeader(in,currentChunk);
            currentChunk = rtmpChunk;
            //chunk头读完，设置状态为读data负载
            checkpoint(RTMPDecodeState.DECODE_PAYLOAD);
        }
        if (state == RTMPDecodeState.DECODE_PAYLOAD){
            //payloadBuf是存储Message数据的池（注意不是Chunk的Data，它会收集多个Chunk的Data）
            //本项目只支持单一流，所以不需要通过chunkStreamId组装Message，所以只需要一个payloadBuf
            if (payloadBuf == null){
                payloadBuf = Unpooled.buffer(currentChunk.getMessageHeader().getMessageLength(),currentChunk.getMessageHeader().getMessageLength());
            }
            //如果数据小于chunkSize则全部读完，否则就要读取chunk的最大数据，等待下一个chunk
            int payloadSize = Math.min(currentChunk.getMessageHeader().getMessageLength() - payloadBuf.readableBytes(),chunkSize);
            byte[] payloadArr = new byte[payloadSize];
            in.readBytes(payloadArr);
            //写入Message数据存储池
            payloadBuf.writeBytes(payloadArr);
            //不管有没有读完下一个chunk开始又要从头部读起，状态改回读头部
            checkpoint(RTMPDecodeState.DECODE_HEADER);
            //如果读完了则封装成完整的Message
            if (payloadBuf.readableBytes() == currentChunk.getMessageHeader().getMessageLength()){
                System.out.println("?");
                byte[] payload = new byte[currentChunk.getMessageHeader().getMessageLength()];
                payloadBuf.readBytes(payload);
                currentChunk.setChunkData(payload);
                out.add(currentChunk);
                payloadBuf = null;
            }
        }

    }
    public void setChunkSize(int size){
        this.chunkSize = size;
    }
}
