package dougou.rtmp.netty.operate;
import java.util.List;
import dougou.rtmp.netty.model.Consts;
import dougou.rtmp.netty.model.RTMPChunk;
import dougou.rtmp.netty.tools.AMF0;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * @program: DouGouServer2
 * @description: 返回Command
 * @author: zihan.wu
 * @create: 2020-12-24 23:03
 **/
public class SetMessageResponseChunk extends RTMPChunk {
    public SetMessageResponseChunk(List<Object> list){
        this.basicHeader = new BasicHeader();
        basicHeader.setFmt((byte)0);
        basicHeader.setChunkStreamId(Consts.PROTOCOL_CHUNK_STREAM_ID);
        this.messageHeader = new MessageHeader();
        messageHeader.setMessageTypeId(Consts.RTMP_COMMAND_MESSAGE);
        messageHeader.setMessageStreamId(Consts.PROTOCOL_CHUNK_STREAM_ID);
        messageHeader.setTimestamp(0);
        ByteBuf buffer = Unpooled.buffer();
        AMF0.encode(buffer,list);
        byte[] payload = new byte[buffer.readableBytes()];
        messageHeader.setMessageLength(payload.length);
        buffer.readBytes(payload);
        this.setChunkData(payload);
    }
}
