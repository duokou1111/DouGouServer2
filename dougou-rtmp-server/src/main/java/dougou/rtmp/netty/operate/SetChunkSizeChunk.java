package dougou.rtmp.netty.operate;

import dougou.rtmp.netty.model.Consts;
import dougou.rtmp.netty.model.RTMPChunk;
import dougou.rtmp.netty.tools.Tools;

/**
 * @program: DouGouServer2
 * @description: 设置客户端ChunkSize
 * @author: zihan.wu
 * @create: 2020-12-24 22:50
 **/
public class SetChunkSizeChunk extends RTMPChunk {
    public SetChunkSizeChunk(int ackSize){
        this.basicHeader = new BasicHeader();
        basicHeader.setFmt((byte) 0x00);
        basicHeader.setChunkStreamId(Consts.PROTOCOL_CHUNK_STREAM_ID);
        this.messageHeader = new MessageHeader();
        messageHeader.setMessageLength(4);
        messageHeader.setMessageTypeId(Consts.SET_CHUNK_SIZE);
        messageHeader.setMessageStreamId(Consts.PROTOCOL_CHUNK_STREAM_ID);
        messageHeader.setTimestamp(0);
        this.setChunkData(Tools.IntToBytes(ackSize));
    }
}
