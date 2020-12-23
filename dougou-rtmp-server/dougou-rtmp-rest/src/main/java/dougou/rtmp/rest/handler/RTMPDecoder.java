package dougou.rtmp.rest.handler;

import com.rtmp.core.model.RTMPDecodeState;
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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //获取当前状态，为了防止粘包问题需要在读消息的时候区分成读消息头和消息体
        RTMPDecodeState state = state();
        //如果没有状态则设置成读消息头
        if (state == null) {
            state(RTMPDecodeState.DECODE_HEADER);
        }
        //如果状态在读消息头
        if (state == RTMPDecodeState.DECODE_HEADER) {
            RTMPChunk rtmpChunk = new RTMPChunk();
            RTMPChunkBasicHeader rtmpChunkBasicHeader = readChunkBasicHeader(in);
            RTMPChunkMessageHeader rtmpChunkMessageHeader = readChunkMessageHeader(in, rtmpChunkBasicHeader.getChunkType());
           /* System.out.println("chunkType:" + rtmpChunkBasicHeader.getChunkType());
            System.out.println("ChunkstreamID:" + rtmpChunkBasicHeader.getChunkStreamId());
            System.out.println("rtmpChunkMessageHeader.getTimeStamp() = " + rtmpChunkMessageHeader.getTimeStamp());
            System.out.println("rtmpChunkMessageHeader.getMessageStreamId() = " + rtmpChunkMessageHeader.getMessageStreamId());
            System.out.println("rtmpChunkMessageHeader.getMessageLength() = " + rtmpChunkMessageHeader.getMessageLength());
            System.out.println("rtmpChunkMessageHeader.getMessageTypeId() = " + rtmpChunkMessageHeader.getMessageTypeId());*/
            if (rtmpChunkMessageHeader.getTimeStamp() == 0x0fff) {
                rtmpChunk.setExtendTimeStamp(in.readInt());
            }
            rtmpChunk.setRtmpChunkBasicHeader(rtmpChunkBasicHeader);
            rtmpChunk.setRtmpChunkMessageHeader(rtmpChunkMessageHeader);
            currentChunk = rtmpChunk;
            checkpoint(RTMPDecodeState.DECODE_PAYLOAD);
        }
        if (state == RTMPDecodeState.DECODE_PAYLOAD){
            if (payloadBuf == null){
                payloadBuf = Unpooled.buffer(currentChunk.getRtmpChunkMessageHeader().getMessageLength(),currentChunk.getRtmpChunkMessageHeader().getMessageLength());
            }
            int payloadSize = Math.min(currentChunk.getRtmpChunkMessageHeader().getMessageLength() - payloadBuf.readableBytes(),chunkSize);
            byte[] payloadArr = new byte[payloadSize];
            in.readBytes(payloadArr);
            payloadBuf.writeBytes(payloadArr);
            checkpoint(RTMPDecodeState.DECODE_HEADER);
            if (payloadBuf.readableBytes() == currentChunk.getRtmpChunkMessageHeader().getMessageLength()){
                byte[] payload = new byte[currentChunk.getRtmpChunkMessageHeader().getMessageLength()];
                payloadBuf.readBytes(payload);
                currentChunk.setPayload(payload);
                out.add(currentChunk.clone());
                payloadBuf = null;
            }
        }

    }
    private RTMPChunkBasicHeader readChunkBasicHeader(ByteBuf in){
        byte b = in.readByte();
        RTMPChunkBasicHeader rtmpChunkBasicHeader = new RTMPChunkBasicHeader();
        rtmpChunkBasicHeader.setChunkType((b & 0xff) >> 6);
        int isreserved = b & 0x3f;
        if(isreserved == 0){
            rtmpChunkBasicHeader.setChunkStreamId((in.readByte() & 0xff) + 64);
        }
        if (isreserved == 1){
            byte secondByte = in.readByte();
            byte thirdByte = in.readByte();
            rtmpChunkBasicHeader.setChunkStreamId((thirdByte & 0xff) << 8 + (secondByte & 0xff) + 64);
        }
        if (isreserved >= 2){
            rtmpChunkBasicHeader.setChunkStreamId(isreserved);
        }
        return rtmpChunkBasicHeader;
    }
    private RTMPChunkMessageHeader readChunkMessageHeader(ByteBuf in,int fmt){

        RTMPChunkMessageHeader rtmpChunkMessageHeader = new RTMPChunkMessageHeader();
        switch (fmt){
            case 0: {
                int timeStamp = in.readMedium();
                int messageLength = in.readMedium();
                byte messageTypeId = in.readByte();
                int streamId = in.readIntLE();
                rtmpChunkMessageHeader.setMessageLength(messageLength);
                rtmpChunkMessageHeader.setMessageStreamId(streamId);
                rtmpChunkMessageHeader.setTimeStamp(timeStamp);
                rtmpChunkMessageHeader.setMessageTypeId(messageTypeId);
                rtmpChunkMessageHeader.setType(0);
                break;
            }
            case 1: {
                int timeStamp = in.readMedium();
                int messageLength = in.readMedium();
                byte messageTypeId = in.readByte();
                rtmpChunkMessageHeader.setMessageLength(messageLength);
                rtmpChunkMessageHeader.setTimeStamp(timeStamp);
                rtmpChunkMessageHeader.setMessageTypeId(messageTypeId);
                rtmpChunkMessageHeader.setType(1);
                break;
            }
            case 2:{
                int timeStamp = in.readMedium();
                rtmpChunkMessageHeader.setType(2);
                rtmpChunkMessageHeader.setTimeStamp(timeStamp);
                rtmpChunkMessageHeader.setMessageLength(currentChunk.getRtmpChunkMessageHeader().getMessageLength());
                rtmpChunkMessageHeader.setMessageTypeId(currentChunk.getRtmpChunkMessageHeader().getMessageTypeId());
                rtmpChunkMessageHeader.setMessageStreamId(currentChunk.getRtmpChunkMessageHeader().getMessageStreamId());
                break;
            }
            case 3:{
                rtmpChunkMessageHeader.setType(3);
                rtmpChunkMessageHeader.setMessageLength(currentChunk.getRtmpChunkMessageHeader().getMessageLength());
                rtmpChunkMessageHeader.setMessageStreamId(currentChunk.getRtmpChunkMessageHeader().getMessageStreamId());
                rtmpChunkMessageHeader.setTimeStamp(currentChunk.getRtmpChunkMessageHeader().getTimeStamp());
                rtmpChunkMessageHeader.setMessageTypeId(currentChunk.getRtmpChunkMessageHeader().getMessageTypeId());
                break;
            }
            default:
                System.out.println("Illegal Chunk Type:"+fmt);
        }
        return rtmpChunkMessageHeader;
    }
    public void setChunkSize(int size){
        this.chunkSize = size;
    }
}
